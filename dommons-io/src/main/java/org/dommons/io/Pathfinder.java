/*
 * @(#)Pathfinder.java     2011-10-17
 */
package org.dommons.io;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.Silewarner;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.coder.URLCoder;
import org.dommons.io.file.FileRoboter;
import org.dommons.io.file.Filenvironment;
import org.dommons.io.jarurl.JarURLHelper;

/**
 * 路径工具集
 * @author Demon 2011-10-17
 */
public final class Pathfinder {

	static final String[] classpath_prefixs = { "classpath:", "cp:" };
	static final Object[] zip_suffix = { "zip", "jar", "war", "ear", "apk" };

	static final Pattern jarfile = Pattern.compile("(?<=file\\:).+(\\.zip|\\.jar|\\.apk)(?=\\!)", Pattern.CASE_INSENSITIVE);

	/**
	 * 获取缓存文件
	 * @param parts 文件路径
	 * @return 缓存文件
	 */
	public static File cacheFile(String... parts) {
		return cacheFile(Stringure.join('/', parts));
	}

	/**
	 * 获取缓存文件
	 * @param name 文件名
	 * @return 缓存文件
	 */
	public static File cacheFile(String name) {
		if (Stringure.isEmpty(name)) return null;
		return Filenvironment.instance().cacheFile(name);
	}

	/**
	 * 数据值转换 将<code>${*}</code>转换为对应系统变量中的值
	 * <ul>
	 * Example : ${JAVA_HOME}/jre/bin/java.exe -> C:/java/IBM/Java50/jre/bin/java.exe
	 * </ul>
	 * @param value 原数据
	 * @return 转换后数据
	 * @throws IllegalArgumentException 输入的<code>value</code>为空字符串
	 */
	public static String convertClassPath(String value) throws IllegalArgumentException {
		return Stringure.convertSystemVariables(value);
	}

	/**
	 * 获取压缩文件下路径集
	 * @param zip ZIP 压缩文件
	 * @param path 子路径
	 * @return 路径集
	 */
	public static URL[] entries(ZipFile zip, String path) {
		if (zip == null || Stringure.isEmpty(path)) return null;
		Collection<URL> urls = new HashSet();
		Pattern pattern = null;
		{
			String[] p = new String[] { path };
			pattern = compile(p);
			path = p[0];
		}
		int pt = Math.max(0, path.length() - 1);
		for (Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements();) {
			ZipEntry ze = en.nextElement();
			if (ze.isDirectory()) continue;
			String name = ze.getName();
			if (name.startsWith(path)) {
				if (pattern.matcher(name.substring(pt)).matches()) urls.add(jarURL(zip, zip.getName(), ze, null));
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	/**
	 * 将文件路径串解析成对应文件对象
	 * @param filePath 文件路径串
	 * @return 文件对象
	 * @throws IllegalArgumentException 输入的<code>filePath</code>为空字符串
	 */
	public static File findFile(String filePath) throws IllegalArgumentException {
		File file = new File(convertClassPath(filePath));
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			return file;
		}
	}

	/**
	 * 解析路径 可使用<code>classpath:</code>或<code>cp:</code>前缀指定类路径，以及使用${Environment}引用环境变量
	 * @param path 路径字符串
	 * @return 路径URL
	 */
	public static URL findPath(String path) {
		if (path == null) return null;
		path = path.trim();
		String lower = path.toLowerCase();
		for (String prefix : classpath_prefixs) {
			if (lower.startsWith(prefix)) return getResource(convertClassPath(path.substring(prefix.length())));
		}
		try {
			return findFile(path).getCanonicalFile().toURI().toURL();
		} catch (IOException e) {
		}
		try {
			return new URL(path);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * 查找路径 可使用<code>classpath:</code>或<code>cp:</code>前缀指定类路径，以及使用${Environment}引用环境变量
	 * @param path 路径字符串
	 * @return 路径URL集
	 */
	public static Collection<URL> findPathes(String path) {
		Collection<URL> urls = new LinkedList();
		if (path != null) {
			path = Stringure.trim(path);
			String lower = path.toLowerCase();
			fetch: {
				for (String prefix : classpath_prefixs) {
					if (lower.startsWith(prefix)) {
						String p = convertClassPath(path.substring(prefix.length()));
						innerResources(p, urls);
						break fetch;
					}
				}

				File[] files = FileRoboter.listFiles(path);
				if (files != null) {
					for (File f : files) {
						try {
							urls.add(f.toURI().toURL());
						} catch (IOException e) {
							Silewarner.warn(Pathfinder.class, e);
						}
					}
				}
			}
		}
		return urls;
	}

	/**
	 * 获取文件路径的绝对路径
	 * @param file 文件
	 * @return 绝对路径
	 * @throws IllegalArgumentException
	 */
	public static String getCanonicalPath(File file) throws IllegalArgumentException {
		Assertor.F.notNull(file);
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	/**
	 * 获取文件路径的绝对路径
	 * @param filePath 文件路径
	 * @return 绝对路径
	 * @throws IllegalArgumentException
	 */
	public static String getCanonicalPath(String filePath) throws IllegalArgumentException {
		return getCanonicalPath(findFile(filePath));
	}

	/**
	 * 解析URL为文件对象
	 * @param url 路径对象
	 * @return 文件对象
	 */
	public static File getFile(URL url) {
		Assertor.F.notNull(url);
		String path = getPath(url);
		String protocol = url.getProtocol();
		if ("jar".equalsIgnoreCase(protocol) || "zip".equalsIgnoreCase(protocol)) {
			String[] p = Stringure.extract(path, jarfile, 1);
			if (p != null && p.length > 0) return findFile(p[0]);
		} else if (!"file".equalsIgnoreCase(protocol)) {
			return null;
		}
		return findFile(path);
	}

	/**
	 * 解析URL为文件路径
	 * @param url 路径对象
	 * @return 文件路径
	 */
	public static String getPath(URL url) {
		Assertor.F.notNull(url);
		try {
			String path = url.getPath();
			if ("file".equalsIgnoreCase(url.getProtocol()) && isWindows()) path = path.substring(1);
			return URLCoder.decode(path, "utf8");
		} catch (RuntimeException e) {
			return url.getPath();
		}
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param clazz 相关类
	 * @param path 路径
	 * @return 文件URL 文件不存在返回<code>null</code>
	 */
	public static URL getResource(Class clazz, String path) {
		URL url = null;
		if (clazz == null) url = getResource(path);
		else url = getResource(clazz.getClassLoader(), clazz, path);
		return url;
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param cl 类加载器
	 * @param clazz 相关类
	 * @param path 路径
	 * @return 文件URL 文件不存在返回<code>null</code>
	 */
	public static URL getResource(ClassLoader cl, Class clazz, String path) {
		Assertor.F.notEmpty(path);
		path = path(clazz, path);
		return getResource(cl, path);
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param cl 类加载器
	 * @param path 路径
	 * @return 文件URL 文件不存在返回<code>null</code>
	 */
	public static URL getResource(ClassLoader cl, String path) {
		if (path == null) return null;
		path = path.trim();
		cl = cl == null ? Pathfinder.class.getClassLoader() : cl;
		return cl.getResource(path);
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param path 路径
	 * @return 文件URL 文件不存在返回<code>null</code>
	 */
	public static URL getResource(String path) {
		URL url = getResource(Pathfinder.class.getClassLoader(), path);
		if (url == null) url = getResource(Thread.currentThread().getContextClassLoader(), path);
		return url;
	}

	/**
	 * 获取压缩文件内容项的URL
	 * @param file 压缩文件
	 * @param ze 内容项
	 * @return URL
	 */
	public static URL getResource(ZipFile file, ZipEntry ze) {
		Assertor.F.notNull(file, "The zip file is must not be null!");
		Assertor.F.notNull(ze, "The zip entry is must not be null!");

		return jarURL(file, file.getName(), ze, null);
	}

	/**
	 * 根据类路径获取资源文件URL集合
	 * @param clazz 相关类
	 * @param path 文件路径
	 * @return 文件URL集合 文件不存在返回<code>null</code>
	 */
	public static URL[] getResources(Class clazz, String path) {
		if (path == null) return null;
		path = path(clazz, path);
		return getResources(clazz == null ? null : clazz.getClassLoader(), path);
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param cl 类加载器
	 * @param path 路径
	 * @return 文件URL集合 文件不存在返回<code>null</code>
	 */
	public static URL[] getResources(ClassLoader cl, String path) {
		if (path == null) return null;
		path = path.trim();
		Collection<URL> list = new HashSet();
		if (cl == null) {
			cl = Pathfinder.class.getClassLoader();
			innerResources(cl, path, list);
			ClassLoader tcl = Thread.currentThread().getContextClassLoader();
			if (!cl.equals(tcl)) innerResources(tcl, path, list);
		} else {
			innerResources(cl, path, list);
		}
		return list.toArray(new URL[list.size()]);
	}

	/**
	 * 根据类路径获取资源文件URL
	 * @param path 路径
	 * @return 文件URL集合 文件不存在返回<code>null</code>
	 */
	public static URL[] getResources(String path) {
		return getResources((ClassLoader) null, path);
	}

	/**
	 * 获取压缩文件
	 * @param url 路径
	 * @return ZIP 压缩文件
	 * @throws IOException
	 */
	public static ZipFile getZip(URL url) throws IOException {
		if (url == null) return null;
		String p = getPath(url);
		Matcher m = jarfile.matcher(p);
		if (m.find()) {
			File file = findFile(m.group());
			if (file.exists() && file.isFile()) return new ZipFile(file);
		} else file: {
			int pt = p.lastIndexOf('.');
			if (pt < 0 || pt >= p.length() - 1) break file;
			String end = p.toLowerCase().substring(pt + 1);
			if (Arrayard.contains(end, zip_suffix)) {
				File file = findFile(p);
				if (file.exists() && file.isFile()) return new ZipFile(file);
			}
		}
		try {
			URLConnection uconn = url.openConnection();
			if (uconn instanceof JarURLConnection) return ((JarURLConnection) uconn).getJarFile();
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * 是否 WINDOWS 环境
	 * @return 是、否
	 */
	protected static boolean isWindows() {
		return Environments.getProperty("os.name").toUpperCase().contains("WINDOW");
	}

	/**
	 * 编译路径串
	 * @param path 路径串
	 * @return 路径匹配正则
	 */
	static Pattern compile(String... path) {
		Pattern pattern = null;
		if (path[0].matches(".*([\\?\\*]+.*)+")) {
			int[] p = Stringure.indexOf(path[0], "^[^?*]*[\\\\\\/](?=[^\\\\\\/]*[?*])");
			pattern = compile(path[0], p == null ? 0 : p[1]);
			path[0] = p == null ? Stringure.empty : path[0].substring(0, p[1]);
		}
		return pattern;
	}

	/**
	 * 加入资源项集
	 * @param dir 文件夹
	 * @param pattern 过滤正则
	 * @param root 根路径
	 * @param urls 路径集
	 */
	static void inner(File dir, Pattern pattern, String root, Collection<URL> urls) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				inner(f, pattern, root, urls);
			} else if (f.isFile()) {
				String path = getCanonicalPath(f);
				try {
					if (pattern.matcher(path.substring(root.length())).matches()) urls.add(f.toURI().toURL());
				} catch (IOException e) {
					Silewarner.warn(Pathfinder.class, e);
				}
			}
		}
	}

	/**
	 * 加入资源项
	 * @param cl 类加载器
	 * @param path 资源路径
	 * @param urls 路径集
	 */
	static void innerResources(ClassLoader cl, String path, Collection<URL> urls) {
		if (cl == null || urls == null) return;
		if (Stringure.isEmpty(path)) return;
		Pattern pattern = null;
		{
			String[] p = new String[] { path };
			pattern = compile(p);
			path = p[0];
		}
		Enumeration<URL> en;
		try {
			en = cl.getResources(path);
			while (en.hasMoreElements()) {
				innerResources(en.nextElement(), pattern, urls);
			}
		} catch (IOException e) {
			// 一般不出错
		}
	}

	/**
	 * 加入资源项
	 * @param path 资源路径
	 * @param urls 路径集
	 */
	static void innerResources(String path, Collection<URL> urls) {
		ClassLoader cl = Pathfinder.class.getClassLoader();
		innerResources(cl, path, urls);
		ClassLoader tcl = Thread.currentThread().getContextClassLoader();
		if (!cl.equals(tcl)) innerResources(tcl, path, urls);
	}

	/**
	 * 加入资源项集
	 * @param parent 父资源路径
	 * @param pattern 过滤正则
	 * @param urls 路径集
	 */
	static void innerResources(URL parent, Pattern pattern, Collection<URL> urls) {
		if (parent == null || urls == null) return;
		if (pattern == null) {
			urls.add(parent);
		} else if ("file".equalsIgnoreCase(Stringure.trim(parent.getProtocol()))) {
			File dir = findFile(getPath(parent));
			inner(dir, pattern, getCanonicalPath(dir), urls);
		} else if ("jar".equalsIgnoreCase(Stringure.trim(parent.getProtocol()))) {
			String p = getPath(parent);
			Matcher m = jarfile.matcher(p);
			if (m.find()) {
				String prefix = p.substring(m.end() + 2);
				int pt = Math.max(0, prefix.length() - 1);
				ZipFile zip = null;
				String path = null;
				boolean closable = true;
				for (int i = 0; i < 2; i++) {
					try {
						{
							File file = findFile(m.group());
							if (file.exists() && file.isFile()) {
								zip = new ZipFile(file);
								path = getCanonicalPath(file);
							} else {
								URLConnection uconn = parent.openConnection();
								if (uconn instanceof JarURLConnection) {
									JarURLConnection jconn = (JarURLConnection) uconn;
									zip = jconn.getJarFile();
									path = jconn.getJarFileURL().getPath();
									closable = false;
								}
							}
							if (zip == null) continue;
						}
						try {
							for (Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements();) {
								ZipEntry ze = en.nextElement();
								if (ze.isDirectory()) continue;
								String name = ze.getName();
								if (name.startsWith(prefix) && pattern.matcher(name.substring(pt)).matches())
									urls.add(jarURL(zip, path, ze, parent));
							}
						} finally {
							if (zip != null && closable) zip.close();
						}
						break;
					} catch (Throwable t) {
					}
				}
			}
		}
	}

	/**
	 * 生成 JAR 路径
	 * @param
	 * @param path 文件路径
	 * @param ze 元素项
	 * @return 路径
	 */
	static URL jarURL(ZipFile zip, String path, ZipEntry ze, URL parent) {
		return JarURLHelper.jarurl(zip, path, ze, parent);
	}

	/**
	 * 生成路径
	 * @param c 类
	 * @param path 路径
	 * @return 新路径
	 */
	static String path(Class c, String path) {
		path = Stringure.trim(path);
		if (path.startsWith("/")) return path.substring(1);
		else if (c == null) return path;
		while (c.isArray()) {
			c = c.getComponentType();
		}
		return c.getPackage().getName().replace('.', '/') + '/' + path;
	}

	/**
	 * 编译
	 * @param pattern 表达式
	 * @param start 开始位
	 * @return 正则
	 */
	private static Pattern compile(String pattern, int start) {
		int len = pattern == null ? 0 : pattern.length();
		StringBuilder buf = new StringBuilder(len);
		for (int i = start; i < len; i++) {
			char ch = pattern.charAt(i);
			if (i == start && ch != '/' && ch != '\\') buf.append("[\\\\\\/]");
			switch (ch) {
			case '\\':
			case '/':
				buf.append("[\\\\\\/]");
				break;

			case '?': {
				int j = i;
				for (; j + 1 < len; j++) {
					if (pattern.charAt(j + 1) != '?') break;
				}
				buf.append("[^\\\\\\/]");
				if (j > i) {
					buf.append("{0,").append(j - i + 1).append('}');
					i = j;
				} else {
					buf.append('?');
				}
				break;
			}

			case '*': {
				int s = 0;
				for (int j = i; j + 1 < len; j++) {
					ch = pattern.charAt(j + 1);
					if (ch == '*') {
						s |= 1;
						continue;
					} else if (ch == '\\' || ch == '/') {
						s |= 2;
						j++;
					}
					i = j;
					break;
				}

				if (s == 3) { // 连续 * 号加斜杠允许包含斜杠即路径分隔符
					buf.append("([^\\\\\\/]*[\\\\\\/])*");
				} else if (s == 2) {
					buf.append("[^\\\\\\/]*[\\\\\\/]");
				} else {
					buf.append("[^\\\\\\/]*");
				}
				break;
			}

			case '.':
			case '[':
			case ']':
			case '|':
			case '{':
			case '}':
			case '(':
			case ')':
				buf.append('\\').append(ch);
				break;

			default:
				if (ch > 0x7e) {
					String hex = Integer.toHexString(ch);
					buf.append('\\');
					if (hex.length() <= 2) {
						fill(hex, buf.append('x'), '0', 2);
					} else {
						fill(hex, buf.append('u'), '0', 4);
					}
				} else {
					buf.append(ch);
				}
				break;
			}
		}
		return Pattern.compile(buf.toString());
	}

	/**
	 * 填充
	 * @param s 字符串
	 * @param buffer 缓存区
	 * @param c 字符串
	 * @param len 预期长度
	 */
	private static void fill(String s, StringBuilder buffer, char c, int len) {
		int l = s == null ? 0 : s.length();
		for (int i = l; i < len; i++) {
			buffer.append(c);
		}
		if (l > 0) buffer.append(s);
	}

	/**
	 * 构造函数
	 */
	protected Pathfinder() {}
}

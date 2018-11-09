/*
 * @(#)FileClassLoader.java     2011-10-20
 */
package org.dommons.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.dommons.classloader.bean.ClasspathItem;
import org.dommons.classloader.util.ClasspathDiscoverer;
import org.dommons.core.collections.enumeration.CollectionEnumeration;
import org.dommons.core.collections.enumeration.IterableEnumeration;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;

/**
 * 文件类加载器
 * @author Demon 2011-10-20
 */
public class FileClassLoader extends ClassLoader {

	/**
	 * 读取内容
	 * @param item 资源项
	 * @return 内容
	 * @throws IOException
	 */
	static byte[] read(ClasspathItem item) throws IOException {
		if (item == null) return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		byte[] b = new byte[1024];
		try {
			in = item.openStream();
			int r = 0;
			while ((r = in.read(b)) > 0) {
				out.write(b, 0, r);
			}
			out.flush();
		} finally {
			if (in != null) in.close();
			out.close();
		}
		return out.toByteArray();
	}

	private final ClasspathDiscoverer discoverer;
	private volatile boolean closed;
	private Map<String, Class> cache;

	/**
	 * 构造函数
	 * @param parent 父加载器
	 * @param files 资源文件集
	 */
	public FileClassLoader(ClassLoader parent, File... files) {
		super(parent);
		cache = new ConcurrentSoftMap();
		discoverer = new ClasspathDiscoverer();
		init(files);
	}

	/**
	 * 构造函数
	 * @param files 资源文件集
	 */
	public FileClassLoader(File... files) {
		this(getSystemClassLoader(), files);
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (closed) return;
		closed = true;
		cache.clear();
		discoverer.close();
	}

	/**
	 * 获取文件集
	 * @return 文件集
	 */
	public File[] getFiles() {
		return discoverer.getFiles();
	}

	public InputStream getResourceAsStream(String resName) {
		InputStream in = null;
		if (getParent() != null) in = getParent().getResourceAsStream(resName);

		if (!checkClose() && in == null) {
			ClasspathItem item = findItem(resName);
			try {
				if (item != null) in = item.opeanStreamWithWrapper();
			} catch (IOException e) {
			}
		}
		return in;
	}

	protected void finalize() throws Throwable {
		if (!closed) close();
	}

	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (checkClose()) throw new ClassNotFoundException(className);
		Class<?> clazz = cache.get(className);
		if (clazz == null) {
			clazz = getClass(className);
			if (clazz == null) throw new ClassNotFoundException(className);
			cache.put(className, clazz);
		}
		return clazz;
	}

	protected URL findResource(String resName) {
		if (checkClose()) return null;
		ClasspathItem item = findItem(resName);
		return item == null ? null : item.getURL();
	}

	protected Enumeration<URL> findResources(String resName) throws IOException {
		if (checkClose()) return IterableEnumeration.empty();
		List<ClasspathItem> items = findItems(resName, true);
		if (items == null || items.isEmpty()) {
			return IterableEnumeration.empty();
		} else {
			Collection<URL> urls = new ArrayList(items.size());
			for (ClasspathItem item : items) {
				if (item != null) urls.add(item.getURL());
			}
			return CollectionEnumeration.create(urls);
		}
	}

	/**
	 * 加载类数据
	 * @param item 类资源项
	 * @return 数据流
	 */
	protected byte[] loadClassData(ClasspathItem item) {
		try {
			return read(item);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 检查是否关闭
	 * @return 是、否
	 */
	boolean checkClose() {
		return closed;
	}

	/**
	 * 生成域
	 * @param item 类资源项
	 * @return 域
	 */
	ProtectionDomain domain(ClasspathItem item) {
		java.security.cert.Certificate certs[] = null;
		CodeSource cs = new CodeSource(item.getRoot(), certs);
		return new ProtectionDomain(cs, null, this, null);
	}

	/**
	 * 查找资源项
	 * @param resName 资源名
	 * @return 资源项
	 */
	ClasspathItem findItem(String resName) {
		List<ClasspathItem> list = findItems(resName, false);
		ClasspathItem item = list == null || list.isEmpty() ? null : list.get(0);
		try {
			return item;
		} finally {
			if (list != null) list.clear();
		}
	}

	/**
	 * 查找资源集
	 * @param resName 资源名
	 * @param loopAll 是否查找所有
	 * @return 资源集
	 */
	List<ClasspathItem> findItems(String resName, boolean loopAll) {
		return discoverer.find(resName, loopAll);
	}

	/**
	 * 获取类实例
	 * @param className 类名
	 * @return 类实例
	 */
	Class<?> getClass(String className) {
		ClasspathItem item = findItem(getResourceName(className));
		byte[] b = loadClassData(item);
		if (b == null) return null;

		// 实现化类
		Class<?> clazz = defineClass(className, b, 0, b.length, domain(item));
		// 定义包
		String pkgName = getPackageName(className);
		if (pkgName != null) {
			Package pkg = getPackage(pkgName);
			if (pkg == null) definePackage(pkgName, null, null, null, null, null, null, null);
		}
		return clazz;
	}

	/**
	 * 初始化资源文件
	 * @param files 资源文件集
	 */
	void init(File[] files) {
		for (File file : files) {
			discoverer.addFile(file);
		}
	}

	/**
	 * 获取包名
	 * @param fullyQualifiedName 类全名
	 * @return 包名
	 */
	private String getPackageName(String fullyQualifiedName) {
		int index = fullyQualifiedName.lastIndexOf('.');
		return index != -1 ? fullyQualifiedName.substring(0, index) : null;
	}

	/**
	 * 获取资源名
	 * @param className 类名
	 * @return 资源名
	 */
	private String getResourceName(String className) {
		return className.replaceAll("\\.", "\\/").concat(".class");
	}
}

/*
 * @(#)DefaultEnvLoader.java     2018-09-19
 */
package org.dommons.core.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.core.Environments;
import org.dommons.core.Silewarner;
import org.dommons.core.string.Stringure;

/**
 * 默认环境加载器
 * @author demon 2018-09-19
 */
class DefaultEnvLoader extends EnvironmentLoader {

	public Properties load(Properties defaults) {
		Properties prop = defaults;
		for (EnvItem item : envs)
			prop = load(prop, item);
		return prop;
	}

	/**
	 * 加载配置
	 * @param prop 父配置
	 * @param item 环境项
	 * @return 配置
	 */
	protected Properties load(Properties prop, EnvItem item) {
		if (item != null) {
			if (Stringure.isEmpty(item.pattern)) prop = load(item.parent, prop);
			else prop = load(prop, item.parent, item.pattern);
		}
		return prop;
	}

	/**
	 * 查询目标文件
	 * @param path 文件路径
	 * @return 文件路径集
	 * @throws IOException
	 */
	Enumeration<URL> gets(String path) throws IOException {
		Enumeration<URL> en = Environments.class.getClassLoader().getResources(path);
		if (en == null) en = Thread.currentThread().getContextClassLoader().getResources(path);
		return en;
	}

	/**
	 * 加载属性集
	 * @param prop 父属性集
	 * @param parent 父包名
	 * @param regex 文件名正则
	 * @return 属性集
	 */
	Properties load(Properties prop, String parent, String regex) {
		prop = create(prop);
		try {
			Pattern pattern = Pattern.compile(regex),
					jarfile = Pattern.compile("(?<=file\\:).+(\\.zip|\\.jar|\\.apk)(?=\\!)", Pattern.CASE_INSENSITIVE);
			for (Enumeration<URL> en = gets(parent); en != null && en.hasMoreElements();) {
				URL url = en.nextElement();
				String pr = Stringure.trim(url.getProtocol()).toLowerCase();
				if ("file".equals(pr)) {
					File file = new File(path(url));
					if (!file.exists() || !file.isDirectory()) continue;
					File[] fs = file.listFiles();
					if (fs == null) continue;
					for (File f : fs) {
						if (!pattern.matcher(f.getName()).matches()) continue;
						load(prop, new FileInputStream(f));
					}
				} else if ("jar".equals(pr)) {
					String p = path(url);
					Matcher m = jarfile.matcher(p);
					if (m.find()) {
						String prefix = p.substring(m.end() + 2);
						int pt = prefix.length();
						if (!prefix.endsWith("/")) pt += 1;
						try {
							File file = new File(m.group());
							ZipFile zip = new ZipFile(file);
							try {
								load(zip, prop, pt, prefix, pattern);
							} finally {
								if (zip != null) zip.close();
							}
						} catch (Throwable t) {
							URLConnection uconn = url.openConnection();
							if (uconn instanceof JarURLConnection) {
								ZipFile zip = ((JarURLConnection) uconn).getJarFile();
								load(zip, prop, pt, prefix, pattern);
							}
						}
					}
				}
			}
		} catch (Throwable t) {
			Silewarner.warn(Environments.class, "load propertiess fail", t);
		}

		return prop;
	}

	/**
	 * 读取属性文件内容
	 * @param path 文件路径
	 * @param prop 默认属性集
	 * @return 属性集
	 * @throws Exception
	 */
	Properties load(String path, Properties prop) {
		prop = create(prop);

		try {
			for (Enumeration<URL> en = gets(path); en != null && en.hasMoreElements();)
				load(prop, en.nextElement().openStream());
		} catch (Throwable t) {
			Silewarner.warn(Environments.class, "load path +'" + path + "' fail", t);
		}
		return prop;
	}

	/**
	 * 加载压缩文件内容
	 * @param zip 压缩文件
	 * @param prop 属性集
	 * @param pt 前缀长度
	 * @param prefix 前缀
	 * @param pattern 匹配正则
	 * @throws IOException
	 */
	private void load(ZipFile zip, Properties prop, int pt, String prefix, Pattern pattern) throws IOException {
		if (zip == null) return;
		for (Enumeration<? extends ZipEntry> zen = zip.entries(); zen.hasMoreElements();) {
			ZipEntry ze = zen.nextElement();
			if (ze.isDirectory()) continue;
			String name = ze.getName();
			if (!name.startsWith(prefix) || !pattern.matcher(name.substring(pt)).matches()) continue;
			load(prop, zip.getInputStream(ze));
		}
	}
}

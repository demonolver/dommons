/*
 * @(#)JarEntryItem.java     2020-03-17
 */
package org.dommons.io.jarurl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dommons.core.collections.map.DataPair;
import org.dommons.core.util.Arrayard;
import org.dommons.io.Pathfinder;

/**
 * Jar 文件子项
 * @author demon 2020-03-17
 */
class JarEntryItem {

	private final URL parent;
	private final String entry;
	private final String file;

	private volatile DataPair<JarFile, Boolean> jar;

	public JarEntryItem(URL parent, String entry, String file) {
		this.parent = parent;
		this.entry = entry;
		this.file = file;
	}

	/**
	 * 获取子项
	 * @return 子项
	 * @throws IOException
	 */
	public JarEntry getEntry() throws IOException {
		return getJarFile().getJarEntry(entry);
	}

	/**
	 * 获取子项名
	 * @return 子项名
	 */
	public String getEntryName() {
		return entry;
	}

	/**
	 * 获取输入流
	 * @return 输入流
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		DataPair<JarFile, Boolean> p = jarFile();
		JarFile jf = p.getKey();
		return new JarEntryInputStream(jf.getInputStream(jf.getEntry(entry)), p.getValue());
	}

	/**
	 * 获取 JAR 文件
	 * @return JAR 文件
	 * @throws IOException
	 */
	public JarFile getJarFile() throws IOException {
		return jarFile().getKey();
	}

	/**
	 * 获取父文件路径
	 * @return 父文件路径
	 */
	public URL getParent() {
		return parent;
	}

	/**
	 * 获取 JAR 文件目标
	 * @return JAR 文件目标
	 * @throws IOException
	 */
	public DataPair<JarFile, Boolean> jarFile() throws IOException {
		if (jar != null && !isClosed(jar.getKey())) return jar;
		JarFile j = null;
		boolean closeable = true;
		File f = Pathfinder.getFile(parent);
		if (f.exists()) {
			j = new JarFile(f);
		} else {
			URLConnection uconn = parent.openConnection();
			if (uconn instanceof JarURLConnection) {
				JarURLConnection jconn = (JarURLConnection) uconn;
				j = jconn.getJarFile();
				closeable = false;
			}
		}
		return (jar = DataPair.create(j, closeable));
	}

	/**
	 * 匹配路径
	 * @param url 路径
	 * @return 是否匹配
	 */
	public boolean match(URL url) {
		return url != null && Arrayard.equals(url.getFile(), file);
	}

	/**
	 * 是否关闭
	 * @param jar JAR 文件
	 * @return 是、否
	 */
	protected boolean isClosed(JarFile jar) {
		try {
			jar.size();
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
}

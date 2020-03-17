/*
 * @(#)JarURLHelper.java     2020-03-17
 */
package org.dommons.io.jarurl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Jar url 处理助手
 * @author demon 2020-03-17
 */
public class JarURLHelper {

	/**
	 * 生成 JAR 子路径
	 * @param zip 压缩文件
	 * @param path 父 JAR 包路径
	 * @param entry 子项
	 * @param parent 父路径
	 * @return 子路径
	 */
	public static URL jarurl(ZipFile zip, String path, ZipEntry entry, URL parent) {
		if (File.separatorChar != '/') path = path.replace(File.separatorChar, '/');
		StringBuilder buf = new StringBuilder();
		int s = 5;
		buf.append("file:");
		if (!path.startsWith("file:")) s = 0;
		if (!path.startsWith("/", s)) buf.append('/');
		buf.append(path.substring(s)).append("!/");
		buf.append(entry.getName());
		try {
			String sf = buf.toString();
			return new URL("jar", null, -1, sf);
		} catch (IOException e) {
			// 一般不出错
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成子路径流处理
	 * @param parent 父路径
	 * @param entry 子项
	 * @param zip 压缩文件
	 * @param sf 子文件路径
	 * @return 流处理
	 */
	protected static URLStreamHandler handler(URL parent, ZipEntry entry, ZipFile zip, String sf) {
		if (parent == null || new File(zip.getName()).exists()) return null;
		return new JarURLStreamHandler(new JarEntryItem(parent, entry.getName(), sf));
	}
}

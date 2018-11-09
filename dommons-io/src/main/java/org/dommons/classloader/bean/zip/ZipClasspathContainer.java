/*
 * @(#)ZipClasspathContainer.java     2011-10-20
 */
package org.dommons.classloader.bean.zip;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.classloader.bean.ClasspathContainer;
import org.dommons.classloader.bean.ClasspathItem;

/**
 * 压缩资源容器
 * @author Demon 2011-10-20
 */
public class ZipClasspathContainer extends ClasspathContainer {

	private final ZipFile zip;

	private String path;

	/**
	 * 构造函数
	 * @param file 文件
	 * @param zip 压缩包
	 */
	public ZipClasspathContainer(File file, ZipFile zip) {
		super(file);
		this.zip = zip;
	}

	public void close() {
		super.close();
		try {
			zip.close();
		} catch (IOException e) {
		}
	}

	protected ClasspathItem doFindItem(String resName) {
		ZipEntry entry = zip.getEntry(resName);
		return entry == null ? null : new ZipClasspathItem(this, entry);
	}

	/**
	 * 获取路径串
	 * @return 路径串
	 */
	protected String getPath() {
		if (path == null) {
			try {
				path = getFile().toURI().toURL().toExternalForm();
			} catch (MalformedURLException e) {
				StringBuilder buffer = new StringBuilder("file:");
				String p = getFile().getPath();
				if (File.separatorChar != '/') p = p.replace(File.separatorChar, '/');
				if (!p.startsWith("/")) buffer.append('/');
				buffer.append(p);
				path = buffer.toString();
			}
		}
		return path;
	}

	/**
	 * 获取压缩包
	 * @return 压缩包
	 */
	protected ZipFile getZip() {
		return zip;
	}
}
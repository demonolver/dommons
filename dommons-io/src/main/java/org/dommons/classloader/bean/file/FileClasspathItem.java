/*
 * @(#)FileClasspathItem.java     2011-10-20
 */
package org.dommons.classloader.bean.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.dommons.classloader.bean.ClasspathContainer;
import org.dommons.classloader.bean.ClasspathItem;
import org.dommons.classloader.util.ClasspathURLHandler;

/**
 * 文件资源项
 * @author Demon 2011-10-20
 */
public class FileClasspathItem extends ClasspathItem {

	private final File file;

	/**
	 * 构造函数
	 * @param file 文件
	 * @param parent 容器
	 */
	public FileClasspathItem(File file, ClasspathContainer parent) {
		super(parent);
		this.file = file;
	}

	public long getLength() {
		return file.isDirectory() ? 0 : file.length();
	}

	public boolean isClass() {
		return file.getName().endsWith(".class");
	}

	public long lastModified() {
		return file.lastModified();
	}

	public InputStream openStream() throws IOException {
		if (file.isFile()) return new FileInputStream(file);
		else return null;
	}

	protected URL createURL() {
		StringBuilder buffer = new StringBuilder();
		String path = null;
		try {
			path = file.getCanonicalPath();
		} catch (IOException e1) {
			path = file.getAbsolutePath();
		}
		if (File.separatorChar != '/') path = path.replace(File.separatorChar, '/');
		if (!path.startsWith("/")) buffer.append('/');
		buffer.append(path);
		if (!path.endsWith("/") && file.isDirectory()) buffer.append('/');
		try {
			return new URL("file", null, -1, buffer.toString(), new ClasspathURLHandler(this));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	protected boolean isActive() {
		return file.exists();
	}
}
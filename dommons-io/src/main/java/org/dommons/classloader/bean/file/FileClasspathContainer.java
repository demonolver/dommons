/*
 * @(#)FileClasspathContainer.java     2011-10-20
 */
package org.dommons.classloader.bean.file;

import java.io.File;

import org.dommons.classloader.bean.ClasspathContainer;
import org.dommons.classloader.bean.ClasspathItem;

/**
 * 文件资源容器
 * @author Demon 2011-10-20
 */
public class FileClasspathContainer extends ClasspathContainer {

	private final File parent;

	/**
	 * 构造函数
	 * @param file 文件
	 */
	public FileClasspathContainer(File file) {
		super(file);
		this.parent = file;
	}

	public boolean isClosed() {
		return super.isClosed() || !parent.exists() || !parent.isDirectory();
	}

	protected ClasspathItem doFindItem(String resName) {
		File file = new File(parent, resName);
		return file.exists() ? new FileClasspathItem(file, this) : null;
	}
}

/*
 * @(#)ClasspathContainer.java     2011-10-20
 */
package org.dommons.classloader.bean;

import java.io.File;
import java.io.IOException;

import org.dommons.classloader.util.InputStreamControllor;

/**
 * 类路径容器
 * @author Demon 2011-10-20
 */
public abstract class ClasspathContainer {

	protected final File file;
	private final InputStreamControllor controllor;

	/**
	 * 构造函数
	 * @param file 文件
	 */
	protected ClasspathContainer(File file) {
		File tmp = null;
		try {
			tmp = file.getCanonicalFile();
		} catch (IOException e) {
			tmp = file.getAbsoluteFile();
		}
		this.file = tmp;
		this.controllor = new InputStreamControllor();
	}

	/**
	 * 容器关闭
	 */
	public void close() {
		controllor.close();
	}

	public boolean equals(Object o) {
		if (o instanceof ClasspathContainer) {
			return file.equals(((ClasspathContainer) o).getFile());
		} else {
			return false;
		}
	}

	/**
	 * 查找资源项
	 * @param resName 资源名
	 * @return 资源项 不存在返回<code>null</code>
	 */
	public ClasspathItem findItem(String resName) {
		if (isClosed()) return null;
		return doFindItem(resName);
	}

	/**
	 * 获取控制器
	 * @return 控制器
	 */
	public InputStreamControllor getControllor() {
		return controllor;
	}

	/**
	 * 获取文件
	 * @return 文件
	 */
	public File getFile() {
		return file;
	}

	public int hashCode() {
		return file.hashCode();
	}

	/**
	 * 是否已关闭
	 * @return 是、否
	 */
	public boolean isClosed() {
		return controllor.isClosed();
	}

	public String toString() {
		return file.toString();
	}

	/**
	 * 执行查找资源项
	 * @param resName 资源名
	 * @return 资源项 不存在返回<code>null</code>
	 */
	protected abstract ClasspathItem doFindItem(String resName);
}

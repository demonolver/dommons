/*
 * @(#)ClasspathItem.java     2011-10-20
 */
package org.dommons.classloader.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 类资源项
 * @author Demon 2011-10-20
 */
public abstract class ClasspathItem {

	private final ClasspathContainer container;

	private URL url;
	private String stringValue;

	/**
	 * 构造函数
	 * @param container 资源容器
	 */
	protected ClasspathItem(ClasspathContainer container) {
		this.container = container;
	}

	/**
	 * 是否活动
	 * @return 是否
	 */
	public boolean active() {
		return !container.isClosed() && isActive();
	}

	public boolean equals(Object o) {
		if (o instanceof ClasspathItem) {
			return getURL().equals(((ClasspathItem) o).getURL());
		} else {
			return false;
		}
	}

	/**
	 * 获取容器
	 * @return 容器
	 */
	public ClasspathContainer getContainer() {
		return container;
	}

	/**
	 * 获取大小
	 * @return 大小
	 */
	public abstract long getLength();

	/**
	 * 获取根路径
	 * @return 根路径
	 */
	public URL getRoot() {
		try {
			return container.getFile().toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * 获取路径
	 * @return 路径
	 */
	public URL getURL() {
		if (url == null) url = createURL();
		return url;
	}

	public int hashCode() {
		return getURL().hashCode();
	}

	/**
	 * 是否类文件
	 * @return 是、否
	 */
	public abstract boolean isClass();

	/**
	 * 获取最后修改时间
	 * @return 修改时间
	 */
	public abstract long lastModified();

	/**
	 * 打开包装流
	 * @return 输入流
	 * @throws IOException
	 */
	public InputStream opeanStreamWithWrapper() throws IOException {
		return container.getControllor().wrap(openStream());
	}

	/**
	 * 打开输入流
	 * @return 输入流
	 * @throws IOException
	 */
	public abstract InputStream openStream() throws IOException;

	public String toString() {
		return stringValue != null ? stringValue : (stringValue = getURL().toString());
	}

	/**
	 * 创建资源项路径
	 * @return 路径
	 */
	protected abstract URL createURL();

	/**
	 * 是否活动
	 * @return 是否
	 */
	protected abstract boolean isActive();
}

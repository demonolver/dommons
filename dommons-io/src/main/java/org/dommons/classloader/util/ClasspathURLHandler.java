/*
 * @(#)ClasspathURLHander.java     2011-10-20
 */
package org.dommons.classloader.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.dommons.classloader.bean.ClasspathItem;

/**
 * 类路径流处理器
 * @author Demon 2011-10-20
 */
public class ClasspathURLHandler extends URLStreamHandler {

	private final ClasspathItem item;

	/**
	 * 构造函数
	 * @param item 资源项
	 */
	public ClasspathURLHandler(ClasspathItem item) {
		this.item = item;
	}

	protected URLConnection openConnection(URL url) throws IOException {
		return new ClasspathURLConnection(url, item);
	}
}

/*
 * @(#)XDocumentFactoryBuilder.java     2011-11-1
 */
package org.dommons.dom;

import java.io.IOException;
import java.util.Properties;

import org.dommons.core.Silewarner;

/**
 * XML 工厂构建器
 * @author Demon 2011-11-1
 */
final class XDocumentFactoryBuilder {

	static final String PROP_RESOURCE = "xdocument.factories"; // 资源文件

	private final Properties prop; // 属性集

	/**
	 * 构造函数
	 */
	protected XDocumentFactoryBuilder() {
		prop = new Properties();
		try {
			prop.load(XDomor.class.getResourceAsStream(PROP_RESOURCE));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException("Properties file not exist![" + PROP_RESOURCE + "]");
		}
	}

	/**
	 * 构建
	 * @return 工厂实例
	 */
	public XDocumentFactory build() {
		for (String entry : getEntries()) {
			String entryClass = prop.getProperty(entry + ".class");
			String entryFactory = prop.getProperty(entry + ".factory");
			if (findClass(entryClass) == null) continue;
			XDocumentFactory factory = build(entryFactory);
			if (factory != null) return factory;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取内容项
	 * @return 内容项
	 */
	protected String[] getEntries() {
		String value = prop.getProperty("factories");
		if (value == null || value.trim().length() == 0) throw new UnsupportedOperationException();
		return value.split("[ ]*[;|:,][ ]*");
	}

	/**
	 * 构建工厂实例
	 * @param className 工厂类名
	 * @return 工厂实例
	 */
	XDocumentFactory build(String className) {
		Class cls = findClass(className);
		if (cls == null) return null;
		try {
			return (XDocumentFactory) cls.newInstance();
		} catch (Exception e) {
			Silewarner.warn(XDocumentFactoryBuilder.class, e);
			return null;
		}
	}

	/**
	 * 查找指定类
	 * @param className 类名
	 * @return 类实例
	 */
	Class findClass(String className) {
		try {
			return Class.forName(className, false, XDocumentFactory.class.getClassLoader());
		} catch (ClassNotFoundException e) {
		}
		try {
			return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
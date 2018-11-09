/*
 * @(#)LoggerFitter.java     2011-10-25
 */
package org.dommons.log;

import java.io.IOException;
import java.util.Properties;

import org.dommons.core.Assertor;
import org.dommons.core.Silewarner;
import org.dommons.core.string.Stringure;
import org.dommons.io.prop.Bundles;

/**
 * 日志适配器
 * @author Demon 2011-10-25
 */
class LoggerFitter {

	/**
	 * 获取日志工厂
	 * @return 日志工厂
	 */
	public static LoggerFactory getFactory() {
		Properties prop = load();
		String[] factroies = prop.getProperty("factories", Stringure.empty).split("(\\s*,\\s*)+");
		for (String factory : factroies) {
			String tar = prop.getProperty(factory + ".target");
			String cls = prop.getProperty(factory + ".class"), ex = prop.getProperty(factory + ".exclude");
			if (Assertor.P.empty(cls) || (tar != null && !existClass(tar) || (ex != null && existClass(ex)))) continue;
			try {
				return (LoggerFactory) findClass(cls).newInstance();
			} catch (Exception e) {
				Silewarner.warn(LoggerFitter.class, "Instance new logger factory [" + cls + "] fail", e);
			}
		}
		return null;
	}

	/**
	 * 读取定义集
	 * @return 定义集
	 */
	protected static Properties load() {
		try {
			return Bundles.loadByClassPath(null, "org/dommons/log/logger.factories");
		} catch (IOException e) {
			return new Properties();
		}
	}

	/**
	 * 类是否存在
	 * @param className 类名
	 * @return 是否存在
	 */
	static boolean existClass(String className) {
		try {
			return findClass(className) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 查找类
	 * @param className 类名
	 * @return 类
	 * @throws ClassNotFoundException
	 */
	static Class findClass(String className) throws ClassNotFoundException {
		try {
			return Class.forName(className, false, LoggerFitter.class.getClassLoader());
		} catch (ClassNotFoundException e) {
		}
		try {
			return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}
}

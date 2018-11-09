/*
 * @(#)LoggerFactory.java     2011-10-25
 */
package org.dommons.log;

import java.util.Map;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;

/**
 * 日志工厂
 * @author Demon 2011-10-25
 */
public abstract class LoggerFactory {

	private static LoggerFactory instance;
	private static final Map<Object, LoggerWrapper> wrappers = new ConcurrentSoftMap();

	/**
	 * 获取工厂实例
	 * @return 日志工厂实例
	 */
	public static LoggerFactory getInstance() {
		synchronized (LoggerFactory.class) {
			if (instance == null) instance = LoggerFitter.getFactory();
		}
		return instance;
	}

	/**
	 * 获取日志记录器
	 * @param clazz 日志类型
	 * @return 日志记录器
	 */
	public static Logger logger(Class clazz) {
		LoggerFactory factory = getInstance();
		return factory == null ? null : factory.getLogger(clazz);
	}

	/**
	 * 获取日志记录器
	 * @param name 日志名称
	 * @return 日志记录器
	 */
	public static Logger logger(String name) {
		LoggerFactory factory = getInstance();
		return factory == null ? null : factory.getLogger(name);
	}

	private final Map<Object, Logger> cache;

	/**
	 * 构造函数
	 */
	protected LoggerFactory() {
		cache = new ConcurrentSoftMap();
		instance = this;
	}

	/**
	 * 获取日志记录器
	 * @param clazz 日志类型
	 * @return 日志记录器
	 */
	public final Logger getLogger(Class clazz) {
		LoggerWrapper w = wrappers.get(clazz);
		if (w == null) wrappers.put(clazz, w = new LoggerWrapper(clazz));
		return w;
	}

	/**
	 * 获取日志记录器
	 * @param name 日志名称
	 * @return 日志记录器
	 */
	public final Logger getLogger(String name) {
		LoggerWrapper w = wrappers.get(name);
		if (w == null) wrappers.put(name, w = new LoggerWrapper(name));
		return w;
	}

	/**
	 * 创建日志记录器
	 * @param clazz 日志类型
	 * @return 日志记录器
	 */
	protected abstract Logger createLogger(Class clazz);

	/**
	 * 创建日志记录器
	 * @param name 日志名称
	 * @return 日志记录器
	 */
	protected abstract Logger createLogger(String name);

	/**
	 * 查找日志记录器实例
	 * @param clazz 日志类型
	 * @return 日志记录器实例
	 */
	protected final Logger findLogger(Class clazz) {
		Logger logger = cache.get(clazz);
		if (logger == null) cache.put(clazz, logger = createLogger(clazz));
		return logger;
	}

	/**
	 * 查找日志记录器实例
	 * @param name 日志名称
	 * @return 日志记录器实例
	 */
	protected final Logger findLogger(String name) {
		Logger logger = cache.get(name);
		if (logger == null) cache.put(name, logger = createLogger(name));
		return logger;
	}
}

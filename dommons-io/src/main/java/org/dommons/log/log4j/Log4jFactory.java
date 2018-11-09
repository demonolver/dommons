/*
 * @(#)Log4jFactory.java     2011-10-26
 */
package org.dommons.log.log4j;

import java.io.File;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.dommons.io.Pathfinder;
import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;
import org.dommons.log.log4j.instance.EnhanceLogger;

/**
 * 日志工厂 for log4j
 * @author Demon 2011-10-26
 */
public class Log4jFactory extends LoggerFactory {

	/**
	 * 清除当前所有日志配置
	 */
	public static void cleanConfiguration() {
		LogManager.getLoggerRepository().resetConfiguration();
	}

	/**
	 * 创建工厂实例
	 * @param path 配置文件路径
	 * @return 工厂实例
	 */
	public static LoggerFactory newInstance(String path) {
		File file = Pathfinder.findFile(path);
		if (file.exists() && file.isFile()) {
			PropertyConfigurator.configure(file.getAbsolutePath());
		} else {
			URL url = Pathfinder.getResource(path);
			if (url != null) PropertyConfigurator.configure(url);
		}
		return new Log4jFactory();
	}

	/**
	 * 创建日志记录器实例
	 * @param target 目标日志记录器
	 * @return 日志记录器实例
	 */
	protected Logger create(org.apache.log4j.Logger target) {
		return EnhanceLogger.class.isInstance(target) ? EnhanceLogger.class.cast(target) : new LoggerW4j(target);
	}

	protected Logger createLogger(Class clazz) {
		return create(org.apache.log4j.Logger.getLogger(clazz));
	}

	protected Logger createLogger(String name) {
		return create(org.apache.log4j.Logger.getLogger(name));
	}
}

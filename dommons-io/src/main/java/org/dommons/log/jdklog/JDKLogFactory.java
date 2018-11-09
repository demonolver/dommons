/*
 * @(#)JDKLogFactory.java     2011-10-26
 */
package org.dommons.log.jdklog;

import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * JDK 默认日志工厂
 * @author Demon 2011-10-26
 */
public class JDKLogFactory extends LoggerFactory {

	/**
	 * 创建日志记录器实例
	 * @param target 目标日志记录器
	 * @return 日志记录器实例
	 */
	protected Logger create(java.util.logging.Logger target) {
		return new LoggerWjdk(target);
	}

	protected Logger createLogger(Class clazz) {
		return createLogger(clazz == null ? null : clazz.getName());
	}

	protected Logger createLogger(String name) {
		return create(java.util.logging.Logger.getLogger(name));
	}
}

/*
 * @(#)CommonLogFactory.java     2011-10-26
 */
package org.dommons.log.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * Common-logging 日志工厂
 * @author Demon 2011-10-26
 */
public class CommonLogFactory extends LoggerFactory {

	/**
	 * 创建日志记录器实例
	 * @param target 目标日志记录器
	 * @return 日志记录器实例
	 */
	protected Logger create(Log target) {
		return new LoggerWcomm(target);
	}

	protected Logger createLogger(Class clazz) {
		return create(LogFactory.getLog(clazz));
	}

	protected Logger createLogger(String name) {
		return create(LogFactory.getLog(name));
	}
}

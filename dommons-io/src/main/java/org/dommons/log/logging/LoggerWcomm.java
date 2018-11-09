/*
 * @(#)LoggerWcomm.java     2011-10-26
 */
package org.dommons.log.logging;

import org.apache.commons.logging.Log;
import org.dommons.log.AbstractLogger;

/**
 * 引用 Common-logging 记录日志
 * @author Demon 2011-10-26
 */
class LoggerWcomm extends AbstractLogger {

	private final Log tar;

	/**
	 * 构造函数
	 * @param tar 目标日志记录器
	 */
	public LoggerWcomm(Log tar) {
		this.tar = tar;
	}

	public boolean isDebugEnabled() {
		return tar.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return tar.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return tar.isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return tar.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return tar.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return tar.isWarnEnabled();
	}

	public void logDebug(String message) {
		tar.debug(message);
	}

	public void logError(Throwable t, String message) {
		tar.error(message, t);
	}

	public void logInfo(String message) {
		tar.info(message);
	}

	public void logTrace(String message) {
		tar.trace(message);
	}

	public void logWarn(Throwable t, String message) {
		tar.warn(message, t);
	}

	protected void logFatal(Throwable t, String message) {
		tar.fatal(message, t);
	}
}

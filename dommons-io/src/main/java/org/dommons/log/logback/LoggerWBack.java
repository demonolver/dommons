/*
 * @(#)LoggerWBack.java     2020-10-21
 */
package org.dommons.log.logback;

import org.dommons.log.AbstractLogger;
import org.slf4j.spi.LocationAwareLogger;

import ch.qos.logback.classic.Logger;

/**
 * logback 日志实现
 * @author demon 2020-10-21
 */
public class LoggerWBack extends AbstractLogger {

	private final Logger tar;

	protected LoggerWBack(Logger logger) {
		super();
		this.tar = logger;
	}

	public boolean isDebugEnabled() {
		return tar.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return tar.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return false;
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

	@Override
	protected void logDebug(String message) {
		log(LocationAwareLogger.DEBUG_INT, message, null);
	}

	@Override
	protected void logError(Throwable t, String message) {
		log(LocationAwareLogger.ERROR_INT, message, t);
	}

	@Override
	protected void logFatal(Throwable t, String message) {
		logError(t, message);
	}

	@Override
	protected void logInfo(String message) {
		log(LocationAwareLogger.INFO_INT, message, null);
	}

	@Override
	protected void logTrace(String message) {
		log(LocationAwareLogger.TRACE_INT, message, null);
	}

	@Override
	protected void logWarn(Throwable t, String message) {
		log(LocationAwareLogger.WARN_INT, message, t);
	}

	/**
	 * 记录日志
	 * @param level 日志级别
	 * @param message 内容
	 * @param t 异常
	 */
	void log(int level, String message, Throwable t) {
		tar.log(null, FQCN, level, message, null, t);
	}
}

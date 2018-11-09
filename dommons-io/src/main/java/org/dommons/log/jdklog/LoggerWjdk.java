/*
 * @(#)LoggerWjdk.java     2011-10-26
 */
package org.dommons.log.jdklog;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dommons.log.AbstractLogger;

/**
 * JDK 默认日志实现
 * @author Demon 2011-10-26
 */
class LoggerWjdk extends AbstractLogger {

	private final Logger tar;

	/**
	 * 构造函数
	 * @param tar 目标日志记录器
	 */
	public LoggerWjdk(Logger tar) {
		this.tar = tar;
	}

	public boolean isDebugEnabled() {
		return tar.isLoggable(Level.FINE);
	}

	public boolean isErrorEnabled() {
		return tar.isLoggable(Level.SEVERE);
	}

	public boolean isFatalEnabled() {
		return tar.isLoggable(Level.OFF);
	}

	public boolean isInfoEnabled() {
		return tar.isLoggable(Level.INFO);
	}

	public boolean isTraceEnabled() {
		return tar.isLoggable(Level.FINER);
	}

	public boolean isWarnEnabled() {
		return tar.isLoggable(Level.WARNING);
	}

	protected void logDebug(String message) {
		tar.fine(message);
	}

	protected void logError(Throwable t, String message) {
		if (t != null) tar.log(Level.SEVERE, message, t);
		else tar.severe(message);

	}

	protected void logFatal(Throwable t, String message) {
		tar.log(Level.OFF, message, t);
	}

	protected void logInfo(String message) {
		tar.info(message);
	}

	protected void logTrace(String message) {
		tar.finer(message);
	}

	protected void logWarn(Throwable t, String message) {
		if (t != null) tar.log(Level.WARNING, message, t);
		else tar.warning(message);
	}
}

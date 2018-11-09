/*
 * @(#)LoggerWSlf4j.java     2018-04-25
 */
package org.dommons.log.slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.dommons.log.AbstractLogger;
import org.slf4j.Logger;

/**
 * slf4j 日志实现
 * @author demon 2018-04-25
 */
public class LoggerWSlf4j extends AbstractLogger {

	private final Logger tar;

	protected LoggerWSlf4j(Logger tar) {
		this.tar = tar;
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

	protected void logDebug(String message) {
		tar.debug(message);
	}

	protected void logError(Throwable t, String message) {
		tar.error(message(message, t));
	}

	protected void logFatal(Throwable t, String message) {
		tar.error(message(message, t));
	}

	protected void logInfo(String message) {
		tar.info(message);
	}

	protected void logTrace(String message) {
		tar.trace(message);
	}

	protected void logWarn(Throwable t, String message) {
		tar.warn(message(message, t));
	}

	/**
	 * 转换日志内容
	 * @param message 信息
	 * @param t 异常
	 * @return 日志内容
	 */
	String message(String message, Throwable t) {
		if (t != null) {
			try {
				StringWriter w = new StringWriter(64);
				if (message != null) w.append(message);
				try {
					PrintWriter pw = new PrintWriter(w);
					pw.println();
					t.printStackTrace(pw);
					return w.toString();
				} finally {
					w.close();
				}
			} catch (IOException e) { // ignored
			}
		}
		return message;
	}
}

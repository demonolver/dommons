/*
 * @(#)LoggerW4j.java     2011-10-26
 */
package org.dommons.log.log4j;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.dommons.log.AbstractLogger;

/**
 * 引用 log4j 记录日志
 * @author Demon 2011-10-26
 */
class LoggerW4j extends AbstractLogger {

	private final Logger tar;

	/**
	 * 构造函数
	 * @param tar 目标日志记录器
	 */
	public LoggerW4j(Logger tar) {
		this.tar = tar;
	}

	public boolean isDebugEnabled() {
		return tar.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return tar.isEnabledFor(Level.ERROR);
	}

	public boolean isFatalEnabled() {
		return tar.isEnabledFor(Level.FATAL);
	}

	public boolean isInfoEnabled() {
		return tar.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return tar.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return tar.isEnabledFor(Level.WARN);
	}

	public void logDebug(String message) {
		forcedLog(Level.DEBUG, message, null);
	}

	public void logError(Throwable t, String message) {
		forcedLog(Level.ERROR, message, t);
	}

	public void logFatal(Throwable t, String message) {
		forcedLog(Level.FATAL, message, t);
	}

	public void logInfo(String message) {
		forcedLog(Level.INFO, message, null);
	}

	public void logTrace(String message) {
		forcedLog(Level.TRACE, message, null);
	}

	public void logWarn(Throwable t, String message) {
		forcedLog(Level.WARN, message, t);
	}

	/**
	 * 调用输出
	 * @param category 日志对象
	 * @param event 日志事件
	 * @param appenders 日志格式集
	 */
	protected void callAppenders(Category category, LoggingEvent event, Collection<Appender> appenders) {
		Enumeration<Appender> en = category.getAllAppenders();
		while (en.hasMoreElements()) {
			Appender appender = en.nextElement();
			if (!appenders.contains(appender)) {
				appender.doAppend(event);
				appenders.add(appender);
			}
		}
	}

	/**
	 * 输出日志
	 * @param level 级别
	 * @param message 信息
	 * @param t 异常
	 */
	protected void forcedLog(Priority level, Object message, Throwable t) {
		LoggingEvent event = new LoggingEvent(getFQCN(), tar, level, message, t);
		Collection<Appender> appenders = new HashSet();
		for (Category c = tar; c != null; c = c.getParent()) {
			if (!c.isEnabledFor(level)) continue;
			callAppenders(c, event, appenders);
		}
	}
}

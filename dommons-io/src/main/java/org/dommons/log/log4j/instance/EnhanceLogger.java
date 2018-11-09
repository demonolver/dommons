/*
 * @(#)EnhanceLogger.java     2011-11-14
 */
package org.dommons.log.log4j.instance;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.dommons.core.Assertor;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.string.Stringure;
import org.dommons.io.message.DefaultTemplate;
import org.dommons.io.message.MessageTemplate;

/**
 * 增强 log4j 日志
 * @author Demon 2011-11-14
 */
public class EnhanceLogger extends Logger implements org.dommons.log.Logger {

	private static final String FQCN = EnhanceLogger.class.getName();

	/**
	 * 信息转换
	 * @param pattern 模板串
	 * @param params 参数集
	 * @return 信息内容
	 * @see MessageFormat
	 */
	protected static String format(CharSequence pattern, Object... params) {
		return toString(DefaultTemplate.compile(pattern), params);
	}

	/**
	 * 信息转换
	 * @param template 信息模板
	 * @param params 参数集
	 * @return 信息内容
	 */
	protected static String toString(MessageTemplate template, Object... params) {
		Assertor.F.notNull(template);
		return template.format(params);
	}

	/**
	 * 构造函数
	 * @param name 日志名称
	 */
	protected EnhanceLogger(String name) {
		super(name);
	}

	public void debug(CharSequence message) {
		if (isDebugEnabled()) logDebug(message);
	}

	public void debug(CharSequence pattern, Object... params) {
		if (isDebugEnabled()) logDebug(format(pattern, params));
	}

	public void debug(MessageTemplate template, Object... params) {
		if (isDebugEnabled()) logDebug(toString(template, params));
	}

	public void error(CharSequence message) {
		if (isErrorEnabled()) logError(null, message);
	}

	public void error(CharSequence pattern, Object... params) {
		if (isErrorEnabled()) logError(null, format(pattern, params));
	}

	public void error(MessageTemplate template, Object... params) {
		if (isErrorEnabled()) logError(null, toString(template, params));
	}

	public void error(Throwable t) {
		if (isErrorEnabled()) logError(t, Stringure.empty);
	}

	public void error(Throwable t, CharSequence message) {
		if (isErrorEnabled()) logError(t, message);
	}

	public void error(Throwable t, CharSequence pattern, Object... params) {
		if (isErrorEnabled()) logError(t, format(pattern, params));
	}

	public void error(Throwable t, MessageTemplate template, Object... params) {
		if (isErrorEnabled()) logError(t, toString(template, params));
	}

	public void fatal(CharSequence message) {
		if (isFatalEnabled()) logFatal(null, message);
	}

	public void fatal(CharSequence pattern, Object... params) {
		if (isFatalEnabled()) logFatal(null, format(pattern, params));
	}

	public void fatal(MessageTemplate template, Object... params) {
		if (isFatalEnabled()) logFatal(null, toString(template, params));
	}

	public void fatal(Throwable t) {
		if (isFatalEnabled()) logFatal(t, Stringure.empty);
	}

	public void fatal(Throwable t, CharSequence message) {
		if (isFatalEnabled()) logFatal(t, message);
	}

	public void fatal(Throwable t, CharSequence pattern, Object... params) {
		if (isFatalEnabled()) logFatal(t, format(pattern, params));
	}

	public void fatal(Throwable t, MessageTemplate template, Object... params) {
		if (isFatalEnabled()) logFatal(t, toString(template, params));
	}

	public void info(CharSequence message) {
		if (isInfoEnabled()) logInfo(message);
	}

	public void info(CharSequence pattern, Object... params) {
		if (isInfoEnabled()) logInfo(format(pattern, params));
	}

	public void info(MessageTemplate template, Object... params) {
		if (isInfoEnabled()) logInfo(toString(template, params));
	}

	public boolean isErrorEnabled() {
		return super.isEnabledFor(Level.ERROR);
	}

	public boolean isFatalEnabled() {
		return super.isEnabledFor(Level.FATAL);
	}

	public boolean isWarnEnabled() {
		return super.isEnabledFor(Level.WARN);
	}

	public void logDebug(CharSequence message) {
		forcedLog(FQCN, Level.DEBUG, message, null);
	}

	public void logError(Throwable t, CharSequence message) {
		forcedLog(FQCN, Level.ERROR, message, t);
	}

	public void logFatal(Throwable t, CharSequence message) {
		forcedLog(FQCN, Level.FATAL, message, t);
	}

	public void logInfo(CharSequence message) {
		forcedLog(FQCN, Level.INFO, message, null);
	}

	public void logTrace(CharSequence message) {
		forcedLog(FQCN, Level.TRACE, message, null);
	}

	public void logWarn(Throwable t, CharSequence message) {
		forcedLog(FQCN, Level.WARN, message, t);
	}

	public void trace(CharSequence message) {
		if (isTraceEnabled()) logTrace(message);
	}

	public void trace(CharSequence pattern, Object... params) {
		if (isTraceEnabled()) logTrace(format(pattern, params));
	}

	public void trace(MessageTemplate template, Object... params) {
		if (isTraceEnabled()) logTrace(toString(template, params));
	}

	public void warn(CharSequence message) {
		if (isWarnEnabled()) logWarn(null, message);
	}

	public void warn(CharSequence pattern, Object... params) {
		if (isWarnEnabled()) logWarn(null, format(pattern, params));
	}

	public void warn(MessageTemplate template, Object... params) {
		if (isWarnEnabled()) logWarn(null, toString(template, params));
	}

	public void warn(Throwable t, CharSequence message) {
		if (isWarnEnabled()) logWarn(t, message);
	}

	public void warn(Throwable t, CharSequence pattern, Object... params) {
		if (isWarnEnabled()) logWarn(t, format(pattern, params));
	}

	public void warn(Throwable t, MessageTemplate template, Object... params) {
		if (isWarnEnabled()) logWarn(t, toString(template, params));
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
			if (appenders.add(appender)) appender.doAppend(event);
		}
	}

	protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
		LoggingEvent event = new LoggingEvent(fqcn, this, level, message, t);
		Collection<Appender> appenders = new HashSet();
		for (Category c = this; c != null; c = c.getParent()) {
			if (!c.isEnabledFor(level)) return;
			callAppenders(c, event, appenders);
		}
	}
}

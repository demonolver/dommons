/*
 * @(#)AbstractLogger.java     2011-10-25
 */
package org.dommons.log;

import org.dommons.core.Assertor;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.string.Stringure;
import org.dommons.io.message.DefaultTemplate;
import org.dommons.io.message.MessageTemplate;

/**
 * 抽象日志实现类
 * @author Demon 2011-10-25
 */
public abstract class AbstractLogger implements Logger {

	protected static final String FQCN = "org.dommons.log.LoggerWrapper";

	/**
	 * 信息转换
	 * @param pattern 模板串
	 * @param params 参数集
	 * @return 信息内容
	 * @see MessageFormat
	 */
	protected static String format(CharSequence pattern, Object... params) {
		if (pattern != null && Stringure.contains(pattern, "{}")) return String.format(String.valueOf(pattern), params);
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

	public void debug(CharSequence message) {
		if (isDebugEnabled()) logDebug(String.valueOf(message));
	}

	public void debug(CharSequence pattern, Object... params) {
		if (isDebugEnabled()) logDebug(format(pattern, params));
	}

	public void debug(MessageTemplate template, Object... params) {
		if (isDebugEnabled()) logDebug(toString(template, params));
	}

	public void error(CharSequence message) {
		if (isErrorEnabled()) logError(null, String.valueOf(message));
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
		if (isErrorEnabled()) logError(t, String.valueOf(message));
	}

	public void error(Throwable t, CharSequence pattern, Object... params) {
		if (isErrorEnabled()) logError(t, format(pattern, params));
	}

	public void error(Throwable t, MessageTemplate template, Object... params) {
		if (isErrorEnabled()) logError(t, toString(template, params));
	}

	public void fatal(CharSequence message) {
		if (isFatalEnabled()) logFatal(null, String.valueOf(message));
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
		if (isFatalEnabled()) logFatal(t, String.valueOf(message));
	}

	public void fatal(Throwable t, CharSequence pattern, Object... params) {
		if (isFatalEnabled()) logFatal(t, format(pattern, params));
	}

	public void fatal(Throwable t, MessageTemplate template, Object... params) {
		if (isFatalEnabled()) logFatal(t, toString(template, params));
	}

	public void info(CharSequence message) {
		if (isInfoEnabled()) logInfo(String.valueOf(message));
	}

	public void info(CharSequence pattern, Object... params) {
		if (isInfoEnabled()) logInfo(format(pattern, params));
	}

	public void info(MessageTemplate template, Object... params) {
		if (isInfoEnabled()) logInfo(toString(template, params));
	}

	public void trace(CharSequence message) {
		if (isTraceEnabled()) logTrace(String.valueOf(message));
	}

	public void trace(CharSequence pattern, Object... params) {
		if (isTraceEnabled()) logTrace(format(pattern, params));
	}

	public void trace(MessageTemplate template, Object... params) {
		if (isTraceEnabled()) logTrace(toString(template, params));
	}

	public void warn(CharSequence message) {
		if (isWarnEnabled()) logWarn(null, String.valueOf(message));
	}

	public void warn(CharSequence pattern, Object... params) {
		if (isWarnEnabled()) logWarn(null, format(pattern, params));
	}

	public void warn(MessageTemplate template, Object... params) {
		if (isWarnEnabled()) logWarn(null, toString(template, params));
	}

	public void warn(Throwable t, CharSequence message) {
		if (isWarnEnabled()) logWarn(t, String.valueOf(message));
	}

	public void warn(Throwable t, CharSequence pattern, Object... params) {
		if (isWarnEnabled()) logWarn(t, format(pattern, params));
	}

	public void warn(Throwable t, MessageTemplate template, Object... params) {
		if (isWarnEnabled()) logWarn(t, toString(template, params));
	}

	/**
	 * 记录调试日志
	 * @param message 内容
	 */
	protected abstract void logDebug(String message);

	/**
	 * 记录异常日志
	 * @param t 异常
	 * @param message 内容
	 */
	protected abstract void logError(Throwable t, String message);

	/**
	 * 记录致命日志
	 * @param t 异常
	 * @param message 内容
	 */
	protected abstract void logFatal(Throwable t, String message);

	/**
	 * 记录信息日志
	 * @param message 内容
	 */
	protected abstract void logInfo(String message);

	/**
	 * 记录跟踪日志
	 * @param message 内容
	 */
	protected abstract void logTrace(String message);

	/**
	 * 记当警告日志
	 * @param t 异常
	 * @param message 内容
	 */
	protected abstract void logWarn(Throwable t, String message);
}

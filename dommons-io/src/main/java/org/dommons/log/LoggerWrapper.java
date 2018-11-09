/*
 * @(#)LoggerWrapper.java     2011-10-25
 */
package org.dommons.log;

import org.dommons.io.message.MessageTemplate;

/**
 * 日志记录器包装
 * @author Demon 2011-10-25
 */
class LoggerWrapper implements Logger {

	private final Class clazz;
	private final String name;

	/**
	 * 构造函数
	 * @param clazz 日志类型
	 */
	public LoggerWrapper(Class clazz) {
		this(clazz, null);
	}

	/**
	 * 构造函数
	 * @param name 日志名称
	 */
	public LoggerWrapper(String name) {
		this(null, name);
	}

	/**
	 * 构造函数
	 * @param clazz 日志类型
	 * @param name 日志名称
	 */
	protected LoggerWrapper(Class clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}

	public void debug(CharSequence message) {
		getTarget().debug(message);

	}

	public void debug(CharSequence pattern, Object... params) {
		getTarget().debug(pattern, params);
	}

	public void debug(MessageTemplate template, Object... params) {
		getTarget().debug(template, params);
	}

	public void error(CharSequence message) {
		getTarget().error(message);
	}

	public void error(CharSequence pattern, Object... params) {
		getTarget().error(pattern, params);
	}

	public void error(MessageTemplate template, Object... params) {
		getTarget().error(template, params);
	}

	public void error(Throwable t) {
		getTarget().error(t);
	}

	public void error(Throwable t, CharSequence message) {
		getTarget().error(t, message);
	}

	public void error(Throwable t, CharSequence pattern, Object... params) {
		getTarget().error(t, pattern, params);
	}

	public void error(Throwable t, MessageTemplate template, Object... params) {
		getTarget().error(t, template, params);
	}

	public void fatal(CharSequence message) {
		getTarget().fatal(message);
	}

	public void fatal(CharSequence pattern, Object... params) {
		getTarget().fatal(pattern, params);
	}

	public void fatal(MessageTemplate template, Object... params) {
		getTarget().fatal(template, params);
	}

	public void fatal(Throwable t) {
		getTarget().fatal(t);
	}

	public void fatal(Throwable t, CharSequence message) {
		getTarget().fatal(t, message);
	}

	public void fatal(Throwable t, CharSequence pattern, Object... params) {
		getTarget().fatal(t, pattern, params);
	}

	public void fatal(Throwable t, MessageTemplate template, Object... params) {
		getTarget().fatal(t, template, params);
	}

	public void info(CharSequence message) {
		getTarget().info(message);
	}

	public void info(CharSequence pattern, Object... params) {
		getTarget().info(pattern, params);
	}

	public void info(MessageTemplate template, Object... params) {
		getTarget().info(template, params);
	}

	public boolean isDebugEnabled() {
		return getTarget().isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return getTarget().isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return getTarget().isFatalEnabled();
	}

	public boolean isInfoEnabled() {
		return getTarget().isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return getTarget().isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return getTarget().isWarnEnabled();
	}

	public void trace(CharSequence message) {
		getTarget().trace(message);
	}

	public void trace(CharSequence pattern, Object... params) {
		getTarget().trace(pattern, params);
	}

	public void trace(MessageTemplate template, Object... params) {
		getTarget().trace(template, params);
	}

	public void warn(CharSequence message) {
		getTarget().warn(message);
	}

	public void warn(CharSequence pattern, Object... params) {
		getTarget().warn(pattern, params);
	}

	public void warn(MessageTemplate template, Object... params) {
		getTarget().warn(template, params);
	}

	public void warn(Throwable t, CharSequence message) {
		getTarget().warn(t, message);
	}

	public void warn(Throwable t, CharSequence pattern, Object... params) {
		getTarget().warn(t, pattern, params);
	}

	public void warn(Throwable t, MessageTemplate template, Object... params) {
		getTarget().warn(t, template, params);
	}

	/**
	 * 获取目标日志记录器
	 * @return 日志记录器
	 */
	protected Logger getTarget() {
		return clazz == null ? LoggerFactory.getInstance().findLogger(name) : LoggerFactory.getInstance().findLogger(clazz);
	}
}

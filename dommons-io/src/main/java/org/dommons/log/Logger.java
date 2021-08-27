/*
 * @(#)Logger.java     2011-10-19
 */
package org.dommons.log;

import org.dommons.core.env.ProguardIgnore;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.io.message.MessageTemplate;

/**
 * 日志记录器
 * @author Demon 2011-10-19
 */
public interface Logger extends ProguardIgnore {

	/**
	 * 记录调试日志
	 * @param message 日志内容
	 */
	public void debug(CharSequence message);

	/**
	 * 记录调试日志
	 * <ul>
	 * Example : debug("Hello {0}!", "world"} -> Hello world!
	 * </ul>
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void debug(CharSequence pattern, Object... params);

	/**
	 * 记录调试日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void debug(MessageTemplate template, Object... params);

	/**
	 * 记录异常日志
	 * @param message 日志内容
	 */
	public void error(CharSequence message);

	/**
	 * 记录异常日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void error(CharSequence pattern, Object... params);

	/**
	 * 记录异常日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void error(MessageTemplate template, Object... params);

	/**
	 * 记录异常日志
	 * @param t 异常
	 */
	public void error(Throwable t);

	/**
	 * 记录异常日志
	 * @param t 异常
	 * @param message 日志内容
	 */
	public void error(Throwable t, CharSequence message);

	/**
	 * 记录异常日志
	 * @param t 异常
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void error(Throwable t, CharSequence pattern, Object... params);

	/**
	 * 记录异常日志
	 * @param t 异常
	 * @param template 模板
	 * @param params 参数集
	 */
	public void error(Throwable t, MessageTemplate template, Object... params);

	/**
	 * 记录致命日志
	 * @param message 日志内容
	 */
	public void fatal(CharSequence message);

	/**
	 * 记录致命日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void fatal(CharSequence pattern, Object... params);

	/**
	 * 记录致命日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void fatal(MessageTemplate template, Object... params);

	/**
	 * 记录致命日志
	 * @param t 致命
	 */
	public void fatal(Throwable t);

	/**
	 * 记录致命日志
	 * @param t 致命
	 * @param message 日志内容
	 */
	public void fatal(Throwable t, CharSequence message);

	/**
	 * 记录致命日志
	 * @param t 致命
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void fatal(Throwable t, CharSequence pattern, Object... params);

	/**
	 * 记录致命日志
	 * @param t 致命
	 * @param template 模板
	 * @param params 参数集
	 */
	public void fatal(Throwable t, MessageTemplate template, Object... params);

	/**
	 * 记录常用日志
	 * @param message 日志内容
	 */
	public void info(CharSequence message);

	/**
	 * 记录常用日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void info(CharSequence pattern, Object... params);

	/**
	 * 记录常用日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void info(MessageTemplate template, Object... params);

	/**
	 * 是否允许记录调试日志
	 * @return 是、否
	 */
	public boolean isDebugEnabled();

	/**
	 * 是否允许记录出错日志
	 * @return 是、否
	 */
	public boolean isErrorEnabled();

	/**
	 * 是否允许记录致命日志
	 * @return 是、否
	 */
	public boolean isFatalEnabled();

	/**
	 * 是否允许记录常规日志
	 * @return 是、否
	 */
	public boolean isInfoEnabled();

	/**
	 * 是否允许记录跟踪日志
	 * @return 是、否
	 */
	public boolean isTraceEnabled();

	/**
	 * 是否允许记录警告日志
	 * @return 是、否
	 */
	public boolean isWarnEnabled();

	/**
	 * 记录跟踪日志
	 * @param message 日志内容
	 */
	public void trace(CharSequence message);

	/**
	 * 记录跟踪日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void trace(CharSequence pattern, Object... params);

	/**
	 * 记录跟踪日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void trace(MessageTemplate template, Object... params);

	/**
	 * 记录警告日志
	 * @param message 日志内容
	 */
	public void warn(CharSequence message);

	/**
	 * 记录警告日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void warn(CharSequence pattern, Object... params);

	/**
	 * 记录警告日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void warn(MessageTemplate template, Object... params);

	/**
	 * 记录警告日志
	 * @param message 日志内容
	 */
	public void warn(Throwable t, CharSequence message);

	/**
	 * 记录警告日志
	 * @param pattern 模板
	 * @param params 参数集
	 * @see MessageFormat
	 */
	public void warn(Throwable t, CharSequence pattern, Object... params);

	/**
	 * 记录警告日志
	 * @param template 模板
	 * @param params 参数集
	 */
	public void warn(Throwable t, MessageTemplate template, Object... params);
}

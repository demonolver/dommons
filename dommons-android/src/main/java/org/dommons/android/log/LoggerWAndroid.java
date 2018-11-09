/*
 * @(#)LoggerWAndroid.java     2014-4-14
 */
package org.dommons.android.log;

import org.dommons.core.string.Stringure;
import org.dommons.log.AbstractLogger;

import android.util.Log;

/**
 * 引用安卓日志记录日志
 * @author Demon 2014-4-14
 */
class LoggerWAndroid extends AbstractLogger {

	/**
	 * 生成日志信息
	 * @param message 日志信息
	 * @param t 异常
	 * @return 完整日志信息
	 */
	static String message(String message, Throwable t) {
		if (t == null) return message;
		StringBuilder buf = new StringBuilder(64);
		if (!Stringure.isEmpty(message)) buf.append(message).append('\n');
		throwable(buf, t, Stringure.empty, null);
		return buf.toString();
	}

	/**
	 * 转换异常信息
	 * @param err 字符缓存区
	 * @param t 异常
	 * @param indent 缩进
	 * @param parentStack 父堆栈
	 */
	static void throwable(StringBuilder err, Throwable t, String indent, StackTraceElement[] parentStack) {
		err.append(t.toString()).append('\n');

		StackTraceElement[] stack = t.getStackTrace();
		if (stack != null) {
			int i = 0, limit = 8, duplicates = parentStack == null ? 0 : countDuplicates(stack, parentStack);
			for (i = 0; i < stack.length - duplicates && i < limit; i++) {
				err.append(indent).append("\tat ");
				err.append(stack[i].toString()).append('\n');
			}
			if (stack.length > i) err.append(indent).append("\t... ").append(Integer.toString(stack.length - i)).append(" more\n");
		}

		// Throwable[] suppressed = t.getSuppressed();
		// if (suppressed != null) {
		// for (Throwable throwable : suppressed) {
		// err.append(indent).append("\tSuppressed: ");
		// throwable(err, throwable, indent + "\t", stack);
		// }
		// }

		Throwable cause = t.getCause();
		if (cause != null) {
			err.append(indent).append("Caused by: ");
			throwable(err, cause, indent, stack);
		}
	}

	/**
	 * 计算重复堆栈
	 * @param currentStack 当前堆栈
	 * @param parentStack 父堆栈
	 * @return 重复数
	 */
	private static int countDuplicates(StackTraceElement[] currentStack, StackTraceElement[] parentStack) {
		int duplicates = 0;
		int parentIndex = parentStack.length;
		for (int i = currentStack.length; --i >= 0 && --parentIndex >= 0;) {
			StackTraceElement parentFrame = parentStack[parentIndex];
			if (parentFrame.equals(currentStack[i])) duplicates++;
			else break;
		}
		return duplicates;
	}

	private final String tag;

	public LoggerWAndroid(String tag) {
		this.tag = tag;
	}

	public boolean isDebugEnabled() {
		return Log.isLoggable(tag(), Log.DEBUG);
	}

	public boolean isErrorEnabled() {
		return Log.isLoggable(tag(), Log.ERROR);
	}

	public boolean isFatalEnabled() {
		return Log.isLoggable(tag(), Log.ASSERT);
	}

	public boolean isInfoEnabled() {
		return Log.isLoggable(tag(), Log.INFO);
	}

	public boolean isTraceEnabled() {
		return Log.isLoggable(tag(), Log.VERBOSE);
	}

	public boolean isWarnEnabled() {
		return Log.isLoggable(tag(), Log.WARN);
	}

	protected void logDebug(String message) {
		Log.d(tag(), message);
	}

	protected void logError(Throwable t, String message) {
		Log.e(tag(), message(message, t));
	}

	protected void logFatal(Throwable t, String message) {
		Log.wtf(tag(), message(message, t));
	}

	protected void logInfo(String message) {
		Log.i(tag(), message);
	}

	protected void logTrace(String message) {
		Log.v(tag(), message);
	}

	protected void logWarn(Throwable t, String message) {
		Log.w(tag(), message(message, t));
	}

	/**
	 * 获取日志标签
	 * @return 标签
	 */
	private String tag() {
		return tag;
	}
}

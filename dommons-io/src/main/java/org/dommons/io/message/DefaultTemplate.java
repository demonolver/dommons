/*
 * @(#)DefaultTemplate.java     2011-10-25
 */
package org.dommons.io.message;

import java.text.ParseException;

import org.dommons.core.Assertor;
import org.dommons.core.format.text.MessageFormat;

/**
 * 默认信息模板实现
 * @author Demon 2011-10-25
 */
public final class DefaultTemplate implements MessageTemplate {

	/**
	 * 编译格式串
	 * @param pattern 格式串
	 * @return 信息模板
	 * @see MessageFormat
	 */
	public static MessageTemplate compile(CharSequence pattern) {
		Assertor.F.notEmpty(pattern);
		return new DefaultTemplate(MessageFormat.compile(pattern));
	}

	private final MessageFormat format;

	/**
	 * 构造函数
	 * @param format 信息格式
	 */
	protected DefaultTemplate(MessageFormat format) {
		this.format = format;
	}

	public String format(Object... arguments) {
		return format.format(arguments);
	}

	public Object[] parse(String message) throws ParseException {
		return format.parse(message);
	}
}

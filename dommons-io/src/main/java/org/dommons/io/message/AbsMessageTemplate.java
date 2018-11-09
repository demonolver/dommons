/*
 * @(#)AbsMessageTemplate.java     2017-01-06
 */
package org.dommons.io.message;

import java.text.ParseException;

import org.dommons.core.format.text.MessageFormat;

/**
 * 抽象信息模板
 * @author demon 2017-01-06
 */
public abstract class AbsMessageTemplate implements MessageTemplate {

	public String format(Object... arguments) {
		MessageFormat f = format();
		return f == null ? null : f.format(arguments);
	}

	public Object[] parse(String message) throws ParseException {
		MessageFormat f = format();
		return f == null ? null : f.parse(message);
	}

	/**
	 * 获取信息模板格式
	 * @return 模板格式
	 */
	protected abstract MessageFormat format();
}

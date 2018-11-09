/*
 * @(#)MessageTemplate.java     2011-10-25
 */
package org.dommons.io.message;

import java.text.ParseException;

/**
 * 信息模板
 * @author Demon 2011-10-25
 */
public interface MessageTemplate {

	/**
	 * 根据模板将参数转换为信息串
	 * @param arguments 参数集
	 * @return 信息串
	 */
	String format(Object... arguments);

	/**
	 * 根据模板将信息串解析为参数集
	 * @param message 信息串
	 * @return 参数集
	 */
	Object[] parse(String message) throws ParseException;
}

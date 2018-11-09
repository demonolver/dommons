/*
 * @(#)Messages.java     2011-10-25
 */
package org.dommons.io.message;

import java.text.ParseException;

/**
 * 消息内容集
 * @author Demon 2011-10-25
 */
public interface Messages {

	/**
	 * 获取信息
	 * @param key 键值
	 * @return 信息内容
	 */
	public String getMessage(String key);

	/**
	 * 获取信息
	 * @param key 键值
	 * @param arguments 参数
	 * @return 信息内容
	 */
	public String getMessage(String key, Object... arguments);

	/**
	 * 解析信息参数
	 * @param key 键值
	 * @param message 信息内容
	 * @return 参数集
	 * @throws ParseException
	 */
	public Object[] parse(String key, String message) throws ParseException;
}

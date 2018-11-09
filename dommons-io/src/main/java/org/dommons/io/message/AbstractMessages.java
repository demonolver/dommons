/*
 * @(#)AbstractMessages.java     2011-10-25
 */
package org.dommons.io.message;

import java.text.ParseException;
import java.util.Map;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.format.text.MessageFormat;

/**
 * 消息内容集抽象类
 * @author Demon 2011-10-25
 */
public abstract class AbstractMessages implements Messages {

	private Map<String, MessageFormat> cache;

	/**
	 * 构造函数
	 */
	protected AbstractMessages() {
		cache = new ConcurrentSoftMap();
	}

	public String getMessage(String key, Object... arguments) {
		return getFormat(key).format(arguments);
	}

	public Object[] parse(String key, String message) throws ParseException {
		return getFormat(key).parse(message);
	}

	/**
	 * 获取格式模板
	 * @param key 键值
	 * @return 格式模板
	 */
	protected MessageFormat getFormat(String key) {
		MessageFormat format = cache.get(key);
		if (format == null) cache.put(key, format = MessageFormat.compile(getMessage(key)));
		return format;
	}
}

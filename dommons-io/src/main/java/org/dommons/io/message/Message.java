/*
 * @(#)Message.java     2011-10-25
 */
package org.dommons.io.message;

import java.io.Serializable;

import org.dommons.core.Assertor;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;

/**
 * 信息项
 * @author Demon 2011-10-25
 */
public class Message extends AbsMessageTemplate implements Serializable, MessageTemplate {

	private static final long serialVersionUID = 1033266646468560186L;

	/**
	 * 创建信息项
	 * @param key 键值
	 * @param value 内容
	 * @return 信息项
	 */
	protected static Message createMessage(String key, String value) {
		return new Message(key, value);
	}

	/** 信息内容 */
	public final String value;
	/** 信息键值 */
	public final String key;

	/** 信息格式化模板 */
	private transient Ref<MessageFormat> format;

	/**
	 * 构造函数
	 * @param key 键值
	 * @param value 内容
	 */
	private Message(String key, String value) {
		Assertor.F.notNull(value);
		this.key = key;
		this.value = value;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Message)) return false;
		Message message = (Message) o;
		return Assertor.P.equals(key, message.key) && Assertor.P.equals(value, message.value);
	}

	public int hashCode() {
		return value.hashCode();
	}

	public String toString() {
		return value;
	}

	/**
	 * 获取信息格式
	 * @return 信息格式
	 */
	protected MessageFormat format() {
		MessageFormat f = format == null ? null : format.get();
		if (f == null) format = new Softref(f = MessageFormat.compile(value));
		return f;
	}
}

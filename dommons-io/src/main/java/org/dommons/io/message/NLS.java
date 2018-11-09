/*
 * @(#)NLS.java     2011-10-25
 */
package org.dommons.io.message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.dommons.core.Assertor;
import org.dommons.core.Silewarner;
import org.dommons.core.convert.Converter;

/**
 * 国际化信息抽象定义包
 * @author Demon 2011-10-25
 * @deprecated {@link org.dommons.io.nls.NLS}
 */
public abstract class NLS {

	/** 未找到信息格式 */
	static final Message NOT_FOUND_FORMAT = Message.createMessage(null, "Not found the message named ''{0}''");

	/**
	 * 初始化信息集
	 * @param clazz 信息类
	 */
	protected static void initializeMessages(Class clazz) {
		Assertor.F.notNull(clazz);
		initializeMessages(clazz.getName(), clazz);
	}

	/**
	 * 初始化信息集
	 * @param pack 资源包
	 * @param name 资源名
	 * @param clazz 信息类
	 */
	protected static void initializeMessages(Package pack, String name, Class clazz) {
		initializeMessages(NLSContents.load(pack, name), clazz);
	}

	/**
	 * 初始化信息集
	 * @param bundleName 资源包名
	 * @param clazz 信息类
	 */
	protected static void initializeMessages(String bundleName, Class clazz) {
		initializeMessages(NLSContents.load(bundleName), clazz);
	}

	/**
	 * 获取信息内容
	 * @param contents 消息内容集
	 * @param name 信息名
	 * @return 内容
	 */
	static String getMessage(NLSContents contents, String name) {
		String message = contents.getMessage(name);
		if (message == null) message = contents.getMessage(name.replace(NLSContents.Separators[0], '.'));
		return message != null ? message : NOT_FOUND_FORMAT.format(name);
	}

	/**
	 * 初始化信息集
	 * @param contents 消息内容集
	 * @param clazz 信息类
	 */
	static void initializeMessages(NLSContents contents, Class clazz) {
		Assertor.F.notNull(clazz);
		for (Field field : clazz.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (!Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) continue;

			String name = field.getName();
			String message = getMessage(contents, name);
			try {
				innerMessage(field, message);
			} catch (Exception e) {
				Silewarner.warn(NLS.class, "Import message [" + message + "] to field [" + field.getName() + "] fail", e);
			}
		}
	}

	/**
	 * 设置信息内容
	 * @param field 字段项
	 * @param message 信息内容
	 * @throws IllegalArgumentException
	 * @throws ClassCastException
	 * @throws IllegalAccessException
	 */
	static void innerMessage(Field field, String message) throws IllegalArgumentException, ClassCastException,
			IllegalAccessException {
		if (!field.isAccessible()) field.setAccessible(true);
		if (field.getType().isAssignableFrom(Message.class)) {
			field.set(null, Message.createMessage(field.getName(), message));
		} else {
			field.set(null, Converter.P.convert(message, field.getType()));
		}
	}

	/**
	 * 构造函数
	 */
	protected NLS() {
	}
}

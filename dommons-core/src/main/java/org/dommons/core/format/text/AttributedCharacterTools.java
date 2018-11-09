/*
 * @(#)AttributedCharacterTools.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * 属性字符工具
 * @author Demon 2011-10-18
 */
class AttributedCharacterTools {
	private static Constructor<AttributedString>[] constructor;

	/**
	 * 创建迭代器
	 * @param iterator 迭代器
	 * @param key 属性键
	 * @param value 值
	 * @return 迭代器
	 */
	public static AttributedCharacterIterator createIterator(
			AttributedCharacterIterator iterator, AttributedCharacterIterator.Attribute key, Object value) {
		AttributedString as = new AttributedString(iterator);
		as.addAttribute(key, value);
		return as.getIterator();
	}

	/**
	 * 创建迭代器
	 * @param iterators 迭代器集合
	 * @return 迭代器
	 */
	public static AttributedCharacterIterator createIterator(AttributedCharacterIterator[] iterators) {
		Constructor<AttributedString> constructor = getConstructor();
		if (constructor == null) return null;
		if (!constructor.isAccessible()) constructor.setAccessible(true);

		AttributedString as = null;
		try {
			as = constructor.newInstance((Object) iterators);
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause().getMessage(), e.getCause());
		}

		return as.getIterator();
	}

	/**
	 * 创建迭代器
	 * @param s 字符串
	 * @return 迭代器
	 */
	public static AttributedCharacterIterator createIterator(String s) {
		AttributedString as = new AttributedString(s);
		return as.getIterator();
	}

	/**
	 * 创建迭代器
	 * @param string 字符串
	 * @param key 属性键
	 * @param value 值
	 * @return 迭代器
	 */
	public static AttributedCharacterIterator createIterator(String string, AttributedCharacterIterator.Attribute key, Object value) {
		AttributedString as = new AttributedString(string);
		as.addAttribute(key, value);
		return as.getIterator();
	}

	/**
	 * 获取构造函数
	 * @return 构造函数
	 */
	protected static Constructor<AttributedString> getConstructor() {
		if (constructor == null) {
			try {
				constructor = new Constructor[] { AttributedString.class.getDeclaredConstructor(AttributedCharacterIterator[].class) };
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		return constructor[0];
	}
}

/*
 * @(#)Primitives.java     2011-10-19
 */
package org.dommons.core.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * 原生类型
 * @author Demon 2011-10-19
 */
public final class Primitives {

	/** 实例 */
	private static Primitives instance = null;

	/**
	 * 是否基本类型
	 * @param type 类型
	 * @return 是、否
	 */
	public static boolean basicType(Class type) {
		if (type.isPrimitive()) return true;
		else if (Number.class.isAssignableFrom(type)) return true;
		else if (CharSequence.class.isAssignableFrom(type)) return true;
		else if (Boolean.class.isAssignableFrom(type)) return true;
		return false;
	}

	/**
	 * 获取对应空值
	 * @param clazz 原生类型
	 * @return 空值
	 */
	public static <T> T getNullValue(Class<T> clazz) {
		return (T) getMap().get(clazz);
	}

	/**
	 * 获取对应对象类型
	 * @param clazz 原生类型
	 * @return 对象类型
	 */
	public static <T> Class<T> toClass(Class<T> clazz) {
		T obj = getNullValue(clazz);
		if (obj == null) return (Class<T>) Void.TYPE;
		else return (Class<T>) obj.getClass();
	}

	/**
	 * 获取对应关系
	 * @return 对应关系
	 */
	static Map<Class, Object> getMap() {
		if (instance == null) {
			synchronized (Primitives.class) {
				if (instance == null) instance = new Primitives();
			}
		}
		return instance.map;
	}

	/** 空值对应表 */
	private Map<Class, Object> map = new HashMap();

	/**
	 * 构造函数
	 */
	private Primitives() {
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		map.put(int.class, Integer.valueOf(0));
		map.put(boolean.class, Boolean.FALSE);
		map.put(char.class, Character.valueOf((char) 0));
		map.put(long.class, Long.valueOf(0));
		map.put(short.class, Short.valueOf((short) 0));
		map.put(double.class, Double.valueOf(0));
		map.put(float.class, Float.valueOf(0));
		map.put(byte.class, Byte.valueOf((byte) 0));
		map.put(Void.TYPE, Void.TYPE.cast(null));
	}
}

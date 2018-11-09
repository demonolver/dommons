/*
 * @(#)VersionAdapter.java     2012-3-28
 */
package org.dommons.db.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Types;

import org.dommons.core.convert.Converter;

/**
 * JAVA 版本适应工具
 * @author Demon 2012-3-28
 */
class VersionAdapter {

	/**
	 * 查找方法
	 * @param cls 类
	 * @param name 方法名
	 * @param args 参数类集
	 * @return 方法
	 */
	public static Method find(Class cls, String name, Class... args) {
		try {
			return cls.getMethod(name, args);
		} catch (Exception e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 是否类实现
	 * @param obj 对象
	 * @param cls 类名
	 * @return 是、否
	 */
	public static boolean instanceOf(Object obj, String cls) {
		Class clazz = findClass(cls);
		return clazz == null ? false : clazz.isInstance(obj);
	}

	/**
	 * 执行方法
	 * @param obj 对象
	 * @param method 方法
	 * @param args 参数集
	 * @return 执行结果
	 */
	public static Object invoke(Object obj, Method method, Object... args) {
		try {
			return method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		} catch (Throwable t) {
			throw Converter.P.convert(t, RuntimeException.class);
		}
	}

	/**
	 * 获取 SQL 类型值
	 * @param name 名称
	 * @param def 默认值
	 * @return 类型值
	 */
	public static int type(String name, int def) {
		try {
			Field field = Types.class.getField(name);
			return field.getInt(null);
		} catch (Throwable e) {
		}
		return def;
	}

	/**
	 * 查找类
	 * @param cls 类名
	 * @return 类
	 */
	static Class findClass(String cls) {
		try {
			return Class.forName(cls, false, VersionAdapter.class.getClassLoader());
		} catch (ClassNotFoundException e) {
		}

		try {
			return Class.forName(cls, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
		}

		return null;
	}
}

/*
 * @(#)BeanProperty.java     2016-12-01
 */
package org.dommons.core.util.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.dommons.core.convert.Converter;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;

/**
 * 数据对象属性
 * @author demon 2016-12-01
 */
public class BeanProperty {

	private Method r;
	private Method w;
	private Ref<Class> tRef;

	protected final String name;

	protected BeanProperty(String name, Method read, Method write) {
		this.name = name;
		setReadMethod(read);
		setWriteMethod(write);
	}

	/**
	 * 获取属性值
	 * @param o 数据对象
	 * @return 属性值
	 */
	public Object get(Object o) {
		Method m = getReadMethod();
		if (o == null) throw new NullPointerException();
		else if (m == null) throw new RuntimeException("no read method.");
		else if (!m.getDeclaringClass().isInstance(o))
			throw new RuntimeException("not instance of " + m.getDeclaringClass().getSimpleName());
		try {
			if (!Modifier.isPublic(o.getClass().getModifiers())) m.setAccessible(true);
			return m.invoke(o);
		} catch (Throwable t) {
			throw Converter.F.convert(t, RuntimeException.class);
		}
	}

	/**
	 * 获取属性名
	 * @return 属性名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取属性类型
	 * @return 属性类型
	 */
	public synchronized Class<?> getPropertyType() {
		Class type = tRef == null ? null : tRef.get();
		if (type == null) {
			try {
				type = findType(getReadMethod(), getWriteMethod());
				tRef = new Softref(type);
			} catch (Throwable t) {
				type = Void.TYPE;
			}
		}
		return type;
	}

	/**
	 * 获取读取方法
	 * @return 读取方法
	 */
	public Method getReadMethod() {
		return r;
	}

	/**
	 * 获取写入方法
	 * @return 写入方法
	 */
	public Method getWriteMethod() {
		return w;
	}

	/**
	 * 设置属性值
	 * @param o 数据对象
	 * @param value 属性值
	 */
	public void set(Object o, Object value) {
		Method m = getWriteMethod();
		if (o == null) throw new NullPointerException();
		else if (m == null) throw new RuntimeException("no write method.");
		else if (!m.getDeclaringClass().isInstance(o))
			throw new RuntimeException("not instance of " + m.getDeclaringClass().getSimpleName());
		value = Converter.P.convert(value, m.getParameterTypes()[0]);
		try {
			if (!Modifier.isPublic(o.getClass().getModifiers())) m.setAccessible(true);
			m.invoke(o, value);
		} catch (Throwable t) {
			throw Converter.F.convert(t, RuntimeException.class);
		}
	}

	/**
	 * 设置读取方法
	 * @param m 方法
	 */
	public synchronized void setReadMethod(Method m) {
		if (m == null) {
			r = null;
		} else {
			Class[] pts = m.getParameterTypes();
			if (pts != null && pts.length > 0) throw new RuntimeException("bad read method arg count: " + m);
			r = m;
		}
	}

	/**
	 * 设置写入方法
	 * @param m
	 */
	public void setWriteMethod(Method m) {
		if (m == null) {
			w = null;
		} else {
			Class[] pts = m.getParameterTypes();
			if (pts == null || pts.length != 1) throw new RuntimeException("bad write method arg count: " + m);
			w = m;
		}
	}

	/**
	 * 解析属性类型
	 * @param r 读取方法
	 * @param w 写入方法
	 * @return 属性类型
	 */
	private Class findType(Method r, Method w) {
		Class t = Void.TYPE;
		if (w != null) t = w.getParameterTypes()[0];
		if (r != null) {
			Class rt = r.getReturnType();
			if (!Void.TYPE.equals(rt)) t = rt;
		}
		return t;
	}
}

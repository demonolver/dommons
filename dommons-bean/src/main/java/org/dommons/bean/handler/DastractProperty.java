/*
 * @(#)DastractProperty.java     2012-7-13
 */
package org.dommons.bean.handler;

import java.lang.reflect.Type;

import org.dommons.bean.DommonProperty;
import org.dommons.bean.transition.DommonTransition;

/**
 * 抽象通用属性项
 * @author Demon 2012-7-13
 */
public abstract class DastractProperty<E> implements DommonProperty {

	protected final DommonTransition transition;
	protected final DastractBean<E> parent;
	protected final String name;

	protected DastractProperty(DommonTransition transition, DastractBean<E> parent, String name) {
		this.transition = transition;
		this.parent = parent;
		this.name = name;
	}

	public Object get() {
		if (!readable()) throw new IllegalStateException("The property '" + getName() + "' is not be read");
		return parent.exists() ? getter() : null;
	}

	public <T> T get(Class<T> type) throws ClassCastException {
		return convert(get(), type);
	}

	public String getName() {
		return name;
	}

	public void set(Object value) {
		if (!writable()) throw new IllegalStateException("The property '" + getName() + "' is not be written");
		if (!parent.exists()) throw new IllegalStateException();
		setter(value);
	}

	/**
	 * 转换值
	 * @param value 原值
	 * @param type 类型
	 * @return 新值
	 */
	protected <T> T convert(Object value, Class<T> type) {
		return transition.transform(value, type);
	}

	/**
	 * 获取属性值
	 * @return 属性值
	 */
	protected abstract Object getter();

	/**
	 * 获取读取值类型
	 * @return 类型
	 */
	protected abstract Type readableType();

	/**
	 * 设置属性值
	 * @param value 属性值
	 * @return 属性值
	 */
	protected abstract Object setter(Object value);

	/**
	 * 获取写入值类型
	 * @return 类型
	 */
	protected abstract Type writableType();
}

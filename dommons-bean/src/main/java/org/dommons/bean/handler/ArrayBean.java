/*
 * @(#)ArrayBean.java     2012-11-2
 */
package org.dommons.bean.handler;

import java.lang.reflect.Type;

import org.dommons.bean.transition.DommonTransition;

/**
 * 数据对象
 * @author Demon 2012-11-2
 */
class ArrayBean extends DastractBean<Object> {

	protected final Type component;

	/**
	 * 构造函数
	 * @param tar 目标数组
	 * @param transition 
	 * @param builder
	 * @param parent
	 * @param component
	 */
	public ArrayBean(Object tar, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent, Type component) {
		super(tar, transition, builder, parent);
		this.component = component;
	}

	protected DastractProperty lookup(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}

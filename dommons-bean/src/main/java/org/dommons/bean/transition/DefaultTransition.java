/*
 * @(#)DefaultTransition.java     2012-7-19
 */
package org.dommons.bean.transition;

import java.lang.reflect.Type;

import org.dommons.bean.handler.DommonBeanBuilder;

/**
 * 默认转换器
 * @author Demon 2012-7-19
 */
class DefaultTransition extends DommonTransition {

	/**
	 * 构造函数
	 * @param builder 通用数据对象构建器
	 */
	public DefaultTransition(DommonBeanBuilder builder) {
		super(builder);
	}

	public <T> T create(Class<T> type) {
		return (T) create((Type) type);
	}

	public Object create(Type type) {
		// TODO Auto-generated method stub
		return null;
	}
}

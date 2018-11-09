/*
 * @(#)CollectionBean.java     2012-11-2
 */
package org.dommons.bean.handler;

import java.lang.reflect.Type;
import java.util.Collection;

import org.dommons.bean.transition.DommonTransition;

/**
 * TODO
 * @author Demon 2012-11-2
 */
class CollectionBean extends DastractBean<Collection> {

	protected final Type etype;

	/**
	 * 构造函数
	 * @param collection 集合体
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 */
	public CollectionBean(Collection collection, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent) {
		this(collection, transition, builder, parent, Object.class);
	}

	/**
	 * 构造函数
	 * @param collection 集合体
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 * @param etype 子元素类型
	 */
	public CollectionBean(Collection collection, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent,
			Type etype) {
		super(collection, transition, builder, parent);
		this.etype = etype;
	}

	protected DastractProperty lookup(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}

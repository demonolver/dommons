/*
 * @(#)DommonBeanBuilder.java     2012-7-13
 */
package org.dommons.bean.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.dommons.bean.DommonBean;
import org.dommons.bean.transition.DommonTransition;
import org.dommons.core.Assertor;

/**
 * 通用数据对象构建器
 * @author Demon 2012-7-13
 */
public final class DommonBeanBuilder {

	protected final DommonTransitionProvider provider;

	/**
	 * 构造函数
	 * @param provider 数据对象转换提供器
	 */
	protected DommonBeanBuilder(DommonTransitionProvider provider) {
		this.provider = provider;
	}

	/**
	 * 转换数据对象为通用数据对象
	 * @param obj 数据对象
	 * @return 通用数据对象
	 */
	public DommonBean bean(Object obj) {
		return obj == null ? null : obj instanceof DommonBean ? (DommonBean) obj : build(obj);
	}

	/**
	 * 加载数据对象为通用数据对象
	 * @param bean 目标数据对象
	 * @return 通用数据对象
	 */
	public <E> DommonBean<E> load(E bean) {
		Assertor.F.notNull(bean);
		return build(bean);
	}

	/**
	 * 构建通用数据对象
	 * @param bean 目标数据对象
	 * @return 通用数据对象
	 */
	protected <O> DommonBean<O> build(O bean) {
		return build(bean, bean.getClass(), null);
	}

	/**
	 * 构建通用数据对象
	 * @param bean 目标数据对象
	 * @param type 类型
	 * @param parent 父数据对象实例
	 * @return 通用数据对象
	 */
	protected <O> DastractBean<O> build(O bean, Type type, DastractBean parent) {
		Class c = DommonTransition.type(type);
		if (Map.class.isAssignableFrom(c)) {
			if (type instanceof ParameterizedType) {
				Type[] types = ((ParameterizedType) type).getActualTypeArguments();
				if (types != null && types.length >= 2)
					return (DastractBean<O>) new MappingBean((Map) bean, provider.getTransition(), this, parent, types[0],
							types[1]);
			}
			return (DastractBean<O>) new MappingBean((Map) bean, provider.getTransition(), this, parent);
		} else if (Collection.class.isAssignableFrom(c)) {
			// TODO
			return null;
		} else if (c.isArray()) {
			return (DastractBean<O>) new ArrayBean(bean, provider.getTransition(), this, parent, c.getComponentType());
		} else {
			return new DataBean(bean, provider.getTransition(), this, parent, c);
		}
	}

	/**
	 * 数据对象转换提供器
	 * @author Demon 2012-7-18
	 */
	public static abstract class DommonTransitionProvider {

		/**
		 * 创建通用数据对象构建器
		 * @return 数据对象构建器
		 */
		protected final DommonBeanBuilder createBuilder() {
			return new DommonBeanBuilder(this);
		}

		protected abstract DommonTransition getTransition();
	}
}

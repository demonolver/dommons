/*
 * @(#)BeanTransformFactory.java     2012-7-13
 */
package org.dommons.bean.transition;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import org.dommons.bean.DommonBean;
import org.dommons.bean.handler.DommonBeanBuilder;
import org.dommons.core.Assertor;

/**
 * 数据对象转换器
 * @author Demon 2012-7-13
 */
public abstract class DommonTransition {

	/**
	 * 分析类型
	 * @param type 类型
	 * @return 类
	 */
	public static Class type(Type type) {
		// TODO
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterized = (ParameterizedType) type;
			return type(parameterized.getRawType());
		} else if (type instanceof GenericArrayType) {
			GenericArrayType array = (GenericArrayType) type;
			Class component = type(array.getGenericComponentType());
			return Array.newInstance(component, 0).getClass();
		} else if (type instanceof TypeVariable) {
			Type[] ts = ((TypeVariable) type).getBounds();
			return ts == null || ts.length < 1 ? Object.class : type(ts[0]);
		} else if (type instanceof WildcardType) {
			Type[] ts = ((WildcardType) type).getUpperBounds();
			if (ts != null && ts.length > 0) {
				return type(ts[0]);
			} else {
				ts = ((WildcardType) type).getLowerBounds();
			}
			return ts == null || ts.length < 1 ? Object.class : type(ts[0]);
		} else {
			return Object.class;
		}
	}

	protected final DommonBeanBuilder builder;

	/**
	 * 构造函数
	 * @param builder 通用数据对象构建器
	 */
	public DommonTransition(DommonBeanBuilder builder) {
		this.builder = builder;
	}

	/**
	 * 创建类型值实例
	 * @param type 类型
	 * @return 值
	 */
	public abstract <T> T create(Class<T> type);

	/**
	 * 创建类型值实例
	 * @param type 类型
	 * @return 值
	 */
	public abstract Object create(Type type);

	/**
	 * 复制通用数据对象
	 * @param source 源通用数据对象
	 * @param target 目标通用数据对象
	 */
	public void duplicate(DommonBean source, DommonBean target) {
		// TODO
	}

	/**
	 * 复制数据对象
	 * @param source 源数据对象
	 * @param target 目标数据对象
	 */
	public void duplicate(Object source, Object target) {
		Assertor.P.notNull(source, "Source object is not be null");
		Assertor.P.notNull(target, "Target object is not be null");

		duplicate(builder.bean(source), builder.bean(target));
	}

	/**
	 * 转换数据对象
	 * @param value 数据对象
	 * @param type 目标类型
	 * @return 新数据对象
	 */
	public <T> T transform(Object value, Class<T> type) {
		return (T) transform(value, (Type) type);
	}

	/**
	 * 转换数据对象
	 * @param value 数据对象
	 * @param type 目标类型
	 * @return 新数据对象
	 */
	public Object transform(Object value, Type type) {
		if (type == null || type(type).isInstance(value)) return value;
		// TODO
		return null;
	}
}

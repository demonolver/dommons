/*
 * @(#)ConvertHandler.java     2011-10-19
 */
package org.dommons.core.convert;

/**
 * 转换处理器
 * @author Demon 2011-10-19
 */
public interface ConvertHandler<S, T> {

	/**
	 * 转换数据
	 * @param obj 数据对象
	 * @param source 对象类型
	 * @param target 目标类型
	 * @return 结果数据
	 */
	public T convert(S obj, Class<? extends S> source, Class<T> target);
}

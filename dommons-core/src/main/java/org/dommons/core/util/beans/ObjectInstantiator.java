/*
 * @(#)ObjectInstantiator.java     2018-08-31
 */
package org.dommons.core.util.beans;

/**
 * 对象实例化器
 * @author demon 2018-08-31
 */
public interface ObjectInstantiator<O> {

	/**
	 * 构建对象
	 * @return 新对象
	 */
	O newInstance();

	/**
	 * 对象创建器
	 * @param <O>
	 * @author demon 2018-08-31
	 */
	public interface ObjectCreator<O> extends ObjectInstantiator<O> {

		/**
		 * 是否对象实例
		 * @param o 目标对象
		 * @return 是、否
		 */
		boolean isInstance(Object o);
	}
}

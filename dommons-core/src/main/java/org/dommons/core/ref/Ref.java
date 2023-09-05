/*
 * @(#)Ref.java     2018-08-22
 */
package org.dommons.core.ref;

/**
 * 对象引用
 * @author demon 2018-08-22
 */
public interface Ref<T> {

	/**
	 * 获取引用对象实例
	 * @return 对象实例
	 */
	T get();
}

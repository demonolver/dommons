/*
 * @(#)Modref.java     2023-02-22
 */
package org.dommons.core.ref;

/**
 * 可变更对象引用
 * @author demon 2023-02-22
 */
public interface Modref<T> extends Ref<T> {

	/**
	 * 设置对象值
	 * @param value 对象值
	 */
	void set(T value);
}

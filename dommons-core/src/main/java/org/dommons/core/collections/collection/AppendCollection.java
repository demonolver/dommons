/*
 * @(#)AppendCollection.java     2011-10-19
 */
package org.dommons.core.collections.collection;

import java.util.Collection;

/**
 * 可追加的数据集
 * @author Demon 2011-10-19
 */
public interface AppendCollection<E, C extends Collection<E>> extends Collection<E> {

	/**
	 * 追加
	 * @param o 数据项
	 * @return 数据集自身
	 */
	AppendCollection<E, C> append(E o);

	/**
	 * 追加数组内容
	 * @param es 数据项数组
	 * @return 数据集自身
	 */
	AppendCollection<E, C> appendArray(E[] es);

	/**
	 * 获取原数据集实体
	 * @return 数据集
	 */
	C entity();
}

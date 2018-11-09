/*
 * @(#)CollectionEnumeration.java     2012-7-5
 */
package org.dommons.core.collections.enumeration;

import java.util.Iterator;

/**
 * 数据集枚举
 * @author Demon 2012-7-5
 */
public class CollectionEnumeration<E> extends IterableEnumeration<E> {

	/**
	 * 创建数据集枚举
	 * @param iterable 数据集
	 * @return 数据集枚举
	 */
	public static <E> IterableEnumeration<E> create(Iterable<E> iterable) {
		return iterable == null ? empty() : new CollectionEnumeration(iterable.iterator());
	}

	/** 目标迭代器 */
	protected final Iterator<? extends E> tar;

	/**
	 * 构造函数
	 * @param it 目标迭代器
	 */
	protected CollectionEnumeration(Iterator<? extends E> it) {
		this.tar = it;
	}

	public boolean hasNext() {
		return tar.hasNext();
	}

	public E next() {
		return tar.next();
	}

	public void remove() {
		tar.remove();
	}
}

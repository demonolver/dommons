/*
 * @(#)AppendCollectionWrapper.java     2011-10-19
 */
package org.dommons.core.collections.collection;

import java.util.Collection;

/**
 * 可追加数据集包装
 * @author Demon 2011-10-19
 */
public class AppendCollectionWrapper<E, C extends Collection<E>> extends AbstractCollectionWrapper<E, C> implements AppendCollection<E, C> {

	private static final long serialVersionUID = -1776114503853550129L;

	/**
	 * 包装
	 * @param collection 数据集
	 * @return 追加数据集
	 */
	public static <E, C extends Collection<E>> AppendCollection<E, C> wrap(C collection) {
		if (collection == null) return null;
		else if (collection instanceof AppendCollection) return AppendCollection.class.cast(collection);
		else return new AppendCollectionWrapper(collection);
	}

	/**
	 * 构造函数
	 * @param tar 目标数据集
	 */
	public AppendCollectionWrapper(C tar) {
		super(tar);
	}

	public AppendCollection<E, C> append(E o) {
		synchronized (this) {
			add(o);
		}
		return this;
	}

	public AppendCollection<E, C> appendArray(E[] es) {
		synchronized (this) {
			for (E e : es) {
				add(e);
			}
		}
		return this;
	}

	public C entity() {
		synchronized (this) {
			return (C) tar();
		}
	}
}

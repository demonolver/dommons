/*
 * @(#)ConcurrentSet.java     2011-10-19
 */
package org.dommons.core.collections.set;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持并发的哈希无重复数据集
 * @author Demon 2011-10-19
 */
public class ConcurrentHashSet<E> extends AbsSet<E> {

	private static final long serialVersionUID = -2579861916163745053L;

	/**
	 * 构造函数
	 */
	public ConcurrentHashSet() {
		super(new ConcurrentHashMap());
	}

	/**
	 * 构造函数
	 * @param c 初始集合内容
	 */
	public ConcurrentHashSet(Collection<? extends E> c) {
		this(Math.max(16, (int) (c == null ? 0 : c.size() / .75d)));
		addAll(c);
	}

	/**
	 * 构造函数
	 * @param initialCapacity 初始容量
	 */
	public ConcurrentHashSet(int initialCapacity) {
		super(new ConcurrentHashMap(initialCapacity));
	}
}

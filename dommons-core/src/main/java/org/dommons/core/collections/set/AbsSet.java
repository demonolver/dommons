/*
 * @(#)AbsSet.java     2011-10-19
 */
package org.dommons.core.collections.set;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 抽象无重复元素的数据集
 * @author Demon 2011-10-19
 */
public abstract class AbsSet<E> extends java.util.AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = -7022606254978591701L;

	/** 默认元素值 */
	private static final Object PRESENT = new Object();

	/**
	 * 包装成忽略大小写无重复数据集
	 * @param set 目标数据集
	 * @param syn 是否线程同步
	 * @return 新数据集
	 */
	public static Set<String> wrap(Set<String> set, boolean syn) {
		if (set instanceof CaseInsensitiveSetWrapper) {
			CaseInsensitiveSetWrapper w = (CaseInsensitiveSetWrapper) set;
			if (w.syn == syn) return w;
			else set = w.tar;
		}
		return new CaseInsensitiveSetWrapper(set, syn);
	}

	private transient Map<E, Object> map;

	/**
	 * 构造函数
	 * @param map 目标映射表
	 */
	protected AbsSet(Map<E, Object> map) {
		this.map = map;
	}

	public boolean add(E o) {
		return map.put(o, PRESENT) == null;
	}

	public boolean addAll(Collection<? extends E> c) {
		if (c == null) return false;
		return super.addAll(c);
	}

	public void clear() {
		map.clear();
	}

	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	public boolean removeAll(Collection<?> c) {
		if (c == null) return false;
		return super.removeAll(c);
	}

	public int size() {
		return map.size();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		map = (Map) s.readObject();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(map);
	}
}
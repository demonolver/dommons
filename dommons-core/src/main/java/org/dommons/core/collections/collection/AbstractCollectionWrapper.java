/*
 * @(#)AbstractCollectionWrapper.java     2012-6-25
 */
package org.dommons.core.collections.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * 抽象数据集合体包装
 * @author Demon 2012-6-25
 */
public abstract class AbstractCollectionWrapper<E, C extends Collection<E>> implements Collection<E>, Serializable {

	private static final long serialVersionUID = -1439263155122275858L;

	private transient C tar;

	protected AbstractCollectionWrapper(C tar) {
		if (tar == null) throw new NullPointerException();
		this.tar = tar;
	}

	public boolean add(E o) {
		return tar.add(o);
	}

	public boolean addAll(Collection<? extends E> c) {
		return tar.addAll(c);
	}

	public void clear() {
		tar.clear();
	}

	public boolean contains(Object o) {
		return tar.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return c != null && tar.containsAll(c);
	}

	public boolean equals(Object o) {
		return tar.equals(o);
	}

	public int hashCode() {
		return tar.hashCode();
	}

	public boolean isEmpty() {
		return tar.isEmpty();
	}

	public Iterator<E> iterator() {
		return tar.iterator();
	}

	public boolean remove(Object o) {
		return tar.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return c != null && tar.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		if (c != null) {
			return tar.retainAll(c);
		} else if (isEmpty()) {
			return false;
		} else {
			clear();
			return true;
		}
	}

	public int size() {
		return tar.size();
	}

	public Object[] toArray() {
		return tar.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return a == null ? (T[]) toArray() : tar.toArray(a);
	}

	public String toString() {
		return tar.toString();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	protected void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		tar = (C) s.readObject();
	}

	/**
	 * 获取目标数据集
	 * @return 目标数据集
	 */
	protected final Collection<E> tar() {
		return tar;
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	protected void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(tar);
	}
}

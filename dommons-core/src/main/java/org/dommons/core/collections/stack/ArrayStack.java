/*
 * @(#)ArrayStack.java     2011-10-18
 */
package org.dommons.core.collections.stack;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.dommons.core.Assertor;

/**
 * 数组堆栈
 * @author Demon 2011-10-18
 */
public class ArrayStack<E> extends AbstractStack<E> implements Stack<E>, Cloneable, Serializable {

	private static final long serialVersionUID = -3200610112072279642L;

	private transient E[] elementDatas;

	private int size;
	private transient int modCount;

	/**
	 * 构造函数
	 */
	public ArrayStack() {
		this(10);
	}

	/**
	 * 构造函数
	 * @param initialCapacity 初始化容器
	 */
	public ArrayStack(int initialCapacity) {
		super();
		if (initialCapacity < 0) initialCapacity = 10;
		ensureCapacity(initialCapacity);
		size = 0;
		modCount = 0;
	}

	public void clear() {
		synchronized (this) {
			size = 0;
		}
	}

	public Object clone() {
		try {
			ArrayStack<E> v = (ArrayStack<E>) super.clone();
			v.elementDatas = (E[]) new Object[size];
			System.arraycopy(elementDatas, 0, v.elementDatas, 0, size);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			throw new InstantiationError();
		}
	}

	public boolean contains(Object o) {
		for (int i = 0; i < size; i++) {
			if (Assertor.P.equals(o, elementDatas[i])) return true;
		}
		return false;
	}

	public Iterator<E> iterator() {
		return new ArrayStackIterator(true);
	}

	public E peek() {
		if (size == 0) return null;
		return elementDatas[size - 1];
	}

	public E pop() {
		if (size == 0) return null;
		return remove(size - 1);
	}

	public boolean push(E o) {
		synchronized (this) {
			ensureCapacity(size + 1);
			elementDatas[size++] = o;
		}
		return true;
	}

	public boolean remove(Object o) {
		for (int i = 0; i < size; i++) {
			if (Assertor.P.equals(o, elementDatas[i])) {
				remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		if (Assertor.P.empty(c)) return false;
		boolean modified = false;
		for (int i = 0; i < size; i++) {
			if (c.contains(elementDatas[i])) {
				remove(i);
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Collection<?> c) {
		if (c == null) return false;
		boolean modified = false;
		for (int i = 0; i < size; i++) {
			if (!c.contains(elementDatas[i])) {
				remove(i);
				modified = true;
			}
		}
		return modified;
	}

	public int size() {
		return size;
	}

	public Iterator<E> stackIterator() {
		return new ArrayStackIterator(false);
	}

	public Object[] toArray() {
		return converToArray(null);
	}

	public <T> T[] toArray(T[] a) {
		if (a == null) throw new NullPointerException();
		return converToArray(a);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder(64);
		buf.append("[");

		for (int i = 0; i < size; i++) {
			if (i > 0) buf.append(", ");
			E o = elementDatas[size];
			buf.append(o == this ? "(this stack)" : String.valueOf(o));
		}

		buf.append("]");
		return buf.toString();
	}

	/**
	 * 元素集转换为数组
	 * @param <T> 类型
	 * @param a 目标数组
	 * @return 结果数组
	 */
	protected <T> T[] converToArray(T[] a) {
		if (a == null) {
			a = (T[]) new Object[size];
		} else if (a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		System.arraycopy(elementDatas, 0, a, 0, size);
		return a;
	}

	/**
	 * 数组扩容
	 * @param minCapacity 最小容器
	 */
	protected void ensureCapacity(int minCapacity) {
		synchronized (this) {
			int oldCapacity = elementDatas == null ? 0 : elementDatas.length;
			if (minCapacity > oldCapacity) {
				Object oldData[] = elementDatas;
				int newCapacity = (oldCapacity * 3) / 2 + 1;
				if (newCapacity < minCapacity) newCapacity = minCapacity;
				elementDatas = (E[]) new Object[newCapacity];
				if (oldData != null) System.arraycopy(oldData, 0, elementDatas, 0, size);
			}
		}
	}

	/**
	 * 移除元素项
	 * @param index 元素项位序
	 */
	protected E remove(int index) {
		synchronized (this) {
			if (index < 0 || index >= size) return null;
			E o = elementDatas[index];
			int numMoved = size - index - 1;
			if (numMoved > 0) System.arraycopy(elementDatas, index + 1, elementDatas, index, numMoved);
			elementDatas[--size] = null;
			modCount++;
			return o;
		}
	}

	/**
	 * 读取序列化内容
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// 读取堆对象默认内容
		s.defaultReadObject();

		// 读取元素项数组数量
		int arrayLength = s.readInt();
		Object[] a = elementDatas = (E[]) new Object[arrayLength];

		// 读取所有元素项
		for (int i = 0; i < size; i++)
			a[i] = s.readObject();
	}

	/**
	 * 写入序列化内容
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// 写入对象默认内容
		s.defaultWriteObject();

		// 写入元素项数组数量
		s.writeInt(elementDatas.length);

		// 写入所有元素项
		for (int i = 0; i < size; i++)
			s.writeObject(elementDatas[i]);
	}

	/**
	 * 数组堆栈迭代器
	 * @author Demon 2011-10-18
	 */
	protected class ArrayStackIterator implements Iterator<E> {

		private final boolean sequence;
		private int index;
		private int expectedModCount;

		protected ArrayStackIterator(boolean sequence) {
			this.sequence = sequence;
			index = sequence ? -1 : size;
			expectedModCount = modCount;
		}

		public boolean hasNext() {
			checkForComodification();
			return sequence ? index < size - 1 : index > 0;
		}

		public E next() {
			checkForComodification();
			return sequence ? get(++index) : get(--index);
		}

		public void remove() {
			ArrayStack.this.remove(index);
			expectedModCount++;
		}

		/**
		 * 获取元素值
		 * @param index 位序
		 * @return 元素值
		 */
		protected E get(int index) {
			if (index < 0 || index >= size) throw new NoSuchElementException();
			return elementDatas[index];
		}

		/**
		 * 检查并发
		 */
		final void checkForComodification() {
			if (modCount != expectedModCount) throw new ConcurrentModificationException();
		}
	}
}

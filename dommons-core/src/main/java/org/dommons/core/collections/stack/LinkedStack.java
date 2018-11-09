/*
 * @(#)LinkedStack.java     2011-10-18
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
 * 链表堆栈
 * @author Demon 2011-10-18
 */
public class LinkedStack<E> extends AbstractStack<E> {

	private static final long serialVersionUID = -5857134367922198448L;

	private transient int size;

	private transient Entry head;

	/**
	 * 构造函数
	 */
	public LinkedStack() {
		super();
		size = 0;
		head = new Entry(null);
		head.setPrevious(head, false);
	}

	public void clear() {
		head.setPrevious(head, false);
		size = 0;
	}

	public Object clone() {
		LinkedStack<E> clone = null;
		try {
			clone = (LinkedStack<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InstantiationError();
		}
		clone.head = new Entry(null);
		clone.clear();

		for (Entry en = head.next; en != head; en = en.next) {
			clone.push(en.value);
		}
		return clone;
	}

	public boolean contains(Object o) {
		for (Entry en = head.next; en != head; en = en.next) {
			if (Assertor.P.equals(o, en.value)) return true;
		}
		return false;
	}

	public Iterator<E> iterator() {
		return new LinkedStackIterator(true);
	}

	public E peek() {
		Entry ele = head.previous;
		return ele == head ? null : ele.value;
	}

	public E pop() {
		Entry ele = head.previous;
		if (ele == head) return null;
		ele.remove();
		return ele.value;
	}

	public boolean push(E o) {
		Entry ele = new Entry(o);
		head.setPrevious(ele, true);
		return true;
	}

	public boolean remove(Object o) {
		for (Entry en = head.next; en != head; en = en.next) {
			if (Assertor.P.equals(o, en.value)) {
				en.remove();
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		if (!Assertor.P.notEmpty(c)) return false;
		boolean modified = false;
		for (Entry en = head.next; en != head; en = en.next) {
			if (c.contains(en.value)) {
				en.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean retainAll(Collection<?> c) {
		if (c == null) return false;
		boolean modified = false;
		for (Entry en = head.next; en != head; en = en.next) {
			if (!c.contains(en.value)) {
				en.remove();
				modified = true;
			}
		}
		return modified;
	}

	public int size() {
		return size;
	}

	public Iterator<E> stackIterator() {
		return new LinkedStackIterator(false);
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

		boolean first = true;
		for (Entry en = head.next; en != head; en = en.next) {
			E o = en.value;
			if (first) {
				first = false;
			} else {
				buf.append(", ");
			}
			buf.append(o == this ? "(this stack)" : String.valueOf(o));
		}

		buf.append("]");
		return buf.toString();
	}

	/**
	 * 转换为数组
	 * @param <T> 数组类型
	 * @param a 目标数组
	 * @return 结果数组
	 */
	protected <T> T[] converToArray(T[] a) {
		int size = size();
		if (a == null) {
			a = (T[]) new Object[size];
		} else if (a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		int i = 0;
		for (Entry en = head.next; i < size && en != head; en = en.next) {
			a[i++] = (T) en.value;
		}
		return a;
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		int size = s.readInt();

		head = new Entry(null);
		head.setPrevious(head, false);

		for (int i = 0; i < size; i++)
			head.setPrevious(new Entry((E) s.readObject()), true);
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		s.writeInt(size);

		for (Entry e = head.next; e != head; e = e.next)
			s.writeObject(e.value);
	}

	/**
	 * 链表堆栈迭代器
	 * @author Demon 2011-10-18
	 */
	protected class LinkedStackIterator implements Iterator<E> {

		private Entry current;
		private final boolean order;

		/**
		 * 构造函数
		 * @param order 是否顺序
		 */
		protected LinkedStackIterator(boolean order) {
			this.order = order;
			current = head;
		}

		public boolean hasNext() {
			Entry next = getNext();
			if (next == null) throw new ConcurrentModificationException();
			return next != head;
		}

		public E next() {
			synchronized (this) {
				Entry next = getNext();
				if (next == null) throw new ConcurrentModificationException();
				current = next;
			}
			if (current == head) throw new NoSuchElementException();
			return current.value;
		}

		public void remove() {
			if (current == head) throw new NoSuchElementException();
			Entry tmp = getPrevious();
			current.remove();
			current = tmp;
		}

		/**
		 * 获取下一个元素
		 * @return 元素项
		 */
		protected Entry getNext() {
			return order ? current.next : current.previous;
		}

		/**
		 * 获取上一个元素
		 * @return 元素项
		 */
		protected Entry getPrevious() {
			return order ? current.previous : current.next;
		}
	}

	/**
	 * 元素项
	 * @author Demon 2011-10-18
	 */
	private class Entry implements Serializable {

		private static final long serialVersionUID = -2982651954735882512L;

		private Entry next;
		private Entry previous;
		private final E value;

		/**
		 * 构造函数
		 * @param value 元素值
		 */
		protected Entry(E value) {
			this.value = value;
		}

		/**
		 * 移除当前元素
		 */
		public void remove() {
			if (this == head) throw new NoSuchElementException();
			synchronized (head) {
				if (previous != null) previous.next = next;
				if (next != null) next.previous = previous;
				previous = null;
				next = null;
				size--;
			}
		}

		/**
		 * 设置上一个元素项
		 * @param ele 元素项
		 * @param append 是否增加
		 */
		public void setPrevious(Entry ele, boolean append) {
			if (ele == null) return;
			synchronized (head) {
				if (previous != null) previous.next = ele;
				ele.previous = previous;
				ele.next = this;
				previous = ele;
				if (append) size++;
			}
		}
	}
}

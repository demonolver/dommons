/*
 * @(#)AbsConcurrentCollectionWrapper.java     2018-10-29
 */
package org.dommons.core.collections.collection.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dommons.core.collections.collection.AbstractCollectionWrapper;

/**
 * 抽象并发数据集合包装
 * @author demon 2018-10-29
 */
public abstract class AbsConcurrentCollectionWrapper<E, C extends Collection<E>> extends AbstractCollectionWrapper<E, C> {

	private static final long serialVersionUID = -6198052628146734875L;

	protected transient ReadWriteLock lock;

	protected AbsConcurrentCollectionWrapper(C tar) {
		this(tar, null);
	}

	protected AbsConcurrentCollectionWrapper(C tar, ReadWriteLock lock) {
		super(tar);
		if (lock == null) lock = new ReentrantReadWriteLock();
		this.lock = lock;

	}

	@Override
	public boolean add(E o) {
		Lock l = lock.writeLock();
		l.lock();
		try {
			return super.add(o);
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Lock l = lock.writeLock();
		l.lock();
		try {
			return super.addAll(c);
		} finally {
			l.unlock();
		}
	}

	@Override
	public void clear() {
		Lock l = lock.writeLock();
		l.lock();
		try {
			super.clear();
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean contains(Object o) {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.contains(o);
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.containsAll(c);
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean equals(Object o) {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.equals(o);
		} finally {
			l.unlock();
		}
	}

	@Override
	public int hashCode() {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.hashCode();
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.isEmpty();
		} finally {
			l.unlock();
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new ConcurrentIterator(super.iterator());
	}

	@Override
	public boolean remove(Object o) {
		Lock l = lock.writeLock();
		l.lock();
		try {
			return super.remove(o);
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Lock l = lock.writeLock();
		l.lock();
		try {
			return super.removeAll(c);
		} finally {
			l.unlock();
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Lock l = lock.writeLock();
		l.lock();
		try {
			return super.retainAll(c);
		} finally {
			l.unlock();
		}
	}

	@Override
	public int size() {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.size();
		} finally {
			l.unlock();
		}
	}

	@Override
	public Object[] toArray() {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.toArray();
		} finally {
			l.unlock();
		}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.toArray(a);
		} finally {
			l.unlock();
		}
	}

	@Override
	public String toString() {
		Lock l = lock.readLock();
		l.lock();
		try {
			return super.toString();
		} finally {
			l.unlock();
		}
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	protected void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		super.readObject(s);
		lock = (ReadWriteLock) s.readObject();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	protected void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		super.writeObject(s);
		s.writeObject(lock);
	}

	/**
	 * 并发迭代器
	 * @author demon 2018-10-29
	 */
	protected class ConcurrentIterator implements Iterator<E> {

		private final Iterator<E> it;

		protected ConcurrentIterator(Iterator<E> it) {
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			Lock l = lock.readLock();
			l.lock();
			try {
				return it.hasNext();
			} finally {
				l.unlock();
			}
		}

		@Override
		public E next() {
			Lock l = lock.readLock();
			l.lock();
			try {
				return it.next();
			} finally {
				l.unlock();
			}
		}

		@Override
		public void remove() {
			Lock l = lock.writeLock();
			l.lock();
			try {
				it.remove();
			} finally {
				l.unlock();
			}
		}
	}
}

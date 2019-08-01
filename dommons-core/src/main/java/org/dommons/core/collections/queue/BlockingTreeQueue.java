/*
 * @(#)BlockingTreeQueue.java     2011-10-19
 */
package org.dommons.core.collections.queue;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.dommons.core.Assertor;
import org.dommons.core.util.thread.ThreadLock;

/**
 * 阻塞树型队列
 * @author Demon 2011-10-19
 */
public class BlockingTreeQueue<E> extends TreeQueue<E> implements BlockingQueue<E>, Serializable {

	private static final long serialVersionUID = 4014602492393591104L;

	/** 线程锁 */
	private transient ThreadLock lock;
	/** 非空等待状态 */
	private transient Condition notEmpty;
	/** 非满等待状态 */
	private transient Condition notFull;
	/** 最大数量限制 */
	private final int maxSize;

	/**
	 * 构造函数
	 */
	public BlockingTreeQueue() {
		this(0);
	}

	/**
	 * 构造函数
	 * @param comparator 比较器
	 */
	public BlockingTreeQueue(Comparator<? super E> comparator) {
		this(0, comparator);
	}

	/**
	 * 构造函数
	 * @param maxSize 最大数量限制
	 */
	public BlockingTreeQueue(int maxSize) {
		this(maxSize, null);
	}

	/**
	 * 构造函数
	 * @param maxSize 最大数量限制
	 * @param comparator 比较器
	 */
	public BlockingTreeQueue(int maxSize, Comparator<? super E> comparator) {
		super(comparator);
		this.maxSize = maxSize > 0 ? maxSize : Integer.MAX_VALUE;
		this.lock = new ThreadLock();
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
	}

	public void clear() {
		lock.writeLock();
		try {
			super.clear();
			notFull.signalAll();
		} finally {
			lock.unLock();
		}
	}

	public boolean contains(Object o) {
		lock.readLock();
		try {
			return super.contains(o);
		} finally {
			lock.unLock();
		}
	}

	public int drainTo(Collection<? super E> c) {
		return drain(c, -1);
	}

	public int drainTo(Collection<? super E> c, int maxElements) {
		if (maxElements < 1) return 0;
		return drain(c, maxElements);
	}

	public Iterator<E> iterator() {
		lock.readLock();
		try {
			return new Itr(super.iterator());
		} finally {
			lock.unLock();
		}
	}

	public boolean offer(E o) {
		Assertor.F.notNull(o);

		lock.writeLock();
		try {
			if (maxSize == super.size()) {
				return false;
			} else {
				boolean res = super.offer(o);
				if (res) notEmpty.signal();
				return res;
			}
		} finally {
			lock.unLock();
		}
	}

	public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException {
		Assertor.F.notNull(o);

		lock.writeLock();
		try {
			long nanos = unit.toNanos(timeout);
			for (;;) {
				if (maxSize > super.size()) {
					if (super.offer(o)) {
						notEmpty.signal();
						return true;
					}
				}
				if (nanos <= 0) return false;
				nanos = notFull.awaitNanos(nanos);
			}
		} catch (InterruptedException ie) {
			notFull.signal(); // propagate to non-interrupted thread
			throw ie;
		} finally {
			lock.unLock();
		}
	}

	public E peek() {
		lock.readLock();
		try {
			return super.peek();
		} finally {
			lock.unLock();
		}
	}

	public E poll() {
		lock.writeLock();
		try {
			int size = super.size();
			E x = super.poll();
			if (size > super.size()) notFull.signal();
			return x;
		} finally {
			lock.unLock();
		}
	}

	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		lock.writeLock();
		try {
			long nanos = unit.toNanos(timeout);
			for (;;) {
				int size = 0;
				if ((size = super.size()) != 0) {
					E x = super.poll();
					if (size > super.size()) {
						notFull.signal();
						return x;
					}
				}
				if (nanos <= 0) return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
		} catch (InterruptedException ie) {
			notEmpty.signal(); // propagate to non-interrupted thread
			throw ie;
		} finally {
			lock.unLock();
		}
	}

	public void put(E o) throws InterruptedException {
		Assertor.F.notNull(o);

		lock.writeLock();
		try {
			for (;;) {
				while (maxSize == super.size())
					notFull.await();

				if (super.offer(o)) {
					break;
				} else {
					notFull.await();
				}
			}
			notEmpty.signal();
		} catch (InterruptedException ie) {
			notFull.signal(); // propagate to non-interrupted thread
			throw ie;
		} finally {
			lock.unLock();
		}
	}

	public int remainingCapacity() {
		return maxSize - size();
	}

	public boolean remove(Object o) {
		Assertor.F.notNull(o);

		lock.writeLock();
		try {
			return super.remove(o);
		} finally {
			lock.unLock();
		}
	}

	public boolean removeAll(Collection<?> c) {
		lock.writeLock();
		try {
			boolean res = super.removeAll(c);
			if (res) notFull.signalAll();
			return res;
		} finally {
			lock.unLock();
		}
	}

	public boolean retainAll(Collection<?> c) {
		lock.writeLock();
		try {
			boolean res = super.retainAll(c);
			if (res) notFull.signalAll();
			return res;
		} finally {
			lock.unLock();
		}
	}

	public int size() {
		lock.readLock();
		try {
			return super.size();
		} finally {
			lock.unLock();
		}
	}

	public E take() throws InterruptedException {
		lock.writeLock();
		try {
			for (;;) {
				int size = 0;
				while ((size = super.size()) == 0)
					notEmpty.await();
				E x = super.poll();
				if (size > super.size()) {
					notFull.signal();
					return x;
				}
			}
		} catch (InterruptedException ie) {
			notEmpty.signal(); // propagate to non-interrupted thread
			throw ie;
		} finally {
			lock.unLock();
		}
	}

	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	public <T> T[] toArray(T[] a) {
		lock.readLock();
		try {
			return super.toArray(a);
		} finally {
			lock.unLock();
		}
	}

	public String toString() {
		lock.readLock();
		try {
			return super.toString();
		} finally {
			lock.unLock();
		}
	}

	/**
	 * 迁移
	 * @param c 目标集合
	 * @param maxElements 最大迁移数
	 * @return 迁移数量
	 */
	private int drain(Collection<? super E> c, int maxElements) {
		Assertor.F.isTrue(c != null && c != this, "This argument of target collection must not be null or which is queue itself!");
		int n = 0;
		if (maxElements < 1) maxElements = -1;
		E e = null;
		lock.writeLock();
		try {
			int size = 0;
			for (int old = super.size(); n != maxElements; old = size) {
				e = super.poll();
				size = super.size();
				if (old == size) break;
				c.add(e);
				n++;
			}
			if (n > 0) notFull.signalAll();
			return n;
		} finally {
			lock.unLock();
		}
	}

	/**
	 * 序列化写入方法
	 * @param s 序列化目标项
	 * @throws java.io.IOException 写入出错
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		lock = new ThreadLock();
		notFull = lock.newCondition();
		notEmpty = lock.newCondition();
	}

	/**
	 * 队列迭代器
	 * @param <E> 元素项类型
	 * @author Demon 2011-10-19
	 */
	private class Itr implements Iterator<E> {

		private final Iterator<E> iter;

		Itr(Iterator<E> i) {
			iter = i;
		}

		public boolean hasNext() {
			lock.readLock();
			try {
				return iter.hasNext();
			} finally {
				lock.unLock();
			}
		}

		public E next() {
			lock.readLock();
			try {
				return iter.next();
			} finally {
				lock.unLock();
			}
		}

		public void remove() {
			lock.writeLock();
			try {
				iter.remove();
				notFull.signal();
			} finally {
				lock.unLock();
			}
		}
	}
}
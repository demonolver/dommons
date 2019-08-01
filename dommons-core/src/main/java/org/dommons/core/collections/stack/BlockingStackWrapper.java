/*
 * @(#)BlockingStackWrapper.java     2011-10-18
 */
package org.dommons.core.collections.stack;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.dommons.core.Assertor;
import org.dommons.core.util.thread.ThreadLock;

/**
 * 阻塞堆栈包装
 * @author Demon 2011-10-18
 */
public class BlockingStackWrapper<E> extends AbstractStack<E> implements BlockingStack<E>, Serializable {
	private static final long serialVersionUID = -2169771247051195412L;

	/**
	 * 阻塞包装
	 * @param <T> 类型
	 * @param stack 目标堆栈
	 * @return 阻塞堆栈
	 */
	public static <T> BlockingStack<T> wrap(Stack<T> stack) {
		Assertor.F.notNull(stack, "The target stack is must not be null!");
		return new BlockingStackWrapper(stack, Integer.MAX_VALUE);
	}

	/**
	 * 阻塞包装
	 * @param <T> 类型
	 * @param stack 目标堆栈
	 * @param maxSize 最大堆栈容量 小于<code>1</code>表示不限容量
	 * @return 阻塞堆栈
	 */
	public static <T> BlockingStack<T> wrap(Stack<T> stack, int maxSize) {
		Assertor.F.notNull(stack, "The target stack is must not be null!");
		if (stack instanceof BlockingStack) return BlockingStack.class.cast(stack);
		return new BlockingStackWrapper(stack, maxSize);
	}

	private final Stack<E> stack;

	private final ThreadLock lock;
	private final Condition notFull;
	private final Condition notEmpty;
	private final int maxSize;

	/**
	 * 构造函数
	 * @param stack 目标堆栈
	 * @param maxSize 最大堆栈容量
	 */
	protected BlockingStackWrapper(Stack<E> stack, int maxSize) {
		this.stack = stack;
		this.maxSize = maxSize < 1 ? Integer.MAX_VALUE : maxSize;
		this.lock = new ThreadLock();
		this.notFull = lock.newCondition();
		this.notEmpty = lock.newCondition();
	}

	public void clear() {
		lock.writeLock();
		try {
			stack.clear();
			notFull.signalAll();
		} finally {
			lock.unLock();
		}
	}

	public boolean contains(Object o) {
		lock.readLock();
		try {
			return stack.contains(o);
		} finally {
			lock.unLock();
		}
	}

	public boolean containsAll(Collection<?> c) {
		lock.readLock();
		try {
			return stack.containsAll(c);
		} finally {
			lock.unLock();
		}
	}

	public int drainTo(Collection<? super E> c) {
		return doDrainTo(c, 0);
	}

	public int drainTo(Collection<? super E> c, int maxElements) {
		if (maxElements < 1) return 0;
		return doDrainTo(c, maxElements);
	}

	public Iterator<E> iterator() {
		lock.readLock();
		try {
			return new BlockingStackIterator(stack.iterator());
		} finally {
			lock.unLock();
		}
	}

	public E peek() {
		lock.readLock();
		try {
			return stack.peek();
		} finally {
			lock.unLock();
		}
	}

	public E pop() {
		lock.writeLock();
		try {
			int size = stack.size();
			E o = stack.pop();
			if (size > stack.size()) notFull.signal();
			return o;
		} finally {
			lock.unLock();
		}
	}

	public E pop(long timeout, TimeUnit unit) throws InterruptedException {
		lock.writeLockInterruptibly();
		try {
			long nanos = unit.toNanos(timeout);
			for (;;) {
				int size = stack.size();
				if (size != 0) {
					E x = stack.pop();
					if (size > stack.size()) {
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

	public boolean push(E o) {
		lock.writeLock();
		try {
			if (maxSize == stack.size()) {
				return false;
			} else {
				boolean res = stack.push(o);
				if (res) notEmpty.signal();
				return res;
			}
		} finally {
			lock.unLock();
		}
	}

	public boolean push(E o, long timeout, TimeUnit unit) throws InterruptedException {
		lock.writeLockInterruptibly();
		try {
			long nanos = unit.toNanos(timeout);
			for (;;) {
				if (maxSize > stack.size()) {
					if (stack.push(o)) {
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

	public void put(E o) throws InterruptedException {
		lock.writeLockInterruptibly();
		try {
			for (;;) {
				while (maxSize == stack.size())
					notFull.await();

				if (stack.push(o)) {
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
		lock.writeLock();
		try {
			boolean res = stack.remove(o);
			if (res) notFull.signal();
			return res;
		} finally {
			lock.unLock();
		}
	}

	public boolean removeAll(Collection<?> c) {
		lock.writeLock();
		try {
			boolean res = stack.removeAll(c);
			if (res) notFull.signalAll();
			return res;
		} finally {
			lock.unLock();
		}
	}

	public boolean retainAll(Collection<?> c) {
		lock.writeLock();
		try {
			boolean res = stack.retainAll(c);
			if (res) notFull.signalAll();
			return res;
		} finally {
			lock.unLock();
		}
	}

	public int size() {
		lock.readLock();
		try {
			return stack.size();
		} finally {
			lock.unLock();
		}
	}

	public Iterator<E> stackIterator() {
		lock.readLock();
		try {
			return new BlockingStackIterator(stack.stackIterator());
		} finally {
			lock.unLock();
		}
	}

	public E take() throws InterruptedException {
		lock.writeLockInterruptibly();
		try {
			for (;;) {
				int size = 0;
				while ((size = stack.size()) == 0)
					notEmpty.await();
				E x = stack.pop();
				if (size > stack.size()) {
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
		lock.readLock();
		try {
			return stack.toArray();
		} finally {
			lock.unLock();
		}
	}

	public <T> T[] toArray(T[] a) {
		lock.readLock();
		try {
			return stack.toArray(a);
		} finally {
			lock.unLock();
		}
	}

	public String toString() {
		lock.readLock();
		try {
			return stack.toString();
		} finally {
			lock.unLock();
		}
	}

	/**
	 * 执行元素迁移
	 * @param c 目标集合体
	 * @param maxElements 最大迁移数量
	 * @return 迁移数量
	 */
	private int doDrainTo(Collection<? super E> c, int maxElements) {
		Assertor.F.isTrue(c != null && c != this, "This argument of target collection must not be null or which is stack itself!");
		int n = 0;
		if (maxElements < 1) maxElements = -1;
		E e = null;
		lock.writeLock();
		try {
			int size = 0;
			for (int old = stack.size(); n != maxElements; old = size) {
				e = stack.pop();
				size = stack.size();
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
	 * 阻塞堆栈迭代器
	 * @author Demon 2011-10-18
	 */
	protected class BlockingStackIterator implements Iterator<E> {

		private final Iterator<E> target;

		/**
		 * 构造函数
		 * @param it 目标迭代器
		 */
		protected BlockingStackIterator(Iterator<E> it) {
			this.target = it;
		}

		public boolean hasNext() {
			lock.readLock();
			try {
				return target.hasNext();
			} finally {
				lock.unLock();
			}
		}

		public E next() {
			lock.readLock();
			try {
				return target.next();
			} finally {
				lock.unLock();
			}
		}

		public void remove() {
			lock.writeLock();
			try {
				target.remove();
				notFull.signal();
			} finally {
				lock.unLock();
			}
		}
	}
}

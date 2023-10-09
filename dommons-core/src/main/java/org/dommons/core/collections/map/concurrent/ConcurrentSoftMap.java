/*
 * @(#)ConcurrentSoftMap.java     2018-10-29
 */
package org.dommons.core.collections.map.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.dommons.core.collections.map.ref.SoftHashMap;

/**
 * 线程安全软引用映射表
 * @author demon 2018-10-29
 */
public class ConcurrentSoftMap<K, V> extends ConcurrentMapWrapper<K, V> {

	private static final long serialVersionUID = -2304594653282810324L;

	private final ThreadLocal<Lock> local;
	private final Lock expungeLock;
	private final Lock readLock;

	public ConcurrentSoftMap() {
		super(new LockSoftMap());
		this.local = new ThreadLocal();
		this.expungeLock = new ExpungeLock();
		this.readLock = new ProxyReadLock(lock.readLock());
		((LockSoftMap) tar()).setParent(this);
	}

	@Override
	protected Lock readLock() {
		return this.readLock;
	}

	class ExpungeLock implements Lock {

		@Override
		public void lock() {
			Lock read = local.get();
			if (read != null) {
				read.unlock();
				writeLock().lock();
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			lock();
		}

		@Override
		public Condition newCondition() {
			return null;
		}

		@Override
		public boolean tryLock() {
			return false;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return false;
		}

		@Override
		public void unlock() {
			Lock read = local.get();
			if (read != null) {
				read.lock();
				writeLock().unlock();
			}
		}
	}

	static class LockSoftMap<K, V> extends SoftHashMap<K, V> {

		private ConcurrentSoftMap parent;

		@Override
		protected Lock expungeLock() {
			return parent != null ? parent.expungeLock : null;
		}

		void setParent(ConcurrentSoftMap parent) {
			this.parent = parent;
		}
	}

	class ProxyReadLock implements Lock {

		private final Lock lock;

		protected ProxyReadLock(Lock lock) {
			this.lock = lock;
		}

		@Override
		public void lock() {
			lock.lock();
			bindLock();
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			lock.lockInterruptibly();
			bindLock();
		}

		@Override
		public Condition newCondition() {
			return lock.newCondition();
		}

		@Override
		public boolean tryLock() {
			boolean b = lock.tryLock();
			if (b) bindLock();
			return b;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			boolean b = lock.tryLock(time, unit);
			if (b) bindLock();
			return b;
		}

		@Override
		public void unlock() {
			lock.unlock();
			local.remove();
		}

		protected void bindLock() {
			local.set(lock);
		}
	}
}

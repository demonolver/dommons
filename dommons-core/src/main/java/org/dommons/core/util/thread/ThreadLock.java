/*
 * @(#)ThreadLock.java     2011-10-18
 */
package org.dommons.core.util.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dommons.core.collections.stack.ArrayStack;
import org.dommons.core.collections.stack.Stack;

/**
 * 线程锁 与当前线程绑定, 加锁和解锁的处理必须在同一个线程内
 * @author Demon 2011-10-18
 */
public class ThreadLock {

	/** 线程信息 */
	private static final ThreadLocal<Stack<Lock>> local = new ThreadLocal();

	/**
	 * 读取锁定中，同一线程无法进行写入锁定
	 * 写入锁定中，同一线程可以继续进行读取锁定和写入锁定
	 */
	private final ReadWriteLock locks; // 锁

	/**
	 * 构造函数
	 */
	public ThreadLock() {
		this.locks = new ReentrantReadWriteLock();
	}

	/**
	 * 新建等待状态
	 * @return 等待状态
	 */
	public Condition newCondition() {
		return locks.writeLock().newCondition();
	}

	/**
	 * 读取锁定
	 */
	public final void readLock() {
		Lock lock = locks.readLock();
		lock.lock();
		put(lock);
	}

	/**
	 * 可中断的读取锁定
	 * @throws InterruptedException
	 */
	public final void readLockInterruptibly() throws InterruptedException {
		Lock lock = locks.readLock();
		lock.lockInterruptibly();
		put(lock);
	}

	/**
	 * 试锁定读取状态
	 * @return 是否锁定成功
	 */
	public boolean tryReadLock() {
		Lock lock = locks.readLock();
		boolean res = lock.tryLock();
		if (res) put(lock);
		return res;
	}

	/**
	 * 试锁定读取状态
	 * @param time 尝试时长
	 * @param unit 时间单位
	 * @return 是否锁定成功
	 * @throws InterruptedException
	 */
	public boolean tryReadLock(long time, TimeUnit unit) throws InterruptedException {
		Lock lock = locks.readLock();
		boolean res = lock.tryLock(time, unit);
		if (res) put(lock);
		return res;
	}

	/**
	 * 试锁定写入状态
	 * @return 是否锁定成功
	 */
	public boolean tryWriteLock() {
		Lock lock = locks.writeLock();
		boolean res = lock.tryLock();
		if (res) put(lock);
		return res;
	}

	/**
	 * 试锁定写入状态
	 * @param time 尝试时长
	 * @param unit 时间单位
	 * @return 是否锁定成功
	 * @throws InterruptedException
	 */
	public boolean tryWriteLock(long time, TimeUnit unit) throws InterruptedException {
		Lock lock = locks.writeLock();
		boolean res = lock.tryLock(time, unit);
		if (res) put(lock);
		return res;
	}

	/**
	 * 解除锁定
	 */
	public final void unLock() {
		Lock lock = get();
		if (lock != null) lock.unlock();
	}

	/**
	 * 写入锁定
	 */
	public final void writeLock() {
		Lock lock = locks.writeLock();
		lock.lock();
		put(lock);
	}

	/**
	 * 可中断的写入锁
	 * @throws InterruptedException
	 */
	public final void writeLockInterruptibly() throws InterruptedException {
		Lock lock = locks.writeLock();
		lock.lockInterruptibly();
		put(lock);
	}

	/**
	 * 获取锁
	 * @return 锁
	 */
	private Lock get() {
		Stack<Lock> stack = local.get();
		if (stack == null) return null;
		try {
			return stack.pop();
		} finally {
			if (stack.isEmpty()) local.remove();
		}
	}

	/**
	 * 加入锁
	 * @param lock 锁
	 */
	private void put(Lock lock) {
		Stack<Lock> stack = local.get();
		if (stack == null) {
			stack = new ArrayStack(5);
			local.set(stack);
		}
		stack.push(lock);
	}
}

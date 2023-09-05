/*
 * @(#)ThreadTask.java     2016-10-17
 */
package org.dommons.core.util.thread;

import java.lang.reflect.Field;
import java.util.concurrent.Future;

import org.dommons.core.util.Arrayard;

/**
 * 线程任务
 * @author demon 2016-10-17
 */
public class ThreadTask extends ThreadLocals implements Runnable {

	/**
	 * 复制上下文
	 * @param tc 上下文
	 * @return 新上下文
	 */
	static Object clone(Object tc) {
		return ThreadLocalMapCloner.clone(tc);
	}

	protected final Runnable r;
	protected final Object tc;

	private Object cc;

	public ThreadTask(Runnable r) {
		this.r = r;
		this.tc = currentParent();
	}

	public boolean equals(Object obj) {
		if (obj instanceof ThreadTask) return Arrayard.equals(r, ((ThreadTask) obj).r);
		return false;
	}

	public int hashCode() {
		return r.hashCode();
	}

	public void run() {
		if (r instanceof Future && ((Future) r).isCancelled()) return;
		if (tc != null) settingThread(tc);
		try {
			r.run();
		} finally {
			if (tc != null) cleanThread();
		}
	}

	public String toString() {
		return r.toString();
	}

	/**
	 * 清除线程上下文件
	 */
	protected void cleanThread() {
		Field f = field();
		if (f == null) return;
		try {
			f.set(Thread.currentThread(), cc);
		} catch (Throwable t) { // ignored
		}
	}

	/**
	 * 获取父线程上下文
	 * @return 线程上下文
	 */
	protected Object currentParent() {
		Field f = field();
		if (f == null) return null;
		try {
			return clone(f.get(Thread.currentThread()));
		} catch (Throwable t) {
			e(t);
			return null;
		}
	}

	/**
	 * 复制线程上下文
	 * @param tc 线程上下文
	 */
	protected void settingThread(Object tc) {
		Field f = field();
		if (f == null) return;
		try {
			cc = f.get(Thread.currentThread());
			f.set(Thread.currentThread(), tc);
		} catch (Throwable t) { // ignored
			e(t);
		}
	}
}

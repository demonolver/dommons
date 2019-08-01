/*
 * @(#)ThreadsExecutor.java     2016-10-17
 */
package org.dommons.core.concurrent;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.dommons.core.string.Stringure;

/**
 * 线程池执行器
 * @author demon 2016-10-17
 */
public class ThreadsExecutor extends AbsThreadsExecutor {

	/**
	 * 关闭线程池
	 * @param es 线程池
	 */
	public static void shutdown(ExecutorService es) {
		if (es == null) return;
		es.shutdown();
		for (;;) {
			try {
				if (es.awaitTermination(500, TimeUnit.MILLISECONDS)) break;
			} catch (InterruptedException e) { // ignored
			}
		}
	}

	public final int size;
	private final int min;

	public ThreadsExecutor() {
		this(Runtime.getRuntime().availableProcessors() + 1);
	}

	public ThreadsExecutor(int size) {
		this(size, (ThreadFactory) null);
	}

	public ThreadsExecutor(int size, Queue queue, String name) {
		this(size, Stringure.isEmpty(name) ? null : new NamedThreadFactory(name), queue);
	}

	public ThreadsExecutor(int size, String name) {
		this(size, null, name);
	}

	public ThreadsExecutor(int size, ThreadFactory threadFactory) {
		this(size, threadFactory, null);
	}

	public ThreadsExecutor(int size, ThreadFactory threadFactory, Queue queue) {
		super(threadFactory, queue);
		this.size = Math.max(1, size);
		int as = Runtime.getRuntime().availableProcessors();
		this.min = this.size > as ? as : (this.size / 2);
	}

	protected int maxSize() {
		return size;
	}

	@Override
	protected int minSize() {
		return min;
	}
}

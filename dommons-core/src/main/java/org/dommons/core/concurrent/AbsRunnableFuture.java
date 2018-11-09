/*
 * @(#)AbsRunnableFuture.java     2017-09-25
 */
package org.dommons.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 抽象执行状态包装
 * @author demon 2017-09-25
 */
public abstract class AbsRunnableFuture<V> implements RunnableFuture<V> {

	protected final RunnableFuture<? extends V> f;

	protected AbsRunnableFuture(RunnableFuture<? extends V> f) {
		this.f = f;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return f.cancel(mayInterruptIfRunning);
	}

	public V get() throws InterruptedException, ExecutionException {
		return f.get();
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return f.get(timeout, unit);
	}

	public boolean isCancelled() {
		return f.isCancelled();
	}

	public boolean isDone() {
		return f.isDone();
	}

	public void run() {
		f.run();
	}
}

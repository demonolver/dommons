/*
 * @(#)FutureRunner.java     2016-11-04
 */
package org.dommons.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 可监视异步任务
 * @author demon 2016-11-04
 */
public class FutureRunner<V> extends FutureTask<V> {

	private boolean s;

	public FutureRunner(Callable<V> callable) {
		super(callable);
		s = false;
	}

	public FutureRunner(Runnable runnable, V result) {
		this(Executors.callable(runnable, result));
	}

	public V get() throws InterruptedException, ExecutionException {
		run();
		return super.get();
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		run();
		return super.get(timeout, unit);
	}

	public void run() {
		synchronized (this) {
			if (s) return;
			s = true;
		}
		if (isCancelled()) return;
		super.run();
	}
}

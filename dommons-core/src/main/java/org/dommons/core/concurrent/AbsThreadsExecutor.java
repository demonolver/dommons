/*
 * @(#)AbsThreadsExecutor.java     2017-10-10
 */
package org.dommons.core.concurrent;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dommons.core.Silewarner;
import org.dommons.core.util.thread.ThreadLocals;
import org.omg.CORBA.portable.UnknownException;

/**
 * 抽象线程池执行器
 * @author demon 2017-10-10
 */
public abstract class AbsThreadsExecutor extends AbstractExecutorService {

	protected static final int RUNNING = 0;
	protected static final int SHUTDOWN = 1;
	protected static final int STOP = 2;
	protected static final int TERMINATED = 3;

	protected final ThreadFactory factory;
	protected final Queue<Runnable> queue;

	private final ReentrantLock mainLock;
	private final Condition termination;

	volatile int runState;
	volatile int poolSize;
	Collection<Worker> workers;
	Collection<Thread> threads;

	protected Reference<ThreadsMonitor> ref;

	protected AbsThreadsExecutor(ThreadFactory threadFactory, Queue<Runnable> queue) {
		super();
		this.factory = threadFactory != null ? threadFactory : Executors.defaultThreadFactory();
		this.queue = queue == null ? new LinkedList() : queue;
		this.workers = new HashSet();
		this.threads = new HashSet();
		this.runState = RUNNING;
		this.mainLock = new ReentrantLock();
		this.termination = mainLock.newCondition();
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		final Lock mainLock = this.mainLock;
		mainLock.lock();
		try {
			for (;;) {
				if (runState == TERMINATED) return true;
				if (nanos <= 0) return false;
				nanos = termination.awaitNanos(nanos);
			}
		} finally {
			mainLock.unlock();
		}
	}

	public void execute(Runnable command) {
		if (command == null) return;
		synchronized (queue) {
			run: if (runState == RUNNING) {
				Runnable r = command;
				if (now(r) && addWorker(r));
				else if (queue.offer(r)) queue.notify();
				else break run;
				command = null;
			}
		}
		if (command != null) reject(command);
	}

	public boolean isShutdown() {
		return runState != RUNNING;
	}

	public boolean isTerminated() {
		return runState == TERMINATED;
	}

	/**
	 * 获取线程池监视器
	 * @return 线程池监视器
	 */
	public ThreadsMonitor monitor() {
		ThreadsMonitor tm = ref == null ? null : ref.get();
		if (tm == null) ref = new SoftReference(tm = new Monitor());
		return tm;
	}

	public void shutdown() {
		final Lock lock = this.mainLock;
		lock.lock();
		try {
			int state = runState;
			if (state < SHUTDOWN) runState = SHUTDOWN;
			tryTerminate();
		} finally {
			lock.unlock();
		}
	}

	public List<Runnable> shutdownNow() {
		throw new UnsupportedOperationException();
	}

	public <T> Future<T> submit(Callable<T> task) {
		if (task == null) return null;
		RunnableFuture<T> rf = newTaskFor(task);
		execute(rf);
		return future(null, rf);
	}

	public Future<?> submit(Runnable task) {
		if (task == null) return null;
		RunnableFuture<Object> rf = newTaskFor(task, null);
		execute(rf);
		return future(null, rf);
	}

	public <T> Future<T> submit(Runnable task, T result) {
		if (task == null) return null;
		RunnableFuture<T> rf = newTaskFor(task, result);
		execute(rf);
		return future(null, rf);
	}

	/**
	 * 放弃任务执行
	 * @param r 任务
	 */
	protected void abandon(Runnable r) {}

	/**
	 * 后置执行
	 * @param r 任务项
	 * @param t 执行异常
	 */
	protected void afterExecute(Runnable r, Throwable t) {
		if (r != null && r instanceof RunnableTask) ((RunnableTask) r).afterExecute(t);
	}

	/**
	 * 前置执行
	 * @param t 执行线程
	 * @param r 任务项
	 */
	protected void beforeExecute(Thread t, Runnable r) {
		if (r != null && r instanceof RunnableTask) ((RunnableTask) r).beforeExecute();
	}

	/**
	 * 是否可合并执行
	 * @param r 任务项
	 * @return 是、否
	 */
	protected boolean forkJoinable(Runnable r) {
		return now(r);
	}

	/**
	 * 转换执行状态
	 * @param rf 原执行状态
	 * @return 线程池执行状态
	 */
	protected <T> RunnableFuture<T> future(Runnable task, RunnableFuture<T> rf) {
		return new Futuren(task, rf);
	}

	/**
	 * 插入任务
	 * @param r 任务
	 */
	protected void insert(Runnable r) {
		if (r == null) return;
		synchronized (queue) {
			if (queue instanceof List) ((List) queue).add(0, r);
			else queue.offer(r);
		}
	}

	/**
	 * 获取最大线程数
	 * @return 最大线程数
	 */
	protected abstract int maxSize();

	/**
	 * 获取最小保有线程数
	 * @return 最小保有线程数
	 */
	protected int minSize() {
		return 1;
	}

	/**
	 * 获取当前线程池大小
	 * @return 线程池大小
	 */
	protected int poolSize() {
		return poolSize;
	}

	/**
	 * 执行拒绝
	 * @param command 任务项
	 */
	protected void reject(Runnable command) {
		abandon(command);
		throw new RejectedExecutionException();
	}

	/**
	 * 执行异步任务
	 * @param task 任务
	 * @param thread 线程
	 */
	protected void run(Runnable task, Thread thread) {
		Future f = null;
		boolean c = false;
		if (task instanceof Future) c = (f = (Future) task).isCancelled();
		Throwable t = null;
		try {
			beforeExecute(thread, task);
			try {
				if (!c) task.run();
			} catch (Throwable ex) {
				t = ex;
			} finally {
				f = null;
				afterExecute(task, t);
			}
		} finally {
			if (f != null) f.cancel(false);
		}
		if (t != null) Silewarner.error(task.getClass(), thread.getName(), t);
	}

	/**
	 * 判断任务项是否可立即执行
	 * @param r 任务项
	 * @return 是、否
	 */
	protected boolean runAtNow(Runnable r) {
		return true;
	}

	/**
	 * 执行状态监听任务
	 * @param task 任务
	 * @param thread 执行线程
	 */
	protected void runFuture(Runnable task, Thread thread) {
		Object local = Local.get(); // 取出当前线程上下文，清空
		Local.set(null);
		try {
			run(task, thread);
		} finally {
			Local.set(local); // 回写原本线程上下文
		}
	}

	/**
	 * 执行工作者任务
	 * @param task 任务
	 * @param thread 线程
	 */
	protected void runWorker(Runnable task, Thread thread) {
		run(task, thread);
	}

	/**
	 * 响应执行器关闭
	 */
	protected void terminated() {}

	/**
	 * 获取待处理任务数
	 * @return 待处理数
	 */
	protected int waitingCount() {
		return queue.size();
	}

	/**
	 * 获取工作线程数
	 * @return 线程数
	 */
	protected int workingCount() {
		final Lock lock = this.mainLock;
		lock.lock();
		try {
			int n = 0;
			for (Worker w : workers) {
				if (w.isWorking()) ++n;
			}
			return n;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 添加线程
	 * @param firstTask 首个任务
	 * @return 新线程
	 */
	Thread addThread(Runnable firstTask) {
		Worker w = new Worker(firstTask);
		Thread t = factory.newThread(w);
		boolean workerStarted = false;
		if (t != null) {
			if (t.isAlive()) return null;
			w.thread = t;
			threads.add(t);
			workers.add(w);
			++poolSize;
			try {
				t.start();
				workerStarted = true;
			} finally {
				if (!workerStarted) workers.remove(w);
			}
		}
		return t;
	}

	/**
	 * 尝试添加工作者
	 * @param r 任务项
	 * @return 是否添加
	 */
	boolean addWorker(Runnable r) {
		Thread t = null;
		final Lock lock = this.mainLock;
		lock.lock();
		try {
			int s = maxSize();
			if ((poolSize < s || poolSize < 1) && runState == RUNNING) t = addThread(r);
		} finally {
			lock.unlock();
		}
		return t != null;
	}

	/**
	 * 获取任务项
	 * @return 任务项
	 */
	Runnable getTask() {
		for (;;) {
			int state = runState;
			if (state > SHUTDOWN) return null;
			synchronized (queue) {
				Iterator<Runnable> it = queue.iterator();
				for (; it.hasNext();) {
					Runnable r = it.next();
					if (r != null && now(r)) {
						it.remove();
						if (it.hasNext()) queue.notify();
						return r;
					}
				}
			}
			if (state == RUNNING) {
				try {
					long s = System.currentTimeMillis(), t = 30000l;
					synchronized (queue) {
						queue.wait(t, 0);
					}
					if (System.currentTimeMillis() - s < t && state == runState) continue;
				} catch (InterruptedException e) {
				}
			}
			if (workerCanExit()) return null;
		}
	}

	/**
	 * 结束工作者
	 * @param w 工作者
	 */
	void workerDone(Worker w) {
		final Lock mainLock = this.mainLock;
		mainLock.lock();
		try {
			workers.remove(w);
			threads.remove(w.thread);
			if (--poolSize == 0) tryTerminate();
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * 是否立即执行
	 * @param r 任务项
	 * @return 是、否
	 */
	private boolean now(Runnable r) {
		return runAtNow(r);
	}

	/**
	 * 尝试关闭执行器
	 */
	private void tryTerminate() {
		if (poolSize == 0) {
			int state = runState;
			if (state < STOP && !queue.isEmpty()) {
				state = RUNNING; // 新增执行器完成剩除任务
				addThread(null);
			}
			if (state == STOP || state == SHUTDOWN) {
				runState = TERMINATED;
				termination.signalAll();
				terminated();
			}
		} else {
			synchronized (queue) {
				queue.notifyAll();
			}
		}
	}

	/**
	 * 工作者是否可退出
	 * @return 是、否
	 */
	private boolean workerCanExit() {
		final Lock mainLock = this.mainLock;
		mainLock.lock();
		boolean canExit;
		try {
			int size = queue.size();
			if (runState == RUNNING) size = Math.max(size, minSize());
			canExit = runState >= STOP || poolSize > size;
		} finally {
			mainLock.unlock();
		}
		return canExit;
	}

	/**
	 * 线程异步任务执行状态
	 * @author demon 2017-09-25
	 */
	protected final class Futuren<V> extends AbsRunnableFuture<V> {

		protected final Runnable task;
		protected boolean runnable;

		protected Futuren(Runnable task, RunnableFuture<V> f) {
			super(f);
			this.task = task == null ? f : task;
			this.runnable = true;
		}

		public V get() throws InterruptedException, ExecutionException {
			try {
				return get(0);
			} catch (TimeoutException e) {
				throw new UnknownException(e);
			}
		}

		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			if (unit == null) throw new NullPointerException();
			return get(unit.toMillis(timeout));
		}

		/**
		 * 获取执行结果
		 * @param timeout 超时时长
		 * @return 执行结果
		 * @throws InterruptedException
		 * @throws ExecutionException
		 * @throws TimeoutException
		 */
		protected V get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
			long s = System.currentTimeMillis(), x = Long.MAX_VALUE;
			done: if (!f.isDone() && runnable) {
				Thread t = Thread.currentThread();
				if (!threads.contains(t)) break done; // 非当前线程池执行，不做尝试
				for (;;) {
					run: if (!f.isDone()) { // 如果目标任务可执行
						Boolean r = runnable();
						if (r != null) {
							if (r) run(task, t);
							else break run;
						} else {
							runnable = false;
						}
						break done;
					}
					if (timeout > 0) x = timeout - System.currentTimeMillis() + s;
					try {
						return f.get(Math.min(500, x), TimeUnit.MILLISECONDS);
					} catch (TimeoutException e) { // ignored
						if (timeout > 0 && x <= 500) throw e;
					}
				}
			}
			if (timeout < 1) {
				return f.get();
			} else {
				x = timeout - System.currentTimeMillis() + s;
				return f.get(Math.max(1, x), TimeUnit.MILLISECONDS);
			}
		}

		/**
		 * 执行异步任务
		 * @param task 任务
		 * @param thread 线程
		 */
		protected void run(Runnable task, Thread thread) {
			runFuture(task, thread);
		}

		/**
		 * 检查任务是否可执行
		 * @return 结果 <code>true</code> 立即执行, <code>false</code> 暂不执行, <code>null</code> 无需执行
		 */
		Boolean runnable() {
			synchronized (queue) {
				for (Iterator<Runnable> it = queue.iterator(); it.hasNext();) {
					Runnable t = it.next();
					if (t == task) {
						if (forkJoinable(task)) {
							it.remove();
							return true;
						} else {
							return false;
						}
					}
				}
			}
			return null;
		}
	}

	/**
	 * 线程上下文访问器
	 * @author demon 2019-04-15
	 */
	protected static final class Local extends ThreadLocals {

		/**
		 * 获取线程上下文内容
		 * @return 上下文内容
		 */
		public static Object get() {
			try {
				return field().get(Thread.currentThread());
			} catch (Throwable t) {
				e(t);
				return null;
			}
		}

		/**
		 * 设置线程上下文内容
		 * @param local 上下文内容
		 */
		public static void set(Object local) {
			try {
				field().set(Thread.currentThread(), local);
			} catch (Throwable t) {
				e(t);
			}
		}
	}

	/**
	 * 线程池
	 * @author demon 2017-10-10
	 */
	protected final class Monitor implements ThreadsMonitor {
		public int getMaxSize() {
			return maxSize();
		}

		public int getPoolSize() {
			return poolSize();
		}

		public int getWaitingCount() {
			return waitingCount();
		}

		public int getWorkingCount() {
			return workingCount();
		}
	}

	/**
	 * 线程执行工作者
	 * @author demon 2016-10-18
	 */
	protected final class Worker implements Runnable {

		final long s;
		Runnable cTask;
		Thread thread;

		private final ReentrantLock runLock = new ReentrantLock();

		public Worker(Runnable task) {
			this.cTask = task;
			this.s = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(8);
		}

		public void run() {
			try {
				Runnable task = cTask;
				cTask = null;
				for (; runState == RUNNING;) {
					while (task != null || (task = getTask()) != null) {
						runTask(task);
						task = null;
					}
					if (System.currentTimeMillis() >= s) break;
				}
			} finally {
				workerDone(this);
			}
		}

		/**
		 * 工作者是否工作中
		 * @return 是、否
		 */
		boolean isWorking() {
			return runLock.isLocked();
		}

		/**
		 * 执行任务
		 * @param task 任务项
		 */
		private void runTask(Runnable task) {
			final Lock lock = this.runLock;
			lock.lock();
			try {
				runWorker(task, thread);
			} finally {
				lock.unlock();
			}
		}
	}
}

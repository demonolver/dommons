/*
 * @(#)AbstractCrontabs.java     2013-10-14
 */
package org.dommons.crontab.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dommons.core.Environments;
import org.dommons.core.Silewarner;
import org.dommons.core.collections.queue.BlockingTreeQueue;
import org.dommons.core.concurrent.ThreadsExecutor;
import org.dommons.crontab.Crontab;
import org.dommons.crontab.Crontabs;
import org.dommons.crontab.setting.CrontabType;
import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * 抽象定时服务
 * @author Demon 2013-10-14
 */
abstract class AbstractCrontabs implements Crontabs {

	static final Logger logger = LoggerFactory.getInstance().getLogger(Crontabs.class);

	/**
	 * 创建线程
	 * @param threadFactory 线程工厂
	 * @param r 异常任务
	 * @return 线程
	 */
	protected static Thread thread(ThreadFactory threadFactory, Runnable r) {
		Thread t = threadFactory != null ? threadFactory.newThread(r) : new Thread(r);
		t.setDaemon(true);
		return t;
	}

	/**
	 * 生成默认执行线程池
	 * @param threadFactory 线程工厂
	 * @return 线程池
	 */
	static ExecutorService defaultExecutor(ThreadFactory threadFactory) {
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1, threadFactory);
	}

	protected final ExecutorService executorService;
	protected final TimeZone tz;
	protected final Runnable closer;
	protected final Runnable dispatcher;
	protected final ThreadFactory threadFactory;
	protected final Lock lock;
	protected final Condition cond;

	private volatile Thread over;
	private volatile Thread thread;

	private final Map<String, CrontabTask> tasks;
	private final BlockingQueue<CrontabTask> queue;

	/**
	 * 构造函数
	 * @param executorService 异步
	 * @param threadFactory 线程工厂
	 * @param tz 时区
	 */
	public AbstractCrontabs(ExecutorService executorService, ThreadFactory threadFactory, TimeZone tz) {
		this.executorService = executorService != null ? executorService : defaultExecutor(threadFactory);
		this.tz = tz != null ? tz : Environments.defaultTimeZone();
		this.tasks = new HashMap();
		this.queue = new BlockingTreeQueue(null);
		this.threadFactory = threadFactory;
		this.lock = new ReentrantLock();
		this.cond = lock.newCondition();

		Runtime.getRuntime().addShutdownHook(over = thread(threadFactory, this.closer = new CrontabsCloser()));

		this.thread = thread(threadFactory, dispatcher = new CrontabsDispatcher());
		this.thread.start();
	}

	public void close() {
		try {
			if (over == null) return;
			lock.lock();
			Thread t = null;
			try {
				if (over == null) return;
				t = over;
				over = null;
				tasks.clear();
				queue.clear();
			} finally {
				lock.unlock();
			}

			wait(thread);
			if (t != null && !t.isAlive()) t.start();
		} catch (Throwable t) {
			Silewarner.warn(AbstractCrontabs.class, t);
		}
	}

	public boolean execute(String key) {
		if (over == null) throw closed();
		CrontabTask task = tasks.get(key);
		return task != null ? task.execute(false, executorService) : false;
	}

	public boolean register(Crontab task) {
		if (over == null || task == null) return false;
		long next = task.nextTime(0, tz);
		if (next < 1000) return false;
		CrontabTask ct = new CrontabTask(task, next);
		String key = ct.key;
		synchronized (tasks) {
			if (tasks.containsKey(key)) return false;
			tasks.put(key, ct);
			return offer(ct);
		}
	}

	public void resume() {
		if (thread != null) return;
		synchronized (this) {
			if (over == null) throw closed();
			if (thread != null) return;
			Thread t = thread(threadFactory, dispatcher);
			t.setDaemon(true);
			t.start();
			thread = t;
		}
	}

	public boolean resume(String key) {
		CrontabTask task = tasks.get(key);
		if (task != null && !task.suspend(false)) return task.register(true, false);
		return false;
	}

	public void suspend() {
		if (over == null || thread == null) return;
		Thread t = null;
		synchronized (this) {
			if (over == null || thread == null) return;
			t = thread;
			thread = null;
		}
		wait(t);
	}

	public boolean suspend(String key) {
		CrontabTask task = tasks.get(key);
		if (task != null && task.suspend(true)) {
			queue.remove(task);
			return true;
		}
		return false;
	}

	/**
	 * 已关闭异常
	 * @return 异常
	 */
	protected IllegalStateException closed() {
		return new IllegalStateException("The crontabs service is closed.");
	}

	/**
	 * 追加任务
	 * @param task 任务
	 * @return 是否成功
	 */
	protected boolean offer(CrontabTask task) {
		if (task != null && task.time >= 1000 && queue.offer(task)) {
			lock.lock();
			try {
				cond.signal();
			} finally {
				lock.unlock();
			}
		}
		return true;
	}

	/**
	 * 等待线程结束
	 * @param thread 线程
	 */
	protected void wait(Thread thread) {
		for (;;) {
			lock.lock();
			try {
				cond.signalAll();
			} finally {
				lock.unlock();
			}
			try {
				if (thread != null && thread.isAlive()) thread.join(500);
				else break;
			} catch (Throwable t) {
			}
		}
	}

	/**
	 * 定时服务关闭器
	 * @author Demon 2013-10-18
	 */
	protected class CrontabsCloser implements Runnable {
		public void run() {
			close();
			ThreadsExecutor.shutdown(executorService);
		}
	}

	/**
	 * 定时服务调度器
	 * @author Demon 2013-10-18
	 */
	protected class CrontabsDispatcher implements Runnable {
		public void run() {
			while (over != null && thread != null) {
				try {
					handle(queue.peek(), false);
				} catch (Throwable t) {
				}
			}
		}

		/**
		 * 等待任务
		 * @param time 时长
		 * @param unit 单位
		 */
		protected void await(long time, TimeUnit unit) {
			lock.lock();
			try {
				try {
					cond.await(time, unit);
				} catch (Throwable t) {
				}
			} finally {
				lock.unlock();
			}
		}

		/**
		 * 处理任务
		 * @param task 任务项
		 * @param removed 是否已移除
		 * @throws InterruptedException
		 */
		protected void handle(CrontabTask task, boolean removed) throws InterruptedException {
			if (over == null || thread == null) return;
			if (task == null) {
				await(5, TimeUnit.SECONDS);
			} else if (task != null) {
				long current = System.currentTimeMillis();
				if (task.time <= current || task.cancelled()) {
					if (removed && tasks.containsKey(task.key)) task.execute(true, executorService);
					else if (!removed) handle(queue.poll(5, TimeUnit.SECONDS), true);
				} else {
					if (removed) offer(task);
					else await(Math.min(task.time - current - 1, 2000), TimeUnit.MILLISECONDS);
				}
			}
		}
	}

	/**
	 * 定时任务
	 * @author Demon 2013-10-18
	 */
	protected class CrontabTask implements Comparable<CrontabTask>, Runnable, CrontabType {

		private final Crontab task;
		private final String key;

		private volatile boolean suspend;
		private volatile long time;
		private volatile int running;

		public CrontabTask(Crontab task, long time) {
			this.task = task;
			this.key = task.key();
			this.time = time;
		}

		public int compareTo(CrontabTask o) {
			if (o == null) return -1;
			else if (this.time < o.time) return -1;
			else if (this.time == o.time) return 0;
			else return 1;
		}

		/**
		 * 执行任务
		 * @param removed 是否已从调度队列中移除
		 * @param executorService 执行线程池
		 * @return 是否执行成功
		 */
		public boolean execute(boolean removed, ExecutorService executorService) {
			int type = task.type();
			switch (type) {
			case TIMING_EXECUTION:
				register(removed, true);
				executorService.execute(this);
				return true;
			case TIMING_CHECK:
				if (!register(removed, false)) return false;
				executorService.execute(this);
				return true;
			case BALANCED_LEISURE:
				if (running() || !remove(removed)) return false;
				executorService.execute(this);
				return true;
			default:
				return false;
			}
		}

		public void run() {
			if (suspend || cancelled()) return;

			synchronized (this) {
				running++;
			}
			try {
				task.run();
			} catch (Throwable t) {
				logger.warn(t, "crontabs service execute task ''{0}'' error.", key);
			} finally {
				synchronized (this) {
					running--;
				}
				if (task.type() == BALANCED_LEISURE) register(true, false);
			}
		}

		/**
		 * 是否已取消
		 * @return 是、否
		 */
		protected boolean cancelled() {
			if (task.isCancelled()) { // 已取消移除任务项
				lock.lock();
				try {
					tasks.remove(key);
				} finally {
					lock.unlock();
				}
			}
			return false;
		}

		/**
		 * 注册下次执行时间
		 * @param removed 是否已从队列移除
		 * @param force 是否强制注册
		 * @return 是否注册
		 */
		protected boolean register(boolean removed, boolean force) {
			if ((running() && !force) || !remove(removed)) return false;
			else if (suspend || cancelled()) return false;

			long next = 0;
			try {
				next = task.nextTime(System.currentTimeMillis(), tz);
				this.time = next;
				offer(this);
			} catch (Throwable t) {
				logger.warn(t, "crontabs service compute next time of task ''{0}'' error.", key);
			}
			return true;
		}

		/**
		 * 移除队列中现有任务
		 * @param removed 是否已移除
		 * @return 是否成功
		 */
		protected boolean remove(boolean removed) {
			if (!removed) queue.remove(this);
			return true;
		}

		/**
		 * 是否运行中
		 * @return 是、否
		 */
		protected boolean running() {
			return running > 0;
		}

		/**
		 * 修改挂起状态
		 * @param suspend 挂起状态
		 * @return 是否挂起
		 */
		protected boolean suspend(boolean suspend) {
			synchronized (this) {
				this.suspend = suspend;
			}
			return this.suspend;
		}
	}
}

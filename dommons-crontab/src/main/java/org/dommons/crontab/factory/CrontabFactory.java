/*
 * @(#)CrontabFactory.java     2013-10-14
 */
package org.dommons.crontab.factory;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.dommons.crontab.Crontab;
import org.dommons.crontab.Crontabs;

/**
 * 定时服务工厂
 * @author Demon 2013-10-14
 */
public class CrontabFactory {

	public final Crontabs crontabs;

	public CrontabFactory() {
		this(5);
	}

	/**
	 * 构造函数
	 * @param threads 线程数
	 */
	public CrontabFactory(int threads) {
		this(threads, null);
	}

	/**
	 * 构造函数
	 * @param threads 线程数
	 * @param threadFactory 线程工厂
	 */
	public CrontabFactory(int threads, ThreadFactory threadFactory, TimeZone tz) {
		this(threadFactory = threadFactory != null ? threadFactory : Executors.defaultThreadFactory(),
				Executors.newFixedThreadPool(threads, threadFactory), tz);
	}

	/**
	 * 构造函数
	 * @param threads 线程数
	 * @param tz 时区
	 */
	public CrontabFactory(int threads, TimeZone tz) {
		this(threads, null, tz);
	}

	/**
	 * 构造函数
	 * @param threadFactory 线程工厂
	 * @param executorService 执行线程池
	 * @param tz 时区
	 */
	public CrontabFactory(ThreadFactory threadFactory, ExecutorService executorService, TimeZone tz) {
		this(new DefaultCrontabs(executorService, threadFactory, tz));
	}

	/**
	 * 构造函数
	 * @param crontabs 定时服务
	 */
	protected CrontabFactory(Crontabs crontabs) {
		if (crontabs == null) throw new NullPointerException();
		this.crontabs = crontabs;
	}

	/**
	 * 设置任务集
	 * @param tasks 任务集
	 */
	public void addTasks(Crontab... tasks) {
		if (tasks != null) setTasks(Arrays.asList(tasks));
	}

	/**
	 * 关闭定时服务
	 */
	public void close() {
		crontabs.close();
	}

	/**
	 * 获取定时服务
	 * @return 定时服务
	 * @deprecated {@link #crontabs()}
	 */
	public Crontabs corntabs() {
		return crontabs();
	}

	/**
	 * 获取定时服务
	 * @return 定时服务
	 */
	public Crontabs crontabs() {
		return crontabs;
	}

	/**
	 * 设置任务集
	 * @param tasks 任务集
	 */
	public void setTasks(Iterable<Crontab> tasks) {
		if (tasks == null) return;
		for (Crontab task : tasks) {
			crontabs.register(task);
		}
	}

	/**
	 * 默认定时服务
	 * @author Demon 2013-10-14
	 */
	protected static class DefaultCrontabs extends AbstractCrontabs {

		public DefaultCrontabs(ExecutorService executorService, ThreadFactory threadFactory, TimeZone tz) {
			super(executorService, threadFactory, tz);
		}
	}
}

/*
 * @(#)ThreadsMonitor.java     2017-10-10
 */
package org.dommons.core.concurrent;

/**
 * 线程池监视器
 * @author demon 2017-10-10
 */
public interface ThreadsMonitor {

	/**
	 * 获取最大线程数
	 * @return 最大线程数
	 */
	public int getMaxSize();

	/**
	 * 获取当前线程数
	 * @return 线程数
	 */
	public int getPoolSize();

	/**
	 * 获取待执行任务数
	 * @return 任务数
	 */
	public int getWaitingCount();

	/**
	 * 获取当前工作线程数
	 * @return 线程数
	 */
	public int getWorkingCount();
}

/*
 * @(#)Crontabs.java     2013-10-14
 */
package org.dommons.crontab;

/**
 * 定时服务
 * @author Demon 2013-10-14
 */
public interface Crontabs {

	/**
	 * 关闭服务
	 */
	public void close();

	/**
	 * 执行任务
	 * @param key 任务键值
	 * @return 是否执行
	 */
	public boolean execute(String key);

	/**
	 * 注册定时任务
	 * @param task 定时任务
	 * @return 是否成功
	 */
	public boolean register(Crontab task);

	/**
	 * 恢复服务
	 */
	public void resume();

	/**
	 * 恢复任务
	 * @param key 任务键值
	 * @return 是否恢复成功
	 */
	public boolean resume(String key);

	/**
	 * 挂起服务
	 */
	public void suspend();

	/**
	 * 挂起任务
	 * @param key 任务键值
	 * @return 是否挂起成功
	 */
	public boolean suspend(String key);
}

/*
 * @(#)Crontab.java     2013-10-14
 */
package org.dommons.crontab;

import java.util.TimeZone;

import org.dommons.crontab.setting.CrontabType;

/**
 * 定时任务
 * @author Demon 2013-10-14
 */
public interface Crontab extends Runnable, CrontabType {

	/**
	 * 取消任务
	 */
	public void cancel();

	/**
	 * 是否已取消
	 * @return 是、否
	 */
	public boolean isCancelled();

	/**
	 * 获取任务键值
	 * @return 任务键值
	 */
	public String key();

	/**
	 * 获取下一次时间点
	 * @param last 前一次时间点 <code>0</code> 表示未做执行
	 * @param tz 时区
	 * @return 时间点
	 * @see java.util.Date#getTime()
	 */
	public long nextTime(long last, TimeZone tz);

	/**
	 * 执行任务
	 */
	public void run();

	/**
	 * 获取定时类型
	 * @return 定时类型
	 * @see org.dommons.crontab.setting.CrontabType
	 */
	public int type();
}

/*
 * @(#)SimpleCrontab.java     2013-10-14
 */
package org.dommons.crontab.task;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.TimeZone;

import org.dommons.core.Environments;
import org.dommons.core.string.Stringure;
import org.dommons.crontab.Crontab;
import org.dommons.crontab.setting.Cronset;
import org.dommons.crontab.setting.CronsetFactory;
import org.dommons.crontab.setting.CrontabType;

/**
 * 简单定时任务
 * @author Demon 2013-10-14
 */
public class SimpleCrontab implements Crontab {

	private String key;
	private int type;

	private Runnable task;

	private Cronset setting;

	private volatile boolean cancelled;

	public SimpleCrontab() {
		this(Stringure.empty);
	}

	public SimpleCrontab(String key) {
		this(key, null);
	}

	public SimpleCrontab(String key, Runnable task) {
		this.key = Stringure.trim(key);
		this.task = task;
		this.type = TIMING_CHECK;
		this.cancelled = false;
	}

	public void cancel() {
		cancelled = true;
	}

	/**
	 * 获取定时时间设置
	 * @return 定时时间设置
	 */
	public Cronset getSetting() {
		return setting;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String key() {
		return key;
	}

	public long nextTime(long last, TimeZone tz) {
		long time = 0;
		if (tz == null) tz = Environments.defaultTimeZone();
		if (setting != null) time = setting.time(last, tz);
		return time;
	}

	public void run() {
		if (task != null) task.run();
	}

	/**
	 * 设置定时类型
	 * @param type 定时类型
	 * @see org.dommons.crontab.setting.CrontabType
	 */
	public void setCrontabType(String type) {
		try {
			Field f = CrontabType.class.getField(type);
			if (Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) this.type = f.getInt(null);
		} catch (Throwable t) {
		}
	}

	/**
	 * 设置定时时间表达式
	 * @param expression 表达式
	 */
	public void setExpression(String expression) {
		setSetting(CronsetFactory.parse(expression));
	}

	/**
	 * 设置任务键值
	 * @param key 任务键值
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 设置定时时间设置
	 * @param setting 定时时间设置
	 */
	public void setSetting(Cronset setting) {
		this.setting = setting;
	}

	/**
	 * 设置任务项
	 * @param task 任务项
	 */
	public void setTask(Runnable task) {
		this.task = task;
	}

	/**
	 * 设置定时类型
	 * @param type 定时类型
	 * @see org.dommons.crontab.setting.CrontabType
	 */
	public void setType(int type) {
		this.type = type;
	}

	public int type() {
		return type;
	}
}

/*
 * @(#)CrontabSetting.java     2013-10-15
 */
package org.dommons.crontab.setting.cron;

import java.util.Calendar;
import java.util.SortedSet;
import java.util.TimeZone;

import org.dommons.crontab.setting.cron.CronSettingFactory.CrontabParser;

/**
 * 类 linux crontab 时间设定
 * @author Demon 2013-10-15
 */
class CrontabSetting extends CronSetting {

	private static final long serialVersionUID = 7716879076840552435L;

	public CrontabSetting(String expression) {
		super(expression);
	}

	public long time(long last, TimeZone tz) {
		if (last < 1000) last = System.currentTimeMillis() - 60000;
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(last + 60000);
		cal.clear(Calendar.MILLISECOND);
		cal.clear(Calendar.SECOND);

		// 循环找一时间点
		for (;;) {
			if (cal.get(Calendar.YEAR) > MAX_YEAR) return 0;
			int[] val = new int[4]; // {min,hour,day,month}
			if (!nextMinute(cal, val, 0)) continue;
			if (!nextHour(cal, val, 1)) continue;
			if (!nextDay(cal, val)) continue;
			if (!nextMonth(cal, val, 3)) continue;
			break;
		}
		return cal.getTimeInMillis();
	}

	/**
	 * 推演下次日期
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected boolean nextDay(Calendar cal, int[] v) {
		if (!nextDayOfMonth(cal, v)) return false;
		if (!nextDayOfWeek(cal, v)) return false;
		return true;
	}

	/**
	 * 推演下次月-日
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected boolean nextDayOfMonth(Calendar cal, int[] v) {
		v[2] = cal.get(Calendar.DAY_OF_MONTH);
		int t = -1;

		SortedSet<Integer> st = daysOfMonth.tailSet(v[2]);
		if (st != null && !st.isEmpty()) {
			t = v[2];
			v[2] = st.first();
		} else {
			v[2] = daysOfMonth.first();
			cal.add(Calendar.MONTH, 1);
		}
		if (v[2] != t) return setter(cal, -1, -1, -1, v[2], 0, 0) == null;
		cal.set(Calendar.DAY_OF_MONTH, v[2]);
		return true;
	}

	/**
	 * 推演下次周-日
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected boolean nextDayOfWeek(Calendar cal, int[] v) {
		int o = cal.get(Calendar.DAY_OF_YEAR), dow = cal.get(Calendar.DAY_OF_WEEK) - 1;
		SortedSet<Integer> st = daysOfWeek.tailSet(dow);
		if (st != null && !st.isEmpty()) {
			dow = st.first();
		} else {
			dow = daysOfWeek.first();
			cal.add(Calendar.WEEK_OF_MONTH, 1);
		}
		setter(cal, Calendar.DAY_OF_WEEK, dow + 1);
		if (o != cal.get(Calendar.DAY_OF_YEAR)) return setter(cal, -1, -1, -1, 0, 0) == null;
		return true;
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		try {
			CrontabParser.build(this, expression);
		} catch (Exception ignore) {
		}
	}
}

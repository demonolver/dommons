/*
 * @(#)CrontabSetting.java     2013-10-15
 */
package org.dommons.crontab.setting.cron;

import java.util.Calendar;
import java.util.SortedSet;
import java.util.TimeZone;

import org.dommons.core.util.Randoms;
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
		if (last < 1000) last = System.currentTimeMillis() - 1000;
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(last + 1000);
		cal.clear(Calendar.MILLISECOND);

		// 循环找一时间点
		for (;;) {
			if (cal.get(Calendar.YEAR) > MAX_YEAR) return 0;
			int[] val = new int[5]; // {sec,min,hour,day,month}
			nextSecond(cal, val, last);
			if (!nextMinute(cal, val, 1)) continue;
			if (!nextHour(cal, val, 2)) continue;
			if (!nextDay(cal, val)) continue;
			if (!nextMonth(cal, val, 4)) continue;
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
		v[3] = cal.get(Calendar.DAY_OF_MONTH);
		int t = -1;

		SortedSet<Integer> st = daysOfMonth.tailSet(v[3]);
		if (st != null && !st.isEmpty()) {
			t = v[3];
			v[3] = st.first();
		} else {
			v[3] = daysOfMonth.first();
			cal.add(Calendar.MONTH, 1);
		}
		cal.set(Calendar.DAY_OF_MONTH, v[3]);
		if (v[3] != t) return setter(cal, -1, -1, -1, 0, 0, 0) == null;
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

	/**
	 * 推演下次秒值
	 * @param cal 时间
	 * @param v 时间值集
	 * @param last 初始值
	 * @return 是否未变更
	 */
	protected boolean nextSecond(Calendar cal, int[] v, long last) {
		if (seconds.isEmpty()) {
			if (cal.getTimeInMillis() - last < 60000) cal.add(Calendar.SECOND, 60 - cal.get(Calendar.SECOND));
			if (cal.get(Calendar.SECOND) == 0) cal.add(Calendar.SECOND, Randoms.randomInteger(6));
		} else {
			v[0] = cal.get(Calendar.SECOND);
			SortedSet<Integer> st = seconds.tailSet(v[0]);
			if (st != null && !st.isEmpty()) {
				v[0] = st.first();
			} else {
				v[0] = seconds.first();
				cal.add(Calendar.MINUTE, 1);
			}
			cal.set(Calendar.SECOND, v[0]);
		}
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

/*
 * @(#)CronSetting.java     2013-10-17
 */
package org.dommons.crontab.setting.cron;

import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dommons.crontab.setting.Cronset;

/**
 * cron 时间设置
 * @author Demon 2013-10-17
 */
abstract class CronSetting implements Cronset {

	private static final long serialVersionUID = 6173099849370871428L;

	protected static final int[] fields = { Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH,
			Calendar.YEAR };

	protected static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;

	/**
	 * 追加数值
	 * @param cal 时间
	 * @param type 字段类型
	 * @param amount 数值
	 * @return 时间
	 */
	protected static Calendar add(Calendar cal, int type, int amount) {
		if (cal == null) return cal;
		int field = fields[type], parent = -1, m = -1;
		if (type < fields.length - 1) parent = fields[type + 1];
		if (parent != -1) m = cal.get(parent);
		cal.add(field, amount);
		if (m > -1 && m != cal.get(parent)) {
			cal = setter(cal, field, 1);
			for (int i = 0; i < type; i++) {
				cal = setter(cal, fields[i], cal.getActualMinimum(fields[i]));
			}
		}
		return cal;
	}

	/**
	 * 设置时间字段小于 <code>0</code> 不做修改
	 * @param cal 时间
	 * @param vals 值集 {年,月,日,时,分,秒}
	 * @return 时间
	 */
	protected static Calendar setter(Calendar cal, int... vals) {
		if (cal == null) return cal;
		for (int i = 0; i < 6 && i < vals.length; i++) {
			if (vals[i] >= 0) cal = setter(cal, fields[5 - i], vals[i]);
		}
		return cal;
	}

	/**
	 * 设置时间字段值
	 * @param cal 时间
	 * @param field 字段
	 * @param val 值
	 * @return 时间
	 */
	protected static Calendar setter(Calendar cal, int field, int val) {
		if (cal == null) return cal;
		cal.set(field, val);
		if (field == Calendar.HOUR_OF_DAY && cal.get(field) != val && val != 24) cal.add(field, 1);
		return cal;
	}

	protected transient TreeSet<Integer> seconds;
	protected transient TreeSet<Integer> minutes;
	protected transient TreeSet<Integer> hours;
	protected transient TreeSet<Integer> daysOfMonth;
	protected transient TreeSet<Integer> months;
	protected transient TreeSet<Integer> daysOfWeek;

	protected final String expression;

	protected CronSetting(String expression) {
		this.expression = expression;
	}

	/**
	 * 推演下次小时值
	 * @param cal 时间
	 * @param v 时间值集
	 * @param index 值序号
	 * @return 是否未变更
	 */
	protected boolean nextHour(Calendar cal, int[] v, int index) {
		v[index] = cal.get(Calendar.HOUR_OF_DAY);
		int t = -1;

		SortedSet<Integer> st = hours.tailSet(v[index]);
		if (st != null && !st.isEmpty()) {
			t = v[index];
			v[index] = st.first();
		} else {
			v[index] = hours.first();
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		if (v[index] != t) return setter(cal, -1, -1, -1, v[index], 0, 0) == null;
		cal.set(Calendar.HOUR_OF_DAY, v[index]);
		return true;
	}

	/**
	 * 推演下次分钟值
	 * @param cal 时间
	 * @param v 时间值集
	 * @param index 值序号
	 * @return 是否未变更
	 */
	protected boolean nextMinute(Calendar cal, int[] v, int index) {
		v[index] = cal.get(Calendar.MINUTE);
		int t = -1;

		SortedSet<Integer> st = minutes.tailSet(v[index]);
		if (st != null && !st.isEmpty()) {
			t = v[index];
			v[index] = st.first();
		} else {
			v[index] = minutes.first();
			cal.add(Calendar.HOUR, 1);
		}
		if (v[index] != t) return setter(cal, -1, -1, -1, -1, v[index], 0) == null;

		cal.set(Calendar.MINUTE, v[index]);
		return true;
	}

	/**
	 * 推演下次月份
	 * @param cal 时间
	 * @param v 时间值集
	 * @param index 值序号
	 * @return 是否未变更
	 */
	protected boolean nextMonth(Calendar cal, int[] v, int index) {
		v[index] = cal.get(Calendar.MONTH) + 1;
		int t = -1;

		SortedSet<Integer> st = months.tailSet(v[index]);
		if (st != null && st.size() != 0) {
			t = v[index];
			v[index] = st.first();
		} else {
			v[index] = months.first();
			cal.add(Calendar.YEAR, 1);
		}
		cal.set(Calendar.MONTH, v[index] - 1);
		if (v[index] != t) return setter(cal, -1, -1, 1, 0, 0, 0) == null;
		return true;
	}

	/**
	 * 重置时间设定
	 */
	protected void reset() {
		if (seconds == null) seconds = new TreeSet();
		else seconds.clear();

		if (minutes == null) minutes = new TreeSet();
		else minutes.clear();

		if (hours == null) hours = new TreeSet();
		else hours.clear();

		if (daysOfMonth == null) daysOfMonth = new TreeSet();
		else daysOfMonth.clear();

		if (months == null) months = new TreeSet();
		else months.clear();

		if (daysOfWeek == null) daysOfWeek = new TreeSet();
		else daysOfWeek.clear();
	}
}

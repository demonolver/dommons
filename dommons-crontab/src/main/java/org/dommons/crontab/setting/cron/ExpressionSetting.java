/*
 * @(#)ExpressionSetting.java     2013-10-15
 */
package org.dommons.crontab.setting.cron;

import java.util.Calendar;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.dommons.crontab.setting.cron.CronSettingFactory.ExpressionParser;

/**
 * 类 spring quartz cronExpression 时间设定
 * @author Demon 2013-10-15
 */
class ExpressionSetting extends CronSetting {

	private static final long serialVersionUID = 116550350978741259L;

	protected static final int NO_SPEC = 98; // '?'

	protected transient TreeSet<Integer> years;

	protected transient boolean lastdayOfWeek;
	protected transient int nthdayOfWeek;

	protected transient boolean lastdayOfMonth;
	protected transient int lastdayOffset;
	protected transient boolean nearestWeekday;

	protected ExpressionSetting(String expression) {
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
			int[] val = new int[6]; // {sec,min,hour,day,month,year}

			nextSecond(cal, val);
			if (!nextMinute(cal, val, 1)) continue;
			if (!nextHour(cal, val, 2)) continue;
			if (!nextDay(cal, val, last)) continue;
			if (!nextMonth(cal, val, 4)) continue;
			if (!nextYear(cal, val)) continue;
			break;
		}

		return cal.getTimeInMillis();
	}

	/**
	 * 推演下次日期
	 * @param cal 日期
	 * @param v 日期值集
	 * @param last 前次时间点
	 * @return 是否未变更
	 */
	protected boolean nextDay(Calendar cal, int[] v, long last) {
		v[3] = cal.get(Calendar.DAY_OF_MONTH);
		boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC), dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
		if (dayOfMSpec && !dayOfWSpec) { // 按月查找日期
			if (!nextDayOfMonth(cal, v, last)) return false;
		} else if (dayOfWSpec && !dayOfMSpec) { // 按周查找日期
			if (!nextDayOfWeek(cal, v)) return false;
		} else { // 不支持同时指定月-日和周-日
			throw new UnsupportedOperationException(
					"Support for specifying both a day-of-week " + "AND a day-of-month parameter is not implemented.");
		}
		cal.set(Calendar.DAY_OF_MONTH, v[3]);
		return true;
	}

	/**
	 * 推演下次月-日
	 * @param cal 时间
	 * @param v 时间值集
	 * @param last 前次时间
	 * @return 是否未变更
	 */
	protected boolean nextDayOfMonth(Calendar cal, int[] v, long last) {
		v[4] = cal.get(Calendar.MONTH) + 1;
		int t = v[3];
		if (lastdayOfMonth && nearestWeekday) {
			v[3] = lastDayOfMonth(cal) - lastdayOffset;
			if (nearestWeekday(cal, v) < last) return setter(cal, -1, v[4], 1) == null;
		} else if (lastdayOfMonth) {
			v[3] = lastDayOfMonth(cal) - lastdayOffset;
			if (t > v[3]) return setter(add(cal, 4, 1), Calendar.DAY_OF_MONTH, 1) == null;
		} else if (nearestWeekday) {
			v[3] = daysOfMonth.first();
			if (nearestWeekday(cal, v) < last) return setter(cal, -1, v[4], daysOfMonth.first()) == null;
		} else {
			SortedSet<Integer> st = daysOfMonth.tailSet(v[3]);
			if (st != null && !st.isEmpty()) {
				int add = st.first() - v[3];
				if (add > 0) return add(cal, 3, add) == null;
			} else {
				return setter(cal, -1, v[4], daysOfMonth.first()) == null;
			}
		}

		if (v[3] != t) return setter(cal, -1, v[4] - 1, v[3], 0, 0, 0) == null;

		return true;
	}

	/**
	 * 推演下次周-日
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected boolean nextDayOfWeek(Calendar cal, int[] v) {
		v[4] = cal.get(Calendar.MONTH) + 1;
		if (lastdayOfWeek) { // 查找月最后一周的
			int add = dayOfWeek(cal);
			int lDay = lastDayOfMonth(cal);

			if (v[3] + add > lDay) return setter(cal, -1, v[4], 1, 0, 0, 0) == null;

			for (; (v[3] + add + 7) <= lDay; add += 7); // 推移到最后一周
			if (add > 0) return add(cal, 3, add) == null;
		} else if (nthdayOfWeek != 0) { // 查找月第 X 周的
			int add = dayOfWeek(cal);
			int week = (v[3] + add + 6) / 7;
			add += (nthdayOfWeek - week) * 7;

			if (add < 0) return setter(cal, -1, v[4], 1, 0, 0, 0) == null;
			else if (add > 0) return add(cal, 3, add) == null;
		} else {
			int add = dayOfWeek(cal);
			if (add > 0) return add(cal, 3, add) == null;
		}
		return true;
	}

	/**
	 * 推演下次秒值
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected boolean nextSecond(Calendar cal, int[] v) {
		v[0] = cal.get(Calendar.SECOND);

		SortedSet<Integer> st = seconds.tailSet(v[0]);
		if (st != null && !st.isEmpty()) {
			v[0] = st.first();
		} else {
			v[0] = seconds.first();
			cal.add(Calendar.MINUTE, 1);
		}
		cal.set(Calendar.SECOND, v[0]);
		return true;
	}

	/**
	 * 推演下次年份
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 是否未变更
	 */
	protected Boolean nextYear(Calendar cal, int[] v) {
		int t = v[5] = cal.get(Calendar.YEAR);

		SortedSet<Integer> st = years.tailSet(v[5]);
		if (st != null && !st.isEmpty()) v[5] = st.first();
		else return null;

		if (v[5] != t) return setter(cal, v[5], 0, 1, 0, 0, 0) == null;

		cal.set(Calendar.YEAR, v[5]);
		return true;
	}

	protected void reset() {
		super.reset();

		if (years == null) years = new TreeSet();
		else years.clear();

		lastdayOfWeek = false;
		nthdayOfWeek = 0;
		lastdayOfMonth = false;
		nearestWeekday = false;
		lastdayOffset = 0;
	}

	/**
	 * 计算周-日后移天数
	 * @param cal 时间
	 * @return 后移天数
	 */
	int dayOfWeek(Calendar cal) {
		int dow = daysOfWeek.first(), cDow = cal.get(Calendar.DAY_OF_WEEK);
		SortedSet<Integer> st = daysOfWeek.tailSet(cDow);
		if (st != null && !st.isEmpty()) dow = st.first();

		int daysToAdd = 0;
		if (cDow < dow) daysToAdd = dow - cDow;
		else if (cDow > dow) daysToAdd = dow + (7 - cDow);
		return daysToAdd;
	}

	/**
	 * 获取月的最后一天
	 * @param cal 时间
	 * @return 最后一天
	 */
	int lastDayOfMonth(Calendar cal) {
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 计算最近工作日
	 * @param cal 时间
	 * @param v 时间值集
	 * @return 最近工作日时间
	 */
	long nearestWeekday(Calendar cal, int[] v) {
		Calendar tcal = Calendar.getInstance(cal.getTimeZone());
		tcal.setTimeInMillis(cal.getTimeInMillis());

		int dow = setter(tcal, Calendar.DAY_OF_MONTH, v[3]).get(Calendar.DAY_OF_WEEK), ldom = lastDayOfMonth(cal);

		if (dow == Calendar.SATURDAY && v[3] == 1) v[3] += 2;
		else if (dow == Calendar.SATURDAY) v[3] -= 1;
		else if (dow == Calendar.SUNDAY && v[3] == ldom) v[3] -= 2;
		else if (dow == Calendar.SUNDAY) v[3] += 1;

		return setter(tcal, Calendar.DAY_OF_MONTH, v[3]).getTimeInMillis();
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		try {
			ExpressionParser.build(this, expression);
		} catch (Exception ignore) {
		}
	}
}

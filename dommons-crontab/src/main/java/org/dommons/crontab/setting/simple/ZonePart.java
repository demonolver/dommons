/*
 * @(#)ZonePart.java     2013-10-17
 */
package org.dommons.crontab.setting.simple;

import java.util.Calendar;
import java.util.TimeZone;

import org.dommons.crontab.setting.simple.DefaultCronsetFactory.FixedPart;

/**
 * 区间设定
 * @author Demon 2013-10-17
 */
class ZonePart implements CronsetPart {

	private static final long serialVersionUID = -7322770079825439300L;

	private FixedPart start;
	private FixedPart end;

	public ZonePart() {
	}

	public ZonePart(String start, String end) {
		if (start != null) setStart(start);
		if (end != null) setEnd(end);
	}

	/**
	 * 设置截止设定
	 * @param end 截止设定
	 */
	public void setEnd(String end) {
		FixedPart p = FixedPart.complie(end, true);
		if (p != null) this.end = p;
	}

	/**
	 * 设置起始设定
	 * @param start 起始设定
	 */
	public void setStart(String start) {
		FixedPart p = FixedPart.complie(start, true);
		if (p != null) this.start = p;
	}

	public long time(long base, TimeZone tz) {
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(base);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);

		long start = cal.getTimeInMillis();
		if (this.start != null) start = this.start.time(cal.getTimeInMillis(), tz);

		long end = Long.MAX_VALUE;
		if (this.end != null) end = this.end.time(start, tz);

		if (start > base) {
			return start;
		} else if (end < base) {
			long time = 0;
			if (this.start != null) {
				time = this.start.time(end, tz);
				if (time < base) time = 0;
			}
			return time;
		}

		return base;
	}
}

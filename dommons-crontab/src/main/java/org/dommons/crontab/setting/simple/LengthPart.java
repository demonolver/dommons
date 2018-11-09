/*
 * @(#)LengthPart.java     2013-10-17
 */
package org.dommons.crontab.setting.simple;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 定时长设定
 * @author Demon 2013-10-17
 */
class LengthPart implements CronsetPart {

	private static final long serialVersionUID = -7191365937938653259L;

	private long length;
	private TimeUnit unit;

	public LengthPart() {
		this(1, null);
	}

	public LengthPart(long length, TimeUnit unit) {
		setLength(length);
		setUnit(unit);
	}

	/**
	 * 获取时长
	 * @return 时长
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 获取单位
	 * @return 单位
	 */
	public TimeUnit getUnit() {
		return unit;
	}

	/**
	 * 设置时长
	 * @param length 时长
	 */
	public void setLength(long length) {
		if (length > 0) this.length = length;
	}

	/**
	 * 设置单位
	 * @param unit 单位
	 */
	public void setUnit(TimeUnit unit) {
		if (unit == null && this.unit != null) return;
		this.unit = unit == null ? TimeUnit.MILLISECONDS : unit;
	}

	public long time(long base, TimeZone tz) {
		return base + unit.toMillis(length);
	}
}

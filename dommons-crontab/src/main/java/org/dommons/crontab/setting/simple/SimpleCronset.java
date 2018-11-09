/*
 * @(#)SimpleCronset.java     2013-10-15
 */
package org.dommons.crontab.setting.simple;

import java.util.concurrent.TimeUnit;

import org.dommons.crontab.setting.simple.DefaultCronsetFactory.DefaultCronset;
import org.dommons.crontab.setting.simple.DefaultCronsetFactory.FixedPart;

/**
 * 简单定时设置
 * @author Demon 2013-10-15
 */
public class SimpleCronset extends DefaultCronset {

	private static final long serialVersionUID = -913277967246024328L;

	public SimpleCronset() {
	}

	/**
	 * 设置延迟时长
	 * @param delay 延迟时长
	 */
	public void setDelay(long delay) {
		if (start != null && start instanceof LengthPart) ((LengthPart) start).setLength(delay);
		else start = new LengthPart(delay, null);
	}

	/**
	 * 设置延迟
	 * @param delay 延迟时长
	 * @param unit 时长单位
	 */
	public void setDelay(long delay, TimeUnit unit) {
		start = new LengthPart(delay, unit);
	}

	/**
	 * 设置延迟单位
	 * @param unit 时长单位
	 */
	public void setDelayUnit(TimeUnit unit) {
		if (start != null && start instanceof LengthPart) ((LengthPart) start).setUnit(unit);
		else start = new LengthPart(1, unit);
	}

	/**
	 * 设置截止时间
	 * @param time 截止时间
	 */
	public void setEnd(String time) {
		FixedPart part = FixedPart.complie(time);
		if (part != null) over = part;
	}

	/**
	 * 设置截止延迟时长
	 * @param delay 延迟时长
	 */
	public void setEndDelay(long delay) {
		if (over != null && over instanceof LengthPart) ((LengthPart) over).setLength(delay);
		else over = new LengthPart(delay, null);
	}

	/**
	 * 设置截止延迟
	 * @param delay 延迟时长
	 * @param unit 时长单位
	 */
	public void setEndDelay(long delay, TimeUnit unit) {
		this.over = new LengthPart(delay, unit);
	}

	/**
	 * 设置截止延迟单位
	 * @param unit 时长单位
	 */
	public void setEndDelayUnit(TimeUnit unit) {
		if (over != null && over instanceof LengthPart) ((LengthPart) over).setUnit(unit);
		else over = new LengthPart(1, unit);
	}

	/**
	 * 设置周期时长
	 * @param period 周期时长
	 */
	public void setPeriod(long period) {
		if (step != null && step instanceof LengthPart) ((LengthPart) step).setLength(period);
		else step = new LengthPart(period, null);
	}

	/**
	 * 设置周期设定
	 * @param period 周期时长
	 * @param unit 时长单位
	 */
	public void setPeriod(long period, TimeUnit unit) {
		step = new LengthPart(period, unit);
	}

	/**
	 * 设置周期单位
	 * @param unit 时长单位
	 */
	public void setPeriodUnit(TimeUnit unit) {
		if (step != null && step instanceof LengthPart) ((LengthPart) step).setUnit(unit);
		else step = new LengthPart(1, unit);
	}

	/**
	 * 设置开始时间
	 * @param time 开始时间
	 */
	public void setStart(String time) {
		FixedPart part = FixedPart.complie(time);
		if (part != null) start = part;
	}

	/**
	 * 设置延迟开始单位
	 * @param unit 单位串
	 */
	public void setUnitforDelay(String unit) {
		setDelayUnit(TimeUnit.valueOf(unit));
	}

	/**
	 * 设置截止延迟单位
	 * @param unit 单位串
	 */
	public void setUnitforEndDelay(String unit) {
		setEndDelayUnit(TimeUnit.valueOf(unit));
	}

	/**
	 * 设置周期单位
	 * @param unit 单位串
	 */
	public void setUnitforPeriod(String unit) {
		setPeriodUnit(TimeUnit.valueOf(unit));
	}

	/**
	 * 设置运行区间
	 * @param start 区间起始时间
	 * @param end 区间截止时间
	 */
	public void setZone(String start, String end) {
		zone = new ZonePart(start, end);
	}

	/**
	 * 设置区间截止时间
	 * @param end 区间截止时间
	 */
	public void setZoneEnd(String end) {
		if (zone != null && zone instanceof ZonePart) ((ZonePart) zone).setEnd(end);
		else zone = new ZonePart(null, end);
	}

	/**
	 * 设置区间起始时间
	 * @param start 区间起始时间
	 */
	public void setZoneStart(String start) {
		if (zone != null && zone instanceof ZonePart) ((ZonePart) zone).setStart(start);
		else zone = new ZonePart(start, null);
	}
}

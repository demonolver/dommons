/*
 * @(#)CronsetPart.java     2013-10-15
 */
package org.dommons.crontab.setting.simple;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * 部分时间设定
 * @author Demon 2013-10-15
 */
interface CronsetPart extends Serializable {

	/**
	 * 计算下一个时间点 为 <code>0</code> 不指定
	 * @param base 基准时间点
	 * @param tz 时区
	 * @return 下一个时间点
	 */
	public long time(long base, TimeZone tz);
}

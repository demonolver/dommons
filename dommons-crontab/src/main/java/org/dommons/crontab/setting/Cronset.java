/*
 * @(#)Cronset.java     2013-10-14
 */
package org.dommons.crontab.setting;

import java.io.Serializable;
import java.util.TimeZone;

/**
 * 定时时间设置
 * @author Demon 2013-10-14
 */
public interface Cronset extends Serializable {

	/**
	 * 计算下一个时间点 为 <code>0</code> 不再执行
	 * @param last 前次时间点
	 * @param tz 时区
	 * @return 下次时间点
	 */
	public long time(long last, TimeZone tz);
}

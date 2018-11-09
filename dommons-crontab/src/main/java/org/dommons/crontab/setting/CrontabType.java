/*
 * @(#)CrontabType.java     2013-10-14
 */
package org.dommons.crontab.setting;

/**
 * 定时任务类型
 * @author Demon 2013-10-14
 */
public interface CrontabType {

	/** 等空闲时间型工作 开始执行后，每次运行间隔相等，即从每次执行完成后的时间开始计算间隔时间 */
	public final int BALANCED_LEISURE = 1001;

	/** 定时检查型工作 从开始时间点，每次间隔一定时间做检查，上次执行仍未完成则跳过本次执行 */
	public final int TIMING_CHECK = 1010;

	/** 定时执行型工作 从开始时间点，每次间隔一定时间执行工作，无论之前任务是否完成 */
	public final int TIMING_EXECUTION = 1100;
}

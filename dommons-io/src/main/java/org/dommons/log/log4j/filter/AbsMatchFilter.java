/*
 * @(#)AbsMatchFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 抽象日志事件匹配过滤器
 * @author demon 2016-11-25
 */
public abstract class AbsMatchFilter extends Filter {

	private boolean accptOnMatch;

	public AbsMatchFilter() {
		super();
		accptOnMatch = false;
	}

	public int decide(LoggingEvent event) {
		if (!match(event)) return -1;
		if (accptOnMatch) {
			return 1;
		}
		return 0;
	}

	/**
	 * 是否接受匹配结果
	 * @return 是、否
	 */
	public boolean isAccptOnMatch() {
		return accptOnMatch;
	}

	/**
	 * 设置是否接受匹配结果
	 * @param accptOnMatch 是、否
	 */
	public void setAccptOnMatch(boolean accptOnMatch) {
		this.accptOnMatch = accptOnMatch;
	}

	/**
	 * 计算匹配
	 * @param event 日志事件
	 * @return 是否匹配
	 */
	protected abstract boolean match(LoggingEvent event);
}

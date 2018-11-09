/*
 * @(#)AbsPatternFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import java.util.regex.Pattern;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 抽象正则过滤器
 * @author demon 2016-11-25
 */
public abstract class AbsPatternFilter extends AbsMatchFilter {

	private Pattern pattern;

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	protected boolean match(LoggingEvent event) {
		String value = pickup(event);
		if (value == null) return false;
		if (pattern == null) {
			return true;
		}
		return pattern.matcher(value).matches();
	}

	/**
	 * 提取日志事件属性项
	 * @param event 日志事件属性项
	 * @return 属性项
	 */
	protected abstract String pickup(LoggingEvent event);
}

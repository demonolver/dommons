/*
 * @(#)IncludeNameFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 名称包含日志过滤器
 * @author demon 2016-11-25
 */
public class IncludeNameFilter extends AbsEnumFilter<String> {

	public IncludeNameFilter() {
		this(true);
	}

	protected IncludeNameFilter(boolean include) {
		super(include);
	}

	protected boolean matchItem(String value, String item) {
		if ((value == null) || (item == null)) return item == null;
		return value.startsWith(item);
	}

	protected String pickup(LoggingEvent event) {
		return event.getLoggerName();
	}

	protected String toItem(String str) {
		return str;
	}

	protected String toString(String item) {
		return item;
	}
}

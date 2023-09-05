/*
 * @(#)IncludeClassFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 类名包含日志过滤器
 * @author demon 2016-11-25
 */
public class IncludeClassFilter extends IncludeNameFilter {

	public IncludeClassFilter() {
		this(true);
	}

	protected IncludeClassFilter(boolean include) {
		super(include);
	}

	protected String pickup(LoggingEvent event) {
		return event.getLocationInformation().getClassName();
	}
}

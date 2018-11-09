/*
 * @(#)NameMatchFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 名称正则匹配过滤器
 * @author demon 2016-11-25
 */
public class NameMatchFilter extends AbsPatternFilter {

	protected String pickup(LoggingEvent event) {
		return event.getLoggerName();
	}
}

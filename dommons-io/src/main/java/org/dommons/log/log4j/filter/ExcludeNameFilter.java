/*
 * @(#)ExcludeNameFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

/**
 * 名称排除日志过滤器
 * @author demon 2016-11-25
 */
public class ExcludeNameFilter extends IncludeNameFilter {

	public ExcludeNameFilter() {
		super(false);
	}
}

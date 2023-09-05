/*
 * @(#)AbsEnumFilter.java     2016-11-25
 */
package org.dommons.log.log4j.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.log4j.spi.LoggingEvent;

/**
 * 抽象枚举匹配过滤器
 * @author demon 2016-11-25
 */
public abstract class AbsEnumFilter<E> extends AbsMatchFilter {

	static final Pattern pattern = Pattern.compile("[:,|;]");

	private final boolean include;
	private final Collection<E> items;

	public AbsEnumFilter(boolean include) {
		super();
		this.include = include;
		items = new HashSet();
	}

	/**
	 * 获取枚举集
	 * @return 枚举集
	 */
	public String getItems() {
		StringBuffer buffer = new StringBuffer();
		for (E item : items) {
			if (buffer.length() > 0) buffer.append(" , ");
			buffer.append(toString(item));
		}
		return buffer.toString();
	}

	/**
	 * 设置枚举集
	 * @param items 枚举集
	 */
	public void setItems(String items) {
		if (items == null) return;
		synchronized (this.items) {
			for (String item : pattern.split(items)) {
				this.items.add(toItem(item));
			}
		}
	}

	protected boolean match(LoggingEvent event) {
		if (items.isEmpty()) return true;
		E value = pickup(event);
		for (E item : items) {
			if (matchItem(value, item)) return include;
		}
		return !include;
	}

	/**
	 * 匹配日志事件属性项
	 * @param value 属性项
	 * @param item 匹配项
	 * @return 是、否
	 */
	protected abstract boolean matchItem(E value, E item);

	/**
	 * 提取日志事件属性
	 * @param event 日志事件
	 * @return 属性项
	 */
	protected abstract E pickup(LoggingEvent event);

	/**
	 * 转换字符串为匹配项
	 * @param str 字符串
	 * @return 匹配项
	 */
	protected abstract E toItem(String str);

	/**
	 * 转换匹配项为字符串
	 * @param item 匹配项
	 * @return 字符串
	 */
	protected abstract String toString(E item);
}

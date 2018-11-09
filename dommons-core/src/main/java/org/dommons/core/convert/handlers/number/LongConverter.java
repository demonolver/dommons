/*
 * @(#)LongConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

/**
 * 长整型转换器
 * @author Demon 2011-10-21
 */
public class LongConverter extends NumberConverter<Long> {

	protected Long createNumber(Number n) {
		return (n instanceof Long) ? (Long) n : Long.valueOf(n.longValue());
	}
}

/*
 * @(#)ShortConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

/**
 * 短整型转换器
 * @author Demon 2011-10-21
 */
public class ShortConverter extends NumberConverter<Short> {

	protected Short createNumber(Number n) {
		return Short.valueOf(n.shortValue());
	}
}

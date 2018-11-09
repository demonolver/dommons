/*
 * @(#)IntegerConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

/**
 * 整型转换器
 * @author Demon 2011-10-21
 */
public class IntegerConverter extends NumberConverter<Integer> {

	protected Integer createNumber(Number n) {
		return Integer.valueOf(n.intValue());
	}
}

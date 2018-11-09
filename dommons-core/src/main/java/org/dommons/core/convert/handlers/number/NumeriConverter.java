/*
 * @(#)NumeriConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

import org.dommons.core.number.Numeric;

/**
 * 通用数字转换器
 * @author Demon 2011-10-21
 */
public class NumeriConverter extends NumberConverter<Numeric> {

	protected Numeric createNumber(Number n) {
		return Numeric.valueOf(n);
	}
}

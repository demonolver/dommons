/*
 * @(#)BigDecimal.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

import java.math.BigDecimal;

/**
 * 大数字转换器
 * @author Demon 2011-10-21
 */
public class BigDecimalConverter extends NumberConverter<BigDecimal> {

	protected BigDecimal createNumber(Number n) {
		return new BigDecimal(n.doubleValue());
	}
}

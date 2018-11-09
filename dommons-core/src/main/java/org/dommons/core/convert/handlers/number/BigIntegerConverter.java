/*
 * @(#)BigIntegerConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

import java.math.BigInteger;

/**
 * 大整数转换器
 * @author Demon 2011-10-21
 */
public class BigIntegerConverter extends NumberConverter<BigInteger> {

	protected BigInteger createNumber(Number n) {
		return BigInteger.valueOf(n.longValue());
	}
}

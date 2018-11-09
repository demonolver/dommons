/*
 * @(#)FloatConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

/**
 * 浮点型转换器
 * @author Demon 2011-10-21
 */
public class FloatConverter extends NumberConverter<Float> {

	protected Float createNumber(Number n) {
		return Float.valueOf(n.floatValue());
	}
}

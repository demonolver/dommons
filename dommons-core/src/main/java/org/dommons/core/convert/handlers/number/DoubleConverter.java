/*
 * @(#)DoubleConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

/**
 * 双精度型转换器
 * @author Demon 2011-10-21
 */
public class DoubleConverter extends NumberConverter<Double> {

	protected Double createNumber(Number n) {
		return (n instanceof Double) ? (Double) n : Double.valueOf(n.doubleValue());
	}
}

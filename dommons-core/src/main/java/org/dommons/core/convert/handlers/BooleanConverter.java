/*
 * @(#)BooleanConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers;

import org.dommons.core.convert.ConvertHandler;

/**
 * 布尔型转换器
 * @author Demon 2011-10-21
 */
public class BooleanConverter implements ConvertHandler<Object, Boolean> {

	/** 真值对照表 */
	private static final String[] trueStrings = { "true", "yes", "y", "t", "on", "1" };
	/** 假值对照表 */
	private static final String[] falseStrings = { "false", "no", "n", "f", "off", "0" };

	public Boolean convert(Object obj, Class<? extends Object> source, Class<Boolean> target) {
		String stringValue = String.valueOf(obj).trim();
		for (String trueString : trueStrings) { // 真值对照表比对
			if (trueString.equalsIgnoreCase(stringValue)) return Boolean.TRUE;
		}

		for (String falseString : falseStrings) { // 假值对照表比对
			if (falseString.equalsIgnoreCase(stringValue)) return Boolean.FALSE;
		}

		return null;
	}
}

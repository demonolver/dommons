/*
 * @(#)EnumConverter.java     2018-05-22
 */
package org.dommons.core.convert.handlers;

/**
 * 枚举转换器
 * @author demon 2018-05-22
 */
public class EnumConverter extends AbstractConverter<CharSequence, Enum> {

	public Enum convert(CharSequence obj, Class<? extends CharSequence> source, Class<Enum> target) {
		return Enum.valueOf(target, String.valueOf(obj));
	}
}

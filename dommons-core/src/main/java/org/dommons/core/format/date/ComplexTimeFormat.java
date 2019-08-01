/*
 * @(#)ComplexTimeFormat.java     2018-10-23
 */
package org.dommons.core.format.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;

/**
 * 复合时间格式
 * @author demon 2018-10-23
 */
public class ComplexTimeFormat extends TimeFormat {

	private static final long serialVersionUID = -7412476855753679017L;

	/**
	 * 编译格式串
	 * @param pattern 格式串
	 * @return 格式化实例
	 */
	public static DateFormat compile(String pattern) {
		return compile(pattern, null, null);
	}

	/**
	 * 编译格式串
	 * @param pattern 格式串
	 * @param locale 语言区域
	 * @return 格式化实例
	 */
	public static DateFormat compile(String pattern, Locale locale) {
		return compile(pattern, locale, null);
	}

	/**
	 * 编译格式串
	 * @param pattern 格式串
	 * @param locale 语言区域
	 * @param zone 时区
	 * @return 格式化实例
	 */
	public static DateFormat compile(String pattern, Locale locale, TimeZone zone) {
		if (Stringure.isEmpty(pattern)) return null;
		if (zone == null) zone = Environments.defaultTimeZone();
		if (locale == null) locale = Environments.defaultLocale();
		return new ComplexTimeFormat(pattern, locale, zone);
	}

	protected ComplexTimeFormat(String pattern, Locale locale, TimeZone zone) {
		super(pattern, locale, zone);
	}

	@Override
	public Date parse(String source) throws ParseException {
		Date d = Converter.F.convert(source, Date.class);
		if (d != null) return d;
		return super.parse(source);
	}

	@Override
	public Object parseObject(String source) throws ParseException {
		return parse(source);
	}
}

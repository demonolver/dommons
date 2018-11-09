/*
 * @(#)StringConverter.java     2011-10-20
 */
package org.dommons.core.convert.handlers;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.dommons.core.Environments;
import org.dommons.core.format.date.TimeFormat;
import org.dommons.core.number.Numeric;
import org.dommons.core.util.Arrayard;

/**
 * 字符串转换器
 * @author Demon 2011-10-20
 */
public class StringConverter extends AbstractConverter<Object, String> {

	/** 时间格式 */
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
	/** 时间格式化工具 */
	static final DateFormat format = TimeFormat.compile(dateFormat);

	/**
	 * 转换为字符串
	 * @param value 值对象
	 * @return 字符串
	 */
	public static String toString(Object value) {
		if (value == null) {
			return "null";
		} else {
			return toString(value, value.getClass());
		}
	}

	/**
	 * 转换为字符串
	 * @param value 值对象
	 * @param source 值类型
	 * @return 字符串
	 */
	protected static String toString(Object value, Class source) {
		if (Date.class.equals(source)) {
			return format.format((Date) value);
		} else if (Calendar.class.isAssignableFrom(source)) {
			Calendar cal = (Calendar) value;
			return createFormat(cal.getTimeZone()).format(cal.getTime());
		} else if (Double.class.isAssignableFrom(source) || BigDecimal.class.isAssignableFrom(source)
				|| Float.class.isAssignableFrom(source)) {
			double v = ((Number) value).doubleValue();
			return (Double.isInfinite(v) || Double.isNaN(v)) ? Double.toString(v) : Numeric.toString((Number) value);
		} else if (Enum.class.isAssignableFrom(source)) {
			return Enum.class.cast(value).name();
		} else if (source.isArray() && ((value instanceof Character[]) || (value instanceof char[]))) {
			return String.valueOf((value instanceof Character[]) ? convert((Character[]) value) : (char[]) value);
		} else if (source.isArray() && ((value instanceof Byte[]) || (value instanceof byte[]))) {
			return new String((value instanceof Byte[]) ? convert((Byte[]) value) : (byte[]) value);
		} else if (!source.isArray()) {
			return String.valueOf(value);
		}
		return Arrayard.toString(value);

	}

	/**
	 * 转换字节对象数组为字节数组
	 * @param bs 字节对象数组
	 * @return 字节数组
	 */
	static byte[] convert(Byte[] bs) {
		int len = bs.length;
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			bytes[i] = bs[i].byteValue();
		}
		return bytes;
	}

	/**
	 * 转换字符对象数组字符数组
	 * @param cs 字符对象数组
	 * @return 字符数组
	 */
	static char[] convert(Character[] cs) {
		int len = cs.length;
		char[] chars = new char[len];
		for (int i = 0; i < len; i++) {
			chars[i] = cs[i].charValue();
		}
		return chars;
	}

	/**
	 * 创建时间格式
	 * @param tz 时区
	 * @return 时间格式
	 */
	static DateFormat createFormat(TimeZone tz) {
		if (tz == null || format.getTimeZone().getRawOffset() == tz.getRawOffset()) {
			return format;
		} else {
			DateFormat format = new SimpleDateFormat(dateFormat, Environments.defaultLocale());
			format.setTimeZone(tz);
			return format;
		}
	}

	/**
	 * 构造函数
	 */
	public StringConverter() {}

	public String convert(Object value, Class<? extends Object> source, Class<String> target) {
		return toString(value, source);
	}
}

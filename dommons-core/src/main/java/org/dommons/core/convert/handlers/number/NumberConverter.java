/*
 * @(#)NumberConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.number;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.dommons.core.Silewarner;
import org.dommons.core.convert.ConvertHandler;
import org.dommons.core.convert.handlers.AbstractLocaleConverter;
import org.dommons.core.format.number.NumericFormat;
import org.dommons.core.format.number.RadixFormat;
import org.dommons.core.string.Stringure;

/**
 * 抽象数字转换器
 * @author Demon 2011-10-21
 */
abstract class NumberConverter<T extends Number> extends AbstractLocaleConverter implements ConvertHandler<Object, T> {

	/** 格式对应关系 */
	private static Collection<StringNumeric> formats;

	/**
	 * 初始化
	 */
	static void initialize() {
		if (formats == null) {
			synchronized (NumberConverter.class) {
				try {
					if (formats == null) formats = initialize(load());
				} catch (IOException e) {
					formats = new HashSet();
				}
			}
		}
	}

	/**
	 * 初始化
	 * @param props 定义集
	 * @return 格式对应关系
	 */
	static Collection<StringNumeric> initialize(Properties props) {
		Map<String, StringNumeric> formats = new HashMap();
		Map<Integer, Collection<String>> prios = new TreeMap();
		Enumeration<Object> en = props.keys();
		if (en != null) {
			while (en.hasMoreElements()) {
				String k = String.valueOf(en.nextElement());
				if (!k.startsWith("pattern.")) continue;

				String pattern = props.getProperty(k);
				String n = k.substring(8);
				String format = props.getProperty("format." + n);
				String type = props.getProperty(Stringure.join('.', "format", n, "type"), "numeric");
				String locale = props.getProperty(Stringure.join('.', "format", n, "locale"));

				try {
					int prio = Integer.parseInt(props.getProperty("priority." + n));
					if (pattern != null && format != null) {
						Pattern key = Pattern.compile(pattern);
						NumberFormat nf = "radix".equals(type) ? new RadixFormat(format) : new NumericFormat(format, locale(locale));
						formats.put(n, new StringNumeric(key, nf));

						Integer p = Integer.valueOf(prio);
						Collection<String> ps = prios.get(p);
						if (ps == null) prios.put(p, ps = new ArrayList());
						ps.add(n);
					}
				} catch (RuntimeException e) {
					Silewarner.warn(NumberConverter.class, "Instance new format [" + format + "] fail", e);
				}
			}
		}

		Collection<StringNumeric> ns = new LinkedHashSet();
		for (Iterator<Collection<String>> it = prios.values().iterator(); it.hasNext(); it.remove()) {
			for (Iterator<String> nit = it.next().iterator(); nit.hasNext(); nit.remove()) {
				StringNumeric sn = formats.remove(nit.next());
				if (sn != null) ns.add(sn);
			}
		}
		return ns;
	}

	/**
	 * 读取定义集
	 * @return 定义集
	 * @throws IOException
	 */
	static Properties load() throws IOException {
		return load(NumberConverter.class, "number.converters");
	}

	/**
	 * 构造函数
	 */
	protected NumberConverter() {
	}

	public T convert(Object value, Class<? extends Object> source, Class<T> target) {
		initialize();
		Number num = null;
		// 分析原数据类型
		if (Number.class.isAssignableFrom(source)) { // 数字类型
			num = (Number) value;
		} else if (CharSequence.class.isAssignableFrom(source)) { // 字符串类型
			num = parseString(String.valueOf(value));
		} else if (Boolean.class.isAssignableFrom(source)) { // 布尔类型
			num = ((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
		} else if (Long.class.equals(target) && Date.class.isAssignableFrom(source)) { // 时间类型转换为长整型
			num = new Long(((Date) value).getTime());
		} else if (Long.class.equals(target) && Calendar.class.isAssignableFrom(source)) { // 日历类型
			num = new Long(((Calendar) value).getTime().getTime());
		}
		// 无法转换
		if (num == null) return null;
		return createNumber(num);
	}

	/**
	 * 创建目标对象实例
	 * @param n 数字
	 * @return 对象实例
	 */
	protected abstract T createNumber(Number n);

	/**
	 * 解析字符串
	 * @param str 字符串值
	 * @return 数字
	 */
	private Number parseString(String str) {
		str = Stringure.trim(str);
		try {
			for (StringNumeric sn : formats) {
				if (sn.matches(str)) return sn.parse(str);
			}
		} catch (ParseException e) {
			// ignore
		} catch (RuntimeException e) {
			// ignore
		}
		return null;
	}

	/**
	 * 字符串数值分析器
	 * @author Demon 2012-6-21
	 */
	static class StringNumeric {

		private final Pattern pattern;
		private final NumberFormat format;

		/**
		 * 构造函数
		 * @param pattern 正则
		 * @param format 数值格式
		 */
		public StringNumeric(Pattern pattern, NumberFormat format) {
			this.pattern = pattern;
			this.format = format;
		}

		public boolean equals(Object o) {
			return o != null && o instanceof StringNumeric && this.pattern.equals(((StringNumeric) o).pattern);
		}

		public int hashCode() {
			return pattern.hashCode();
		}

		/**
		 * 是否匹配值
		 * @param value 值
		 * @return 是、否
		 */
		public boolean matches(String value) {
			return pattern.matcher(value).matches();
		}

		/**
		 * 解析数值
		 * @param value 数值串
		 * @return 数值
		 * @throws ParseException
		 */
		public Number parse(String value) throws ParseException {
			return format.parse(value);
		}
	}
}
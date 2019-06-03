/*
 * @(#)DateTimeConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.Silewarner;
import org.dommons.core.collections.map.DataPair;
import org.dommons.core.collections.map.Mapped;
import org.dommons.core.convert.ConvertHandler;
import org.dommons.core.convert.handlers.AbstractLocaleConverter;
import org.dommons.core.format.date.TimeFormat;
import org.dommons.core.string.Stringure;

/**
 * 抽象时间转换器
 * @author Demon 2011-10-21
 */
abstract class DateTimeConverter<T> extends AbstractLocaleConverter implements ConvertHandler<Object, T> {

	/** 格式表 */
	protected static Map<Pattern, DateFormat> map;

	/**
	 * 初始化
	 */
	static void initialize() {
		if (map == null) {
			synchronized (DateTimeConverter.class) {
				try {
					if (map == null) map = initialize(load());
				} catch (IOException e) {
					map = new HashMap();
				}
			}
		}
	}

	/**
	 * 初始化
	 * @param props 定义集
	 * @return 格式表
	 */
	static Map<Pattern, DateFormat> initialize(Properties props) {
		Map<String, DataPair<Pattern, DateFormat>> map = new HashMap();
		Map<Integer, Collection<String>> prios = new TreeMap();
		Enumeration<Object> en = props.keys();
		if (en != null) {
			while (en.hasMoreElements()) {
				String k = String.valueOf(en.nextElement());
				if (!k.startsWith("pattern.")) continue;

				String pattern = props.getProperty(k);
				String n = k.substring(8);
				String format = props.getProperty("format." + n);
				String locale = props.getProperty(Stringure.join('.', "format", n, "locale"));
				String timezone = props.getProperty(Stringure.join('.', "format", n, "timezone"));

				try {
					int prio = Integer.parseInt(props.getProperty("priority." + n));
					if (pattern != null && format != null) {
						Pattern key = Pattern.compile(pattern);
						Locale l = Stringure.isEmpty(locale) ? null : locale(locale);
						TimeZone tz = Stringure.isEmpty(timezone) ? null : TimeZone.getTimeZone(timezone);
						DateFormat df = TimeFormat.compile(format, l, tz);

						map.put(n, DataPair.create(key, df));

						Integer p = Integer.valueOf(prio);
						Mapped.touch(prios, p, LinkedList.class).add(n);
					}
				} catch (RuntimeException e) {
					Silewarner.warn(DateTimeConverter.class, "Instance new format [" + format + "] fail", e);
				}
			}
		}

		Map<Pattern, DateFormat> formats = new LinkedHashMap();
		for (Iterator<Collection<String>> it = prios.values().iterator(); it.hasNext(); it.remove()) {
			for (Iterator<String> nit = it.next().iterator(); nit.hasNext(); nit.remove()) {
				DataPair<Pattern, DateFormat> pair = map.remove(nit.next());
				if (pair != null) formats.put(pair.getKey(), pair.getValue());
			}
		}
		return formats;
	}

	/**
	 * 读取定义集
	 * @return 定义集
	 * @throws IOException
	 */
	static Properties load() throws IOException {
		return load(DateTimeConverter.class, "date.converters");
	}

	/**
	 * 构造函数
	 */
	protected DateTimeConverter() {}

	public T convert(Object obj, Class<? extends Object> source, Class<T> target) {
		initialize();
		Date date = null;
		if (Date.class.isAssignableFrom(source)) { // 时间类型
			date = (Date) obj;
		} else if (Calendar.class.isAssignableFrom(source)) { // 日历类型
			date = ((Calendar) obj).getTime();
		} else if (Number.class.isAssignableFrom(source)) { // 长整型
			date = new Date(((Number) obj).longValue());
		} else { // 转换为字符串，按常用时间格式转换
			String stringValue = String.valueOf(obj);
			if ("null".equals(stringValue)) return null;
			for (Entry<Pattern, DateFormat> en : map.entrySet()) {
				Pattern pattern = en.getKey();
				Matcher matcher = pattern.matcher(stringValue);
				if (matcher.find()) {
					try {
						String s = matcher.group().replaceAll("\\s+", " ");
						date = en.getValue().parse(s);
						break;
					} catch (ParseException e) {
						// ignore
					} catch (RuntimeException e) {
						// ignore
					}
				}
			}
		}

		return date == null ? null : createDate(date);
	}

	/**
	 * 创建目标时间类型
	 * @param date 时间值
	 * @return 目标值
	 */
	protected abstract T createDate(Date date);
}

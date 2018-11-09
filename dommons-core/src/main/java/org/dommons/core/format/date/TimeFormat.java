/*
 * @(#)TimeFormat.java     2011-10-18
 */
package org.dommons.core.format.date;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.number.Numeric;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.thread.ThreadCache;

/**
 * 时间格式化工具 解决 JDK 默认时区不正确和并发问题
 * @author Demon 2011-10-18
 */
public class TimeFormat extends DateFormat {

	private static final long serialVersionUID = -4740117576896394908L;

	static final ThreadCache<Map<String, SimpleDateFormat>> cache = new ThreadCache(WeakHashMap.class);
	static final Map<String, TimeFormat> models = new ConcurrentSoftMap();
	static final Map<String, String> ps = new ConcurrentSoftMap();

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
		Assertor.F.notEmpty(pattern);
		if (zone == null) zone = Environments.defaultTimeZone();
		if (locale == null) locale = Environments.defaultLocale();
		pattern = pattern(pattern, locale);
		String key = Stringure.join(':', locale, zone.getRawOffset(), pattern);
		TimeFormat tf = models.get(key);
		if (tf == null) models.put(key, tf = new TimeFormat(pattern, locale, zone));
		return tf;
	}

	/**
	 * 编译格式串
	 * @param pattern 格式串
	 * @param zone 时区
	 * @return 格式化实例
	 */
	public static DateFormat compile(String pattern, TimeZone zone) {
		return compile(pattern, null, zone);
	}

	/**
	 * 获取时间格式化实例
	 * @param dateStyle 日期样式
	 * @param timeStyle 时间样式
	 * @return 格式化实例
	 * @see DateFormat#FULL
	 * @see DateFormat#LONG
	 * @see DateFormat#MEDIUM
	 * @see DateFormat#SHORT
	 */
	public static DateFormat getInstance(int dateStyle, int timeStyle) {
		return getInstance(dateStyle, timeStyle, (Locale) null);
	}

	/**
	 * 获取时间格式化实例
	 * @param dateStyle 日期样式
	 * @param timeStyle 时产样式
	 * @param locale 语言区域
	 * @return 格式化实例
	 * @see DateFormat#FULL
	 * @see DateFormat#LONG
	 * @see DateFormat#MEDIUM
	 * @see DateFormat#SHORT
	 */
	public static DateFormat getInstance(int dateStyle, int timeStyle, Locale locale) {
		return getInstance(dateStyle, timeStyle, locale, null);
	}

	/**
	 * 获取时间格式化实例
	 * @param dateStyle 日期样式
	 * @param timeStyle 时间样式
	 * @param locale 语言区域
	 * @param zone 时区
	 * @return 格式化实例
	 */
	public static DateFormat getInstance(int dateStyle, int timeStyle, Locale locale, TimeZone zone) {
		dateStyle = Numeric.between(dateStyle, FULL, SHORT) ? dateStyle : -1;
		timeStyle = Numeric.between(timeStyle, FULL, SHORT) ? timeStyle : -1;
		int flag = (dateStyle >= 0 ? 1 : 0) | (timeStyle >= 0 ? 2 : 0);
		if (flag == 0) dateStyle = timeStyle = DEFAULT;
		String key = Stringure.join(':', dateStyle, timeStyle, locale, zone == null ? null : zone.getRawOffset());

		TimeFormat tf = models.get(key);
		if (tf == null) models.put(key, tf = new TimeFormat(flag, dateStyle, timeStyle, locale, zone));
		return tf;
	}

	/**
	 * 获取时间格式化实例
	 * @param dateStyle 日期样式
	 * @param timeStyle 时间样式
	 * @param zone 时区
	 * @return 格式化实例
	 * @see DateFormat#FULL
	 * @see DateFormat#LONG
	 * @see DateFormat#MEDIUM
	 * @see DateFormat#SHORT
	 */
	public static DateFormat getInstance(int dateStyle, int timeStyle, TimeZone zone) {
		return getInstance(dateStyle, timeStyle, null, zone);
	}

	public static DateFormat noClone(DateFormat format) {
		if (format == null || !(format instanceof TimeFormat)) return format;
		return ((TimeFormat) format).noClone();
	}

	/**
	 * 规整格式
	 * @param regex 格式串
	 * @return 新格式串
	 */
	protected static String pattern(String regex, Locale locale) {
		regex = Stringure.trimToNull(regex);
		if (regex == null || locale == null) return null;
		String key = Stringure.join(':', locale.toString(), regex);
		String p = ps.get(key);
		if (p == null) ps.put(key, p = new SimpleDateFormat(regex, locale).toPattern());
		return p;
	}

	private final int dateStyle;
	private final int timeStyle;

	private final TimeZone zone;
	private final Locale locale;
	private final int flag;

	private transient Integer hashCodeValue;
	private transient String key;
	private transient String pattern;

	/**
	 * 构造函数
	 * @param flag 类型
	 * @param dateStyle 日期样式
	 * @param timeStyle 时间样式
	 * @param locale 语言区域
	 * @param zone 时区
	 */
	protected TimeFormat(int flag, int dateStyle, int timeStyle, Locale locale, TimeZone zone) {
		super();
		this.flag = flag;

		this.dateStyle = dateStyle;
		this.timeStyle = timeStyle;

		this.locale = locale != null ? locale : Environments.defaultLocale();
		this.zone = zone != null ? zone : Environments.defaultTimeZone();
	}

	/**
	 * 构造函数
	 * @param pattern 格式表达式
	 * @param locale 语言区域
	 * @param zone 时区
	 */
	protected TimeFormat(String pattern, Locale locale, TimeZone zone) {
		this(0, 0, 0, locale, zone);
		if (pattern == null) throw new NullPointerException();
		this.pattern = pattern;
	}

	public Object clone() {
		TimeFormat tf = new TimeFormat(flag, dateStyle, timeStyle, (Locale) locale.clone(), (TimeZone) zone.clone());
		tf.pattern = pattern;
		return tf;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TimeFormat)) return false;
		TimeFormat tar = (TimeFormat) obj;
		return this.flag == tar.flag && this.dateStyle == tar.dateStyle && this.timeStyle == tar.timeStyle && this.locale.equals(tar.locale)
				&& this.zone.equals(tar.zone) && Assertor.P.equals(this.pattern, tar.pattern);
	}

	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		return realFormat().format(date, toAppendTo, fieldPosition);
	}

	/**
	 * 获取日期样式
	 * @return 日期样式
	 */
	public int getDateStyle() {
		return dateStyle;
	}

	/**
	 * 获取时间样式
	 * @return 时间样式
	 */
	public int getTimeStyle() {
		return timeStyle;
	}

	public TimeZone getTimeZone() {
		return zone;
	}

	public int hashCode() {
		return hashCodeValue == null
				? (hashCodeValue = flag ^ dateStyle ^ timeStyle ^ (pattern == null ? 0 : pattern.hashCode()) ^ locale.hashCode()
						^ zone.getRawOffset())
				: hashCodeValue;
	}

	public Date parse(String source, ParsePosition pos) {
		return realFormat().parse(source, pos);
	}

	public void setCalendar(Calendar newCalendar) {}

	public void setLenient(boolean lenient) {}

	public void setNumberFormat(NumberFormat newNumberFormat) {}

	public void setTimeZone(TimeZone zone) {}

	/**
	 * 转换为格式表达式
	 * @return 格式表达式
	 */
	public String toPattern() {
		return pattern == null ? pattern = realFormat().toPattern() : pattern;
	}

	public String toString() {
		return toPattern();
	}

	/**
	 * 生成禁止克隆格式化器
	 * @return 格式化器
	 */
	protected DateFormat noClone() {
		return new NoClone();
	}

	/**
	 * 获取真实时间格式化工具
	 * @return 时间格式
	 */
	protected SimpleDateFormat realFormat() {
		Map<String, SimpleDateFormat> map = cache.get();

		String key = key();
		SimpleDateFormat format = map.get(key);
		if (format == null) map.put(key, format = create());

		return format;
	}

	/**
	 * 获取格式键值
	 * @return 键值
	 */
	String key() {
		if (key == null) {
			synchronized (this) {
				if (key == null) {
					StringBuilder buf = new StringBuilder();
					buf.append(flag).append(':');
					buf.append(dateStyle).append(':');
					buf.append(timeStyle).append(':');
					buf.append(locale.toString()).append(':');
					buf.append(zone.getRawOffset()).append(':');
					buf.append(Stringure.trim(pattern));
					key = buf.toString();
				}
			}
		}
		return key;
	}

	/**
	 * 创建真实格式
	 * @return 真实格式
	 */
	private SimpleDateFormat create() {
		SimpleDateFormat format = null;
		switch (flag) {
		case 1:
			format = (SimpleDateFormat) DateFormat.getDateInstance(dateStyle, locale);
			break;
		case 2:
			format = (SimpleDateFormat) DateFormat.getTimeInstance(timeStyle, locale);
			break;
		case 3:
			format = (SimpleDateFormat) DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
			break;

		default:
			format = new SimpleDateFormat(pattern, locale);
			break;
		}
		format.setTimeZone(zone);

		return format;
	}

	/**
	 * 禁止克隆
	 * @author demon 2018-09-04
	 */
	protected class NoClone extends DateFormat {

		private static final long serialVersionUID = -6925917909813233263L;

		public Object clone() {
			return this;
		}

		public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
			return TimeFormat.this.format(date, toAppendTo, fieldPosition);
		}

		public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
			return TimeFormat.this.formatToCharacterIterator(obj);
		}

		public Date parse(String source) throws ParseException {
			return TimeFormat.this.parse(source);
		}

		public Date parse(String source, ParsePosition pos) {
			return TimeFormat.this.parse(source, pos);
		}

		public Object parseObject(String source) throws ParseException {
			return TimeFormat.this.parseObject(source);
		}

		public Object parseObject(String source, ParsePosition pos) {
			return TimeFormat.this.parseObject(source, pos);
		}
	}
}

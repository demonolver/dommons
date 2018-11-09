/*
 * @(#)NumericFormat.java     2011-10-18
 */
package org.dommons.core.format.number;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.thread.ThreadCache;

/**
 * 数字格式化 解决 JDK 默认数字格式式化的并发问题
 * @author Demon 2011-10-18
 */
public class NumericFormat extends NumberFormat {

	private static final long serialVersionUID = 7037541181017277064L;

	static final ThreadCache<Map<NumericFormat, DecimalFormat>> cache = new ThreadCache(WeakHashMap.class);

	static final Map<String, NumericFormat> modelCache = new ConcurrentSoftMap();

	/** 货币符 */
	public static final char currency = '\u00A4';
	/** 千分符 */
	public static final char permille = '\u2030';

	/** 默认格式 */
	public static final int DEFAULT = 1;
	/** 货币格式 */
	public static final int CURRENCY = 2;
	/** 百分数格式 */
	public static final int PERCENT = 3;
	/** 整型格式 */
	public static final int INTEGER = 4;

	/**
	 * 编译格式
	 * @param pattern 格式串
	 * @return 数字格式化
	 */
	public static NumberFormat compile(String pattern) {
		if (pattern == null) throw new NullPointerException();
		return new NumericFormat(pattern);
	}

	/**
	 * 编译格式
	 * @param pattern 格式串
	 * @param locale 语言区域
	 * @return 数字格式化
	 */
	public static NumberFormat compile(String pattern, Locale locale) {
		if (pattern == null) throw new NullPointerException();
		return new NumericFormat(pattern, locale);
	}

	/**
	 * 获取格式化实例
	 * @param type 格式类型
	 * @return 数字格式化器
	 */
	public static NumberFormat getInstance(int type) {
		return getInstance(null, type);
	}

	/**
	 * 获取格式化实例
	 * @param locale 语言区域
	 * @param type 格式类型
	 * @return 数字格式化器
	 */
	public static NumberFormat getInstance(Locale locale, int type) {
		Assertor.F.between(type, DEFAULT, PERCENT, "Illegal type '" + type + "'");
		String key = Stringure.join(':', locale, type);
		NumericFormat format = modelCache.get(key);
		if (format == null) modelCache.put(key, format = new NumericFormat(type, locale));
		return format;
	}

	private int type;
	private String pattern;
	private final Locale locale;

	private transient volatile int hashCodeValue = 0;

	/**
	 * 构造函数
	 * @param pattern 格式串
	 */
	public NumericFormat(String pattern) {
		this(pattern, null);
	}

	/**
	 * 构造函数
	 * @param pattern 格式串
	 * @param locale 语言区域
	 */
	public NumericFormat(String pattern, Locale locale) {
		super();
		this.type = -1;
		this.locale = locale != null ? locale : Environments.defaultLocale();
		this.pattern = new DecimalFormat(pattern, new DecimalFormatSymbols(this.locale)).toPattern();
	}

	/**
	 * 构造函数
	 * @param type 默认格式类型
	 * @param locale 语言区域
	 */
	protected NumericFormat(int type, Locale locale) {
		super();
		if (type < DEFAULT || type > INTEGER) throw new IllegalArgumentException("invalid type");
		this.type = type;
		this.locale = locale != null ? locale : Environments.defaultLocale();
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof NumericFormat)) return false;
		NumericFormat tar = (NumericFormat) obj;
		return this.type == tar.type && this.locale.equals(tar.locale) && Assertor.P.equals(this.pattern, tar.pattern);
	}

	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return number != number ? toAppendTo.append("NaN") : toRealFormat().format(number, toAppendTo, pos);
	}

	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return toRealFormat().format(number, toAppendTo, pos);
	}

	public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
		return number == null ? toAppendTo : toRealFormat().format(number, toAppendTo, pos);
	}

	/**
	 * 获取默认类型
	 * @return 默认类型
	 */
	public int getType() {
		return type;
	}

	public int hashCode() {
		return hashCodeValue == 0 ? (hashCodeValue = type ^ (pattern == null ? 0 : pattern.hashCode()) ^ locale.hashCode()) : hashCodeValue;
	}

	public Number parse(String source, ParsePosition parsePosition) {
		return toRealFormat().parse(source, parsePosition);
	}

	/**
	 * 转换为格式串
	 * @return 格式串
	 */
	public String toPattern() {
		return pattern != null ? pattern : (pattern = toRealFormat().toPattern());
	}

	/**
	 * 转换为真实格式化器
	 * @return 真实格式化器
	 */
	DecimalFormat toRealFormat() {
		Map<NumericFormat, DecimalFormat> map = cache.get();

		DecimalFormat format = map.get(this);
		if (format == null) map.put(this, format = create());

		return format;
	}

	/**
	 * 创建真实格式化器
	 * @return 真实格式化器
	 */
	private DecimalFormat create() {
		DecimalFormat format = null;
		switch (type) {
		case DEFAULT:
			format = (DecimalFormat) NumberFormat.getInstance(locale);
			break;
		case CURRENCY:
			format = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
			break;
		case PERCENT:
			format = (DecimalFormat) NumberFormat.getPercentInstance(locale);
			break;
		case INTEGER:
			format = (DecimalFormat) NumberFormat.getIntegerInstance(locale);
			break;
		default:
			format = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
			break;
		}
		format.setParseBigDecimal(true);
		return format;
	}
}

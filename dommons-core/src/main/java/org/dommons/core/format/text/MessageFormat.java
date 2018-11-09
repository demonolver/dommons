/*
 * @(#)MessageFormat.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import org.dommons.core.Environments;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.thread.ThreadCache;

/**
 * 文本信息格式
 * <table>
 * <tr>
 * <td><i>pattern:</i><code>[content]</code> <code>{ ArgumentIndex , FormatType , FormatStyle }</code> <code>[content]</code>...</td>
 * </tr>
 * </table>
 * <p>
 * <table border=1>
 * <tr>
 * <th id="ft">Format Type
 * <th id="fs">Format Style
 * <th id="sc">Subformat Created
 * </tr>
 * <tr>
 * <td headers="ft"><i>(none)</i>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc"><code>null</code>
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=5><code>number</code>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc"><code>NumberFormat.getInstance(getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>integer</code>
 * <td headers="sc"><code>NumberFormat.getIntegerInstance(getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>currency</code>
 * <td headers="sc"><code>NumberFormat.getCurrencyInstance(getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>percent</code>
 * <td headers="sc"><code>NumberFormat.getPercentInstance(getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new DecimalFormat(subformatPattern, new DecimalFormatSymbols(getLocale()))</code>
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=3><code>radix</code>
 * <td headers="fs"><code>hex</code>
 * <td headers="sc"><code>new RadixFormat("{16}")</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>binary</code>
 * <td headers="sc"><code>new RaidxFormat("{2}")</code>
 * </tr>
 * <tr>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new RadixFormat(subformatPattern)</code>
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=6><code>date</code>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>short</code>
 * <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.SHORT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>medium</code>
 * <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>long</code>
 * <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.LONG, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>full</code>
 * <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.FULL, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new SimpleDateFormat(subformatPattern, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=6><code>time</code>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.DEFAULT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>short</code>
 * <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.SHORT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>medium</code>
 * <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.DEFAULT, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>long</code>
 * <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.LONG, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><code>full</code>
 * <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.FULL, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new SimpleDateFormat(subformatPattern, getLocale())</code>
 * </tr>
 * <tr>
 * <td headers="ft"><code>choice</code>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new ChoiceFormat(subformatPattern)</code>
 * </tr>
 * <tr>
 * <td headers="ft"><code>text</code>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>new TextFormat(subformatPattern)</code>
 * </tr>
 * <tr>
 * <td headers="ft"><code>complex</code>
 * <td headers="fs"><i>SubformatPattern</i>
 * <td headers="sc"><code>{FormatType , FormatStyle}, textPattern</code>
 * </tr>
 * </table>
 * <p>
 * @author Demon 2011-10-18
 * @see java.text.MessageFormat
 */
public class MessageFormat extends Format {
	private static final long serialVersionUID = 505964793270592631L;

	static final ThreadCache<Map<String, MessageFormat>> models = new ThreadCache(WeakHashMap.class);

	/**
	 * 格式编译
	 * @param pattern 格式串
	 * @return 文本信息格式
	 */
	public static MessageFormat compile(CharSequence pattern) {
		return compile(pattern, null);
	}

	/**
	 * 格式编译
	 * @param pattern 格式串
	 * @param locale 语言区域
	 * @return 文本信息格式
	 */
	public static MessageFormat compile(CharSequence pattern, Locale locale) {
		if (locale == null) locale = Environments.defaultLocale();
		String key = Stringure.join(':', locale, pattern);
		Map<String, MessageFormat> map = models.get();
		MessageFormat mf = map.get(key);
		if (mf == null) map.put(key, mf = new MessageFormat(pattern, locale));
		return mf;
	}

	/**
	 * 格式化
	 * @param pattern 格式串
	 * @param arguments 参数集
	 * @return 格式化内容串
	 */
	public static String format(CharSequence pattern, Object... arguments) {
		return compile(pattern).format(arguments);
	}

	private final Locale locale;

	private MessagePattern pattern;
	private transient MessageFormatter formatter;
	private transient MessageParser parser;

	/**
	 * 构造函数
	 * @param pattern 格式定义串
	 */
	protected MessageFormat(CharSequence pattern) {
		this(pattern, null);
	}

	/**
	 * 构造函数
	 * @param pattern 格式定义串
	 * @param locale 语言区域
	 */
	protected MessageFormat(CharSequence pattern, Locale locale) {
		this.locale = locale == null ? Environments.defaultLocale() : locale;
		if (pattern != null) applyPattern(pattern);
	}

	public boolean equals(Object obj) {
		if (this == obj) // quick check
			return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MessageFormat other = (MessageFormat) obj;
		return pattern.equals(other.pattern);
	}

	/**
	 * 格式化
	 * @param arguments 参数集
	 * @return 结果
	 */
	public final String format(Object... arguments) {
		return format(new StringBuffer(), new FieldPosition(0), arguments).toString();
	}

	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		return format(toAppendTo, pos, (obj instanceof Object[] ? (Object[]) obj : new Object[] { obj }));
	}

	/**
	 * 格式化
	 * @param result 结果缓冲区
	 * @param pos 字段项位置
	 * @param arguments 参数集
	 * @return 结果
	 */
	public final StringBuffer format(StringBuffer result, FieldPosition pos, Object... arguments) {
		return getFormatter().format(arguments, result, pos, null);
	}

	public AttributedCharacterIterator formatToCharacterIterator(Object arguments) {
		StringBuffer result = new StringBuffer();
		List<AttributedCharacterIterator> iterators = new ArrayList();

		if (arguments == null) {
			throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
		}
		getFormatter().format((Object[]) arguments, result, null, iterators);
		if (iterators.size() == 0) return AttributedCharacterTools.createIterator(Stringure.empty);

		return AttributedCharacterTools.createIterator(iterators.toArray(new AttributedCharacterIterator[iterators.size()]));
	}

	public int hashCode() {
		return pattern.hashCode();
	}

	/**
	 * 解析字符串
	 * @param source 文本内容
	 * @return 数据集
	 * @throws ParseException
	 */
	public Object[] parse(String source) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		Object[] result = parse(source, pos);
		if (pos.getIndex() == 0) throw new ParseException("MessageFormat parse error!", pos.getErrorIndex());

		return result;
	}

	/**
	 * 解析字符串
	 * @param source 文本内容
	 * @param pos 解析起始位
	 * @return 数据集
	 */
	public Object[] parse(String source, ParsePosition pos) {
		return getParser().parse(source, pos);
	}

	public Object parseObject(String source, ParsePosition pos) {
		return parse(source, pos);
	}

	/**
	 * 转换为格式串
	 * @return 格式串
	 */
	public String toPattern() {
		return pattern.toString();
	}

	public String toString() {
		return toPattern();
	}

	/**
	 * 应用格式化模版
	 * @param pattern 模版
	 */
	protected void applyPattern(CharSequence pattern) {
		this.pattern = MessagePattern.parse(pattern, locale);
		formatter = null;
		parser = null;
	}

	/**
	 * 获取格式化工具
	 * @return 格式化工具
	 */
	protected MessageFormatter getFormatter() {
		return formatter != null ? formatter : (formatter = new MessageFormatter(pattern));
	}

	/**
	 * 获取解析工具
	 * @return 解析工具
	 */
	protected MessageParser getParser() {
		return parser != null ? parser : (parser = new MessageParser(pattern));
	}
}

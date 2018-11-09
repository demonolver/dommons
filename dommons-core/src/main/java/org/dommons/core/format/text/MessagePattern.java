/*
 * @(#)MessagePattern.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.Format;
import java.util.Locale;

import org.dommons.core.Assertor;
import org.dommons.core.convert.Converter;
import org.dommons.core.format.date.TimeFormat;
import org.dommons.core.format.number.NumericFormat;
import org.dommons.core.format.number.RadixFormat;
import org.dommons.core.util.Arrayard;

/**
 * 信息格式定义
 * @author Demon 2011-10-18
 */
class MessagePattern implements Serializable, Cloneable {

	private static final long serialVersionUID = -2615497230849065631L;

	/** 起始格式数量 */
	static final int INITIAL_FORMATS = 10;

	/** 子格式种类 */
	protected static final String[] typeList = { "", "number", "radix", "date", "time", "choice", "text", "complex" };

	/** 数字格式模型 */
	protected static final String[] modifierList = { "", "currency", "percent", "integer" };

	/** 时间格式模型 */
	protected static final String[] dateModifierList = { "", "short", "medium", "long", "full" };

	/** 进制格式模型 */
	protected static final String[] radixModifierList = { "hex", "binary" };

	protected static final String hexPattern = "{16}";

	protected static final String binaryPattern = "{2}";

	/**
	 * 解析格式定义
	 * @param pattern 格式串
	 * @param locale 语言区域
	 * @return 格式定义
	 */
	public static MessagePattern parse(CharSequence pattern, Locale locale) {
		MessagePattern mp = new MessagePattern(pattern, locale);
		mp.parse();
		return mp;
	}

	/**
	 * 复制并转换引号
	 * @param source 原文
	 * @param start 起始位置
	 * @param end 结束位置
	 * @param target 目标缓冲区
	 */
	protected static final void copyAndFixQuotes(CharSequence source, int start, int end, Appendable target) {
		for (int i = start; i < end; ++i) {
			char ch = source.charAt(i);
			if (ch == FormatKeyword.left.charValue()) {
				FormatKeyword.left.convert(target);
			} else if (ch == FormatKeyword.right.charValue()) {
				FormatKeyword.right.convert(target);
			} else if (ch == FormatKeyword.quote.charValue()) {
				FormatKeyword.quote.convert(target);
			} else {
				try {
					target.append(ch);
				} catch (IOException e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
			}
		}
	}

	/**
	 * 查找关键字位置
	 * @param s 字符串
	 * @param list 关键字列表
	 * @return 位置
	 */
	protected static final int findKeyword(String s, String[] list) {
		s = s.trim().toLowerCase();
		for (int i = 0; i < list.length; ++i) {
			if (s.equals(list[i])) return i;
		}
		return -1;
	}

	/**
	 * 转换子格式
	 * @param format 子格式
	 * @param locale 语言区域
	 * @param result 结果缓冲区
	 * @return 是否转换成功
	 */
	protected static boolean subToPattern(Format format, Locale locale, StringBuilder result) {
		if (format instanceof NumericFormat) {
			result.append(typeList[1]);
			switch (((NumericFormat) format).getType()) {
			case NumericFormat.DEFAULT:
				// do nothing
				break;
			case NumericFormat.CURRENCY:
				result.append(FormatKeyword.comma.charValue()).append(modifierList[1]);
				break;
			case NumericFormat.PERCENT:
				result.append(FormatKeyword.comma.charValue()).append(modifierList[2]);
				break;
			case NumericFormat.INTEGER:
				result.append(FormatKeyword.comma.charValue()).append(modifierList[3]);
				break;
			default:
				result.append(FormatKeyword.comma.charValue()).append(((NumericFormat) format).toPattern());
				break;
			}
		} else if (format instanceof RadixFormat) {
			String pattern = ((RadixFormat) format).toPattern();
			if (pattern.equals(hexPattern)) {
				result.append(typeList[2]).append(FormatKeyword.comma.charValue()).append(radixModifierList[0]);
			} else if (pattern.equals(binaryPattern)) {
				result.append(typeList[2]).append(FormatKeyword.comma.charValue()).append(radixModifierList[1]);
			} else {
				result.append(typeList[2]).append(FormatKeyword.comma.charValue()).append(pattern);
			}
		} else if (format instanceof TimeFormat) {
			boolean date = ((TimeFormat) format).getDateStyle() >= 0;
			int type = date ? ((TimeFormat) format).getDateStyle() : ((TimeFormat) format).getTimeStyle();
			switch (type) {
			case DateFormat.DEFAULT:
				break;
			case DateFormat.SHORT:
				result.append(typeList[date ? 3 : 4]).append(FormatKeyword.comma.charValue()).append(dateModifierList[1]);
				break;
			case DateFormat.LONG:
				result.append(typeList[date ? 3 : 4]).append(FormatKeyword.comma.charValue()).append(dateModifierList[3]);
				break;
			case DateFormat.FULL:
				result.append(typeList[date ? 3 : 4]).append(FormatKeyword.comma.charValue()).append(dateModifierList[4]);
				break;
			default:
				result.append(typeList[3]).append(FormatKeyword.comma.charValue()).append(((TimeFormat) format).toPattern());
				break;
			}
		} else if (format instanceof ChoiceFormat) {
			result.append(typeList[5]).append(FormatKeyword.comma.charValue()).append(((ChoiceFormat) format).toPattern());
		} else if (format instanceof TextFormat) {
			result.append(typeList[6]).append(FormatKeyword.comma.charValue()).append(((TextFormat) format).toPattern());
		} else if (format instanceof ComplexFormat) {
			result.append(typeList[7]).append(FormatKeyword.comma.charValue()).append(((ComplexFormat) format).toPattern());
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 解析日期格式
	 * @param segment 子格式串
	 * @param locale 语言区域
	 * @return 子格式
	 */
	static Format parseDate(String segment, Locale locale) {
		switch (findKeyword(segment, dateModifierList)) {
		case 0: // default
			return TimeFormat.getInstance(DateFormat.DEFAULT, -1, locale);
		case 1: // short
			return TimeFormat.getInstance(DateFormat.SHORT, -1, locale);
		case 2: // medium
			return TimeFormat.getInstance(DateFormat.MEDIUM, -1, locale);
		case 3: // long
			return TimeFormat.getInstance(DateFormat.LONG, -1, locale);
		case 4: // full
			return TimeFormat.getInstance(DateFormat.FULL, -1, locale);
		default: // pattern
			return TimeFormat.compile(segment, locale);
		}
	}

	/**
	 * 解析数字格式
	 * @param segment 子格式串
	 * @param locale 语言区域
	 * @return 子格式
	 */
	static Format parseNumber(String segment, Locale locale) {
		switch (findKeyword(segment, modifierList)) {
		case 0: // default;
			return NumericFormat.getInstance(locale, NumericFormat.DEFAULT);
		case 1: // currency
			return NumericFormat.getInstance(locale, NumericFormat.CURRENCY);
		case 2: // percent
			return NumericFormat.getInstance(locale, NumericFormat.PERCENT);
		case 3: // integer
			return NumericFormat.getInstance(locale, NumericFormat.INTEGER);
		default: // pattern
			return new NumericFormat(segment, locale);
		}
	}

	/**
	 * 解析进制格式
	 * @param segment 子格式串
	 * @param locale 语言区域
	 * @return 子格式
	 */
	static Format parseRadix(String segment, Locale locale) {
		switch (findKeyword(segment, radixModifierList)) {
		case 0: // hex
			return new RadixFormat(hexPattern);
		case 1: // binary
			return new RadixFormat(binaryPattern);
		default:
			return new RadixFormat(segment);
		}
	}

	/**
	 * 解析时间格式
	 * @param segment 子格式串
	 * @param locale 语言区域
	 * @return 子格式
	 */
	static Format parseTime(String segment, Locale locale) {
		switch (findKeyword(segment.toString(), dateModifierList)) {
		case 0: // default
			return TimeFormat.getInstance(-1, DateFormat.DEFAULT, locale);
		case 1: // short
			return TimeFormat.getInstance(-1, DateFormat.SHORT, locale);
		case 2: // medium
			return TimeFormat.getInstance(-1, DateFormat.MEDIUM, locale);
		case 3: // long
			return TimeFormat.getInstance(-1, DateFormat.LONG, locale);
		case 4: // full
			return TimeFormat.getInstance(-1, DateFormat.FULL, locale);
		default:
			return TimeFormat.compile(segment, locale);
		}
	}

	/** 语言区域 */
	protected final Locale locale;
	/** 子格式串 */
	protected Format[] formats = new Format[INITIAL_FORMATS];
	/** 格式位序 */
	protected int[] offsets = new int[INITIAL_FORMATS];
	/** 参数位序 */
	protected int[] argumentNumbers = new int[INITIAL_FORMATS];

	/** 格式总数 */
	protected int maxOffset = -1;
	/** 起始格式 */
	protected String pattern;

	private transient String patternValue;

	/**
	 * 构造函数
	 * @param pattern 模版串
	 * @param locale 语言区域
	 */
	protected MessagePattern(CharSequence pattern, Locale locale) {
		this.locale = locale;
		this.pattern = String.valueOf(pattern);
	}

	public MessagePattern clone() throws CloneNotSupportedException {
		MessagePattern other = (MessagePattern) super.clone();

		// 克隆子格式
		other.formats = formats.clone();
		for (int i = 0; i < formats.length; ++i) {
			if (formats[i] != null) other.formats[i] = (Format) formats[i].clone();
		}
		// 克隆位置定义
		other.offsets = offsets.clone();
		other.argumentNumbers = argumentNumbers.clone();

		return other;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MessagePattern other = (MessagePattern) obj;
		// 子属性比较
		return (maxOffset == other.maxOffset && pattern.equals(other.pattern) && Assertor.P.equals(locale, other.locale)
				&& Arrayard.equals(offsets, other.offsets) && Arrayard.equals(argumentNumbers, other.argumentNumbers)
				&& Arrayard.equals(formats, other.formats));
	}

	/**
	 * 获取参数编号
	 * @param index 位序
	 * @return 参数编号
	 */
	public int getArgumentNumber(int index) {
		if (index < 0 || index >= maxOffset) throw new ArrayIndexOutOfBoundsException(index);
		return argumentNumbers[index];
	}

	/**
	 * 获取子格式
	 * @param index 位序
	 * @return 子格式
	 */
	public Format getFormat(int index) {
		if (index < 0 || index >= maxOffset) throw new ArrayIndexOutOfBoundsException(index);
		return formats[index];
	}

	/**
	 * 获取格式数量
	 * @return 数量
	 */
	public int getMaxOffset() {
		return maxOffset;
	}

	/**
	 * 获取位置
	 * @param index 位序
	 * @return 位置
	 */
	public int getOffset(int index) {
		if (index < 0 || index >= maxOffset) throw new ArrayIndexOutOfBoundsException(index);
		return offsets[index];
	}

	/**
	 * 获取起始格式
	 * @return 格式串
	 */
	public String getPattern() {
		return String.valueOf(pattern);
	}

	public int hashCode() {
		return pattern.hashCode();
	}

	/**
	 * 格式解析
	 */
	public void parse() {
		StringBuilder[] segments = new StringBuilder[4];
		for (int i = 0; i < segments.length; ++i) {
			segments[i] = new StringBuilder();
		}
		int part = 0;
		int formatNumber = 0;
		boolean inQuote = false;
		int braceStack = 0;
		maxOffset = -1;
		for (int i = 0; i < pattern.length(); ++i) {
			char ch = pattern.charAt(i);
			if (part == 0) {
				if (ch == FormatKeyword.quote.charValue()) {
					if (i + 1 < pattern.length() && pattern.charAt(i + 1) == FormatKeyword.quote.charValue()) {
						segments[part].append(ch); // 处理连续引号
						++i;
					} else {
						inQuote = !inQuote;
					}
				} else if (ch == FormatKeyword.left.charValue() && !inQuote) {
					part = 1;
				} else {
					segments[part].append(ch);
				}
			} else if (inQuote) { // 将引号内的内容直接加入缓冲区
				segments[part].append(ch);
				if (ch == FormatKeyword.quote.charValue()) {
					inQuote = false;
				}
			} else {
				if (FormatKeyword.comma.equals(ch)) {
					if (part < 3) part += 1;
					else segments[part].append(ch);
				} else if (FormatKeyword.left.equals(ch)) {
					++braceStack;
					segments[part].append(ch);
				} else if (FormatKeyword.right.equals(ch)) {
					if (braceStack == 0) {
						part = 0;
						makeFormat(i, formatNumber++, segments);
					} else {
						--braceStack;
						segments[part].append(ch);
					}
				} else if (FormatKeyword.quote.equals(ch)) {
					inQuote = true;
					segments[part].append(ch);
				} else {
					segments[part].append(ch);
				}
			}
		}
		if (braceStack == 0 && part != 0) {
			maxOffset = -1;
			throw new IllegalArgumentException("Unmatched braces in the pattern.");
		}
		this.pattern = segments[0].toString();
	}

	/**
	 * 转换为格式串
	 * @return 格式串
	 */
	public String toPattern() {
		int lastOffset = 0;
		StringBuilder result = new StringBuilder();
		// 逐个将子格式转换为定义串
		for (int i = 0; i <= maxOffset; ++i) {
			copyAndFixQuotes(pattern, lastOffset, offsets[i], result);
			lastOffset = offsets[i];
			result.append(FormatKeyword.left.charValue()).append(argumentNumbers[i]);
			if (formats[i] == null) {
				// do nothing, string format
			} else {
				result.append(FormatKeyword.comma.charValue());
				if (!subToPattern(formats[i], locale, result)) result.setLength(result.length() - 1);
			}
			result.append(FormatKeyword.right.charValue());
		}
		copyAndFixQuotes(pattern, lastOffset, pattern.length(), result);
		return result.toString();
	}

	public String toString() {
		if (patternValue == null) patternValue = toPattern();
		return patternValue;
	}

	/**
	 * 构建格式
	 * @param position 起始位置
	 * @param offsetNumber 数量
	 * @param segments 格式串
	 */
	protected void makeFormat(int position, int offsetNumber, StringBuilder[] segments) {
		// 解析子格式参数位序
		int argumentNumber;
		try {
			argumentNumber = Integer.parseInt(segments[1].toString()); // always unlocalized!
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("can't parse argument number " + segments[1]);
		}
		if (argumentNumber < 0) {
			throw new IllegalArgumentException("negative argument number " + argumentNumber);
		}

		// 扩容子格式数组
		if (offsetNumber >= formats.length) {
			int newLength = formats.length * 2;
			Format[] newFormats = new Format[newLength];
			int[] newOffsets = new int[newLength];
			int[] newArgumentNumbers = new int[newLength];
			System.arraycopy(formats, 0, newFormats, 0, maxOffset + 1);
			System.arraycopy(offsets, 0, newOffsets, 0, maxOffset + 1);
			System.arraycopy(argumentNumbers, 0, newArgumentNumbers, 0, maxOffset + 1);
			formats = newFormats;
			offsets = newOffsets;
			argumentNumbers = newArgumentNumbers;
		}
		int oldMaxOffset = maxOffset;
		maxOffset = offsetNumber;
		offsets[offsetNumber] = segments[0].length();
		argumentNumbers[offsetNumber] = argumentNumber;

		// 解析子格式
		Format newFormat = null;
		switch (findKeyword(segments[2].toString(), typeList)) {
		case 0:
			break;
		case 1: // number
			newFormat = parseNumber(segments[3].toString(), locale);
			break;
		case 2: // radix
			newFormat = parseRadix(segments[3].toString(), locale);
			break;
		case 3: // date
			newFormat = parseDate(segments[3].toString(), locale);
			break;
		case 4:// time
			newFormat = parseTime(segments[3].toString(), locale);
			break;
		case 5:// choice
			try {
				newFormat = new ChoiceFormat(segments[3].toString());
			} catch (Exception e) {
				maxOffset = oldMaxOffset;
				throw new IllegalArgumentException("Choice Pattern incorrect");
			}
			break;
		case 6:// text
			try {
				newFormat = new TextFormat(segments[3].toString());
			} catch (Exception e) {
				maxOffset = oldMaxOffset;
				throw new IllegalArgumentException("Text Pattern incorrect");
			}
			break;
		case 7:// complex
			try {
				newFormat = new ComplexFormat(segments[3].toString(), locale);
			} catch (Exception e) {
				maxOffset = oldMaxOffset;
				throw new IllegalArgumentException("Extend Pattern incorrect");
			}
			break;
		default:
			maxOffset = oldMaxOffset;
			throw new IllegalArgumentException("unknown format type at ");
		}
		formats[offsetNumber] = newFormat;
		// 清空缓冲区
		segments[1].setLength(0);
		segments[2].setLength(0);
		segments[3].setLength(0);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		boolean isValid = maxOffset >= -1 && formats.length > maxOffset && offsets.length > maxOffset && argumentNumbers.length > maxOffset;
		if (isValid) {
			int lastOffset = pattern.length() + 1;
			for (int i = maxOffset; i >= 0; --i) {
				if ((offsets[i] < 0) || (offsets[i] > lastOffset)) {
					isValid = false;
					break;
				} else {
					lastOffset = offsets[i];
				}
			}
		}
		if (!isValid) {
			throw new InvalidObjectException("Could not reconstruct MessagePattern from corrupt stream.");
		}
	}
}

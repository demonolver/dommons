/*
 * @(#)ComplexFormat.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

/**
 * 文本复合格式
 * @author Demon 2011-10-18
 * @see MessageFormat
 */
class ComplexFormat extends Format {

	private static final long serialVersionUID = -7747949260745414642L;

	private final Locale locale;

	private Format sub;
	private TextFormat text;

	/**
	 * 构造函数
	 * @param pattern 格式串
	 * @param locale 语言区域
	 */
	public ComplexFormat(String pattern, Locale locale) {
		this.locale = locale;
		applyPattern(pattern);
	}

	/**
	 * 应用格式串
	 * @param pattern 格式串
	 */
	public void applyPattern(String pattern) {
		if (pattern == null || pattern.length() == 0) return;
		Format sub = null;
		TextFormat text = null;

		StringBuilder[] buffers = new StringBuilder[3];
		for (int i = 0; i < 3; i++) {
			buffers[i] = new StringBuilder();
		}
		int len = pattern.length();

		int n = 0;
		int braceStack = 0;
		boolean inSub = false;
		boolean inQuote = false;
		for (int i = 0; i < len; i++) {
			char ch = pattern.charAt(i);
			if (i == 0 && FormatKeyword.left.equals(ch)) {
				inSub = true;
			} else if (inQuote) {
				buffers[n].append(ch);
				if (FormatKeyword.quote.equals(ch)) inQuote = false;
			} else if (inSub) {
				if (FormatKeyword.left.equals(ch)) {
					buffers[n].append(ch);
					braceStack++;
				} else if (FormatKeyword.right.equals(ch)) {
					if (braceStack == 0) {
						inSub = false;
						sub = parseSubFormat(buffers);
					} else {
						braceStack--;
						buffers[n].append(ch);
					}
				} else if (FormatKeyword.quote.equals(ch)) {
					buffers[n].append(ch);
					inQuote = true;
				} else if (FormatKeyword.comma.equals(ch)) {
					if (n < 1) n++;
				} else {
					buffers[n].append(ch);
				}
			} else if (FormatKeyword.comma.equals(ch) && n != 2) {
				n = 2;
			} else {
				n = 2;
				buffers[2].append(ch);
			}
		}

		text = new TextFormat(buffers[2].toString(), locale);

		this.sub = sub;
		this.text = text;
	}

	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		return text.format(subFormat(obj), toAppendTo, pos);
	}

	public Object parseObject(String source, ParsePosition pos) {
		ParsePosition textPos = new ParsePosition(pos.getIndex());
		ParsePosition subPos = new ParsePosition(0);
		source = (String) text.parseObject(source, textPos);
		if (textPos.getIndex() == pos.getIndex()) {
			pos.setErrorIndex(pos.getIndex());
			return null;
		}
		Object value = sub.parseObject(source, subPos);
		if (subPos.getIndex() > 0) {
			pos.setIndex(textPos.getIndex());
		} else {
			pos.setErrorIndex(pos.getIndex());
		}
		return value;
	}

	/**
	 * 转换为格式串
	 * @return 格式串
	 */
	public String toPattern() {
		StringBuilder buffer = new StringBuilder();
		if (sub != null) {
			buffer.append(FormatKeyword.left.charValue());
			if (!MessagePattern.subToPattern(sub, locale, buffer)) return "";
			buffer.append(FormatKeyword.right.charValue()).append(FormatKeyword.comma.charValue());
		}
		buffer.append(text.toPattern());
		return buffer.toString();
	}

	/**
	 * 解析子格式
	 * @param buffers 内容字符缓冲集
	 * @return 子格式
	 */
	protected Format parseSubFormat(StringBuilder[] buffers) {
		Format newFormat = null;
		switch (MessagePattern.findKeyword(buffers[0].toString(), MessagePattern.typeList)) {
		case 0:
			break;
		case 1: // number
			newFormat = MessagePattern.parseNumber(buffers[1].toString(), locale);
			break;
		case 2: // radix
			newFormat = MessagePattern.parseRadix(buffers[1].toString(), locale);
			break;
		case 3: // date
			newFormat = MessagePattern.parseDate(buffers[1].toString(), locale);
			break;
		case 4:// time
			newFormat = MessagePattern.parseTime(buffers[1].toString(), locale);
			break;
		case 5:// choice
			try {
				newFormat = new ChoiceFormat(buffers[1].toString());
			} catch (Exception e) {
				throw new IllegalArgumentException("Choice Pattern incorrect");
			}
			break;
		default:
			throw new IllegalArgumentException("unknown format type at ");
		}
		buffers[0].setLength(0);
		buffers[1].setLength(0);
		return newFormat;
	}

	/**
	 * 子格式化
	 * @param obj 参数
	 * @return 格式串
	 */
	protected String subFormat(Object obj) {
		if (sub != null) {
			return sub.format(obj);
		} else {
			Format subFormatter = null;
			if (obj instanceof Number) {
				subFormatter = NumberFormat.getInstance(locale);
			} else if (obj instanceof Date) {
				subFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);// fix
			}

			return subFormatter == null ? String.valueOf(obj) : subFormatter.format(obj);
		}
	}

	/**
	 * 子格式解析
	 * @param source 原文本
	 * @param pos 解析位置
	 * @return 结果
	 */
	protected Object subParse(String source, ParsePosition pos) {
		if (sub != null) {
			return sub.parseObject(source, pos);
		} else {
			pos.setIndex(source.length());
			return source;
		}
	}
}

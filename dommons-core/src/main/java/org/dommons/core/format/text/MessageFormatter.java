/*
 * @(#)MessageFormatter.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat.Field;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;

/**
 * 信息格式化工具
 * @author Demon 2011-10-18
 */
class MessageFormatter {

	static final String NULL_STRING = "null";

	/**
	 * 追加内容
	 * @param result 结果缓冲区
	 * @param iterator 字符迭代器
	 */
	protected static void append(Appendable result, CharacterIterator iterator) {
		if (iterator.first() != CharacterIterator.DONE) {
			char aChar;

			try {
				result.append(iterator.first());
				while ((aChar = iterator.next()) != CharacterIterator.DONE) {
					result.append(aChar);
				}
			} catch (IOException e) {
				throw Converter.P.convert(e, RuntimeException.class);
			}
		}
	}

	/** 格式定义 */
	protected final MessagePattern pattern;

	/**
	 * 构造函数
	 * @param pattern 格式定义
	 */
	protected MessageFormatter(MessagePattern pattern) {
		Assertor.F.notNull(pattern);
		this.pattern = pattern;
	}

	/**
	 * 格式化
	 * @param arguments 参数集
	 * @param result 结果缓冲区
	 * @param fp 字段位置
	 * @param characterIterators 字符迭代器列表
	 * @return 结果缓冲区
	 */
	public StringBuffer format(
			Object[] arguments, StringBuffer result, FieldPosition fp, List<AttributedCharacterIterator> characterIterators) {
		int lastOffset = 0;
		int last = result.length();
		for (int i = 0; i <= pattern.maxOffset; ++i) {
			result.append(pattern.pattern.substring(lastOffset, pattern.offsets[i]));
			lastOffset = pattern.offsets[i];
			int argumentNumber = pattern.argumentNumbers[i];
			if (arguments == null || argumentNumber >= arguments.length) {
				result.append(FormatKeyword.left.charValue()).append(argumentNumber).append(FormatKeyword.right.charValue());
				continue;
			}
			Object obj = arguments[argumentNumber];
			String arg = null;
			Format subFormatter = null;
			if (obj == null) {
				arg = NULL_STRING;
			} else if (pattern.formats[i] != null) {
				subFormatter = pattern.formats[i];
				if (subFormatter instanceof ChoiceFormat) {
					arg = pattern.formats[i].format(obj);
					if (arg.indexOf(FormatKeyword.left.charValue()) >= 0) {
						subFormatter = new MessageFormat(arg, pattern.locale);
						obj = arguments;
						arg = null;
					}
				}
			} else if (obj instanceof Number) {
				subFormatter = NumberFormat.getInstance(pattern.locale);
			} else if (obj instanceof Date) {
				DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, pattern.locale);// fix
				format.setTimeZone(TimeZone.getTimeZone(Environments.getProperty("user.timezone")));
				subFormatter = format;
			} else if (obj instanceof String) {
				arg = (String) obj;
			} else {
				arg = obj.toString();
				if (arg == null) arg = NULL_STRING;
			}

			// At this point we are in two states, either subFormatter
			// is non-null indicating we should format obj using it,
			// or arg is non-null and we should use it as the value.
			if (characterIterators != null) {
				// If characterIterators is non-null, it indicates we need
				// to get the CharacterIterator from the child formatter.
				if (last != result.length()) {
					characterIterators.add(AttributedCharacterTools.createIterator(result.substring(last)));
					last = result.length();
				}
				if (subFormatter != null) {
					AttributedCharacterIterator subIterator = subFormatter.formatToCharacterIterator(obj);

					append(result, subIterator);
					if (last != result.length()) {
						characterIterators
								.add(AttributedCharacterTools.createIterator(subIterator, Field.ARGUMENT, new Integer(argumentNumber)));
						last = result.length();
					}
					arg = null;
				}
				if (arg != null && arg.length() > 0) {
					result.append(arg);
					characterIterators.add(AttributedCharacterTools.createIterator(arg, Field.ARGUMENT, new Integer(argumentNumber)));
					last = result.length();
				}
			} else {
				if (subFormatter != null) arg = subFormatter.format(obj);
				last = result.length();
				result.append(arg);
				if (i == 0 && fp != null && Field.ARGUMENT.equals(fp.getFieldAttribute())) {
					fp.setBeginIndex(last);
					fp.setEndIndex(result.length());
				}
				last = result.length();
			}
		}
		result.append(pattern.pattern.substring(lastOffset));
		if (characterIterators != null && last != result.length())
			characterIterators.add(AttributedCharacterTools.createIterator(result.substring(last)));
		return result;
	}
}

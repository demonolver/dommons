/*
 * @(#)MessageParser.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.text.ParsePosition;

import org.dommons.core.Assertor;

/**
 * 信息解析工具
 * @author Demon 2011-10-18
 */
class MessageParser {

	protected final MessagePattern pattern;

	/**
	 * 构造函数
	 * @param pattern 格式定义
	 */
	protected MessageParser(MessagePattern pattern) {
		Assertor.F.notNull(pattern);
		this.pattern = pattern;
	}

	/**
	 * 解析文本
	 * @param source 原文本
	 * @param pos 解析位置
	 * @return 参数集
	 */
	public Object[] parse(String source, ParsePosition pos) {
		if (source == null) {
			Object[] empty = {};
			return empty;
		}

		int maximumArgumentNumber = -1;
		for (int i = 0; i <= pattern.maxOffset; i++) {
			if (pattern.argumentNumbers[i] > maximumArgumentNumber) {
				maximumArgumentNumber = pattern.argumentNumbers[i];
			}
		}
		Object[] resultArray = new Object[maximumArgumentNumber + 1];

		int patternOffset = 0;
		int sourceOffset = pos.getIndex();
		ParsePosition tempStatus = new ParsePosition(0);
		for (int i = 0; i <= pattern.maxOffset; ++i) {
			// match up to format
			int len = pattern.offsets[i] - patternOffset;
			if (len == 0 || pattern.pattern.regionMatches(patternOffset, source, sourceOffset, len)) {
				sourceOffset += len;
				patternOffset += len;
			} else {
				pos.setErrorIndex(sourceOffset);
				return null; // leave index as is to signal error
			}

			// now use format
			if (pattern.formats[i] == null) { // string format
				// if at end, use longest possible match
				// otherwise uses first match to intervening string
				// does NOT recursively try all possibilities
				int tempLength = (i != pattern.maxOffset) ? pattern.offsets[i + 1] : pattern.pattern.length();

				int next;
				if (patternOffset >= tempLength) {
					next = source.length();
				} else {
					next = source.indexOf(pattern.pattern.substring(patternOffset, tempLength), sourceOffset);
				}

				if (next < 0) {
					pos.setErrorIndex(sourceOffset);
					return null; // leave index as is to signal error
				} else {
					String strValue = source.substring(sourceOffset, next);
					String argpattern = new StringBuilder().append(FormatKeyword.left.charValue()).append(pattern.argumentNumbers[i])
							.append(FormatKeyword.right.charValue()).toString();
					if (!strValue.equals(argpattern)) resultArray[pattern.argumentNumbers[i]] = source.substring(sourceOffset, next);
					sourceOffset = next;
				}
			} else {
				tempStatus.setIndex(sourceOffset);
				resultArray[pattern.argumentNumbers[i]] = pattern.formats[i].parseObject(source, tempStatus);
				if (tempStatus.getIndex() == sourceOffset) {
					pos.setErrorIndex(sourceOffset);
					return null; // leave index as is to signal error
				}
				sourceOffset = tempStatus.getIndex(); // update
			}
		}
		int len = pattern.pattern.length() - patternOffset;
		if (len == 0 || pattern.pattern.regionMatches(patternOffset, source, sourceOffset, len)) {
			pos.setIndex(sourceOffset + len);
		} else {
			pos.setErrorIndex(sourceOffset);
			return null; // leave index as is to signal error
		}
		return resultArray;
	}
}

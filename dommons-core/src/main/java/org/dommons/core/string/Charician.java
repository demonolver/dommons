/*
 * @(#)Charician.java     2011-10-18
 */
package org.dommons.core.string;

import java.io.IOException;
import java.util.regex.Pattern;

import org.dommons.core.Assertor;
import org.dommons.core.Silewarner;
import org.dommons.core.number.Radix64.Radix64Digits;
import org.dommons.core.util.Arrayard;

/**
 * 字符工具集
 * @author Demon 2011-10-18
 */
public final class Charician extends Radix64Digits {

	static final Pattern ascII = Pattern.compile("\\\\u[0-9A-Fa-f]{4}");

	/**
	 * 转换为ASCII码串
	 * @param ch 字符
	 * @return 字符串
	 */
	public static String ascII(char ch) {
		return ascII(ch, new StringBuilder(6)).toString();
	}

	/**
	 * 转换为ASCII码串
	 * @param ch 字符
	 * @param appendable 目标缓冲区
	 */
	public static <A extends Appendable> A ascII(char ch, A appendable) {
		Assertor.F.notNull(appendable);
		try {
			appendable.append("\\u");
			appendable.append(digits[(ch >> 12) & 0xF]);
			appendable.append(digits[(ch >> 8) & 0xF]);
			appendable.append(digits[(ch >> 4) & 0xF]);
			appendable.append(digits[ch & 0xF]);
		} catch (IOException ioe) {
			Silewarner.warn(Charician.class, ioe);
		}
		return appendable;
	}

	/**
	 * 转换为ASCII码串
	 * @param ch 字符
	 * @return 字符串
	 */
	public static String ascII(Character ch) {
		return ch == null ? null : ascII(ch.charValue());
	}

	/**
	 * 解析ASC码
	 * @param str 字符串
	 * @return 字符
	 */
	public static Character ascII(String str) {
		if (!ascII.matcher(str).matches()) return null;
		int value = 0;
		for (int i = 2; i < 6; i++) {
			char c = Character.toUpperCase(str.charAt(i));
			value = (value << 4) + converts[c];
		}
		return Character.valueOf((char) value);
	}

	/**
	 * 是否中文字符
	 * @param c 字符
	 * @return 是、否
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return Arrayard.contains(ub, Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS, Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
			Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
			Character.UnicodeBlock.GENERAL_PUNCTUATION, Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS,
			Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
	}

	protected Charician() {
	}
}

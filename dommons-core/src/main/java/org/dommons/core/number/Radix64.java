/*
 * @(#)Radix64.java     2011-10-18
 */
package org.dommons.core.number;

/**
 * 64进制数字转换类 基于 Base 64 字符项集合
 * @author Demon 2011-10-18
 */
public class Radix64 {

	/** 最大进制值 */
	public static final int max_radix = 64;

	/**
	 * 按 16 进制将长整型数字转换为字符串
	 * @param l 长整型数字
	 * @return 字符串
	 */
	public static String toHex(long l) {
		return toUnsignedString(l, 4);
	}

	/**
	 * 按64进制将字符串转换为整型数字
	 * @param str 字符串值
	 * @return 整型数字
	 */
	public static int toInteger(CharSequence str) {
		return toInteger(str, max_radix);
	}

	/**
	 * 按指定进制将字符串转换为整型数字
	 * @param str 字符串值
	 * @param radix 进制基数
	 * @return 整型数字
	 */
	public static int toInteger(CharSequence str, int radix) {
		long l = toLong(str, radix);
		if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) throw new NumberFormatException("For input string: \"" + str + "\"");
		return (int) l;
	}

	/**
	 * 按64进制将字符串转换为长整型数字
	 * @param str 字符串值
	 * @return 长整型数字
	 */
	public static long toLong(CharSequence str) {
		return toLong(str, max_radix);
	}

	/**
	 * 按指定进制将字符串转换为长整型数字
	 * @param str 字符中值
	 * @param radix 进制基数
	 * @return 长整型数字
	 */
	public static long toLong(CharSequence str, int radix) {
		if (str == null) throw new NumberFormatException("null");

		if (radix < Character.MIN_RADIX) throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
		if (radix > max_radix) throw new NumberFormatException("radix " + radix + "  greater than " + max_radix);

		int len = str.length();
		if (len == 0) throw new NumberFormatException("For input string: \"" + str + "\"");
		long limit, multmin;
		int digit;
		int i = 0;
		long result = 0;
		boolean negative = false;
		if (str.charAt(0) == '-') {
			negative = true;
			limit = Long.MIN_VALUE;
			i++;
		} else {
			limit = -Long.MAX_VALUE;
		}
		multmin = limit / radix;

		do {
			char c = str.charAt(i++);
			// 36 进制以下不区分字符大小写
			if (radix <= 36) c = Character.toUpperCase(c);

			if (c > Radix64Digits.converts.length || c < 0) throw new NumberFormatException("For input string: \"" + str + "\"");
			digit = Radix64Digits.converts[c];
			if (digit < 0 || digit >= radix) throw new NumberFormatException("For input string: \"" + str + "\"");
			if (result < multmin) throw new NumberFormatException("For input string: \"" + str + "\"");
			result *= radix;
			result -= digit;
			if (result < limit) throw new NumberFormatException("For input string: \"" + str + "\"");
		} while (i < len);
		return negative ? result : -result;
	}

	/**
	 * 按64进制将整型数字转换为字符串
	 * @param i 整型数字
	 * @return 字符串
	 */
	public static String toString(int i) {
		return toString((long) i);
	}

	/**
	 * 按指定进制将整型数字转换为字符串
	 * @param i 整型数字
	 * @param radix 指定进制
	 * @return 字符串
	 */
	public static String toString(int i, int radix) {
		return toString((long) i, radix);
	}

	/**
	 * 按64进制将长整型数字转换为字符串
	 * @param l 长整型数字
	 * @return 字符串
	 */
	public static String toString(long l) {
		return toUnsignedString(l, 6);
	}

	/**
	 * 按指定进制长整型数字转换为字符串
	 * @param l 长整型数字
	 * @param radix 进制基数
	 * @return 字符串
	 */
	public static String toString(long l, int radix) {
		if (radix < Character.MIN_RADIX) throw new NumberFormatException("radix " + radix + " less than " + Character.MIN_RADIX);
		if (radix > max_radix) throw new NumberFormatException("radix " + radix + "  greater than " + max_radix);

		boolean negative = false;
		if (l < 0) {
			negative = true;
			l = -l;
		}

		int s = 65;
		char[] buf = new char[s];
		int charPos = s;
		do {
			buf[--charPos] = Radix64Digits.digits[(int) (l % radix)];
			l /= radix;
		} while (l != 0);
		if (negative) buf[--charPos] = '-';
		return new String(buf, charPos, 65 - charPos);
	}

	/**
	 * 转换
	 * @param l 长整型数字
	 * @param shift 指数
	 * @return 字符串
	 */
	private static String toUnsignedString(long l, int shift) {
		char[] buf = new char[64];
		int charPos = 64;
		int radix = 1 << shift;
		long mask = radix - 1;
		do {
			buf[--charPos] = Radix64Digits.digits[(int) (l & mask)];
			l >>>= shift;
		} while (l != 0);
		return new String(buf, charPos, (64 - charPos));
	}

	/**
	 * 64 进制字符码表
	 * @author Demon 2012-3-19
	 */
	public static abstract class Radix64Digits {
		/** 字符码表 */
		protected static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
				'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
				'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '+', '/' };

		/** 解码转换对照表 */
		protected static final int[] converts = new int[128];

		/**
		 * 解码转换对照表
		 * @deprecated {@link #converts}
		 */
		protected static final int[] pem_convert_array = converts;

		static { // 解码表初始化
			for (int i = converts.length - 1; i >= 0; i--)
				converts[i] = -1;
			for (int i = 0; i < digits.length; i++)
				converts[digits[i]] = i;
		}
	}
}

/*
 * @(#)HexCoder.java     2011-10-26
 */
package org.dommons.security.coder;

import org.dommons.core.Environments;
import org.dommons.core.number.Radix64.Radix64Digits;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;

/**
 * 十六进制转码器
 * @author Demon 2011-10-26
 */
public class HexCoder extends Radix64Digits implements Coder {

	/** 转码器实例 该转码只需单例运行 */
	private static Ref<HexCoder> instance;

	/**
	 * 解码
	 * @param content 十六进制串
	 * @return 原字节集
	 */
	public static byte[] decodeBuffer(CharSequence content) {
		if (content == null) return null;
		byte[] bytes = new byte[content.length() / 2];
		for (int i = bytes.length - 1; i >= 0; i--) {
			int n = 16 * check(indexOf(content.charAt(2 * i)), content);
			n += check(indexOf(content.charAt(2 * i + 1)), content);
			bytes[i] = (byte) (n & 0xFF);
		}
		return bytes;
	}

	/**
	 * 转码
	 * @param code 原字节集
	 * @return 十六进制串
	 */
	public static String encodeBuffer(byte[] code) {
		if (code == null) return null;

		StringBuilder builder = new StringBuilder(code.length * 2);
		for (int i = 0; i < code.length; i++) {
			builder.append(digits[(code[i] & 0x0F0) >> 4]);
			builder.append(digits[code[i] & 0x0F]);
		}

		return builder.toString();
	}

	/**
	 * 获取转码器实例
	 * @return 转码器实例
	 */
	public static HexCoder instance() {
		HexCoder coder = instance == null ? null : instance.get();
		if (coder == null) instance = new Softref(coder = new HexCoder());
		return coder;
	}

	/**
	 * 检查序号合法性
	 * @param index 序号
	 * @param content 十六进制串内容
	 */
	static int check(int index, CharSequence content) {
		if (index < 0) throw new IllegalArgumentException("The argument is not a string of hex! " + content);
		return index;
	}

	/**
	 * 查找序号
	 * @param ch 字符
	 * @return 序号 未找到返回值 < 0
	 */
	static int indexOf(char ch) {
		ch = Character.toUpperCase(ch);
		if (ch < 0 || ch > 'F') return -1;
		return converts[ch];
	}

	/**
	 * 构造函数
	 */
	protected HexCoder() {}

	public String decode(String code) {
		return code == null ? null : Stringure.toString(decodeBuffer(code), Environments.getProperty("dommons.security.encoding"));
	}

	public String encode(String code) {
		return code == null ? null : encodeBuffer(Stringure.toBytes(code, (Environments.getProperty("dommons.security.encoding"))));
	}
}

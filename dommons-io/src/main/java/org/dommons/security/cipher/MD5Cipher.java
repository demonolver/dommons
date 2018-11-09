/*
 * @(#)MD5Cipher.java     2011-10-26
 */
package org.dommons.security.cipher;

import java.security.MessageDigest;

import org.dommons.core.convert.Converter;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.security.coder.B64Coder;
import org.dommons.security.coder.HexCoder;

/**
 * MD5 加密工具
 * @author Demon 2011-10-26
 */
public class MD5Cipher implements Cipher {

	static final String algorithm = "MD5";

	private static Ref<MD5Cipher> instance;

	/**
	 * 加密
	 * @param code 原文
	 * @return 密文
	 */
	public static byte[] encode(byte[] code) {
		try {
			return MessageDigest.getInstance(algorithm).digest(code);
		} catch (Exception e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 加密并以 BASE64 编码生成字符串
	 * @param code 原文
	 * @return 密文
	 */
	public static String encodeBASE64(byte[] code) {
		return B64Coder.encodeBuffer(encode(code));
	}

	/**
	 * 加密并以十六进制编码生成字符串
	 * @param code 原文
	 * @return 密文
	 */
	public static String encodeHex(byte[] code) {
		return HexCoder.encodeBuffer(encode(code));
	}

	/**
	 * 获取加密器实例
	 * @return 加密器
	 */
	public static MD5Cipher instance() {
		MD5Cipher cipher = instance == null ? null : instance.get();
		if (cipher == null) instance = new Softref(cipher = new MD5Cipher());
		return cipher;
	}

	/**
	 * 构造函数
	 */
	protected MD5Cipher() {
	}

	public String decode(String code) {
		throw new UnsupportedOperationException();
	}

	public String encode(String code) {
		return encodeHex(SymCipher.bytes(code));
	}
}
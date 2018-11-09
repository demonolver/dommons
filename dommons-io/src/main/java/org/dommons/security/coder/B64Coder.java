/*
 * @(#)B64Coder.java     2011-10-26
 */
package org.dommons.security.coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.dommons.core.Environments;
import org.dommons.core.string.Stringure;

/**
 * Base64 转码器
 * @author Demon 2011-10-26
 */
public class B64Coder implements Coder {

	/** 编码转换对照表 */
	private static final char[] pem_array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	private static final byte[] pem_convert_array = new byte[256]; // 解码转换对照表

	private static final Pattern lineReplace = Pattern.compile("[\\\r\\\n]");

	private static B64Coder instance;

	static { // 解码表初始化
		for (int i = pem_convert_array.length - 1; i >= 0; i--)
			pem_convert_array[i] = -1;
		for (int i = 0; i < pem_array.length; i++)
			pem_convert_array[pem_array[i]] = (byte) i;
	}

	/**
	 * 解码转换
	 * @param str 编码后字符串
	 * @return 原码字符集
	 */
	public static byte[] decodeBuffer(CharSequence str) {
		return decodeBuffer(str, false);
	}

	/**
	 * 解码转换
	 * @param str 编码后字符串
	 * @param whole 是否完整性校验
	 * @return 原码字符集
	 */
	public static byte[] decodeBuffer(CharSequence str, boolean whole) {
		if (str == null) return null;
		char[] chars = lineReplace.matcher(str).replaceAll(Stringure.empty).toCharArray();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(chars.length * 3 / 4);
		try {
			for (int i = 0, size = chars.length; i < size; i += 4) {
				if (i + 4 < size) decodeAtom(bos, chars, i, 4, false);
				else decodeAtom(bos, chars, i, size - i, whole);
			}
		} finally {
			try {
				bos.close();
			} catch (IOException e) { // ignore
			}
		}

		return bos.toByteArray();

	}

	/**
	 * 编码转换
	 * @param bytes 原码字节流
	 * @param builder 字符缓存区
	 * @return 字符缓存区
	 */
	public static StringBuilder encode(byte[] bytes, StringBuilder builder) {
		if (bytes == null) return builder;
		int size = bytes.length;
		if (builder == null) builder = new StringBuilder(size * 4 / 3 + 4);

		for (int i = 0; i < size; i += 3) {
			if (i + 3 <= size) encodeAtom(builder, bytes, i, 3);
			else encodeAtom(builder, bytes, i, size - i);
		}
		return builder;
	}

	/**
	 * 编码转换
	 * @param bytes 原码字节流
	 * @return 编码字符串
	 */
	public static String encodeBuffer(byte[] bytes) {
		if (bytes == null) return null;
		return encode(bytes, null).toString();
	}

	/**
	 * 获取工具实例
	 * @return 编解码工具实例
	 */
	public static B64Coder instance() {
		if (instance != null) return instance;
		synchronized (B64Coder.class) {
			if (instance == null) instance = new B64Coder();
		}
		return instance;
	}

	/**
	 * 部分解码转换
	 * @param os 输出
	 * @param chars 字符集合
	 * @param start 起始位
	 * @param size 字符数
	 * @param whole 是否完整性校验
	 */
	static void decodeAtom(ByteArrayOutputStream os, char[] chars, int start, int size, boolean whole) {
		int j = -1, k = -1, l = -1, i = -1;
		if (size < 2) throw new IllegalArgumentException("BASE64Decoder: Not enough bytes for an atom.");

		if ((size > 3) && (chars[start + 3] == '=')) size = 3;
		if ((size > 2) && (chars[start + 2] == '=')) size = 2;
		switch (size) {
		case 4:
			i = pem_convert_array[(chars[start + 3] & 0xFF)];
		case 3:
			l = pem_convert_array[(chars[start + 2] & 0xFF)];
		case 2:
			k = pem_convert_array[(chars[start + 1] & 0xFF)];
			j = pem_convert_array[(chars[start] & 0xFF)];
		}
		switch (size) {
		case 2:
			os.write((byte) (j << 2 & 0xFC | k >>> 4 & 0x3));
			if ((k << 4 & 0xF0) != 0) throw new IllegalArgumentException("BASE64Decoder: Not whole for this chars.");
			break;
		case 3:
			os.write((byte) (j << 2 & 0xFC | k >>> 4 & 0x3));
			os.write((byte) (k << 4 & 0xF0 | l >>> 2 & 0xF));
			if ((l << 6 & 0xC0) != 0) throw new IllegalArgumentException("BASE64Decoder: Not whole for this chars.");
			break;
		case 4:
			os.write((byte) (j << 2 & 0xFC | k >>> 4 & 0x3));
			os.write((byte) (k << 4 & 0xF0 | l >>> 2 & 0xF));
			os.write((byte) (l << 6 & 0xC0 | i & 0x3F));
		}
	}

	/**
	 * 部分编码转换
	 * @param builder 结果集
	 * @param bytes 字符集
	 * @param start 起始位
	 * @param size 字符数
	 */
	static void encodeAtom(StringBuilder builder, byte[] bytes, int start, int size) {
		int i, j, k;
		if (size == 1) {
			i = bytes[start];
			j = 0;
			k = 0;
			builder.append(pem_array[(i >>> 2 & 0x3F)]);
			builder.append(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			builder.append('=');
			builder.append('=');
		} else if (size == 2) {
			i = bytes[start];
			j = bytes[(start + 1)];
			k = 0;
			builder.append(pem_array[(i >>> 2 & 0x3F)]);
			builder.append(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			builder.append(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
			builder.append('=');
		} else {
			i = bytes[start];
			j = bytes[(start + 1)];
			k = bytes[(start + 2)];
			builder.append(pem_array[(i >>> 2 & 0x3F)]);
			builder.append(pem_array[((i << 4 & 0x30) + (j >>> 4 & 0xF))]);
			builder.append(pem_array[((j << 2 & 0x3C) + (k >>> 6 & 0x3))]);
			builder.append(pem_array[(k & 0x3F)]);
		}
	}

	private B64Coder() {
	}

	public String decode(String code) {
		return code == null ? null : Stringure.toString(decodeBuffer(code), Environments.getProperty("dommons.security.encoding"));
	}

	public String encode(String code) {
		return code == null ? null : encodeBuffer(Stringure.toBytes(code, Environments.getProperty("dommons.security.encoding")));
	}
}

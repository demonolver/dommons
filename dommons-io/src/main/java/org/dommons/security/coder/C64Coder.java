/*
 * @(#)C64Coder.java     2012-4-10
 */
package org.dommons.security.coder;

import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;

/**
 * 字符 BASE 64 转码器
 * @author Demon 2012-4-10
 */
public class C64Coder implements Coder {

	private static Ref<C64Coder> ref;

	/**
	 * 获取转码器实例
	 * @return 转码器实例
	 */
	public static C64Coder instance() {
		C64Coder coder = ref == null ? null : ref.get();
		if (coder == null) ref = new Softref(coder = new C64Coder());
		return coder;
	}

	/**
	 * 转换字符串为字节流
	 * @param str 字符串
	 * @return 字节流
	 */
	public static byte[] toBytes(String str) {
		int len = str == null ? 0 : str.length();
		if (len == 0) return null;

		byte[] bs = new byte[len * 3];
		int index = 1;
		int fi = 0;
		int f = 0;

		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (c < 0) throw new UnsupportedOperationException("Unsupported char '" + c + '\'');
			int h = c >> 8;
			int l = c & 0xFF;

			f = f << 1;
			if (h > 0) {
				f |= 1;
				bs[index++] = (byte) (h + Byte.MIN_VALUE);
			}

			bs[index++] = (byte) (l + Byte.MIN_VALUE);

			int d = i % 8;
			if (d > 0 && (d == 7 || i == len - 1)) {
				f = f << (7 - d);

				bs[fi] = (byte) (f + Byte.MIN_VALUE);

				f = 0;
				if (i < len - 1) fi = index++;
			}
		}

		return zoom(bs, index);
	}

	/**
	 * 转换字节流为字符串
	 * @param bytes 字节流
	 * @return 字符串
	 */
	public static String toString(byte[] bytes) {
		int len = bytes == null ? 0 : bytes.length;
		if (len == 0) {
			return null;
		} else if (len == 1) {
			return Character.toString((char) 0);
		}

		StringBuilder builder = new StringBuilder(len / 2);
		int index = 0;
		int f = -1;
		for (int i = 0; i < len; i++) {
			if (index % 9 == 8) {
				f = -1;
				index = 0;
			}

			int b = bytes[i] - Byte.MIN_VALUE;

			if (index == 0 && f < 0) {
				f = b;
				if (i == len - 1) builder.append((char) 0);
			} else {
				if ((f >> (7 - index) & 1) == 0) {
					builder.append((char) b);
				} else {
					int l = 0;
					if (i < len - 1) l = bytes[++i] - Byte.MIN_VALUE;

					builder.append((char) ((b << 8) | l));
				}
				index++;
			}
		}

		return builder.toString();
	}

	/**
	 * 数组缩放
	 * @param b 数组
	 * @param len 长度
	 * @return 新数组
	 */
	static byte[] zoom(byte[] b, int len) {
		if (b.length == len) return b;

		byte[] r = new byte[len];
		System.arraycopy(b, 0, r, 0, len);
		return r;
	}

	protected C64Coder() {}

	public String decode(String code) {
		return code == null ? null : toString(B64Coder.decodeBuffer(code));
	}

	public String encode(String code) {
		return code == null ? null : B64Coder.encodeBuffer(toBytes(code));
	}
}

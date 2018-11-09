/*
 * @(#)RashCipher.java     2011-10-26
 */
package org.dommons.security.cipher;

import java.util.Map;

import org.dommons.core.Assertor;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.number.Radix64;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Randoms;
import org.dommons.security.coder.B64Coder;
import org.dommons.security.coder.C64Coder;

/**
 * 乱序加密工具
 * @author Demon 2011-10-26
 */
public class RashCipher implements Cipher {

	/** 默认密钥 */
	private static final String DEFAULT_KEY = "c#E";

	static final Map<String, RashCipher> cache = new ConcurrentSoftMap();

	/**
	 * 获取加密工具实例
	 * @return 加密工具实例
	 */
	public static RashCipher instance() {
		return instance(null);
	}

	/**
	 * 获取加密工具实例
	 * @param key 密钥
	 * @return 加密工具实例
	 */
	public static RashCipher instance(String key) {
		String k = Stringure.trim(key);
		RashCipher rc = cache.get(k);
		if (rc == null) cache.put(k, rc = new RashCipher(key));
		return rc;
	}

	/**
	 * 去除密钥
	 * @param b 原值
	 * @param key 密钥
	 * @return 真值
	 */
	protected static String decodeKey(byte[] b, String key) {
		byte[] k = C64Coder.toBytes(key);
		int len = b.length - 1;
		if (b[len] != sum(avg(k), (byte) len)) return null;
		byte[] c = new byte[len];
		for (int i = 0; i < len; i++) {
			c[i] = sub(b[b.length - 2 - i], k[i % k.length]);
		}
		return C64Coder.toString(c);
	}

	/**
	 * 加入密钥
	 * @param code 真值
	 * @param key 密钥
	 * @return 新值
	 */
	protected static byte[] encodeKey(String code, String key) {
		byte[] b = C64Coder.toBytes(code);
		byte[] k = C64Coder.toBytes(key);
		int len = b.length;
		byte[] n = new byte[len + 1];
		for (int i = 0; i < len; i++) {
			n[len - 1 - i] = sum(b[i], k[i % k.length]);
		}
		n[len] = sum(avg(k), (byte) b.length);
		return n;
	}

	/**
	 * 取平均
	 * @param b 数组
	 * @return 字节
	 */
	static byte avg(byte... b) {
		if (b == null || b.length == 0) return 0;
		long l = 0;
		for (byte t : b) {
			l += t;
		}
		return (byte) (l / b.length);
	}

	/**
	 * 移位转换
	 * @param builder 字符串缓存区
	 * @param m 位数
	 * @param forward 字符串缓存区
	 */
	static void move(StringBuilder builder, int m, boolean forward) {
		int len = builder.length();
		move(builder, m, 0, len >> 1, forward);
		move(builder, m, len >> 1, len, forward);
	}

	/**
	 * 转位转换
	 * @param builder 字符串缓存区
	 * @param m 位数
	 * @param start 开始位数
	 * @param len 长度
	 * @param forward 是否正向
	 * @return 字符串缓存区
	 */
	static StringBuilder move(StringBuilder builder, int m, int start, int len, boolean forward) {
		m = (m % (len - start)) + 1;
		String part = null;
		int p = 0;
		if (forward) {
			part = builder.substring(len - m, len);
			builder.delete(len - m, len);
			p = start;
		} else {
			part = builder.substring(start, start + m);
			builder.delete(start, start + m);
			p = len - m;
		}

		StringBuilder r = new StringBuilder(m);
		for (int i = 0; i < part.length(); i++) {
			r.append(part.charAt(i));
		}

		builder.insert(p, r);
		return builder;
	}

	/**
	 * 相减
	 * @param s 字节
	 * @param b 字节
	 * @return 字节
	 */
	static byte sub(byte s, byte b) {
		return (byte) ((long) s - b);
	}

	/**
	 * 求和
	 * @param b 字节数组
	 * @return 字节
	 */
	static byte sum(byte... b) {
		if (b == null || b.length == 0) return 0;
		long l = 0;
		for (byte t : b) {
			l += t;
		}
		return (byte) l;
	}

	/**
	 * 对长度信息进行解密
	 * @param code 密文
	 * @return 数字
	 */
	private static int decodeLength(String code) {
		Assertor.F.notEmpty(code);
		return Radix64.toInteger(code);
	}

	/**
	 * 对长度进行加密
	 * @param n 数字
	 * @return 密文
	 */
	private static String encodeLength(int n) {
		Assertor.F.isTrue(n > 0, "this argument of length must greater than zero!");
		return Radix64.toString(n);
	}

	private final String key; // 密钥

	/**
	 * 构造函数
	 * @param key 密钥
	 */
	public RashCipher(String key) {
		String tmp = Stringure.isEmpty(key) ? DEFAULT_KEY : key.trim();
		this.key = B64Coder.instance().encode(tmp);
	}

	/**
	 * 构造函数
	 */
	protected RashCipher() {
		this(null);
	}

	public String decode(String cryptograph) {
		if (!Assertor.P.notEmpty(cryptograph)) return null;

		StringBuilder code = new StringBuilder(cryptograph);
		// 对密文进行混淆还原
		int size = code.length();
		// 获取混淆前密文长度
		String ms = code.substring((size >> 1) - 1, size >> 1);
		code.delete((size >> 1) - 1, size >> 1);

		int p = decodeLength(ms);
		int m = p & 3;
		p = p >> 2;

		Assertor.F.isTrue(p >= 0 && p < size - 1, "Invalid cryptograph!" + cryptograph);
		int llen = decodeLength(code.substring(p + 1, p + 2));
		m = ((m << 2) | (llen & 3)) + 1;
		llen = llen >> 2;

		Assertor.F.isTrue(p + llen + 1 < size, "Invalid cryptograph!" + cryptograph);

		String length = code.substring(p + 2, p + 2 + llen);
		code.delete(p + 1, p + 2 + llen);
		move(code, m, false);

		int len = decodeLength(length);
		Assertor.F.isTrue(len < code.length(), "Invalid cryptograph!" + cryptograph);
		int patch = len % 4;
		patch = patch == 0 ? 0 : 4 - patch;

		StringBuilder builder = new StringBuilder(len + patch);

		int elen = (cryptograph.length() - 2 - llen) >> 1;
		int rl = size - len - 2 - length.length();
		// 还原密文
		unrash(code, builder, size, len, elen, rl);

		move(builder, m, false);
		// 对混淆还原后的密文再进行base64解码
		String plaintext = null;
		plaintext = decodeKey(B64Coder.decodeBuffer(builder), key);
		if (plaintext == null) {
			throw new IllegalArgumentException("Invalid secret key!It could not decode the cryptograph " + cryptograph);
		}

		return plaintext;
	}

	public String encode(String code) {
		if (!Assertor.P.notEmpty(code)) return null;
		// 将明文加上密钥按base64进行转码
		StringBuilder text = SymCipher.cutTailEquals(B64Coder.encode(encodeKey(code, key), null));
		int len = text.length();
		// 对密文移位转换
		int m = Randoms.randomInteger(Math.min(len >> 1, 16)) + 1;
		move(text, m, true);

		// 对密文再次做混淆
		String lContext = encodeLength(len);
		int size = (len / 8 + 2) * 8;
		StringBuilder builder = new StringBuilder(size);
		int elen = (size - 2 - lContext.length()) >> 1;

		int rl = size - len - 2 - lContext.length(); // 随机字符串长度
		rash(text, builder, size, len, elen, rl);

		int l = builder.length();
		// 对新密文再次移位
		move(builder, m, true);

		// 进一步将混淆前密文长度信息混淆到密文中
		int p = Randoms.randomInteger(Math.min(l, 15)) + 1;

		l = (lContext.length() << 2) | ((m - 1) & 3);
		builder.insert(p + 1, encodeLength(l)).insert(p + 2, lContext);
		builder.insert(builder.length() >> 1, encodeLength((p << 2) | ((m - 1) >> 2)));

		return builder.toString();
	}

	/**
	 * 混淆
	 * @param text 文本内容
	 * @param builder 字符缓存区
	 * @param size 混淆后大小
	 * @param len 文本长度
	 * @param elen 后段长度
	 * @param rl 随机串长度
	 * @return 字符缓存区
	 */
	protected StringBuilder rash(CharSequence text, StringBuilder builder, int size, int len, int elen, int rl) {
		StringBuilder end = new StringBuilder(elen);
		String r = Randoms.randomAlphabetic(rl);

		int ki = 0; // 密文串位数
		int ri = 0; // 随机串位数
		int n = size / 2;
		// 将密文按奇偶次序重新排序，奇数位排入前半段中，偶数位排入后半段，两个密文间插入随机字符
		for (int i = 0; i < n; i += 2) {
			if (ri < rl) {
				builder.append(r.charAt(ri++));
			} else {
				for (; ki < len; ki++) {
					if (ki % 2 == 0) {
						builder.append(text.charAt(ki));
					} else {
						end.append(text.charAt(ki));
					}
				}
				break;
			}
			if (ki < len) {
				builder.append(text.charAt(ki++));
			} else if (ri < rl) {
				String tmp = r.substring(ri, ri += elen - i);
				end.append(tmp);
				builder.append(r.substring(ri));
				break;
			}
			if (ki < len) {
				end.append(text.charAt(ki++));
			} else if (ri < rl) {
				end.append(r.substring(ri, ri += elen - i));
				builder.append(r.substring(ri));
				break;
			}
			if (ri < rl) {
				end.append(r.charAt(ri++));
			} else {
				for (; ki < len; ki++) {
					if (ki % 2 == 0) {
						builder.append(text.charAt(ki));
					} else {
						end.append(text.charAt(ki));
					}
				}
				break;
			}
		}
		builder.append(end);

		return builder;
	}

	/**
	 * 反混淆
	 * @param code 密文内容
	 * @param builder 字符缓存区
	 * @param size 密文长度
	 * @param len 原文长度
	 * @param elen 后段长度
	 * @param rl 随机串长度
	 * @return 字符缓存区
	 */
	protected StringBuilder unrash(CharSequence code, StringBuilder builder, int size, int len, int elen, int rl) {
		if (rl % 2 == 1 && len % 2 == 1) {
			int middle = (size = code.length()) - elen;
			for (int i = 0; i < len; i++) {
				if (i == size - len) {
					if (i % 2 == 0) {
						builder.append(code.charAt(i));
						for (int j = i + 1; j < middle; j++) {
							builder.append(code.charAt(middle + j));
							builder.append(code.charAt(j));
						}
						if (len % 2 == 0) {
							builder.append(code.charAt(size - 1));
						}
					} else {
						builder.append(code.charAt(middle + i));
						for (int j = i + 1; j < middle; j++) {
							builder.append(code.charAt(j));
							builder.append(code.charAt(middle + j));
						}
						if (len % 2 == 1) {
							builder.append(code.charAt(size - middle));
						}
					}
					break;
				} else {
					if (i % 2 == 0) {
						builder.append(code.charAt(i + 1));
					} else {
						builder.append(code.charAt(middle + i));
					}
				}
			}
		} else {
			int middle = (size = code.length()) - elen - 1;
			for (int i = 0; i < len; i++) {
				if (i == size - len) {
					if (i % 2 == 0) {
						builder.append(code.charAt(i));
						for (int j = i + 1; j <= middle; j++) {
							builder.append(code.charAt(middle + j));
							builder.append(code.charAt(j));
						}
						if (len % 2 == 0) {
							builder.append(code.charAt(size - 1));
						}
					} else {
						builder.append(code.charAt(middle + i));
						for (int j = i + 1; j <= middle; j++) {
							builder.append(code.charAt(j));
							builder.append(code.charAt(middle + j));
						}
						if (len % 2 == 1) {
							builder.append(code.charAt(size - middle));
						}
					}
					break;
				} else {
					if (i % 2 == 0) {
						builder.append(code.charAt(i + 1));
					} else {
						builder.append(code.charAt(middle + i));
					}
				}
			}
		}

		return builder;
	}
}
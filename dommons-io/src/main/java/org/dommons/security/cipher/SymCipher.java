/*
 * @(#)SymCipher.java     2011-10-26
 */
package org.dommons.security.cipher;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.security.coder.B64Coder;

/**
 * 对称加密器
 * @author Demon 2011-10-26
 */
abstract class SymCipher implements org.dommons.security.cipher.Cipher {

	/**
	 * 获取密钥
	 * @param cipher 加密器
	 * @return 密钥
	 */
	public static byte[] extractKey(SymCipher cipher) {
		return cipher.key.clone();
	}

	/**
	 * 转换字节集
	 * @param str 字符串
	 * @return 字节集
	 */
	protected static byte[] bytes(String str) {
		return Stringure.toBytes(str, enc());
	}

	/**
	 * 生成加密密钥
	 * @param key 密钥种子
	 * @param length 长度
	 * @return 密钥
	 */
	protected static byte[] generate(byte[] key, int length) {
		key = MD5Cipher.encode(key);
		if (key.length == length) return key;
		byte[] k = new byte[length];
		int c = 0, p = 0;
		do {
			p = Math.min(key.length, length - c);
			System.arraycopy(key, 0, k, c, p);
			c += p;
		} while (c < length);
		return k;
	}

	/**
	 * 删除尾部等号
	 * @param builder 原字符串
	 * @return 新字符串
	 */
	static StringBuilder cutTailEquals(StringBuilder builder) {
		if (builder == null) return builder;
		int len = builder.length();
		int end = len;
		for (int i = len - 1; i > 0; i--) {
			if (builder.charAt(i) == '=') {
				end--;
			} else {
				break;
			}
		}
		return end == len ? builder : builder.delete(end, len);
	}

	/**
	 * 获取安全加密字符集
	 * @return 字符集
	 */
	static String enc() {
		return Environments.getProperty("dommons.security.encoding");
	}

	private final byte[] key;

	/**
	 * 构造函数
	 * @param key 加密密钥种子
	 */
	protected SymCipher(byte[] key) {
		if (key == null || key.length < 1) this.key = generateKey(key);
		else this.key = generateKey(key);
	}

	/**
	 * 解密
	 * @param code 密文
	 * @return 明文
	 */
	public byte[] decode(byte[] code) {
		return doCipher(code, Cipher.DECRYPT_MODE);
	}

	public String decode(String code) {
		return Stringure.toString(doCipher(B64Coder.decodeBuffer(code), Cipher.DECRYPT_MODE), enc());
	}

	/**
	 * 加密
	 * @param code 明文
	 * @return 密文
	 */
	public byte[] encode(byte[] code) {
		return doCipher(code, Cipher.ENCRYPT_MODE);
	}

	public String encode(String code) {
		StringBuilder builder = null;
		builder = B64Coder.encode(doCipher(bytes(code), Cipher.ENCRYPT_MODE), null);
		return builder == null ? null : cutTailEquals(builder).toString();
	}

	/**
	 * 获取加密算法
	 * @return 加密算法
	 */
	protected abstract String algorithm();

	/**
	 * 获取默认密钥种子
	 * @return 默认密钥
	 */
	protected abstract byte[] defaultKey();

	/**
	 * 执行加密
	 * @param code 代码
	 * @param mode 计算模式
	 * @return 结果
	 */
	protected byte[] doCipher(byte[] code, int mode) {
		try {
			Cipher cipher = Cipher.getInstance(algorithm()); // 创建密码器
			cipher.init(mode, key()); // 初始化
			return cipher.doFinal(code); // 加密
		} catch (Exception e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 生成加密密钥
	 * @param key 密钥种子
	 * @return 密钥
	 */
	protected byte[] generateKey(byte[] key) {
		// 足位密钥直接使用，不转换
		if (matchKey(key)) return key;
		return generate(key, keyLength());
	}

	/**
	 * 获取密钥位长度
	 * @return 长度
	 */
	protected abstract int keyLength();

	/**
	 * 检查密钥是否合格
	 * @param key 密钥
	 * @return 是、否
	 */
	protected abstract boolean matchKey(byte[] key);

	/**
	 * 获取加密密钥
	 * @return 密钥
	 */
	SecretKey key() {
		return new SecretKeySpec(key, algorithm());
	}
}

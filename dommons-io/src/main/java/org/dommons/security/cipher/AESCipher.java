/*
 * @(#)AESCipher.java     2011-10-26
 */
package org.dommons.security.cipher;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.log.LoggerFactory;
import org.dommons.security.coder.B64Coder;

/**
 * AES 算法加密器
 * @author Demon 2011-10-26
 */
public class AESCipher extends SymCipher {

	static final String algorithm = "AES";
	static final byte[] default_key;
	static final Map<String, AESCipher> cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));

	static {
		default_key = bytes('{' + algorithm + '}');
	}

	/**
	 * 生成密钥
	 * @param key 密钥种子
	 * @return 密钥
	 */
	public static byte[] generate(byte[] key) {
		if (key.length == 16) return key;
		KeyGenerator kgen = null;
		SecureRandom random = null;
		try {
			kgen = KeyGenerator.getInstance(algorithm);
			random = SecureRandom.getInstance(SecureProvider.algorithm, SecureProvider.instance);
		} catch (NoSuchAlgorithmException e) {
			LoggerFactory.getInstance().getLogger(SymCipher.class).error(e);
		}
		random.setSeed(key);
		kgen.init(128, random);
		SecretKey secretKey = kgen.generateKey();
		return secretKey.getEncoded();
	}

	/**
	 * 生成密钥
	 * @param key 密钥种子
	 * @return 密钥
	 */
	public static byte[] generate(String key) {
		return generate(bytes(key));
	}

	/**
	 * 获取加密器实例
	 * @param key 密钥
	 * @return AES 加密器
	 */
	public static AESCipher instance(byte... key) {
		String k = B64Coder.encodeBuffer(key);
		AESCipher aes = cache.get(k);
		if (aes == null) cache.put(k, aes = new AESCipher(key));
		return aes;
	}

	/**
	 * 获取加密器实例
	 * @param key 密钥
	 * @return AES 加密器
	 */
	public static AESCipher instance(String key) {
		return instance(bytes(key));
	}

	protected AESCipher(byte[] key) {
		super(key);
	}

	protected String algorithm() {
		return algorithm;
	}

	protected byte[] defaultKey() {
		return default_key;
	}

	protected byte[] generateKey(byte[] key) {
		return super.generateKey(key);
	}

	protected int keyLength() {
		return 16;
	}

	protected boolean matchKey(byte[] key) {
		return key.length == 16;
	}
}

/*
 * @(#)TripleCipher.java     2011-10-26
 */
package org.dommons.security.cipher;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.MemcacheMap;

/**
 * 3DES 加密器
 * @author Demon 2011-10-26
 */
public class TripleCipher extends SymCipher {

	static final String algorithm = "TripleDES";
	static final byte[] default_key;
	static final Map<byte[], TripleCipher> cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));

	static {
		default_key = bytes("{3DES}");
	}

	/**
	 * 创建加密器
	 * @param key 密钥
	 * @return 加密器
	 */
	public static TripleCipher instance(byte... key) {
		TripleCipher triple = cache.get(key);
		if (triple == null) cache.put(key, triple = new TripleCipher(key));
		return triple;
	}

	/**
	 * 创建加密器
	 * @param key 密钥
	 * @return 加密器
	 */
	public static TripleCipher instance(String key) {
		return instance(bytes(key));
	}

	protected TripleCipher(byte[] key) {
		super(key);
	}

	protected String algorithm() {
		return algorithm;
	}

	protected byte[] defaultKey() {
		return default_key;
	}

	protected int keyLength() {
		return 24;
	}

	protected boolean matchKey(byte[] key) {
		return key.length == keyLength();
	}
}

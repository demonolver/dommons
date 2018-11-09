/*
 * @(#)Cipher.java     2011-10-26
 */
package org.dommons.security.cipher;

/**
 * 加密工具接口
 * @author Demon 2011-10-26
 */
public interface Cipher {

	/**
	 * 对密文进行解码，转为明文
	 * @param code 密文
	 * @return 明文
	 */
	public String decode(String code);

	/**
	 * 对明文进行转码，转为密文
	 * @param code 明文
	 * @return 密文
	 */
	public String encode(String code);
}

/*
 * @(#)Randoms.java     2011-10-26
 */
package org.dommons.core.util;

import java.util.Random;
import java.util.UUID;

import org.dommons.core.number.Radix64.Radix64Digits;

/**
 * 随机工具方法
 * @author Demon 2011-10-26
 */
public final class Randoms extends Radix64Digits {

	/**
	 * 获取随机字符串, 仅含英文字符
	 * @param count 字符串长度
	 * @return 字符串
	 */
	public static String randomAlphabetic(int count) {
		Random random = getRandom();
		StringBuilder builder = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			builder.append(digits[randomInteger(random, 52) + 10]);
		}
		return builder.toString();
	}

	/**
	 * 获取随机字符串, 包含英文字符和数字
	 * @param count 字符串长度
	 * @return 字符串
	 */
	public static String randomAlphanumeric(int count) {
		Random random = getRandom();
		StringBuilder builder = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			builder.append(digits[randomInteger(random, 62)]);
		}
		return builder.toString();
	}

	/**
	 * 获取随机数字
	 * @param limit 大小限制
	 * @return 数字
	 */
	public static int randomInteger(int limit) {
		return randomInteger(null, limit);
	}

	/**
	 * 获取随机字符串, 仅包含数字
	 * @param count 字符串长度
	 * @return 字符串
	 */
	public static String randomNumeric(int count) {
		Random random = getRandom();
		StringBuilder builder = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			builder.append(digits[randomInteger(random, 10)]);
		}
		return builder.toString();
	}

	/**
	 * 生成随机 UUID
	 * @return UUID
	 */
	public static UUID randomUUID() {
		return UUID.randomUUID();
	}

	/**
	 * 获取随机数生成器
	 * @return 随机数生成器
	 */
	protected static Random getRandom() {
		return new Random();
	}

	/**
	 * 获取随机数字
	 * @param random 随机对象 <code>null</code>自动创建新随机对象
	 * @param limit 大小限制
	 * @return 数字
	 */
	private static int randomInteger(Random random, int limit) {
		random = random == null ? getRandom() : random;
		return random.nextInt(limit);
	}

	/**
	 * 构造函数
	 */
	protected Randoms() {
	}
}

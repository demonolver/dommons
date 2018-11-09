/*
 * @(#)ArrayEnumeration.java     2012-7-5
 */
package org.dommons.core.collections.enumeration;

import java.util.NoSuchElementException;
import org.dommons.core.util.Arrayard;

/**
 * 数组迭代枚举
 * @author Demon 2012-7-5
 */
public class ArrayEnumeration<E> extends IterableEnumeration<E> {

	/**
	 * 创建布尔型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Boolean> create(boolean[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建布尔型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Boolean> create(boolean[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建字节型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Byte> create(byte[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建字节型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Byte> create(byte[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建字符数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Character> create(char[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建字符数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Character> create(char[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建双精度数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Double> create(double[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建双精度数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Double> create(double[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static <E> IterableEnumeration<E> create(E[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static <E> IterableEnumeration<E> create(E[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建浮点型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Float> create(float[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建浮点型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Float> create(float[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建整型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Integer> create(int[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建整型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Integer> create(int[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建长整型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Long> create(long[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建长整型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Long> create(long[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Object> create(Object array) {
		return array == null || !array.getClass().isArray() ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Object> create(Object array, int offset, int length) {
		return array == null || !array.getClass().isArray() ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/**
	 * 创建短整型数组迭代枚举
	 * @param array 数组
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Short> create(short[] array) {
		return array == null ? empty() : new ArrayEnumeration(array);
	}

	/**
	 * 创建短整型数组迭代枚举
	 * @param array 数组
	 * @param offset 起始索引
	 * @param length 长度 0 表示不限制长度
	 * @return 数组迭代枚举
	 */
	public static IterableEnumeration<Short> create(short[] array, int offset, int length) {
		return array == null ? empty() : new ArrayEnumeration(array, offset, length);
	}

	/** 目标数组 */
	protected final Object tar;
	/** 数组长度 */
	protected final int len;
	/** 迭代起始索引 */
	protected final int offset;
	/** 光标 */
	private int cursor;

	/**
	 * 构造函数
	 * @param array 数组
	 */
	protected ArrayEnumeration(Object array) {
		this(array, -1, -1);
	}

	/**
	 * 构造函数
	 * @param array 数组
	 * @param offset 开始位置
	 * @param len 长度
	 */
	protected ArrayEnumeration(Object array, int offset, int len) {
		this.tar = array;
		this.cursor = this.offset = (offset < 0 ? 0 : offset);
		this.len = len > 0 ? Math.min(len + this.offset, Arrayard.length(array)) : Arrayard.length(array);
	}

	public boolean hasNext() {
		return cursor < len;
	}

	public E next() {
		if (cursor >= len || cursor < offset) throw new NoSuchElementException();
		return (E) Arrayard.get(tar, cursor++);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 迭代器重置
	 */
	public void reset() {
		cursor = offset;
	}

	/**
	 * 设置元素值
	 * @param o 元素值
	 * @return 原元素值
	 */
	public E set(E o) {
		if (cursor <= offset || cursor > len) throw new IllegalStateException();
		E old = (E) Arrayard.get(tar, cursor - 1);
		Arrayard.set(tar, cursor - 1, o);
		return old;
	}
}

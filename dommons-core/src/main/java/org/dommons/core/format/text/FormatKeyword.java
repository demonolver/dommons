/*
 * @(#)FormatKeyword.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.io.IOException;

import org.dommons.core.convert.Converter;

/**
 * 格式关键字
 * @author Demon 2011-10-18
 */
enum FormatKeyword {

	/** 逗号 */
	comma(','),
	/** 左括号 */
	left('{', "'{'"),
	/** 右括号 */
	right('}', "'}'"),
	/** 引号 */
	quote('\'', "''");

	private final boolean converable;
	private final char ch;

	private final String[] str;

	/**
	 * 构造函数
	 * @param ch 关键字符
	 */
	private FormatKeyword(char ch) {
		this.ch = ch;
		converable = false;
		str = null;
	}

	/**
	 * 构造函数
	 * @param ch 关键字符
	 * @param str 替代串
	 */
	private FormatKeyword(char ch, String str) {
		this.ch = ch;
		converable = true;
		this.str = new String[] { str };
	}

	/**
	 * 字符值
	 * @return 字符值
	 */
	public char charValue() {
		return ch;
	}

	/**
	 * 转换
	 * @param buffer 结果缓冲区
	 */
	public void convert(Appendable buffer) {
		if (!converable || buffer == null) return;
		try {
			if (str != null) buffer.append(str[0]);
		} catch (IOException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 比较字符是否相同
	 * @param ch 关键字符
	 * @return 是、否
	 */
	public boolean equals(char ch) {
		return this.ch == ch;
	}
}

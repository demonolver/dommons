/*
 * @(#)SQLion.java     2017-09-25
 */
package org.dommons.db;

/**
 * SQL 工具方法类
 * @author demon 2017-09-25
 */
public class SQLion {

	/**
	 * 转换相似内容串，去除关键字
	 * @param str 内容串
	 * @return 新内容串
	 */
	public static String escapeLike(String str) {
		int len = str == null ? 0 : str.length();
		if (len == 0) return str;
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '_':
			case '%':
			case '\\':
				buf.append('\\').append(c);
				break;
			default:
				buf.append(c);
				break;
			}
		}
		return buf.length() > len ? buf.toString() : str;
	}
}

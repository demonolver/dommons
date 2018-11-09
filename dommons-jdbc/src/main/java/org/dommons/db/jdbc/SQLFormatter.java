/*
 * @(#)SQLFormatter.java     2012-2-11
 */
package org.dommons.db.jdbc;

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.dommons.core.convert.Converter;
import org.dommons.core.format.date.TimeFormat;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.string.Stringure;

/**
 * SQL 格式化器
 * @author Demon 2012-2-11
 */
class SQLFormatter {

	/**
	 * 追加时间值
	 * @param value 时间值
	 * @param buffer 缓冲区
	 * @param dateType 时间类型
	 * @param type 数据库类型
	 * @param version 数据库版本
	 * @return 缓冲区
	 */
	public static StringBuilder appendDate(Object value, StringBuilder buffer, int dateType, String type, String version) {
		if (value == null) return buffer.append("null");

		String dt = null;
		switch (dateType) {
		case Types.DATE:
			dt = "date";
			break;
		case Types.TIME:
			dt = "time";
			break;
		case Types.TIMESTAMP:
			dt = "timestamp";
			break;
		default:
			return appendString(Converter.P.convert(value, String.class), buffer);
		}

		String format = SQLDialect.getSQLTemplate(type, version, dt + ".format");
		String template = SQLDialect.getSQLTemplate(type, version, dt);

		if (value instanceof Calendar) {
			buffer.append(MessageFormat.format(template,
				TimeFormat.compile(format, ((Calendar) value).getTimeZone()).format(((Calendar) value).getTime())));
		} else if (value instanceof Date) {
			buffer.append(MessageFormat.format(template, TimeFormat.compile(format).format(value)));
		} else {
			try {
				Date date = Converter.P.convert(value, Date.class);
				buffer.append(MessageFormat.format(template, TimeFormat.compile(format).format(date)));
			} catch (RuntimeException e) {
				appendString(Converter.P.convert(value, String.class), buffer);
			}
		}
		return buffer;
	}

	/**
	 * 追加字符串值
	 * @param value 值
	 * @param buffer 缓冲区
	 * @return 缓冲区
	 */
	public static StringBuilder appendString(String value, StringBuilder buffer) {
		if (value == null) return buffer.append("null");

		int len = value.length();
		for (int i = 0; i < len; i++) {
			char ch = value.charAt(i);
			switch (ch) {
			case '\\':
			case '\'':
			case '\"':
				buffer.append('\\');
			default:
				buffer.append(ch);
				break;
			}
		}
		return buffer;
	}

	/**
	 * 格式化 SQL
	 * @param sql SQL 语句
	 * @return 格式后 SQL
	 */
	public static String format(CharSequence sql) {
		return format(sql, null);
	}

	public static String format(CharSequence sql, SQLFormatFilter filter) {
		if (sql == null) return null;

		int len = sql.length();
		return len == 0 ? Stringure.empty : new SQLFormatter(sql, len).format(filter);
	}

	public final CharSequence sql;

	public final int len;
	public int index;

	/** 标识符，斜杠 : 2，字符串值 : 1，空格 : 4 */
	public int flag;

	private char ch;
	private char cflag;

	protected SQLFormatter(CharSequence sql, int len) {
		this.sql = sql;
		this.len = len;
	}

	/**
	 * 获取当前字符
	 * @return 字符
	 */
	public char currentChar() {
		return ch;
	}

	/**
	 * 格式化
	 * @return 格式化后 SQL
	 */
	public String format() {
		return format((SQLFormatFilter) null);
	}

	/**
	 * 格式化
	 * @param filter 过滤器
	 * @return 格式化后 SQL
	 */
	public String format(SQLFormatFilter filter) {
		StringBuilder buffer = new StringBuilder(len);

		for (index = 0; index < len; index++) {
			ch = sql.charAt(index);
			if (filter != null && filter.isFilter(this)) { // 使用过滤器做转换
				filter.doFormat(this, buffer);
			} else if ((flag & 1) > 0 && ch == '\\') { // 字符串值中的斜杠值
				buffer.append(ch);
				flag ^= 2; // 标识斜杠或去除标识
			} else if ((flag & 2) == 0 && (((flag & 1) == 0 && (ch == '\'' || ch == '\"')) || ((flag & 1) > 0 && ch == cflag))) { // 非斜杠标识的引号
				buffer.append(ch);
				flag = (flag & ~4) ^ 1; // 去除空格标识，添加或去除字符串值标识
				cflag = (flag & 1) > 0 ? ch : '\\';
			} else if ((flag & 1) == 0 && Character.isWhitespace(ch)) { // 非字符串值的空格型字符
				if ((flag & 4) == 0 && buffer.length() > 0) buffer.append(' ');
				flag |= 4; // 标识空格
			} else {
				buffer.append(ch);
				flag &= ~(2 | 4); // 去除斜杠、空格标识
			}
		}

		int len = buffer.length();
		for (; len > 0; len--) {
			char ch = buffer.charAt(len - 1);
			if (!Character.isWhitespace(ch) && ch != ';') break;
		}

		if (len < buffer.length()) buffer.setLength(len);

		return buffer.toString();
	}

	public String toString() {
		return format();
	}

	/**
	 * SQL 格式化过滤器
	 * @author Demon 2012-2-11
	 */
	interface SQLFormatFilter {

		/**
		 * 执行转换
		 * @param formatter 格式化实例
		 * @param target 目标缓冲区
		 */
		void doFormat(SQLFormatter formatter, StringBuilder target);

		/**
		 * 是否需过滤
		 * @param formatter 格式化实例
		 * @return 是、否
		 */
		boolean isFilter(SQLFormatter formatter);
	}
}

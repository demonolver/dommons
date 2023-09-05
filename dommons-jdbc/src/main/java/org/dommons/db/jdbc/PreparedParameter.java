/*
 * @(#)PreparedParameter.java     2012-2-11
 */
package org.dommons.db.jdbc;

import java.nio.charset.Charset;
import java.sql.Types;
import java.util.Date;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.security.coder.B64Coder;

/**
 * 预编译参数
 * @author Demon 2012-2-11
 */
abstract class PreparedParameter {

	/**
	 * 转换 Base64
	 * @param bs 字节集
	 * @return Base64 串
	 */
	static String base4(byte[] bs) {
		return "from_base64(\'" + B64Coder.encodeBuffer(bs) + "\')";
	}

	/**
	 * 字节转换字符串
	 * @param bs 字节集
	 * @return 字符串
	 */
	static String string(byte[] bs) {
		try {
			return Stringure.toString(bs, Charset.defaultCharset());
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * 转换字符串
	 * @param value 值
	 * @return 字符串
	 */
	static String string(Object value) {
		if (value instanceof byte[]) {
			byte[] bs = (byte[]) value;
			String s = string(bs);
			if (s == null) s = base4(bs);
			return s;
		} else {
			return Converter.P.convert(value, String.class);
		}
	}

	private final int type;
	private final Object value;

	/**
	 * 构造函数
	 * @param type 参数类型
	 * @param value 参数值
	 */
	protected PreparedParameter(int type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * 拼合参数
	 * @param buf 缓冲区
	 */
	public void merge(StringBuilder buf) {
		switch (type) {
		case Types.BIT:
		case Types.TINYINT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
			SQLFormatter.appendString(toString(Long.class), buf);
			break;

		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.DECIMAL:
			SQLFormatter.appendString(toString(Number.class), buf);
			break;

		case Types.BOOLEAN:
			SQLFormatter.appendString(toString(Boolean.class), buf);
			break;

		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			SQLFormatter.appendDate(value, buf, type, getType(), getVersion());
			break;

		case Types.VARCHAR:
		case Types.CHAR:
			buf.append('\'');
			SQLFormatter.appendString(toString(String.class), buf);
			buf.append('\'');
			break;

		default:
			if (type == VersionAdapter.type("NVARCHAR", -9) || type == VersionAdapter.type("LONGNVARCHAR", -16)) {
				buf.append('\'');
				SQLFormatter.appendString(toString(String.class), buf);
				buf.append('\'');
			} else if (type == VersionAdapter.type("SQLXML", 2009)) {
				try {
					if (VersionAdapter.instanceOf(value, "java.sql.SQLXML")) SQLFormatter.appendString(Converter.P.convert(
						VersionAdapter.invoke(value, VersionAdapter.find(VersionAdapter.findClass("java.sql.SQLXML"), "getString")),
						String.class), buf);
				} catch (RuntimeException e) {
					SQLFormatter.appendString(toString(String.class), buf);
				}
			} else if (type == VersionAdapter.type("ROWID", -8)) {
				SQLFormatter.appendString(VersionAdapter.instanceOf(value, "java.sql.RowId") ? value.toString() : toString(String.class),
					buf);
			} else if (value instanceof byte[]) {
				byte[] bs = (byte[]) value;
				String s = string(bs);
				if (s != null) {
					buf.append('\'');
					SQLFormatter.appendString(s, buf);
					buf.append('\'');
				} else {
					buf.append(base4(bs));
				}
			} else {
				SQLFormatter.appendString(toString(String.class), buf);
			}

			break;
		}
	}

	public String toString() {
		switch (type) {
		case Types.BIT:
		case Types.TINYINT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
			return toString(Long.class);

		case Types.FLOAT:
		case Types.DECIMAL:
		case Types.DOUBLE:
			return toString(Number.class);

		case Types.BOOLEAN:
			return toString(Boolean.class);

		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return toString(Date.class);

		default:
			if (type == VersionAdapter.type("SQLXML", 2009)) {
				if (VersionAdapter.instanceOf(value, "java.sql.SQLXML")) {
					try {
						return Converter.P.convert(
							VersionAdapter.invoke(value, VersionAdapter.find(VersionAdapter.findClass("java.sql.SQLXML"), "getString")),
							String.class);
					} catch (RuntimeException e) {
					}
				}
				return toString(Object.class);
			} else if (type == VersionAdapter.type("ROWID", -8)) {
				return VersionAdapter.instanceOf(value, "java.sql.RowId") ? value.toString() : toString(Object.class);
			} else {
				return toString(Object.class);
			}
		}
	}

	/**
	 * 获取数据库类型
	 * @return 数据库类型
	 */
	protected abstract String getType();

	/**
	 * 获取数据库版本
	 * @return 数据库版本
	 */
	protected abstract String getVersion();

	/**
	 * 转换为字符串
	 * @param cls 计划数据类型
	 * @return 字符串
	 */
	protected String toString(Class cls) {

		Object value = this.value;
		try {
			if (String.class.equals(cls)) value = Converter.P.convert(this.value, cls);
		} catch (RuntimeException e) {
		}
		return string(value);
	}
}

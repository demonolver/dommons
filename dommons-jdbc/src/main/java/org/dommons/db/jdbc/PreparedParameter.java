/*
 * @(#)PreparedParameter.java     2012-2-11
 */
package org.dommons.db.jdbc;

import java.sql.Types;
import java.util.Date;
import org.dommons.core.convert.Converter;

/**
 * 预编译参数
 * @author Demon 2012-2-11
 */
abstract class PreparedParameter {

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
	 * @param buffer 缓冲区
	 */
	public void merge(StringBuilder buffer) {
		switch (type) {
		case Types.BIT:
		case Types.TINYINT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
			SQLFormatter.appendString(toString(Long.class), buffer);
			break;

		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.DECIMAL:
			SQLFormatter.appendString(toString(Number.class), buffer);
			break;

		case Types.BOOLEAN:
			SQLFormatter.appendString(toString(Boolean.class), buffer);
			break;

		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			SQLFormatter.appendDate(value, buffer, type, getType(), getVersion());
			break;

		case Types.VARBINARY:
		case Types.VARCHAR:
		case Types.CHAR:
		case Types.BINARY:
			buffer.append('\'');
			SQLFormatter.appendString(toString(String.class), buffer);
			buffer.append('\'');
			break;

		default:
			if (type == VersionAdapter.type("NVARCHAR", -9) || type == VersionAdapter.type("LONGNVARCHAR", -16)) {
				buffer.append('\'');
				SQLFormatter.appendString(toString(String.class), buffer);
				buffer.append('\'');
			} else if (type == VersionAdapter.type("SQLXML", 2009)) {
				try {
					if (VersionAdapter.instanceOf(value, "java.sql.SQLXML"))
						SQLFormatter.appendString(Converter.P.convert(
							VersionAdapter.invoke(value, VersionAdapter.find(VersionAdapter.findClass("java.sql.SQLXML"), "getString")),
							String.class), buffer);
				} catch (RuntimeException e) {
					SQLFormatter.appendString(toString(String.class), buffer);
				}
			} else if (type == VersionAdapter.type("ROWID", -8)) {
				SQLFormatter.appendString(VersionAdapter.instanceOf(value, "java.sql.RowId") ? value.toString() : toString(String.class),
					buffer);
			} else {
				SQLFormatter.appendString(toString(String.class), buffer);
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
				return toString(String.class);
			} else if (type == VersionAdapter.type("ROWID", -8)) {
				return VersionAdapter.instanceOf(value, "java.sql.RowId") ? value.toString() : toString(String.class);
			} else {
				return toString(String.class);
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
		Object value = null;
		try {
			value = Converter.P.convert(this.value, cls);
		} catch (RuntimeException e) {
			value = this.value;
		}
		return Converter.P.convert(value, String.class);
	}
}

/*
 * @(#)SPreparedStatement.java     2012-2-1
 */
package org.dommons.db.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.db.jdbc.SQLFormatter.SQLFormatFilter;
import org.dommons.io.prop.Bundles;

/**
 * 显 SQL 预编译数据库连接状态
 * @author Demon 2012-2-1
 */
class ShowablePreparedStatement extends ShowableStatement implements PreparedStatement {

	protected static boolean merge = Bundles.getBoolean(Environments.getProperties(), "merge.prepared.sql.params", Boolean.TRUE);

	protected PreparedContent content;

	protected final String sql;

	/**
	 * 构造函数
	 * @param pstat 连接状态
	 * @param conn 连接
	 * @param sql SQL 语句
	 */
	protected ShowablePreparedStatement(PreparedStatement pstat, ShowableConnection conn, String sql) {
		super(pstat, conn);
		this.sql = sql;
		this.content = new PreparedContent();
	}

	public void addBatch() throws SQLException {
		super.addBatch();
		addBatchSQL(this.content);
		this.content = new PreparedContent();
	}

	public void clearParameters() throws SQLException {
		super.clearParameters();
		if (content != null) content.clear();
	}

	public void close() throws SQLException {
		super.close();
		if (content != null) content.clear();
	}

	public boolean execute() throws SQLException {
		return execute(new Execution<Boolean, Object>() {
			public Boolean execute(Object sql) throws SQLException {
				return $execute();
			}
		});
	}

	public ResultSet executeQuery() throws SQLException {
		return execute(new Execution<ResultSet, Object>() {
			public ResultSet execute(Object sql) throws SQLException {
				return $executeQuery();
			}
		});
	}

	public int executeUpdate() throws SQLException {
		return execute(new Execution<Integer, Object>() {
			public Integer execute(Object sql) throws SQLException {
				return $executeUpdate();
			}
		});
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		super.setAsciiStream(parameterIndex, x);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		super.setAsciiStream(parameterIndex, x, length);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		super.setAsciiStream(parameterIndex, x, length);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		super.setBigDecimal(parameterIndex, x);
		content.register(parameterIndex, Types.DECIMAL, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		super.setBinaryStream(parameterIndex, x);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		super.setBinaryStream(parameterIndex, x, length);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		super.setBinaryStream(parameterIndex, x, length);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		super.setBlob(parameterIndex, x);
		content.register(parameterIndex, Types.BLOB, x);
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		super.setBlob(parameterIndex, inputStream);
		content.register(parameterIndex, Types.BLOB, inputStream);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		super.setBlob(parameterIndex, inputStream, length);
		content.register(parameterIndex, Types.BLOB, inputStream);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		super.setBoolean(parameterIndex, x);
		content.register(parameterIndex, Types.BOOLEAN, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		super.setByte(parameterIndex, x);
		content.register(parameterIndex, Types.BIT, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		super.setBytes(parameterIndex, x);
		content.register(parameterIndex, Types.BINARY, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		super.setCharacterStream(parameterIndex, reader);
		content.register(parameterIndex, Types.OTHER, reader);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		super.setCharacterStream(parameterIndex, reader, length);
		content.register(parameterIndex, Types.OTHER, reader);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		super.setCharacterStream(parameterIndex, reader, length);
		content.register(parameterIndex, Types.OTHER, reader);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		super.setClob(parameterIndex, x);
		content.register(parameterIndex, Types.CLOB, x);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		super.setClob(parameterIndex, reader);
		content.register(parameterIndex, Types.CLOB, reader);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		super.setClob(parameterIndex, reader, length);
		content.register(parameterIndex, Types.CLOB, reader);
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		super.setDate(parameterIndex, x);
		content.register(parameterIndex, Types.DATE, x);
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		super.setDate(parameterIndex, x, cal);
		content.register(parameterIndex, Types.DATE, toCalendar(x, cal));
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		super.setDouble(parameterIndex, x);
		content.register(parameterIndex, Types.DOUBLE, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		super.setFloat(parameterIndex, x);
		content.register(parameterIndex, Types.FLOAT, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		super.setInt(parameterIndex, x);
		content.register(parameterIndex, Types.INTEGER, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		super.setLong(parameterIndex, x);
		content.register(parameterIndex, Types.BIGINT, x);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		super.setNCharacterStream(parameterIndex, value);
		content.register(parameterIndex, Types.OTHER, value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		super.setNCharacterStream(parameterIndex, value, length);
		content.register(parameterIndex, Types.OTHER, value);
	}

	public void setNClob(int parameterIndex, java.sql.NClob value) throws SQLException {
		super.setNClob(parameterIndex, value);
		content.register(parameterIndex, VersionAdapter.type("NCLOB", 2011), value);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		super.setNClob(parameterIndex, reader);
		content.register(parameterIndex, VersionAdapter.type("NCLOB", 2011), reader);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		super.setNClob(parameterIndex, reader, length);
		content.register(parameterIndex, VersionAdapter.type("NCLOB", 2011), reader);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		super.setNString(parameterIndex, value);
		content.register(parameterIndex, VersionAdapter.type("NVARCHAR", -9), value);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		super.setNull(parameterIndex, sqlType);
		content.registerNull(parameterIndex);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		super.setNull(parameterIndex, sqlType, typeName);
		content.registerNull(parameterIndex);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		super.setObject(parameterIndex, x);
		if (x == null) {
			content.registerNull(parameterIndex);
		} else if (x instanceof java.util.Date) {
			content.register(parameterIndex, Types.TIMESTAMP, x);
		} else if (x instanceof String) {
			content.register(parameterIndex, Types.VARCHAR, x);
		}
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		super.setObject(parameterIndex, x, targetSqlType);
		content.register(parameterIndex, targetSqlType, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		super.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		content.register(parameterIndex, targetSqlType, x);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		super.setRef(parameterIndex, x);
		content.register(parameterIndex, Types.REF, x);
	}

	public void setRowId(int parameterIndex, java.sql.RowId x) throws SQLException {
		super.setRowId(parameterIndex, x);
		content.register(parameterIndex, VersionAdapter.type("ROWID", -8), x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		super.setShort(parameterIndex, x);
		content.register(parameterIndex, Types.SMALLINT, x);
	}

	public void setSQLXML(int parameterIndex, java.sql.SQLXML xmlObject) throws SQLException {
		super.setSQLXML(parameterIndex, xmlObject);
		content.register(parameterIndex, VersionAdapter.type("SQLXML", 2009), xmlObject);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		super.setString(parameterIndex, x);
		content.register(parameterIndex, Types.VARCHAR, x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		super.setTime(parameterIndex, x);
		content.register(parameterIndex, Types.TIME, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		super.setTime(parameterIndex, x, cal);
		content.register(parameterIndex, Types.TIME, toCalendar(x, cal));
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		super.setTimestamp(parameterIndex, x);
		content.register(parameterIndex, Types.TIMESTAMP, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		super.setTimestamp(parameterIndex, x, cal);
		content.register(parameterIndex, Types.TIMESTAMP, toCalendar(x, cal));
	}

	/**
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		super.setUnicodeStream(parameterIndex, x, length);
		content.register(parameterIndex, Types.OTHER, x);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		super.setURL(parameterIndex, x);
		content.register(parameterIndex, Types.OTHER, x);
	}

	/**
	 * 执行 SQL
	 * @param sql SQL
	 * @param execution 执行项
	 * @return 执行结果
	 * @throws SQLException
	 */
	protected <R> R execute(Execution<R, Object> execution) throws SQLException {
		last(null);

		R r = null;
		SQLException se = null;
		long time = conn.timestamp();
		try {
			r = execution.execute(null);
			time = conn.timestamp() - time;
			return r;
		} catch (SQLException e) {
			se = e;
			time = conn.timestamp() - time;
			throw transform(e, content);
		} finally {
			if (se == null && r instanceof Boolean) {
				registerLast(content, (Boolean) r, time);
			} else if (se == null && r instanceof ResultSet) {
				log(content, se, count((ResultSet) r), time);
			} else {
				log(content, se, r, time);
			}
		}
	}

	private boolean $execute() throws SQLException {
		return super.execute();
	}

	private ResultSet $executeQuery() throws SQLException {
		return super.executeQuery();
	}

	private int $executeUpdate() throws SQLException {
		return super.executeUpdate();
	}

	/**
	 * 转换时间
	 * @param date 时间值
	 * @param cal 时区
	 * @return 日历
	 */
	private Calendar toCalendar(java.util.Date date, Calendar cal) {
		Calendar p = cal == null ? Calendar.getInstance(Environments.defaultTimeZone(), Environments.defaultLocale())
				: Calendar.getInstance(cal.getTimeZone(), Environments.defaultLocale());
		p.setTimeInMillis(date == null ? 0 : date.getTime());
		return p;
	}

	/**
	 * 预编译内容
	 * @author Demon 2012-2-11
	 */
	protected class PreparedContent {

		public Map<Integer, Object> params;

		/**
		 * 构造函数
		 */
		protected PreparedContent() {
			params = new HashMap();
		}

		/**
		 * 清除参数集
		 */
		public void clear() {
			params.clear();
		}

		/**
		 * 注册参数
		 * @param index 序号
		 * @param type 类型
		 * @param value 参数值
		 */
		public void register(int index, int type, Object value) {
			// 存在上次未记录信息，注册新参数前，固化上次 SQL
			if (last != null && last[0].equals(this)) last[0] = toString();
			params.put(Integer.valueOf(index), value == null ? null : new ShowablePreparedParameter(type, value));
		}

		/**
		 * 注册空参数
		 * @param index 序号
		 */
		public void registerNull(int index) {
			params.put(Integer.valueOf(index), null);
		}

		public String toString() {
			return merge ? merge() : present();
		}

		/**
		 * 显示整合参数后的 SQL
		 * @return SQL 语句
		 */
		protected String merge() {
			return SQLFormatter.format(sql, new SQLFormatFilter() {
				int index = 1;

				public void doFormat(SQLFormatter formatter, StringBuilder target) {
					if ((formatter.flag & 4) == 0 && target.length() > 0) target.append(' ');

					Integer key = Integer.valueOf(index++);
					Object value = params.get(key);

					if (value == null && !params.containsKey(key)) { // 未设置参数，标识无参数 no
						target.append("no");
					} else if (value == null) {
						target.append("null");
					} else if (value instanceof PreparedParameter) {
						((PreparedParameter) value).merge(target);
					} else {
						SQLFormatter.appendString(Converter.P.convert(value, String.class), target);
					}

					target.append(' ');
					formatter.flag |= 4;
				}

				public boolean isFilter(SQLFormatter formatter) {
					return (formatter.flag & 1) == 0 && formatter.currentChar() == '?';
				}
			});
		}

		/**
		 * 显示 SQL 及参数
		 * @return SQL 语句
		 */
		protected String present() {
			StringBuilder buf = new StringBuilder(sql).append(';');
			int s = 0;
			for (int i = 1; s < params.size(); i++) {
				if (i == 1) {
					buf.append(" params: ");
				} else {
					buf.append(',');
				}

				Integer key = Integer.valueOf(i);
				if (params.containsKey(key)) {
					Object value = params.get(key);
					buf.append(PreparedParameter.string(value));
					s++;
				}
			}
			return buf.toString();
		}
	}

	/**
	 * 显 SQL 预编译参数
	 * @author Demon 2012-2-11
	 */
	protected class ShowablePreparedParameter extends PreparedParameter {

		/**
		 * 构造函数
		 * @param type 参数类型
		 * @param value 参数值
		 */
		public ShowablePreparedParameter(int type, Object value) {
			super(type, value);
		}

		protected String getType() {
			return conn.type;
		}

		protected String getVersion() {
			return conn.version;
		}
	}
}

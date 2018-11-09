/*
 * @(#)EssentialPreparedStatement.java     2012-2-2
 */
package org.dommons.db.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 基本预编译连接状态实现
 * @author Demon 2012-2-2
 */
public class EssentialPreparedStatement<C extends EssentialConnection> extends EssentialStatement<C> implements PreparedStatement {

	/**
	 * 构造函数
	 * @param stat 连接状态
	 * @param conn 连接实例
	 */
	protected EssentialPreparedStatement(Statement stat, C conn) {
		super(stat, conn);
	}

	public void addBatch() throws SQLException {
		((PreparedStatement) tar).addBatch();
	}

	public void clearParameters() throws SQLException {
		((PreparedStatement) tar).clearParameters();
	}

	public boolean execute() throws SQLException {
		return ((PreparedStatement) tar).execute();
	}

	public ResultSet executeQuery() throws SQLException {
		return result(((PreparedStatement) tar).executeQuery());
	}

	public int executeUpdate() throws SQLException {
		return ((PreparedStatement) tar).executeUpdate();
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return ((PreparedStatement) tar).getMetaData();
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return ((PreparedStatement) tar).getParameterMetaData();
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		((PreparedStatement) tar).setArray(parameterIndex, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setAsciiStream", int.class, InputStream.class), parameterIndex, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		((PreparedStatement) tar).setAsciiStream(parameterIndex, x, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setAsciiStream", int.class, InputStream.class, long.class),
			parameterIndex, x, length);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		((PreparedStatement) tar).setBigDecimal(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBinaryStream", int.class, InputStream.class), parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		((PreparedStatement) tar).setBinaryStream(parameterIndex, x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBinaryStream", int.class, InputStream.class, long.class),
			parameterIndex, x, length);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		((PreparedStatement) tar).setBlob(parameterIndex, x);
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBlob", int.class, InputStream.class), parameterIndex,
			inputStream);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBlob", int.class, InputStream.class, long.class),
			parameterIndex, inputStream, length);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		((PreparedStatement) tar).setBoolean(parameterIndex, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		((PreparedStatement) tar).setByte(parameterIndex, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		((PreparedStatement) tar).setBytes(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setCharacterStream", int.class, Reader.class), parameterIndex,
			reader);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		((PreparedStatement) tar).setCharacterStream(parameterIndex, reader, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setCharacterStream", int.class, Reader.class, long.class),
			parameterIndex, reader, length);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		((PreparedStatement) tar).setClob(parameterIndex, x);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClob", int.class, Reader.class), parameterIndex, reader);
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClob", int.class, Reader.class, long.class), parameterIndex,
			reader, length);
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		((PreparedStatement) tar).setDate(parameterIndex, x);
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		((PreparedStatement) tar).setDate(parameterIndex, x);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		((PreparedStatement) tar).setDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		((PreparedStatement) tar).setFloat(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		((PreparedStatement) tar).setInt(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		((PreparedStatement) tar).setLong(parameterIndex, x);
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNCharacterStream", int.class, Reader.class), parameterIndex,
			value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNCharacterStream", int.class, Reader.class, long.class),
			parameterIndex, value, length);
	}

	public void setNClob(int parameterIndex, java.sql.NClob value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", int.class, java.sql.NClob.class), parameterIndex, value);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", int.class, Reader.class), parameterIndex, reader);
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", int.class, Reader.class, long.class), parameterIndex,
			reader, length);
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNString", int.class, String.class), parameterIndex, value);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		((PreparedStatement) tar).setNull(parameterIndex, sqlType);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		((PreparedStatement) tar).setNull(parameterIndex, sqlType, typeName);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		((PreparedStatement) tar).setObject(parameterIndex, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		((PreparedStatement) tar).setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		((PreparedStatement) tar).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		((PreparedStatement) tar).setRef(parameterIndex, x);
	}

	public void setRowId(int parameterIndex, java.sql.RowId x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setRowId", int.class, java.sql.RowId.class), parameterIndex, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		((PreparedStatement) tar).setShort(parameterIndex, x);
	}

	public void setSQLXML(int parameterIndex, java.sql.SQLXML xmlObject) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setSQLXML", int.class, java.sql.SQLXML.class), parameterIndex,
			xmlObject);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		((PreparedStatement) tar).setString(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		((PreparedStatement) tar).setTime(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		((PreparedStatement) tar).setTime(parameterIndex, x, cal);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		((PreparedStatement) tar).setTimestamp(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		((PreparedStatement) tar).setTimestamp(parameterIndex, x, cal);
	}

	/**
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		((PreparedStatement) tar).setUnicodeStream(parameterIndex, x, length);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		((PreparedStatement) tar).setURL(parameterIndex, x);
	}

}

/*
 * @(#)EssentialCallableStatement.java     2012-2-2
 */
package org.dommons.db.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.dommons.core.convert.Converter;

/**
 * 基本存储过程调用连接状态
 * @author Demon 2012-2-2
 */
public class EssentialCallableStatement<C extends EssentialConnection> extends EssentialPreparedStatement<C> implements CallableStatement {

	/**
	 * 构造函数
	 * @param stat 连接状态
	 * @param conn 连接实例
	 */
	protected EssentialCallableStatement(Statement stat, C conn) {
		super(stat, conn);
	}

	public Array getArray(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getArray(parameterIndex);
	}

	public Array getArray(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getArray(parameterName);
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getBigDecimal(parameterIndex);
	}

	/**
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return ((CallableStatement) tar).getBigDecimal(parameterIndex, scale);
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getBigDecimal(parameterName);
	}

	public Blob getBlob(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getBlob(parameterIndex);
	}

	public Blob getBlob(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getBlob(parameterName);
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getBoolean(parameterIndex);
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getBoolean(parameterName);
	}

	public byte getByte(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getByte(parameterIndex);
	}

	public byte getByte(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getByte(parameterName);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getBytes(parameterIndex);
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getBytes(parameterName);
	}

	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getCharacterStream", int.class), parameterIndex), Reader.class);
	}

	public Reader getCharacterStream(String parameterName) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "", String.class), parameterName),
			Reader.class);
	}

	public Clob getClob(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getClob(parameterIndex);
	}

	public Clob getClob(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getClob(parameterName);
	}

	public Date getDate(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getDate(parameterIndex);
	}

	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getDate(parameterIndex, cal);
	}

	public Date getDate(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getDate(parameterName);
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getDate(parameterName, cal);
	}

	public double getDouble(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getDouble(parameterIndex);
	}

	public double getDouble(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getDouble(parameterName);
	}

	public float getFloat(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getFloat(parameterIndex);
	}

	public float getFloat(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getFloat(parameterName);
	}

	public int getInt(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getInt(parameterIndex);
	}

	public int getInt(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getInt(parameterName);
	}

	public long getLong(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getLong(parameterIndex);
	}

	public long getLong(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getLong(parameterName);
	}

	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return Converter.P
				.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNCharacterStream", int.class), parameterIndex),
					Reader.class);
	}

	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNCharacterStream", String.class), parameterName),
			Reader.class);
	}

	public java.sql.NClob getNClob(int parameterIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNClob", int.class), parameterIndex),
			java.sql.NClob.class);
	}

	public java.sql.NClob getNClob(String parameterName) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNClob", String.class), parameterName), java.sql.NClob.class);
	}

	public String getNString(int parameterIndex) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNString", int.class), parameterIndex), String.class);
	}

	public String getNString(String parameterName) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNString", String.class), parameterName), String.class);
	}

	public Object getObject(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getObject(parameterIndex);
	}

	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getObject", int.class, Class.class), parameterIndex, type),
			type);
	}

	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return ((CallableStatement) tar).getObject(parameterIndex, map);
	}

	public Object getObject(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getObject(parameterName);
	}

	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getObject", String.class, Class.class), parameterName, type),
			type);
	}

	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return ((CallableStatement) tar).getObject(parameterName, map);
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getRef(parameterIndex);
	}

	public Ref getRef(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getRef(parameterName);
	}

	public java.sql.RowId getRowId(int parameterIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getRowId", int.class), parameterIndex),
			java.sql.RowId.class);
	}

	public java.sql.RowId getRowId(String parameterName) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getRowId", String.class), parameterName), java.sql.RowId.class);
	}

	public short getShort(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getShort(parameterIndex);
	}

	public short getShort(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getShort(parameterName);
	}

	public java.sql.SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getSQLXML", int.class), parameterIndex),
			java.sql.SQLXML.class);
	}

	public java.sql.SQLXML getSQLXML(String parameterName) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getSQLXML", String.class), parameterName),
			java.sql.SQLXML.class);
	}

	public String getString(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getString(parameterIndex);
	}

	public String getString(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getString(parameterName);
	}

	public Time getTime(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getTime(parameterIndex);
	}

	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getTime(parameterIndex, cal);
	}

	public Time getTime(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getTime(parameterName);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getTime(parameterName, cal);
	}

	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getTimestamp(parameterIndex);
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getTimestamp(parameterIndex, cal);
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getTimestamp(parameterName);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) tar).getTimestamp(parameterName, cal);
	}

	public URL getURL(int parameterIndex) throws SQLException {
		return ((CallableStatement) tar).getURL(parameterIndex);
	}

	public URL getURL(String parameterName) throws SQLException {
		return ((CallableStatement) tar).getURL(parameterName);
	}

	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterIndex, sqlType);
	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterIndex, sqlType, scale);
	}

	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterIndex, sqlType, typeName);
	}

	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterName, sqlType);
	}

	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterName, sqlType, scale);
	}

	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		((CallableStatement) tar).registerOutParameter(parameterName, sqlType, typeName);
	}

	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		VersionAdapter
				.invoke(tar, VersionAdapter.find(tar.getClass(), "setAsciiStream", String.class, InputStream.class), parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		((CallableStatement) tar).setAsciiStream(parameterName, x, length);
	}

	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setAsciiStream", String.class, InputStream.class, long.class),
			parameterName, x, length);
	}

	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		((CallableStatement) tar).setBigDecimal(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBinaryStream", String.class, InputStream.class), parameterName,
			x);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		((CallableStatement) tar).setBinaryStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBinaryStream", String.class, InputStream.class, long.class),
			parameterName, x, length);
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBlob", String.class, Blob.class), parameterName, x);
	}

	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBlob", String.class, InputStream.class), parameterName,
			inputStream);
	}

	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setBlob", String.class, InputStream.class, long.class),
			parameterName, inputStream, length);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		((CallableStatement) tar).setBoolean(parameterName, x);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		((CallableStatement) tar).setByte(parameterName, x);
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		((CallableStatement) tar).setBytes(parameterName, x);
	}

	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setCharacterStream", String.class, Reader.class), parameterName,
			reader);
	}

	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		((CallableStatement) tar).setCharacterStream(parameterName, reader, length);
	}

	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setCharacterStream", String.class, Reader.class, long.class),
			parameterName, reader, length);
	}

	public void setClob(String parameterName, Clob x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClob", String.class, Clob.class), parameterName, x);
	}

	public void setClob(String parameterName, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClob", String.class, Reader.class), parameterName, reader);
	}

	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClob", String.class, Reader.class, long.class), parameterName,
			reader, length);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		((CallableStatement) tar).setDate(parameterName, x);
	}

	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		((CallableStatement) tar).setDate(parameterName, x, cal);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		((CallableStatement) tar).setDouble(parameterName, x);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		((CallableStatement) tar).setFloat(parameterName, x);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		((CallableStatement) tar).setInt(parameterName, x);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		((CallableStatement) tar).setLong(parameterName, x);
	}

	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNCharacterStream", String.class, Reader.class), parameterName,
			value);
	}

	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNCharacterStream", String.class, Reader.class, long.class),
			parameterName, value, length);
	}

	public void setNClob(String parameterName, java.sql.NClob value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", String.class, java.sql.NClob.class), parameterName,
			value);
	}

	public void setNClob(String parameterName, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", String.class, Reader.class), parameterName, reader);
	}

	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNClob", String.class, Reader.class, long.class), parameterName,
			reader, length);
	}

	public void setNString(String parameterName, String value) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNString", String.class, String.class), parameterName, value);
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		((CallableStatement) tar).setNull(parameterName, sqlType);
	}

	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		((CallableStatement) tar).setNull(parameterName, sqlType, typeName);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		((CallableStatement) tar).setObject(parameterName, x);
	}

	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		((CallableStatement) tar).setObject(parameterName, x, targetSqlType);
	}

	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		((CallableStatement) tar).setObject(parameterName, x, targetSqlType, scale);
	}

	public void setRowId(String parameterName, java.sql.RowId x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setRowId", String.class, java.sql.RowId.class), parameterName, x);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		((CallableStatement) tar).setShort(parameterName, x);
	}

	public void setSQLXML(String parameterName, java.sql.SQLXML xmlObject) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setSQLXML", String.class, java.sql.SQLXML.class), parameterName,
			xmlObject);
	}

	public void setString(String parameterName, String x) throws SQLException {
		((CallableStatement) tar).setString(parameterName, x);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		((CallableStatement) tar).setTime(parameterName, x);
	}

	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		((CallableStatement) tar).setTime(parameterName, x, cal);
	}

	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		((CallableStatement) tar).setTimestamp(parameterName, x);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		((CallableStatement) tar).setTimestamp(parameterName, x, cal);
	}

	public void setURL(String parameterName, URL val) throws SQLException {
		((CallableStatement) tar).setURL(parameterName, val);
	}
}

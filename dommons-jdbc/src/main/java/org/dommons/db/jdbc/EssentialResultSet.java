/*
 * @(#)EssentialResultSet.java     2012-2-1
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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.dommons.core.convert.Converter;

/**
 * 基本结果集状态实现
 * @author Demon 2012-2-1
 */
public class EssentialResultSet<S extends EssentialStatement> implements ResultSet {

	/** 目标结果集 */
	protected final ResultSet tar;

	/** 父连接状态 */
	protected final S stat;

	/** 是否关闭 */
	private volatile boolean closed;

	/**
	 * 构造函数
	 * @param rs 结果集
	 * @param stat 连接状态
	 */
	protected EssentialResultSet(ResultSet rs, S stat) {
		this.tar = rs;
		this.closed = false;
		this.stat = stat;
	}

	public boolean absolute(int row) throws SQLException {
		return tar.absolute(row);
	}

	public void afterLast() throws SQLException {
		tar.afterLast();
	}

	public void beforeFirst() throws SQLException {
		tar.beforeFirst();
	}

	public void cancelRowUpdates() throws SQLException {
		tar.cancelRowUpdates();
	}

	public void clearWarnings() throws SQLException {
		tar.clearWarnings();
	}

	public void close() throws SQLException {
		if (!closed) {
			tar.close();
			stat.remove(this);
			closed = true;
		}
	}

	public void deleteRow() throws SQLException {
		tar.deleteRow();
	}

	public int findColumn(String columnLabel) throws SQLException {
		return tar.findColumn(columnLabel);
	}

	public boolean first() throws SQLException {
		return tar.first();
	}

	public Array getArray(int columnIndex) throws SQLException {
		return tar.getArray(columnIndex);
	}

	public Array getArray(String columnLabel) throws SQLException {
		return tar.getArray(columnLabel);
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return tar.getAsciiStream(columnIndex);
	}

	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return tar.getAsciiStream(columnLabel);
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return tar.getBigDecimal(columnIndex);
	}

	/**
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return tar.getBigDecimal(columnIndex, scale);
	}

	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return tar.getBigDecimal(columnLabel);
	}

	/**
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return tar.getBigDecimal(columnLabel, scale);
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return tar.getBinaryStream(columnIndex);
	}

	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return tar.getBinaryStream(columnLabel);
	}

	public Blob getBlob(int columnIndex) throws SQLException {
		return tar.getBlob(columnIndex);
	}

	public Blob getBlob(String columnLabel) throws SQLException {
		return tar.getBlob(columnLabel);
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		return tar.getBoolean(columnIndex);
	}

	public boolean getBoolean(String columnLabel) throws SQLException {
		return tar.getBoolean(columnLabel);
	}

	public byte getByte(int columnIndex) throws SQLException {
		return tar.getByte(columnIndex);
	}

	public byte getByte(String columnLabel) throws SQLException {
		return tar.getByte(columnLabel);
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		return tar.getBytes(columnIndex);
	}

	public byte[] getBytes(String columnLabel) throws SQLException {
		return tar.getBytes(columnLabel);
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return tar.getCharacterStream(columnIndex);
	}

	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return tar.getCharacterStream(columnLabel);
	}

	public Clob getClob(int columnIndex) throws SQLException {
		return tar.getClob(columnIndex);
	}

	public Clob getClob(String columnLabel) throws SQLException {
		return tar.getClob(columnLabel);
	}

	public int getConcurrency() throws SQLException {
		return tar.getConcurrency();
	}

	public String getCursorName() throws SQLException {
		return tar.getCursorName();
	}

	public Date getDate(int columnIndex) throws SQLException {
		return tar.getDate(columnIndex);
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return tar.getDate(columnIndex, cal);
	}

	public Date getDate(String columnLabel) throws SQLException {
		return tar.getDate(columnLabel);
	}

	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return tar.getDate(columnLabel, cal);
	}

	public double getDouble(int columnIndex) throws SQLException {
		return tar.getDouble(columnIndex);
	}

	public double getDouble(String columnLabel) throws SQLException {
		return tar.getDouble(columnLabel);
	}

	public int getFetchDirection() throws SQLException {
		return tar.getFetchDirection();
	}

	public int getFetchSize() throws SQLException {
		return tar.getFetchSize();
	}

	public float getFloat(int columnIndex) throws SQLException {
		return tar.getFloat(columnIndex);
	}

	public float getFloat(String columnLabel) throws SQLException {
		return tar.getFloat(columnLabel);
	}

	public int getHoldability() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getHoldability")), int.class);
	}

	public int getInt(int columnIndex) throws SQLException {
		return tar.getInt(columnIndex);
	}

	public int getInt(String columnLabel) throws SQLException {
		return tar.getInt(columnLabel);
	}

	public long getLong(int columnIndex) throws SQLException {
		return tar.getLong(columnIndex);
	}

	public long getLong(String columnLabel) throws SQLException {
		return tar.getLong(columnLabel);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return tar.getMetaData();
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNCharacterStream", int.class), columnIndex), Reader.class);
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return Converter.P
				.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNCharacterStream", String.class), columnLabel),
					Reader.class);
	}

	public java.sql.NClob getNClob(int columnIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNClob", int.class), columnIndex),
			java.sql.NClob.class);
	}

	public java.sql.NClob getNClob(String columnLabel) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNClob", String.class), columnLabel),
			java.sql.NClob.class);
	}

	public String getNString(int columnIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNString", int.class), columnIndex),
			String.class);
	}

	public String getNString(String columnLabel) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNString", String.class), columnLabel), String.class);
	}

	public Object getObject(int columnIndex) throws SQLException {
		return tar.getObject(columnIndex);
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "", int.class, Class.class), columnIndex, type), type);
	}

	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return tar.getObject(columnIndex, map);
	}

	public Object getObject(String columnLabel) throws SQLException {
		return tar.getObject(columnLabel);
	}

	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return Converter.P.convert(
			VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getObject", String.class, Class.class), columnLabel, type),
			type);
	}

	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return tar.getObject(columnLabel, map);
	}

	public Ref getRef(int columnIndex) throws SQLException {
		return tar.getRef(columnIndex);
	}

	public Ref getRef(String columnLabel) throws SQLException {
		return tar.getRef(columnLabel);
	}

	public int getRow() throws SQLException {
		return tar.getRow();
	}

	public java.sql.RowId getRowId(int columnIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getRowId", int.class), columnIndex),
			java.sql.RowId.class);
	}

	public java.sql.RowId getRowId(String columnLabel) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getRowId", String.class), columnLabel),
			java.sql.RowId.class);
	}

	public short getShort(int columnIndex) throws SQLException {
		return tar.getShort(columnIndex);
	}

	public short getShort(String columnLabel) throws SQLException {
		return tar.getShort(columnLabel);
	}

	public java.sql.SQLXML getSQLXML(int columnIndex) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getSQLXML", int.class), columnIndex),
			java.sql.SQLXML.class);
	}

	public java.sql.SQLXML getSQLXML(String columnLabel) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getSQLXML", String.class), columnLabel),
			java.sql.SQLXML.class);
	}

	public Statement getStatement() throws SQLException {
		return stat;
	}

	public String getString(int columnIndex) throws SQLException {
		return tar.getString(columnIndex);
	}

	public String getString(String columnLabel) throws SQLException {
		return tar.getString(columnLabel);
	}

	public Time getTime(int columnIndex) throws SQLException {
		return tar.getTime(columnIndex);
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return tar.getTime(columnIndex, cal);
	}

	public Time getTime(String columnLabel) throws SQLException {
		return tar.getTime(columnLabel);
	}

	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return tar.getTime(columnLabel, cal);
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return tar.getTimestamp(columnIndex);
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return tar.getTimestamp(columnIndex, cal);
	}

	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return tar.getTimestamp(columnLabel);
	}

	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return tar.getTimestamp(columnLabel, cal);
	}

	public int getType() throws SQLException {
		return tar.getType();
	}

	/**
	 * @deprecated
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return tar.getUnicodeStream(columnIndex);
	}

	/**
	 * @deprecated
	 */
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return tar.getUnicodeStream(columnLabel);
	}

	public URL getURL(int columnIndex) throws SQLException {
		return tar.getURL(columnIndex);
	}

	public URL getURL(String columnLabel) throws SQLException {
		return tar.getURL(columnLabel);
	}

	public SQLWarning getWarnings() throws SQLException {
		return tar.getWarnings();
	}

	public void insertRow() throws SQLException {
		tar.insertRow();
	}

	public boolean isAfterLast() throws SQLException {
		return tar.isAfterLast();
	}

	public boolean isBeforeFirst() throws SQLException {
		return tar.isBeforeFirst();
	}

	public boolean isClosed() throws SQLException {
		return closed || Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "isClosed")), boolean.class);
	}

	public boolean isFirst() throws SQLException {
		return tar.isFirst();
	}

	public boolean isLast() throws SQLException {
		return tar.isLast();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "isWrapperFor", Class.class), iface),
			boolean.class);
	}

	public boolean last() throws SQLException {
		return tar.last();
	}

	public void moveToCurrentRow() throws SQLException {
		tar.moveToCurrentRow();
	}

	public void moveToInsertRow() throws SQLException {
		tar.moveToInsertRow();
	}

	public boolean next() throws SQLException {
		return tar.next();
	}

	public boolean previous() throws SQLException {
		return tar.previous();
	}

	public void refreshRow() throws SQLException {
		tar.refreshRow();
	}

	public boolean relative(int rows) throws SQLException {
		return tar.relative(rows);
	}

	public boolean rowDeleted() throws SQLException {
		return tar.rowDeleted();
	}

	public boolean rowInserted() throws SQLException {
		return tar.rowInserted();
	}

	public boolean rowUpdated() throws SQLException {
		return tar.rowUpdated();
	}

	public void setFetchDirection(int direction) throws SQLException {
		tar.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException {
		tar.setFetchSize(rows);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "unwrap", Class.class), iface), iface);
	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
		tar.updateArray(columnIndex, x);
	}

	public void updateArray(String columnLabel, Array x) throws SQLException {
		tar.updateArray(columnLabel, x);
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateAsciiStream", int.class, InputStream.class), columnIndex, x);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		tar.updateAsciiStream(columnIndex, x, length);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateAsciiStream", int.class, InputStream.class, long.class),
			columnIndex, x, length);
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateAsciiStream", String.class, InputStream.class), columnLabel,
			x);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		tar.updateAsciiStream(columnLabel, x, length);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateAsciiStream", String.class, InputStream.class, long.class),
			columnLabel, x, length);
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		tar.updateBigDecimal(columnIndex, x);
	}

	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		tar.updateBigDecimal(columnLabel, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBinaryStream", int.class, InputStream.class), columnIndex, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		tar.updateBinaryStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBinaryStream", int.class, InputStream.class, long.class),
			columnIndex, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBinaryStream", String.class, InputStream.class), columnLabel,
			x);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		tar.updateBinaryStream(columnLabel, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBinaryStream", String.class, InputStream.class, long.class),
			columnLabel, x, length);
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		tar.updateBlob(columnIndex, x);
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBlob", int.class, InputStream.class), columnIndex,
			inputStream);
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBlob", int.class, InputStream.class, long.class),
			columnIndex, inputStream, length);
	}

	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		tar.updateBlob(columnLabel, x);
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBlob", String.class, InputStream.class), columnLabel,
			inputStream);
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateBlob", String.class, InputStream.class, long.class),
			columnLabel, inputStream, length);
	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		tar.updateBoolean(columnIndex, x);
	}

	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		tar.updateBoolean(columnLabel, x);
	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		tar.updateByte(columnIndex, x);
	}

	public void updateByte(String columnLabel, byte x) throws SQLException {
		tar.updateByte(columnLabel, x);
	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		tar.updateBytes(columnIndex, x);
	}

	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		tar.updateBytes(columnLabel, x);
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateCharacterStream", int.class, Reader.class), columnIndex, x);
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		tar.updateCharacterStream(columnIndex, x, length);
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateCharacterStream", int.class, Reader.class, long.class),
			columnIndex, x, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateCharacterStream", String.class, Reader.class), columnLabel,
			reader);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		tar.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateCharacterStream", String.class, Reader.class, long.class),
			columnLabel, reader, length);
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
		tar.updateClob(columnIndex, x);
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateClob", int.class, Reader.class), columnIndex, reader);
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateClob", int.class, Reader.class, long.class), columnIndex,
			reader, length);
	}

	public void updateClob(String columnLabel, Clob x) throws SQLException {
		tar.updateClob(columnLabel, x);
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateClob", String.class, Reader.class), columnLabel, reader);
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateClob", String.class, Reader.class, long.class), columnLabel,
			reader, length);
	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		tar.updateDate(columnIndex, x);
	}

	public void updateDate(String columnLabel, Date x) throws SQLException {
		tar.updateDate(columnLabel, x);
	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		tar.updateDouble(columnIndex, x);
	}

	public void updateDouble(String columnLabel, double x) throws SQLException {
		tar.updateDouble(columnLabel, x);
	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		tar.updateFloat(columnIndex, x);
	}

	public void updateFloat(String columnLabel, float x) throws SQLException {
		tar.updateFloat(columnLabel, x);
	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		tar.updateInt(columnIndex, x);
	}

	public void updateInt(String columnLabel, int x) throws SQLException {
		tar.updateInt(columnLabel, x);
	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		tar.updateLong(columnIndex, x);
	}

	public void updateLong(String columnLabel, long x) throws SQLException {
		tar.updateLong(columnLabel, x);
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNCharacterStream", int.class, Reader.class), columnIndex, x);
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNCharacterStream", int.class, Reader.class, long.class),
			columnIndex, x, length);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "", String.class, Reader.class), columnLabel, reader);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNCharacterStream", String.class, Reader.class, long.class),
			columnLabel, reader, length);
	}

	public void updateNClob(int columnIndex, java.sql.NClob nClob) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", int.class, java.sql.NClob.class), columnIndex, nClob);
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", int.class, Reader.class), columnIndex, reader);
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", int.class, Reader.class, long.class), columnIndex,
			reader, length);
	}

	public void updateNClob(String columnLabel, java.sql.NClob nClob) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", String.class, java.sql.NClob.class), columnLabel,
			nClob);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", String.class, Reader.class), columnLabel, reader);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNClob", String.class, Reader.class, long.class), columnLabel,
			reader, length);
	}

	public void updateNString(int columnIndex, String nString) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNString", int.class, String.class), columnIndex, nString);
	}

	public void updateNString(String columnLabel, String nString) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateNString", String.class, String.class), columnLabel, nString);
	}

	public void updateNull(int columnIndex) throws SQLException {
		tar.updateNull(columnIndex);
	}

	public void updateNull(String columnLabel) throws SQLException {
		tar.updateNull(columnLabel);
	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		tar.updateObject(columnIndex, x);
	}

	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		tar.updateObject(columnIndex, x, scaleOrLength);
	}

	public void updateObject(String columnLabel, Object x) throws SQLException {
		tar.updateObject(columnLabel, x);
	}

	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		tar.updateObject(columnLabel, x, scaleOrLength);
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
		tar.updateRef(columnIndex, x);
	}

	public void updateRef(String columnLabel, Ref x) throws SQLException {
		tar.updateRef(columnLabel, x);
	}

	public void updateRow() throws SQLException {
		tar.updateRow();
	}

	public void updateRowId(int columnIndex, java.sql.RowId x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateRowId", int.class, java.sql.RowId.class), columnIndex, x);
	}

	public void updateRowId(String columnLabel, java.sql.RowId x) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateRowId", String.class, java.sql.RowId.class), columnLabel, x);
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		tar.updateShort(columnIndex, x);
	}

	public void updateShort(String columnLabel, short x) throws SQLException {
		tar.updateShort(columnLabel, x);
	}

	public void updateSQLXML(int columnIndex, java.sql.SQLXML xmlObject) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateSQLXML", int.class, java.sql.SQLXML.class), columnIndex,
			xmlObject);
	}

	public void updateSQLXML(String columnLabel, java.sql.SQLXML xmlObject) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "updateSQLXML", String.class, java.sql.SQLXML.class), columnLabel,
			xmlObject);
	}

	public void updateString(int columnIndex, String x) throws SQLException {
		tar.updateString(columnIndex, x);
	}

	public void updateString(String columnLabel, String x) throws SQLException {
		tar.updateString(columnLabel, x);
	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
		tar.updateTime(columnIndex, x);
	}

	public void updateTime(String columnLabel, Time x) throws SQLException {
		tar.updateTime(columnLabel, x);
	}

	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		tar.updateTimestamp(columnIndex, x);
	}

	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		tar.updateTimestamp(columnLabel, x);
	}

	public boolean wasNull() throws SQLException {
		return tar.wasNull();
	}
}

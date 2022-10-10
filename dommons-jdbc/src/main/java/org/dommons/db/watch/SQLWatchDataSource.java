/*
 * @(#)SQLWatchDataSource.java     2022-09-30
 */
package org.dommons.db.watch;

import java.net.URI;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.dommons.core.collections.map.DataPair;
import org.dommons.core.string.Stringure;
import org.dommons.db.jdbc.DatabaseGeneral;
import org.dommons.db.jdbc.ShowableDataSource;

/**
 * SQL 可观察数据源
 * @author demon 2022-09-30
 */
public class SQLWatchDataSource extends ShowableDataSource {

	protected final SQLWatchFilter filter;

	private Entry<String, Integer> host;

	protected final SQLWatchConnection empty;

	public SQLWatchDataSource(DataSource dataSource, SQLWatchFilter filter) {
		super(dataSource);
		this.filter = filter;
		this.empty = (SQLWatchConnection) connection(new EmptyConnection(), null);
	}

	public SQLWatchDataSource(DataSource dataSource, String name, SQLWatchFilter filter) {
		super(dataSource, name);
		this.filter = filter;
		this.empty = (SQLWatchConnection) connection(new EmptyConnection(), null);
	}

	@Override
	public Connection getConnection() throws SQLException {
		long s = empty.timestamp();
		SQLWatchConnection sc = null;
		try {
			Connection c = super.getConnection();
			if (c instanceof SQLWatchConnection) sc = (SQLWatchConnection) c;
			return c;
		} finally {
			if (sc != null && filter != null) filter.onFilter(sc.buildConnectContent(s));
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		long s = empty.timestamp();
		SQLWatchConnection sc = null;
		try {
			Connection c = super.getConnection(username, password);
			if (c instanceof SQLWatchConnection) sc = (SQLWatchConnection) c;
			return c;
		} finally {
			if (sc != null && filter != null) filter.onFilter(sc.buildConnectContent(s));
		}
	}

	/**
	 * 注入连接URL
	 * @param <C> 数据源类型
	 * @param url 连接URL
	 * @return 数据源实例
	 */
	public <C extends SQLWatchDataSource> C withJDBCUrl(String url) {
		if (Stringure.isEmpty(url)) return (C) this;
		parse: try {
			DataPair<String, Integer> info = DataPair.create(null, Integer.valueOf(0));
			URI u = SQLWatchConnection.parse(url);
			if (u == null) break parse;
			info.setKey(u.getHost());
			info.setValue(u.getPort());
			host = info;
		} catch (Throwable t) { // ignored
		}
		return (C) this;
	}

	@Override
	protected Connection connection(Connection conn, DatabaseGeneral general) {
		return host(new SQLWatchConnection(conn, general, filter));
	}

	@Override
	protected Connection connection(Connection conn, String name, String type, String version) {
		DatabaseGeneral general = new DatabaseGeneral(name).setType(type).setVersion(version);
		return connection(conn, general);
	}

	/**
	 * 注入连接地址
	 * @param sc 连接实例
	 * @return 新连接实例
	 */
	protected Connection host(SQLWatchConnection sc) {
		return host != null ? sc.set(host.getKey(), host.getValue()) : sc;
	}

	private static class EmptyConnection implements Connection {

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		@Override
		public Statement createStatement() throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return null;
		}

		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			return null;
		}

		@Override
		public String nativeSQL(String sql) throws SQLException {
			return null;
		}

		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {

		}

		@Override
		public boolean getAutoCommit() throws SQLException {
			return false;
		}

		@Override
		public void commit() throws SQLException {

		}

		@Override
		public void rollback() throws SQLException {

		}

		@Override
		public void close() throws SQLException {

		}

		@Override
		public boolean isClosed() throws SQLException {
			return false;
		}

		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return null;
		}

		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {

		}

		@Override
		public boolean isReadOnly() throws SQLException {
			return false;
		}

		@Override
		public void setCatalog(String catalog) throws SQLException {

		}

		@Override
		public String getCatalog() throws SQLException {
			return null;
		}

		@Override
		public void setTransactionIsolation(int level) throws SQLException {

		}

		@Override
		public int getTransactionIsolation() throws SQLException {
			return 0;
		}

		@Override
		public SQLWarning getWarnings() throws SQLException {
			return null;
		}

		@Override
		public void clearWarnings() throws SQLException {

		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return null;
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return null;
		}

		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return null;
		}

		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

		}

		@Override
		public void setHoldability(int holdability) throws SQLException {

		}

		@Override
		public int getHoldability() throws SQLException {
			return 0;
		}

		@Override
		public Savepoint setSavepoint() throws SQLException {
			return null;
		}

		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return null;
		}

		@Override
		public void rollback(Savepoint savepoint) throws SQLException {

		}

		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {

		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return null;
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return null;
		}

		@Override
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return null;
		}

		@Override
		public Clob createClob() throws SQLException {
			return null;
		}

		@Override
		public Blob createBlob() throws SQLException {
			return null;
		}

		@Override
		public NClob createNClob() throws SQLException {
			return null;
		}

		@Override
		public SQLXML createSQLXML() throws SQLException {
			return null;
		}

		@Override
		public boolean isValid(int timeout) throws SQLException {
			return false;
		}

		@Override
		public void setClientInfo(String name, String value) throws SQLClientInfoException {

		}

		@Override
		public void setClientInfo(Properties properties) throws SQLClientInfoException {

		}

		@Override
		public String getClientInfo(String name) throws SQLException {
			return null;
		}

		@Override
		public Properties getClientInfo() throws SQLException {
			return null;
		}

		@Override
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return null;
		}

		@Override
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return null;
		}

		@Override
		public void setSchema(String schema) throws SQLException {

		}

		@Override
		public String getSchema() throws SQLException {
			return null;
		}

		@Override
		public void abort(Executor executor) throws SQLException {

		}

		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

		}

		@Override
		public int getNetworkTimeout() throws SQLException {
			return 0;
		}
	}
}

/*
 * @(#)AbstractDataSource.java     2012-3-28
 */
package org.dommons.db.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.dommons.core.convert.Converter;

/**
 * 抽象数据源
 * @author Demon 2012-3-28
 */
public abstract class AbstractDataSource implements DataSource {

	/** 目标数据源 */
	protected final DataSource tar;

	/**
	 * 构造函数
	 * @param dataSource 目标数据源
	 */
	protected AbstractDataSource(DataSource dataSource) {
		this.tar = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return tar.getConnection();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return tar.getConnection(username, password);
	}

	public int getLoginTimeout() throws SQLException {
		return tar.getLoginTimeout();
	}

	public PrintWriter getLogWriter() throws SQLException {
		return tar.getLogWriter();
	}

	public Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(DataSource.class, "getParentLogger")), Logger.class);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(DataSource.class, "isWrapperFor", Class.class), iface),
			boolean.class);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		tar.setLoginTimeout(seconds);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		tar.setLogWriter(out);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(DataSource.class, "unwrap", Class.class), iface), iface);
	}
}

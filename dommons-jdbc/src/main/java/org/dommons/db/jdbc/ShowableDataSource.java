/*
 * @(#)ShowableDataSource.java     2012-4-9
 */
package org.dommons.db.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dommons.core.string.Stringure;

/**
 * 显 SQL 数据源
 * @author Demon 2012-4-9
 */
public class ShowableDataSource extends AbstractDataSource {

	private String name;
	private String type;
	private String version;

	/**
	 * 构造函数
	 * @param dataSource 目标数据源
	 */
	public ShowableDataSource(DataSource dataSource) {
		this(dataSource, null);
	}

	/**
	 * 构造函数
	 * @param dataSource 目标数据源
	 * @param name 数据源名称
	 */
	public ShowableDataSource(DataSource dataSource, String name) {
		super(dataSource);
		this.name = name;
	}

	public Connection getConnection() throws SQLException {
		return connection(super.getConnection());
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return connection(super.getConnection(username, password));
	}

	/**
	 * 转换连接实例
	 * @param conn 目标连接
	 * @return 显 SQL 连接
	 * @throws SQLException
	 */
	protected Connection connection(Connection conn) throws SQLException {
		if (conn == null) return conn;
		if (name == null || type == null || version == null) {
			if (Stringure.isEmpty(name)) name = conn.getCatalog();
			DatabaseMetaData metaData = conn.getMetaData();
			type = metaData.getDatabaseProductName();
			version = metaData.getDatabaseProductVersion();
		}
		return new ShowableConnection(conn, name, type, version);
	}
}

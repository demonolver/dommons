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

	private final DatabaseGeneral general;

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
		this.general = new DatabaseGeneral(name);
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
		if (!fetch(general)) {
			if (Stringure.isEmpty(general.getName())) general.setName(conn.getCatalog());
			DatabaseMetaData metaData = conn.getMetaData();
			general.setType(metaData.getDatabaseProductName());
			general.setVersion(metaData.getDatabaseProductVersion());
		}
		return connection(conn, general);
	}

	/**
	 * 转换连接实例
	 * @param conn 目标连接
	 * @param general 数据库信息
	 * @return 显 SQL 连接
	 */
	protected Connection connection(Connection conn, DatabaseGeneral general) {
		return new ShowableConnection(conn, general);
	}

	/**
	 * 转换连接实例
	 * @param conn 目标连接
	 * @param name 数据库名
	 * @param type 数据库类型
	 * @param version 数据库版本
	 * @return 显 SQL 连接
	 * @deprecated {@link #connection(Connection, DatabaseGeneral)}
	 */
	protected Connection connection(Connection conn, String name, String type, String version) {
		return new ShowableConnection(conn, name, type, version);
	}

	private boolean fetch(DatabaseGeneral general) {
		if (general.getName() == null) return false;
		else if (general.getType() == null) return false;
		else if (general.getVersion() == null) return false;
		return true;
	}
}

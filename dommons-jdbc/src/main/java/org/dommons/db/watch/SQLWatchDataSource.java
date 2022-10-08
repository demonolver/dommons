/*
 * @(#)SQLWatchDataSource.java     2022-09-30
 */
package org.dommons.db.watch;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dommons.db.jdbc.DatabaseGeneral;
import org.dommons.db.jdbc.ShowableDataSource;

/**
 * SQL 可观察数据源
 * @author demon 2022-09-30
 */
public class SQLWatchDataSource extends ShowableDataSource {

	protected final SQLWatchFilter filter;

	public SQLWatchDataSource(DataSource dataSource, SQLWatchFilter filter) {
		super(dataSource);
		this.filter = filter;
	}

	public SQLWatchDataSource(DataSource dataSource, String name, SQLWatchFilter filter) {
		super(dataSource, name);
		this.filter = filter;
	}

	@Override
	protected Connection connection(Connection conn) throws SQLException {
		return new SQLWatchConnection(conn, filter);
	}

	@Override
	protected Connection connection(Connection conn, DatabaseGeneral general) {
		return new SQLWatchConnection(conn, general, filter);
	}

	@Override
	protected Connection connection(Connection conn, String name, String type, String version) {
		DatabaseGeneral general = new DatabaseGeneral(name).setType(type).setVersion(version);
		return connection(conn, general);
	}
}

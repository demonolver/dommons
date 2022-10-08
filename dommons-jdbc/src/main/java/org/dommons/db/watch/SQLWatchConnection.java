/*
 * @(#)SQLWatchConnection.java     2022-09-30
 */
package org.dommons.db.watch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.dommons.db.jdbc.DatabaseGeneral;
import org.dommons.db.jdbc.ShowableConnection;

/**
 * SQL 可观察数据库连接
 * @author demon 2022-09-30
 */
public class SQLWatchConnection extends ShowableConnection {

	protected final SQLWatchFilter filter;

	public SQLWatchConnection(Connection conn, SQLWatchFilter filter) {
		super(conn);
		this.filter = filter;
	}

	public SQLWatchConnection(Connection conn, DatabaseGeneral general, SQLWatchFilter filter) {
		super(conn, general);
		this.filter = filter;
	}

	@Override
	protected CallableStatement callableStatement(CallableStatement cstat, String sql) {
		// TODO Auto-generated method stub
		return super.callableStatement(cstat, sql);
	}

	@Override
	protected PreparedStatement preparedStatement(PreparedStatement pstat, String sql) {
		// TODO Auto-generated method stub
		return super.preparedStatement(pstat, sql);
	}

	@Override
	protected Statement statement(Statement stat) {
		// TODO Auto-generated method stub
		return super.statement(stat);
	}
}

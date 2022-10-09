/*
 * @(#)SQLWatchDataSource.java     2022-09-30
 */
package org.dommons.db.watch;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.dommons.core.collections.map.DataPair;
import org.dommons.db.jdbc.DatabaseGeneral;
import org.dommons.db.jdbc.ShowableDataSource;

/**
 * SQL 可观察数据源
 * @author demon 2022-09-30
 */
public class SQLWatchDataSource extends ShowableDataSource {

	protected final SQLWatchFilter filter;

	private Entry<String, Integer> host;

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
		return host(new SQLWatchConnection(conn, filter));
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

	/**
	 * 注入连接URL
	 * @param <C> 数据源类型
	 * @param url 连接URL
	 * @return 数据源实例
	 */
	protected <C extends SQLWatchDataSource> C withJDBCUrl(String url) {
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
}

/*
 * @(#)SQLWatchConnection.java     2022-09-30
 */
package org.dommons.db.watch;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.dommons.core.collections.map.DataPair;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.db.jdbc.DatabaseGeneral;
import org.dommons.db.jdbc.ShowableConnection;
import org.dommons.db.watch.SQLWatchFilter.SQLWatchContent;
import org.dommons.db.watch.SQLWatchFilter.SQLWatchKind;

/**
 * SQL 可观察数据库连接
 * @author demon 2022-09-30
 */
public class SQLWatchConnection extends ShowableConnection {

	/**
	 * 解析 JDBC URL
	 * @param url 连接串
	 * @return URI 信息
	 */
	public static URI parse(String url) {
		URI u = URI.create(url);
		while (u != null) {
			String s = u.getSchemeSpecificPart();
			if (s == null || s.isEmpty()) break;
			else if (s.startsWith("//")) break;
			u = URI.create(s);
		}
		return u;
	}

	protected final SQLWatchFilter filter;
	private String $catalog;

	private Entry<String, Integer> $tarinfo;

	public SQLWatchConnection(Connection conn, DatabaseGeneral general, SQLWatchFilter filter) {
		super(conn, general);
		this.filter = filter;
	}

	public SQLWatchConnection(Connection conn, SQLWatchFilter filter) {
		super(conn);
		this.filter = filter;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		long s = timestamp();
		String last = catalog();
		try {
			if (Arrayard.equals(catalog, last)) return;
			super.setCatalog(catalog);
			this.$catalog = catalog;
		} finally {
			if (filter != null) {
				String sql = "use `" + catalog + "`;";
				SQLWatchContent content = new SQLWatchContent(sql).setKind(SQLWatchKind.CONFIG).setCatalog(last);
				content.setUniqueID(unique(connectID)).setMillis(toMillis(timestamp() - s));
				filter.onFilter(handleConnection(content));
			}
		}
	}

	/**
	 * 构建连接SQL上下文
	 * @param start 开始时间戳
	 * @return 上下文
	 */
	protected SQLWatchContent buildConnectContent(long start) {
		SQLWatchContent content = new SQLWatchContent("/* new connection; */").setCatalog(Stringure.trim($catalog));
		content.setKind(SQLWatchKind.CONNECT).setMillis(toMillis(timestamp() - start)).setUniqueID(unique(connectID));
		return handleConnection(content);
	}

	/**
	 * 获取数据库名
	 * @return 数据库名
	 */
	protected String catalog() {
		if ($catalog != null) return $catalog;
		try {
			this.$catalog = Stringure.trim(getCatalog());
		} catch (SQLException e) {
			throw new UnsupportedOperationException(e);
		}
		return $catalog;
	}

	/**
	 * 注入数据库名
	 * @param <C> 连接类型
	 * @param catalog 数据库名
	 * @return 连接实例
	 */
	protected <C extends SQLWatchConnection> C catalog(String catalog) {
		this.$catalog = catalog;
		return (C) this;
	}

	/**
	 * 注入连接信息
	 * @param content 上下文内容
	 * @return 新上下文内容
	 */
	protected SQLWatchContent handleConnection(SQLWatchContent content) {
		if ($tarinfo == null) {
			DataPair<String, Integer> info = DataPair.create(null, Integer.valueOf(0));
			parse: try {
				String url = getMetaData().getURL();
				URI u = parse(url);
				if (u == null) break parse;
				info.setKey(u.getHost());
				info.setValue(u.getPort());
			} catch (Throwable t) { // ignored
			}
			$tarinfo = info;
		}
		content.setHost($tarinfo.getKey()).setPort($tarinfo.getValue());
		return content;
	}

	@Override
	protected void onAction(ConnectionAction action, Throwable t, Number millis) {
		super.onAction(action, t, millis);
		if (filter != null) {
			String sql = null;
			if (action == ConnectionAction.CLOSE) sql = "/* close; */";
			else if (action == ConnectionAction.COMMIT) sql = "commit;";
			else if (action == ConnectionAction.ROLLBACK) sql = "rollback;";
			else if (action == ConnectionAction.TRANSACTION) sql = "start transaction;";
			else return;
			SQLWatchContent content = new SQLWatchContent(sql).setCatalog(catalog());
			content.setKind(action == ConnectionAction.CLOSE ? SQLWatchKind.CONNECT : SQLWatchKind.CONFIG);
			content.setUniqueID(unique(connectID)).setMillis(millis);
			filter.onFilter(handleConnection(content));
		}
	}

	@Override
	protected void onExecute(Object content, Object result, Number millis, Boolean select, String sql, String connectID) {
		super.onExecute(content, result, millis, select, sql, connectID);
		if (filter != null) {
			SQLWatchKind kind = kind(select);
			if (content instanceof BatchSQL) {
				Collection<Entry<String, Object>> batch = splitBatch((BatchSQL) content, result);
				Collection<SQLWatchContent> cs = new ArrayList(batch.size());
				String c = catalog();
				for (Entry<String, Object> en : batch) {
					SQLWatchContent swc = new SQLWatchContent(en.getKey()).setCatalog(c);
					swc.setKind(kind).setUniqueID(connectID);
					swc.setBatchMillis(millis).setCountInBatch(batch.size());
					cs.add(handleConnection(swc));
				}
				if (!cs.isEmpty()) filter.onFilter(cs);
			} else {
				SQLWatchContent swc = new SQLWatchContent(sql).setKind(kind).setCatalog(catalog());
				swc.setKind(kind).setUniqueID(connectID).setMillis(millis);
				filter.onFilter(handleConnection(swc));
			}
		}
	}

	/**
	 * 设置连接目标信息
	 * @param <C> 连接类型
	 * @param host 连接地址
	 * @param port 连接端口
	 * @return 连接实例
	 */
	protected <C extends SQLWatchConnection> C set(String host, Integer port) {
		this.$tarinfo = DataPair.create(host, port);
		return (C) this;
	}

	@Override
	protected long timestamp() {
		return super.timestamp();
	}

	@Override
	protected Number toMillis(long time) {
		return super.toMillis(time);
	}

	@Override
	protected String unique(String connectID) {
		return super.unique(connectID);
	}

	SQLWatchKind kind(Boolean select) {
		if (Boolean.TRUE.equals(select)) return SQLWatchKind.SELECT;
		else if (Boolean.FALSE.equals(select)) return SQLWatchKind.UPDATE;
		else return SQLWatchKind.UNKNOWN;
	}

	Collection<Entry<String, Object>> splitBatch(BatchSQL batch, Object result) {
		Collection<Object> rs = new LinkedList(), bs = new LinkedList();
		if (!(result instanceof BatchResult)) rs.add(result);
		else((BatchResult) result).toResults(rs);
		batch.toBatch(bs);
		Collection<Entry<String, Object>> list = new ArrayList(bs.size());
		for (Iterator<Object> bit = bs.iterator(), rit = bs.iterator(); bit.hasNext();) {
			Object sql = bit.next();
			Object r = rit.hasNext() ? rit.next() : Integer.valueOf(0);
			String s = String.valueOf(sql);
			if (!s.endsWith(";")) s += ';';
			list.add(DataPair.create(s, r));
		}
		return list;
	}
}

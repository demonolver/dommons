/*
 * @(#)SQLWatchFilter.java     2022-09-30
 */
package org.dommons.db.watch;

import java.io.Serializable;
import java.util.Collection;

/**
 * SQL 可观察过滤
 * @author demon 2022-09-30
 */
public interface SQLWatchFilter {

	/**
	 * 执行过滤
	 * @param cs SQL 上下文集
	 */
	void onFilter(Collection<SQLWatchContent> cs);

	/**
	 * 执行过滤
	 * @param content SQL 上下文
	 */
	void onFilter(SQLWatchContent content);

	/**
	 * SQL 可观察上下文
	 * @author demon 2022-10-08
	 */
	public static class SQLWatchContent implements Serializable {
		private static final long serialVersionUID = 8803148819295078584L;

		private SQLWatchKind kind;
		private String SQL;
		private Object result = Integer.valueOf(0);
		private Number millis;
		private String uniqueID;
		private String host;
		private int port;
		private String catalog;

		private Number batchMillis;
		private Integer countInBatch;

		public SQLWatchContent() {
			super();
		}

		public SQLWatchContent(String SQL) {
			super();
			this.SQL = SQL;
		}

		public Number getBatchMillis() {
			return batchMillis;
		}

		public String getCatalog() {
			return catalog;
		}

		public Integer getCountInBatch() {
			return countInBatch;
		}

		public String getHost() {
			return host;
		}

		public SQLWatchKind getKind() {
			return kind;
		}

		public Number getMillis() {
			return millis;
		}

		public int getPort() {
			return port;
		}

		public Object getResult() {
			return result;
		}

		public String getSQL() {
			return SQL;
		}

		public String getUniqueID() {
			return uniqueID;
		}

		public SQLWatchContent setBatchMillis(Number batchMillis) {
			this.batchMillis = batchMillis;
			return this;
		}

		public SQLWatchContent setCatalog(String catalog) {
			this.catalog = catalog;
			return this;
		}

		public SQLWatchContent setCountInBatch(Integer countInBatch) {
			this.countInBatch = countInBatch;
			return this;
		}

		public SQLWatchContent setHost(String host) {
			this.host = host;
			return this;
		}

		public SQLWatchContent setKind(SQLWatchKind kind) {
			this.kind = kind;
			return this;
		}

		public SQLWatchContent setMillis(Number millis) {
			this.millis = millis;
			return this;
		}

		public SQLWatchContent setPort(int port) {
			this.port = port;
			return this;
		}

		public SQLWatchContent setResult(Object result) {
			this.result = result;
			return this;
		}

		public SQLWatchContent setSQL(String SQL) {
			this.SQL = SQL;
			return this;
		}

		public SQLWatchContent setUniqueID(String uniqueID) {
			this.uniqueID = uniqueID;
			return this;
		}
	}

	/**
	 * SQL 种类
	 * @author demon 2022-10-08
	 */
	public static enum SQLWatchKind {
		SELECT,
		UPDATE,
		CONFIG,
		UNKNOWN,
		CONNECT;
	}
}

/*
 * @(#)ShowableConnection.java     2012-2-1
 */
package org.dommons.db.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.net.UniQueness;
import org.dommons.io.nls.NLSItem;
import org.dommons.io.prop.Bundles;
import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * 显 SQL 数据库连接
 * @author Demon 2012-2-1
 */
public class ShowableConnection extends EssentialConnection {

	static final Logger logger = LoggerFactory.getInstance().getLogger(ShowableConnection.class);

	private static Map<String, Boolean> scrollInsensitives;

	/**
	 * 生成连接编号
	 * @return 连接编号
	 */
	protected static String generateID() {
		return UniQueness.generateHexUUID().toLowerCase();
	}

	/**
	 * 是否支持双向游标
	 * @param name 数据源名称
	 * @param conn 连接实例
	 * @return 是、否
	 */
	protected static boolean supportsScrollInsensitive(String name, Connection conn) {
		if (!Bundles.getBoolean(Environments.getProperties(), "connection.scroll.insensitive", Boolean.TRUE)) return false;
		if (Assertor.P.empty(name)) return false;

		if (scrollInsensitives == null) {
			synchronized (ShowableConnection.class) {
				if (scrollInsensitives == null) scrollInsensitives = new HashMap();
			}
		}

		Boolean b = null;
		b = scrollInsensitives.get(name);
		if (b == null) {
			try {
				b = conn.getMetaData().supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} catch (SQLException e) {
				b = Boolean.FALSE;
			}

			synchronized (scrollInsensitives) {
				scrollInsensitives.put(name, b);
			}
		}

		return b;
	}

	private static DatabaseGeneral databaseGeneral(Connection conn) {
		try {
			DatabaseGeneral general = new DatabaseGeneral();
			general.setName(conn.getCatalog());
			DatabaseMetaData metaData = conn.getMetaData();
			general.setType(metaData.getDriverName());
			general.setVersion(metaData.getDriverVersion());
			return general;
		} catch (SQLException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}

	protected final DatabaseGeneral general;

	/** 连接编号 */
	protected final String connectID;

	private volatile SQLShowableAction $action;

	/**
	 * 构造函数
	 * @param conn 数据库连接
	 */
	public ShowableConnection(Connection conn) {
		this(conn, databaseGeneral(conn));
	}

	public ShowableConnection(Connection conn, DatabaseGeneral general) {
		super(conn);
		this.general = general;
		this.connectID = generateID();
	}

	/**
	 * 构造函数
	 * @param conn 数据库连接
	 * @param name 数据库名称
	 * @param type 数据库类型
	 * @param version 数据库版本
	 * @deprecated {@link #ShowableConnection(Connection, DatabaseGeneral)}
	 */
	public ShowableConnection(Connection conn, String name, String type, String version) {
		this(conn, new DatabaseGeneral(name).setType(type).setVersion(version));
	}

	public void close() throws SQLException {
		long s = timestamp();
		try {
			super.close();
			onAction(ConnectionAction.CLOSE, toMillis(timestamp() - s));
		} catch (SQLException e) {
			onAction(ConnectionAction.CLOSE, e, toMillis(timestamp() - s));
			throw e;
		}
	}

	public void commit() throws SQLException {
		long s = timestamp();
		try {
			super.commit();
			onAction(ConnectionAction.COMMIT, toMillis(timestamp() - s));
		} catch (SQLException e) {
			onAction(ConnectionAction.COMMIT, e, toMillis(timestamp() - s));
			throw e;
		}
	}

	public Statement createStatement() throws SQLException {
		return supportsScrollInsensitive() ? super.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				: super.createStatement();
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return supportsScrollInsensitive()
				? super.prepareCall(SQLFormatter.format(sql), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				: super.prepareCall(SQLFormatter.format(sql));
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareCall(SQLFormatter.format(sql), resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareCall(SQLFormatter.format(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return supportsScrollInsensitive()
				? super.prepareStatement(SQLFormatter.format(sql), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
				: super.prepareStatement(SQLFormatter.format(sql));
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareStatement(SQLFormatter.format(sql), autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareStatement(SQLFormatter.format(sql), resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareStatement(SQLFormatter.format(sql), resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareStatement(SQLFormatter.format(sql), columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		return super.prepareStatement(sql, columnNames);
	}

	public void rollback() throws SQLException {
		long s = timestamp();
		try {
			super.rollback();
			onAction(ConnectionAction.ROLLBACK, toMillis(timestamp() - s));
		} catch (SQLException e) {
			onAction(ConnectionAction.ROLLBACK, e, toMillis(timestamp() - s));
			throw e;
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		long s = timestamp();
		try {
			super.rollback(savepoint);
			onAction(ConnectionAction.ROLLBACK, toMillis(timestamp() - s));
		} catch (SQLException e) {
			onAction(ConnectionAction.ROLLBACK, e, toMillis(timestamp() - s));
			throw e;
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		long s = timestamp();
		try {
			super.setAutoCommit(autoCommit);
			if (!autoCommit) onAction(ConnectionAction.TRANSACTION, toMillis(timestamp() - s));
		} catch (SQLException e) {
			if (!autoCommit) onAction(ConnectionAction.TRANSACTION, e, toMillis(timestamp() - s));
			throw e;
		}
	}

	protected SQLShowableAction buildAction() {
		return new SQLShowableAction();
	}

	protected CallableStatement callableStatement(CallableStatement cstat, String sql) {
		return new ShowablePreparedStatement(cstat, this, sql, getAction());
	}

	protected SQLShowableAction getAction() {
		return $action != null ? $action : ($action = buildAction());
	}

	/**
	 * 响应动作执行
	 * @param action 动作
	 * @param millis 耗时（毫秒）
	 */
	protected void onAction(ConnectionAction action, Number millis) {
		onAction(action, null, millis);
	}

	/**
	 * 响应动作执行
	 * @param action 动作
	 * @param t 异常内容
	 * @param millis 耗时（毫秒）
	 */
	protected void onAction(ConnectionAction action, Throwable t, Number millis) {
		NLSItem msg = null;
		if (t == null) {
			if (action == ConnectionAction.CLOSE) msg = JDBCMessages.m.connection_close_trace();
			else if (action == ConnectionAction.COMMIT) msg = JDBCMessages.m.transaction_commit_trace();
			else if (action == ConnectionAction.ROLLBACK) msg = JDBCMessages.m.transaction_rollback_trace();
			else if (action == ConnectionAction.TRANSACTION) msg = JDBCMessages.m.transaction_start_trace();
			else return;
			logger.trace(msg, general.getName(), unique(connectID));
		} else {
			if (action == ConnectionAction.CLOSE) msg = JDBCMessages.m.connection_close_error();
			else if (action == ConnectionAction.COMMIT) msg = JDBCMessages.m.transaction_commit_error();
			else if (action == ConnectionAction.ROLLBACK) msg = JDBCMessages.m.transaction_rollback_error();
			else if (action == ConnectionAction.TRANSACTION) msg = JDBCMessages.m.transaction_start_error();
			else return;
			logger.warn(msg, general.getName(), unique(connectID), t == null ? Stringure.empty : t.toString());
		}
	}

	/**
	 * 响应 SQL 执行
	 * @param content SQL 内容
	 * @param result 结果
	 * @param millis 耗时（毫秒）
	 * @param select 是否查询
	 * @param sql SQL 文本
	 * @param connectID 连接ID
	 */
	protected void onExecute(Object content, Object result, Number millis, Boolean select, String sql, String connectID) {
		// nothing
	}

	protected PreparedStatement preparedStatement(PreparedStatement pstat, String sql) {
		return new ShowablePreparedStatement(pstat, this, sql, getAction());
	}

	protected Statement statement(Statement stat) {
		return new ShowableStatement(stat, this, getAction());
	}

	/**
	 * 获取时间戳
	 * @return 时间戳
	 */
	protected long timestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * 转换时间差为毫秒
	 * @param time 时间差
	 * @return 毫秒值
	 */
	protected Number toMillis(long time) {
		return Long.valueOf(time);
	}

	/**
	 * 生成唯一键值
	 * @param connectID 连接ID
	 * @return 键值
	 */
	protected String unique(String connectID) {
		return connectID;
	}

	/**
	 * 是否支持游标移动
	 * @return 是、否
	 */
	boolean supportsScrollInsensitive() {
		return supportsScrollInsensitive(general.getName(), tar);
	}

	/**
	 * 批量执行结果
	 * @author demon 2022-10-08
	 */
	protected static class BatchResult extends Number {
		private static final long serialVersionUID = 6583267989864836424L;

		protected Collection<Integer> results;

		public BatchResult(int... rs) {
			this.results = Arrayard.asList(rs);
		}

		@Override
		public double doubleValue() {
			return longValue();
		}

		@Override
		public float floatValue() {
			return longValue();
		}

		@Override
		public int intValue() {
			return (int) longValue();
		}

		@Override
		public long longValue() {
			long total = 0;
			for (Integer r : results)
				total += r.longValue();
			return total;
		}

		public void toResults(Collection<Object> tar) {
			tar.addAll(results);
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			for (int r : results) {
				if (buffer.length() > 0) buffer.append(',');
				buffer.append(NumberFormat.getIntegerInstance().format(r));
			}
			return buffer.toString();
		}
	}

	/**
	 * 批量 SQL
	 * @author demon 2022-09-30
	 */
	protected static class BatchSQL {
		protected Collection<Object> children;

		public void toBatch(Collection<Object> tar) {
			tar.addAll(children);
		}

		public String toString() {
			StringBuilder buffer = new StringBuilder(1024);
			for (Object child : children) {
				if (buffer.length() > 0) buffer.append("\n\t");
				buffer.append(child).append(';');
			}
			return buffer.toString();
		}
	}

	/**
	 * 连接动作类型
	 * @author demon 2022-10-08
	 */
	protected static enum ConnectionAction {
		CLOSE,
		COMMIT,
		ROLLBACK,
		TRANSACTION;
	}

	/**
	 * 显 SQL 处理动作
	 * @author demon 2022-09-30
	 */
	protected class SQLShowableAction {
		public void onExecute(Object sql, SQLException se, Object result, long time, Boolean select) {
			// 在 SQL 语句后补上分号
			String s = String.valueOf(sql);
			if (!s.endsWith(";")) s += ';';

			Number millis = toMillis(time);
			String connectID = unique(ShowableConnection.this.connectID);
			ShowableConnection.this.onExecute(sql, se != null ? se : result, millis, select, s, connectID);
			if (se == null) {
				int r = Converter.F.convert(result, int.class);
				ShowableLimit limit = ShowableLimit.get();
				Object res = result instanceof BatchResult ? result.toString() : result;
				if (millis.intValue() < limit.time && r < limit.count) {
					logger.debug(JDBCMessages.m.sql_execute_success(), general.getName(), connectID, s, res, millis);
				} else {
					logger.info(JDBCMessages.m.sql_execute_success(), general.getName(), connectID, s, res, millis);
				}
			} else {
				logger.warn(JDBCMessages.m.sql_execute_error(), general.getName(), connectID, s, se.getErrorCode(), se.getSQLState(),
					se.getMessage(), millis);
			}
		}
	}

	static class ShowableLimit {

		static ShowableLimit li;

		public static ShowableLimit get() {
			return li != null ? li : (li = new ShowableLimit());
		}

		public final long time;
		public final int count;

		public ShowableLimit() {
			this.time = Bundles.getLong(Environments.getProperties(), 1000, "sql.info.limit.time", "sql.time.limit");
			this.count = Bundles.getInteger(Environments.getProperties(), 3000, "sql.info.limit.count", "sql.count.limit");
		}
	}
}

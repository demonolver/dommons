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
import java.util.HashMap;
import java.util.Map;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
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

	/** 数据库名称 */
	protected String name;
	/** 数据库类型 */
	protected final String type;
	/** 数据库版本 */
	protected final String version;

	/** 连接编号 */
	protected final String connectID;

	/**
	 * 构造函数
	 * @param conn 数据库连接
	 */
	public ShowableConnection(Connection conn) {
		super(conn);
		try {
			this.name = conn.getCatalog();
			DatabaseMetaData metaData = conn.getMetaData();
			this.type = metaData.getDriverName();
			this.version = metaData.getDriverVersion();
		} catch (SQLException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
		this.connectID = generateID();
	}

	/**
	 * 构造函数
	 * @param conn 数据库连接
	 * @param name 数据库名称
	 * @param type 数据库类型
	 * @param version 数据库版本
	 */
	public ShowableConnection(Connection conn, String name, String type, String version) {
		super(conn);
		this.name = name;
		this.type = type;
		this.version = version;
		this.connectID = generateID();
	}

	public void close() throws SQLException {
		try {
			super.close();
			trace(JDBCMessages.m.connection_close_trace());
		} catch (SQLException e) {
			warn(JDBCMessages.m.connection_close_error(), e);
			throw e;
		}
	}

	public void commit() throws SQLException {
		try {
			super.commit();
			trace(JDBCMessages.m.transaction_commit_trace());
		} catch (SQLException e) {
			warn(JDBCMessages.m.transaction_commit_error(), e);
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
		try {
			super.rollback();
			trace(JDBCMessages.m.transaction_rollback_trace());
		} catch (SQLException e) {
			warn(JDBCMessages.m.transaction_rollback_error(), e);
			throw e;
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			super.rollback(savepoint);
			trace(JDBCMessages.m.transaction_rollback_trace());
		} catch (SQLException e) {
			warn(JDBCMessages.m.transaction_rollback_error(), e);
			throw e;
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			super.setAutoCommit(autoCommit);
			if (!autoCommit) trace(JDBCMessages.m.transaction_start_trace());
		} catch (SQLException e) {
			if (!autoCommit) warn(JDBCMessages.m.transaction_start_error(), e);
			throw e;
		}
	}

	protected CallableStatement callableStatement(CallableStatement cstat, String sql) {
		return new ShowablePreparedStatement(cstat, this, sql);
	}

	protected PreparedStatement preparedStatement(PreparedStatement pstat, String sql) {
		return new ShowablePreparedStatement(pstat, this, sql);
	}

	protected Statement statement(Statement stat) {
		return new ShowableStatement(stat, this);
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
		return supportsScrollInsensitive(name, tar);
	}

	/**
	 * 记录跟踪信息
	 * @param msg 信息
	 */
	void trace(NLSItem msg) {
		logger.trace(msg, name, unique(connectID));
	}

	/**
	 * 记录警告信息
	 * @param msg 信息
	 * @param t 异常
	 */
	void warn(NLSItem msg, Throwable t) {
		logger.warn(msg, name, unique(connectID), t == null ? Stringure.empty : t.toString());
	}
}

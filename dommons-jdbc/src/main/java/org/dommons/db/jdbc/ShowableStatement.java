/*
 * @(#)ShowableStatement.java     2012-2-1
 */
package org.dommons.db.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.Silewarner;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.io.prop.Bundles;

/**
 * 显 SQL 数据库连接状态
 * @author Demon 2012-2-1
 */
class ShowableStatement extends EssentialCallableStatement<ShowableConnection> implements Statement {

	static final long time_limit = Bundles.getLong(Environments.getProperties(), "sql.time.limit", 1000);
	static final int count_limit = Bundles.getInteger(Environments.getProperties(), "sql.count.limit", 3000);

	/**
	 * 获取结果行数
	 * @param rs 查询结果
	 * @return 行数 <code>0</code>无结果或ResultSet为<code>null</code>, 未知表示无法获得行数
	 */
	protected static String count(ResultSet rs) {
		String count = Stringure.empty;
		try {
			// 如结果的游标类型不是单向游标，则移动游标获取结果行数
			if (rs != null && rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
				rs.last();
				count = NumberFormat.getIntegerInstance().format(rs.getRow());
				rs.beforeFirst();
			} else { // 否则返回未知，表示无法获得行数
				count = JDBCMessages.m.execute_result_unknow();
			}
		} catch (SQLException e) {
			// 一般不出错
			throw new RuntimeException(e);
		}
		return count;
	}

	/**
	 * 转换批量执行结果
	 * @param results 批量结果
	 * @return 结果串
	 */
	protected static String toResult(int... results) {
		if (results == null) return Stringure.empty;

		StringBuilder buffer = new StringBuilder();
		for (int r : results) {
			if (buffer.length() > 0) buffer.append(',');
			buffer.append(NumberFormat.getIntegerInstance().format(r));
		}
		return buffer.toString();
	}

	/**
	 * 创建批量执行信息
	 * @param batchs 批量执行内容
	 * @return 批量执行信息
	 */
	static Object createBatch(Collection<Object> batchs) {
		if (batchs == null) return null;
		int s = batchs.size();
		if (s == 0) {
			return null;
		} else if (s == 1) {
			return batchs.iterator().next();
		}

		BatchSQL bs = new BatchSQL();
		bs.children = new ArrayList(batchs.size());
		for (Iterator<Object> it = batchs.iterator(); it.hasNext(); it.remove()) {
			bs.children.add(it.next());
		}

		return bs;
	}

	private List<Object> batchs;

	protected Object[] last;

	/**
	 * 构造函数
	 * @param stat 连接状态
	 * @param conn 连接
	 */
	protected ShowableStatement(Statement stat, ShowableConnection conn) {
		super(stat, conn);
	}

	public void addBatch(String sql) throws SQLException {
		super.addBatch(sql);
		addBatchSQL(sql);
	}

	public void clearBatch() throws SQLException {
		super.clearBatch();
		if (batchs != null) batchs.clear();
	}

	public void close() throws SQLException {
		last(null);
		super.close();
		if (batchs != null) batchs.clear();
	}

	public boolean execute(String sql) throws SQLException {
		return execute(sql, new Execution<Boolean, String>() {
			public Boolean execute(String sql) throws SQLException {
				return $execute(sql);
			}
		});
	}

	public boolean execute(String sql, final int autoGeneratedKeys) throws SQLException {
		return execute(sql, new Execution<Boolean, String>() {
			public Boolean execute(String sql) throws SQLException {
				return $execute(sql, autoGeneratedKeys);
			}
		});
	}

	public boolean execute(String sql, final int[] columnIndexes) throws SQLException {
		return execute(sql, new Execution<Boolean, String>() {
			public Boolean execute(String sql) throws SQLException {
				return $execute(sql, columnIndexes);
			}
		});
	}

	public boolean execute(String sql, final String[] columnNames) throws SQLException {
		return execute(sql, new Execution<Boolean, String>() {
			public Boolean execute(String sql) throws SQLException {
				return $execute(sql, columnNames);
			}
		});
	}

	public int[] executeBatch() throws SQLException {
		last(null);

		int[] result = null;
		long time = System.currentTimeMillis();
		SQLException se = null;
		try {
			result = super.executeBatch();
			time = System.currentTimeMillis() - time;
			return result;
		} catch (SQLException e) {
			se = e;
			time = System.currentTimeMillis() - time;
			throw transform(e, batchs == null ? null : batchs.toArray());
		} finally {
			logBatchs(se, toResult(result), time);
		}
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		return execute(sql, new Execution<ResultSet, String>() {
			public ResultSet execute(String sql) throws SQLException {
				return $executeQuery(sql);
			}
		});
	}

	public int executeUpdate(String sql) throws SQLException {
		return execute(sql, new Execution<Integer, String>() {
			public Integer execute(String sql) throws SQLException {
				return $executeUpdate(sql);
			}
		});
	}

	public int executeUpdate(String sql, final int autoGeneratedKeys) throws SQLException {
		return execute(sql, new Execution<Integer, String>() {
			public Integer execute(String sql) throws SQLException {
				return $executeUpdate(sql, autoGeneratedKeys);
			}
		});
	}

	public int executeUpdate(String sql, final int[] columnIndexes) throws SQLException {
		return execute(sql, new Execution<Integer, String>() {
			public Integer execute(String sql) throws SQLException {
				return $executeUpdate(sql, columnIndexes);
			}
		});
	}

	public int executeUpdate(String sql, final String[] columnNames) throws SQLException {
		return execute(sql, new Execution<Integer, String>() {
			public Integer execute(String sql) throws SQLException {
				return $executeUpdate(sql, columnNames);
			}
		});
	}

	public ResultSet getResultSet() throws SQLException {
		return last(super.getResultSet());
	}

	public int getUpdateCount() throws SQLException {
		return last(super.getUpdateCount());
	}

	/**
	 * 添加批处理 SQL 语句
	 * @param sql SQL 语句
	 */
	protected void addBatchSQL(Object sql) {
		if (batchs == null) batchs = new ArrayList();
		batchs.add(sql);
	}

	/**
	 * 执行 SQL
	 * @param sql SQL
	 * @param execution 执行项
	 * @return 执行结果
	 * @throws SQLException
	 */
	protected <R> R execute(String sql, Execution<R, String> execution) throws SQLException {
		Assertor.F.notEmpty(sql, JDBCMessages.m.execute_sql_empty());
		last(null);

		sql = SQLFormatter.format(sql);
		R r = null;
		SQLException se = null;
		long time = System.currentTimeMillis();
		try {
			r = execution.execute(sql);
			time = System.currentTimeMillis() - time;
			return r;
		} catch (SQLException e) {
			se = e;
			time = System.currentTimeMillis() - time;
			throw transform(e, sql);
		} finally {
			if (se == null && r instanceof Boolean) {
				registerLast(sql, (Boolean) r, time);
			} else if (se == null && r instanceof ResultSet) {
				log(sql, se, count((ResultSet) r), time);
			} else {
				log(sql, se, r, time);
			}
		}
	}

	/**
	 * 记录上一未记录 SQL 语句
	 * @param result 新结果
	 * @return 结果值
	 */
	protected <R> R last(R result) {
		if (last != null) {
			ResultSet rs = null;
			try {
				Object r;
				try {
					r = result != null ? result : ((Boolean) last[1] ? rs = tar.getResultSet() : super.getUpdateCount());
				} catch (SQLException e) {
					r = last[1];
				}
				log(last[0], null, r instanceof ResultSet ? count((ResultSet) r) : r, Converter.P.convert(last[2], long.class));
				result = (R) r;
			} finally {
				try {
					if (rs != null) rs.close();
				} catch (SQLException e) {
					Silewarner.warn(ShowableConnection.class, "connection close", e);
				}
			}
			last = null;
		}
		return result;
	}

	/**
	 * 记录 SQL 执行信息
	 * @param sql SQL 语句
	 * @param se 执行异常
	 * @param result 执行结果
	 * @param time 运行时长
	 */
	protected void log(Object sql, SQLException se, Object result, long time) {
		// 在 SQL 语句后补上分号
		String s = String.valueOf(sql);
		if (!s.endsWith(";")) s += ';';

		String connectID = conn.unique(conn.connectID);
		if (se == null) {
			int r = Converter.F.convert(result, int.class);
			if (time < time_limit && r < count_limit) {
				ShowableConnection.logger.debug(JDBCMessages.m.sql_execute_success(), conn.name, connectID, s, result, time);
			} else {
				ShowableConnection.logger.info(JDBCMessages.m.sql_execute_success(), conn.name, connectID, s, result, time);
			}
		} else {
			ShowableConnection.logger.warn(JDBCMessages.m.sql_execute_error(), conn.name, connectID, s, se.getErrorCode(), se.getSQLState(),
				se.getMessage(), time);
		}
	}

	/**
	 * 记录批处理 SQL 执行信息
	 * @param se 异常信息
	 * @param result 执行结果
	 * @param time 运行时长
	 */
	protected void logBatchs(SQLException se, Object result, long time) {
		if (batchs != null) log(createBatch(batchs), se, result, time);
	}

	/**
	 * 注册结果未知 SQL
	 * @param sql SQL 语句
	 * @param result 当前结果
	 * @param time 运行时长
	 */
	protected void registerLast(Object sql, boolean result, long time) {
		last = new Object[] { sql, result, time };
	}

	/**
	 * 转换 SQL 异常
	 * @param se SQL 异常
	 * @param sql SQL 语句
	 * @return 转换后异常
	 */
	protected SQLException transform(SQLException se, Object... sql) {
		if (se != null && Assertor.P.notEmpty(sql)) {
			StringBuilder buf = new StringBuilder(se.getLocalizedMessage()).append(" SQL: [");
			for (int i = 0; i < sql.length; i++) {
				if (i > 0) buf.append(' ');
				String s = String.valueOf(sql[i]);
				buf.append(s);
				if (!s.endsWith(";")) buf.append(';');
			}
			buf.append(']');
			SQLException sew = new SQLException(buf.toString(), se.getSQLState(), se.getErrorCode());
			sew.setStackTrace(se.getStackTrace());
			sew.initCause(se.getCause());
			sew.setNextException(se.getNextException());
			return sew;
		} else {
			return se;
		}
	}

	private boolean $execute(String sql) throws SQLException {
		return super.execute(sql);
	}

	private boolean $execute(String sql, int autoGeneratedKeys) throws SQLException {
		return super.execute(sql, autoGeneratedKeys);
	}

	private boolean $execute(String sql, int[] columnIndexes) throws SQLException {
		return super.execute(sql, columnIndexes);
	}

	private boolean $execute(String sql, String[] columnNames) throws SQLException {
		return super.execute(sql, columnNames);
	}

	private ResultSet $executeQuery(String sql) throws SQLException {
		return super.executeQuery(sql);
	}

	private int $executeUpdate(String sql) throws SQLException {
		return super.executeUpdate(sql);
	}

	private int $executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return super.executeUpdate(sql, autoGeneratedKeys);
	}

	private int $executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return super.executeUpdate(sql, columnIndexes);
	}

	private int $executeUpdate(String sql, String[] columnNames) throws SQLException {
		return super.executeUpdate(sql, columnNames);
	}

	/**
	 * 可执行体
	 * @author Demon 2012-2-2
	 */
	protected interface Execution<R, P> {

		/**
		 * 执行
		 * @param sql 语行
		 * @return 结果
		 * @throws SQLException
		 */
		public R execute(P sql) throws SQLException;
	}

	/**
	 * 批量 SQL
	 * @author Demon 2012-2-10
	 */
	static class BatchSQL {
		private Collection<Object> children;

		public String toString() {
			StringBuilder buffer = new StringBuilder(1024);
			for (Object child : children) {
				if (buffer.length() > 0) buffer.append("\n\t");
				buffer.append(child).append(';');
			}
			return buffer.toString();
		}
	}
}

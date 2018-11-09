/*
 * @(#)ShowableMessages.java     2012-2-1
 */
package org.dommons.db.jdbc;

import org.dommons.io.nls.NLS;
import org.dommons.io.nls.NLSFactory;
import org.dommons.io.nls.NLSItem;

/**
 * JDBC 包装信息集
 * @author Demon 2012-2-1
 */
interface JDBCMessages extends NLS {

	static final JDBCMessages m = NLSFactory.create(JDBCMessages.class.getPackage(), "jdbc.messages", JDBCMessages.class);

	public NLSItem connection_close_trace();
	public NLSItem connection_close_error();

	public NLSItem transaction_start_trace();
	public NLSItem transaction_start_error();

	public NLSItem transaction_commit_trace();
	public NLSItem transaction_commit_error();

	public NLSItem transaction_rollback_trace();
	public NLSItem transaction_rollback_error();

	public String execute_sql_empty();
	public String execute_result_unknow();

	public NLSItem sql_execute_success();
	public NLSItem sql_execute_error();
}

/*
 * @(#)EssentialConnection.java     2012-1-17
 */
package org.dommons.db.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.dommons.core.Assertor;
import org.dommons.core.convert.Converter;

/**
 * 基本数据库连接包装实现
 * @author Demon 2012-1-17
 */
public class EssentialConnection extends ClosableContainer implements Connection {

	/** 目标连接 */
	protected final Connection tar;

	/**
	 * 构造函数
	 * @param conn 目标连接
	 */
	public EssentialConnection(Connection conn) {
		Assertor.F.notNull(conn);
		this.tar = conn;
	}

	public void abort(Executor executor) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "abort", Executor.class), executor);
	}

	public void clearWarnings() throws SQLException {
		tar.clearWarnings();
	}

	public void close() throws SQLException {
		if (!isClosed()) {
			closeChildren();
			tar.close();
		}
	}

	public void commit() throws SQLException {
		if (!isClosed() && !tar.getAutoCommit()) tar.commit();
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar,
			VersionAdapter.find(tar.getClass(), "createArrayOf", String.class, Object[].class), typeName, elements), Array.class);
	}

	public Blob createBlob() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "createBlob")), Blob.class);
	}

	public Clob createClob() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "createClob")), Clob.class);
	}

	public java.sql.NClob createNClob() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "createNClob")), java.sql.NClob.class);
	}

	public java.sql.SQLXML createSQLXML() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "createSQLXML")), java.sql.SQLXML.class);
	}

	public Statement createStatement() throws SQLException {
		return add(statement(tar.createStatement()));
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return add(statement(tar.createStatement(resultSetType, resultSetConcurrency)));
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return add(statement(tar.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)));
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar,
			VersionAdapter.find(tar.getClass(), "createStruct", String.class, Object[].class), typeName, attributes), Struct.class);
	}

	public boolean getAutoCommit() throws SQLException {
		return tar.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		return tar.getCatalog();
	}

	public Properties getClientInfo() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getClientInfo")), Properties.class);
	}

	public String getClientInfo(String name) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getClientInfo", String.class), name),
			String.class);
	}

	public int getHoldability() throws SQLException {
		return tar.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return wrap(tar.getMetaData());
	}

	public int getNetworkTimeout() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getNetworkTimeout")), int.class);
	}

	public String getSchema() throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "")), String.class);
	}

	public int getTransactionIsolation() throws SQLException {
		return tar.getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return tar.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		return tar.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		return tar.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return tar.isReadOnly();
	}

	public boolean isValid(int timeout) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "isValid", int.class), timeout),
			boolean.class);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "isWrapperFor", Class.class), iface),
			boolean.class);
	}

	public String nativeSQL(String sql) throws SQLException {
		return tar.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return add(callableStatement(tar.prepareCall(sql), sql));
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return add(callableStatement(tar.prepareCall(sql, resultSetType, resultSetConcurrency), sql));
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return add(callableStatement(tar.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql));
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql), sql));
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql, autoGeneratedKeys), sql));
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql, resultSetType, resultSetConcurrency), sql));
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql));
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql, columnIndexes), sql));
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return add(preparedStatement(tar.prepareStatement(sql, columnNames), sql));
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		tar.releaseSavepoint(savepoint);
	}

	public void rollback() throws SQLException {
		if (!isClosed() && !tar.getAutoCommit()) tar.rollback();
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		if (!isClosed() && !tar.getAutoCommit()) tar.rollback(savepoint);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		tar.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		tar.setCatalog(catalog);
	}

	public void setClientInfo(Properties properties) throws java.sql.SQLClientInfoException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClientInfo", Properties.class), properties);
	}

	public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setClientInfo", String.class, String.class), name, value);
	}

	public void setHoldability(int holdability) throws SQLException {
		tar.setHoldability(holdability);
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setNetworkTimeout", Executor.class, int.class), executor,
			milliseconds);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		tar.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		return tar.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return tar.setSavepoint(name);
	}

	public void setSchema(String schema) throws SQLException {
		VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "setSchema", String.class), schema);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		tar.setTransactionIsolation(level);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		tar.setTypeMap(map);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "unwrap", Class.class), iface), iface);
	}

	/**
	 * 包装连接状态
	 * @param cstat 连接状态
	 * @param sql SQL 语句
	 * @return 新连接状态
	 */
	protected CallableStatement callableStatement(CallableStatement cstat, String sql) {
		return new EssentialCallableStatement(cstat, this);
	}

	/**
	 * 包装连接状态
	 * @param pstat 连接状态
	 * @param sql SQL 语句
	 * @return 新连接状态
	 */
	protected PreparedStatement preparedStatement(PreparedStatement pstat, String sql) {
		return new EssentialPreparedStatement(pstat, this);
	}

	/**
	 * 包装连接状态
	 * @param stat 连接状态
	 * @return 新连接状态
	 */
	protected Statement statement(Statement stat) {
		return new EssentialStatement(stat, this);
	}

	/**
	 * 包装数据库信息数据
	 * @param metaData 目标数据
	 * @return 新数据项
	 */
	protected DatabaseMetaData wrap(DatabaseMetaData metaData) {
		return new EssentialMetaData(metaData);
	}

	/**
	 * 基本数据库信息数据包装实现
	 * @author Demon 2012-2-1
	 */
	protected class EssentialMetaData implements DatabaseMetaData {

		/** 目标数据项 */
		protected final DatabaseMetaData tar;

		/**
		 * 构造函数
		 * @param metaData 目标数据库信息数据
		 */
		protected EssentialMetaData(DatabaseMetaData metaData) {
			this.tar = metaData;
		}

		public boolean allProceduresAreCallable() throws SQLException {
			return tar.allProceduresAreCallable();
		}

		public boolean allTablesAreSelectable() throws SQLException {
			return tar.allTablesAreSelectable();
		}

		public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
			return Converter.P.convert(
				VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "autoCommitFailureClosesAllResultSets")), boolean.class);
		}

		public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
			return tar.dataDefinitionCausesTransactionCommit();
		}

		public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
			return tar.dataDefinitionIgnoredInTransactions();
		}

		public boolean deletesAreDetected(int type) throws SQLException {
			return tar.deletesAreDetected(type);
		}

		public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
			return tar.doesMaxRowSizeIncludeBlobs();
		}

		public boolean generatedKeyAlwaysReturned() throws SQLException {
			return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "generatedKeyAlwaysReturned")),
				boolean.class);
		}

		public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)
				throws SQLException {
			return wrap(tar.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern));
		}

		public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
			return wrap(tar.getBestRowIdentifier(catalog, schema, table, scope, nullable));
		}

		public ResultSet getCatalogs() throws SQLException {
			return wrap(tar.getCatalogs());
		}

		public String getCatalogSeparator() throws SQLException {
			return tar.getCatalogSeparator();
		}

		public String getCatalogTerm() throws SQLException {
			return tar.getCatalogTerm();
		}

		public ResultSet getClientInfoProperties() throws SQLException {
			return wrap(Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getClientInfoProperties")),
				ResultSet.class));
		}

		public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
			return wrap(tar.getColumnPrivileges(catalog, schema, table, columnNamePattern));
		}

		public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
				throws SQLException {
			return wrap(tar.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
		}

		public Connection getConnection() throws SQLException {
			return EssentialConnection.this;
		}

		public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog,
				String foreignSchema, String foreignTable) throws SQLException {
			return wrap(tar.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable));
		}

		public int getDatabaseMajorVersion() throws SQLException {
			return tar.getDatabaseMajorVersion();
		}

		public int getDatabaseMinorVersion() throws SQLException {
			return tar.getDatabaseMinorVersion();
		}

		public String getDatabaseProductName() throws SQLException {
			return tar.getDatabaseProductName();
		}

		public String getDatabaseProductVersion() throws SQLException {
			return tar.getDatabaseProductVersion();
		}

		public int getDefaultTransactionIsolation() throws SQLException {
			return tar.getDefaultTransactionIsolation();
		}

		public int getDriverMajorVersion() {
			return tar.getDriverMajorVersion();
		}

		public int getDriverMinorVersion() {
			return tar.getDriverMinorVersion();
		}

		public String getDriverName() throws SQLException {
			return tar.getDriverName();
		}

		public String getDriverVersion() throws SQLException {
			return tar.getDriverVersion();
		}

		public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
			return wrap(tar.getExportedKeys(catalog, schema, table));
		}

		public String getExtraNameCharacters() throws SQLException {
			return tar.getExtraNameCharacters();
		}

		public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
				throws SQLException {
			return wrap(Converter.P.convert(VersionAdapter.invoke(tar,
				VersionAdapter.find(tar.getClass(), "getFunctionColumns", String.class, String.class, String.class, String.class), catalog,
				schemaPattern, functionNamePattern, columnNamePattern), ResultSet.class));
		}

		public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
			return wrap(Converter.P.convert(VersionAdapter.invoke(tar,
				VersionAdapter.find(tar.getClass(), "getFunctions", String.class, String.class, String.class), catalog, schemaPattern,
				functionNamePattern), ResultSet.class));
		}

		public String getIdentifierQuoteString() throws SQLException {
			return tar.getIdentifierQuoteString();
		}

		public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
			return wrap(tar.getImportedKeys(catalog, schema, table));
		}

		public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
			return wrap(tar.getIndexInfo(catalog, schema, table, unique, approximate));
		}

		public int getJDBCMajorVersion() throws SQLException {
			return tar.getJDBCMajorVersion();
		}

		public int getJDBCMinorVersion() throws SQLException {
			return tar.getJDBCMinorVersion();
		}

		public int getMaxBinaryLiteralLength() throws SQLException {
			return tar.getMaxBinaryLiteralLength();
		}

		public int getMaxCatalogNameLength() throws SQLException {
			return tar.getMaxCatalogNameLength();
		}

		public int getMaxCharLiteralLength() throws SQLException {
			return tar.getMaxCharLiteralLength();
		}

		public int getMaxColumnNameLength() throws SQLException {
			return tar.getMaxColumnNameLength();
		}

		public int getMaxColumnsInGroupBy() throws SQLException {
			return tar.getMaxColumnsInGroupBy();
		}

		public int getMaxColumnsInIndex() throws SQLException {
			return tar.getMaxColumnsInIndex();
		}

		public int getMaxColumnsInOrderBy() throws SQLException {
			return tar.getMaxColumnsInOrderBy();
		}

		public int getMaxColumnsInSelect() throws SQLException {
			return tar.getMaxColumnsInSelect();
		}

		public int getMaxColumnsInTable() throws SQLException {
			return tar.getMaxColumnsInTable();
		}

		public int getMaxConnections() throws SQLException {
			return tar.getMaxConnections();
		}

		public int getMaxCursorNameLength() throws SQLException {
			return tar.getMaxCursorNameLength();
		}

		public int getMaxIndexLength() throws SQLException {
			return tar.getMaxIndexLength();
		}

		public int getMaxProcedureNameLength() throws SQLException {
			return tar.getMaxProcedureNameLength();
		}

		public int getMaxRowSize() throws SQLException {
			return tar.getMaxRowSize();
		}

		public int getMaxSchemaNameLength() throws SQLException {
			return tar.getMaxSchemaNameLength();
		}

		public int getMaxStatementLength() throws SQLException {
			return tar.getMaxStatementLength();
		}

		public int getMaxStatements() throws SQLException {
			return tar.getMaxStatements();
		}

		public int getMaxTableNameLength() throws SQLException {
			return tar.getMaxTableNameLength();
		}

		public int getMaxTablesInSelect() throws SQLException {
			return tar.getMaxTablesInSelect();
		}

		public int getMaxUserNameLength() throws SQLException {
			return tar.getMaxUserNameLength();
		}

		public String getNumericFunctions() throws SQLException {
			return tar.getNumericFunctions();
		}

		public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
			return wrap(tar.getPrimaryKeys(catalog, schema, table));
		}

		public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
				throws SQLException {
			return wrap(tar.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
		}

		public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
			return wrap(tar.getProcedures(catalog, schemaPattern, procedureNamePattern));
		}

		public String getProcedureTerm() throws SQLException {
			return tar.getProcedureTerm();
		}

		public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
				throws SQLException {
			return wrap(Converter.P.convert(VersionAdapter.invoke(tar,
				VersionAdapter.find(tar.getClass(), "getPseudoColumns", String.class, String.class, String.class, String.class), catalog,
				schemaPattern, tableNamePattern, columnNamePattern), ResultSet.class));
		}

		public int getResultSetHoldability() throws SQLException {
			return tar.getResultSetHoldability();
		}

		public java.sql.RowIdLifetime getRowIdLifetime() throws SQLException {
			return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "getRowIdLifetime")),
				java.sql.RowIdLifetime.class);
		}

		public ResultSet getSchemas() throws SQLException {
			return wrap(tar.getSchemas());
		}

		public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
			return wrap(Converter.P.convert(VersionAdapter.invoke(tar,
				VersionAdapter.find(tar.getClass(), "getSchemas", String.class, String.class), catalog, schemaPattern), ResultSet.class));
		}

		public String getSchemaTerm() throws SQLException {
			return tar.getSchemaTerm();
		}

		public String getSearchStringEscape() throws SQLException {
			return tar.getSearchStringEscape();
		}

		public String getSQLKeywords() throws SQLException {
			return tar.getSQLKeywords();
		}

		public int getSQLStateType() throws SQLException {
			return tar.getSQLStateType();
		}

		public String getStringFunctions() throws SQLException {
			return tar.getStringFunctions();
		}

		public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
			return wrap(tar.getSuperTables(catalog, schemaPattern, tableNamePattern));
		}

		public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
			return wrap(tar.getSuperTypes(catalog, schemaPattern, typeNamePattern));
		}

		public String getSystemFunctions() throws SQLException {
			return tar.getSystemFunctions();
		}

		public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
			return wrap(tar.getTablePrivileges(catalog, schemaPattern, tableNamePattern));
		}

		public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
			return wrap(tar.getTables(catalog, schemaPattern, tableNamePattern, types));
		}

		public ResultSet getTableTypes() throws SQLException {
			return wrap(tar.getTableTypes());
		}

		public String getTimeDateFunctions() throws SQLException {
			return tar.getTimeDateFunctions();
		}

		public ResultSet getTypeInfo() throws SQLException {
			return wrap(tar.getTypeInfo());
		}

		public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
			return wrap(tar.getUDTs(catalog, schemaPattern, typeNamePattern, types));
		}

		public String getURL() throws SQLException {
			return tar.getURL();
		}

		public String getUserName() throws SQLException {
			return tar.getUserName();
		}

		public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
			return wrap(tar.getVersionColumns(catalog, schema, table));
		}

		public boolean insertsAreDetected(int type) throws SQLException {
			return tar.insertsAreDetected(type);
		}

		public boolean isCatalogAtStart() throws SQLException {
			return tar.isCatalogAtStart();
		}

		public boolean isReadOnly() throws SQLException {
			return tar.isReadOnly();
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return Converter.P.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "isWrapperFor", Class.class), iface),
				boolean.class);
		}

		public boolean locatorsUpdateCopy() throws SQLException {
			return tar.locatorsUpdateCopy();
		}

		public boolean nullPlusNonNullIsNull() throws SQLException {
			return tar.nullPlusNonNullIsNull();
		}

		public boolean nullsAreSortedAtEnd() throws SQLException {
			return tar.nullsAreSortedAtEnd();
		}

		public boolean nullsAreSortedAtStart() throws SQLException {
			return tar.nullsAreSortedAtStart();
		}

		public boolean nullsAreSortedHigh() throws SQLException {
			return tar.nullsAreSortedHigh();
		}

		public boolean nullsAreSortedLow() throws SQLException {
			return tar.nullsAreSortedLow();
		}

		public boolean othersDeletesAreVisible(int type) throws SQLException {
			return tar.othersDeletesAreVisible(type);
		}

		public boolean othersInsertsAreVisible(int type) throws SQLException {
			return tar.othersInsertsAreVisible(type);
		}

		public boolean othersUpdatesAreVisible(int type) throws SQLException {
			return tar.othersUpdatesAreVisible(type);
		}

		public boolean ownDeletesAreVisible(int type) throws SQLException {
			return tar.ownDeletesAreVisible(type);
		}

		public boolean ownInsertsAreVisible(int type) throws SQLException {
			return tar.ownInsertsAreVisible(type);
		}

		public boolean ownUpdatesAreVisible(int type) throws SQLException {
			return tar.ownUpdatesAreVisible(type);
		}

		public boolean storesLowerCaseIdentifiers() throws SQLException {
			return tar.storesLowerCaseIdentifiers();
		}

		public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
			return tar.storesLowerCaseQuotedIdentifiers();
		}

		public boolean storesMixedCaseIdentifiers() throws SQLException {
			return tar.storesMixedCaseIdentifiers();
		}

		public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
			return tar.storesMixedCaseQuotedIdentifiers();
		}

		public boolean storesUpperCaseIdentifiers() throws SQLException {
			return tar.storesUpperCaseIdentifiers();
		}

		public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
			return tar.storesUpperCaseQuotedIdentifiers();
		}

		public boolean supportsAlterTableWithAddColumn() throws SQLException {
			return tar.supportsAlterTableWithAddColumn();
		}

		public boolean supportsAlterTableWithDropColumn() throws SQLException {
			return tar.supportsAlterTableWithDropColumn();
		}

		public boolean supportsANSI92EntryLevelSQL() throws SQLException {
			return tar.supportsANSI92EntryLevelSQL();
		}

		public boolean supportsANSI92FullSQL() throws SQLException {
			return tar.supportsANSI92FullSQL();
		}

		public boolean supportsANSI92IntermediateSQL() throws SQLException {
			return tar.supportsANSI92IntermediateSQL();
		}

		public boolean supportsBatchUpdates() throws SQLException {
			return tar.supportsBatchUpdates();
		}

		public boolean supportsCatalogsInDataManipulation() throws SQLException {
			return tar.supportsCatalogsInDataManipulation();
		}

		public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
			return tar.supportsCatalogsInIndexDefinitions();
		}

		public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
			return tar.supportsCatalogsInPrivilegeDefinitions();
		}

		public boolean supportsCatalogsInProcedureCalls() throws SQLException {
			return tar.supportsCatalogsInProcedureCalls();
		}

		public boolean supportsCatalogsInTableDefinitions() throws SQLException {
			return tar.supportsCatalogsInTableDefinitions();
		}

		public boolean supportsColumnAliasing() throws SQLException {
			return tar.supportsColumnAliasing();
		}

		public boolean supportsConvert() throws SQLException {
			return tar.supportsConvert();
		}

		public boolean supportsConvert(int fromType, int toType) throws SQLException {
			return tar.supportsConvert(fromType, toType);
		}

		public boolean supportsCoreSQLGrammar() throws SQLException {
			return tar.supportsCoreSQLGrammar();
		}

		public boolean supportsCorrelatedSubqueries() throws SQLException {
			return tar.supportsCorrelatedSubqueries();
		}

		public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
			return tar.supportsDataDefinitionAndDataManipulationTransactions();
		}

		public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
			return tar.supportsDataManipulationTransactionsOnly();
		}

		public boolean supportsDifferentTableCorrelationNames() throws SQLException {
			return tar.supportsDifferentTableCorrelationNames();
		}

		public boolean supportsExpressionsInOrderBy() throws SQLException {
			return tar.supportsExpressionsInOrderBy();
		}

		public boolean supportsExtendedSQLGrammar() throws SQLException {
			return tar.supportsExtendedSQLGrammar();
		}

		public boolean supportsFullOuterJoins() throws SQLException {
			return tar.supportsFullOuterJoins();
		}

		public boolean supportsGetGeneratedKeys() throws SQLException {
			return tar.supportsGetGeneratedKeys();
		}

		public boolean supportsGroupBy() throws SQLException {
			return tar.supportsGroupBy();
		}

		public boolean supportsGroupByBeyondSelect() throws SQLException {
			return tar.supportsGroupByBeyondSelect();
		}

		public boolean supportsGroupByUnrelated() throws SQLException {
			return tar.supportsGroupByUnrelated();
		}

		public boolean supportsIntegrityEnhancementFacility() throws SQLException {
			return tar.supportsIntegrityEnhancementFacility();
		}

		public boolean supportsLikeEscapeClause() throws SQLException {
			return tar.supportsLikeEscapeClause();
		}

		public boolean supportsLimitedOuterJoins() throws SQLException {
			return tar.supportsLimitedOuterJoins();
		}

		public boolean supportsMinimumSQLGrammar() throws SQLException {
			return tar.supportsMinimumSQLGrammar();
		}

		public boolean supportsMixedCaseIdentifiers() throws SQLException {
			return tar.supportsMixedCaseIdentifiers();
		}

		public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
			return tar.supportsMixedCaseQuotedIdentifiers();
		}

		public boolean supportsMultipleOpenResults() throws SQLException {
			return tar.supportsMultipleOpenResults();
		}

		public boolean supportsMultipleResultSets() throws SQLException {
			return tar.supportsMultipleResultSets();
		}

		public boolean supportsMultipleTransactions() throws SQLException {
			return tar.supportsMultipleTransactions();
		}

		public boolean supportsNamedParameters() throws SQLException {
			return tar.supportsNamedParameters();
		}

		public boolean supportsNonNullableColumns() throws SQLException {
			return tar.supportsNonNullableColumns();
		}

		public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
			return tar.supportsOpenCursorsAcrossCommit();
		}

		public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
			return tar.supportsOpenCursorsAcrossRollback();
		}

		public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
			return tar.supportsOpenStatementsAcrossCommit();
		}

		public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
			return tar.supportsOpenStatementsAcrossRollback();
		}

		public boolean supportsOrderByUnrelated() throws SQLException {
			return tar.supportsOrderByUnrelated();
		}

		public boolean supportsOuterJoins() throws SQLException {
			return tar.supportsOuterJoins();
		}

		public boolean supportsPositionedDelete() throws SQLException {
			return tar.supportsPositionedDelete();
		}

		public boolean supportsPositionedUpdate() throws SQLException {
			return tar.supportsPositionedUpdate();
		}

		public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
			return tar.supportsResultSetConcurrency(type, concurrency);
		}

		public boolean supportsResultSetHoldability(int holdability) throws SQLException {
			return tar.supportsResultSetHoldability(holdability);
		}

		public boolean supportsResultSetType(int type) throws SQLException {
			return tar.supportsResultSetType(type);
		}

		public boolean supportsSavepoints() throws SQLException {
			return tar.supportsSavepoints();
		}

		public boolean supportsSchemasInDataManipulation() throws SQLException {
			return tar.supportsSchemasInDataManipulation();
		}

		public boolean supportsSchemasInIndexDefinitions() throws SQLException {
			return tar.supportsSchemasInIndexDefinitions();
		}

		public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
			return tar.supportsSchemasInPrivilegeDefinitions();
		}

		public boolean supportsSchemasInProcedureCalls() throws SQLException {
			return tar.supportsSchemasInProcedureCalls();
		}

		public boolean supportsSchemasInTableDefinitions() throws SQLException {
			return tar.supportsSchemasInTableDefinitions();
		}

		public boolean supportsSelectForUpdate() throws SQLException {
			return tar.supportsSelectForUpdate();
		}

		public boolean supportsStatementPooling() throws SQLException {
			return tar.supportsStatementPooling();
		}

		public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
			return Converter.P.convert(
				VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "supportsStoredFunctionsUsingCallSyntax")), boolean.class);
		}

		public boolean supportsStoredProcedures() throws SQLException {
			return tar.supportsStoredProcedures();
		}

		public boolean supportsSubqueriesInComparisons() throws SQLException {
			return tar.supportsSubqueriesInComparisons();
		}

		public boolean supportsSubqueriesInExists() throws SQLException {
			return tar.supportsSubqueriesInExists();
		}

		public boolean supportsSubqueriesInIns() throws SQLException {
			return tar.supportsSubqueriesInIns();
		}

		public boolean supportsSubqueriesInQuantifieds() throws SQLException {
			return tar.supportsSubqueriesInQuantifieds();
		}

		public boolean supportsTableCorrelationNames() throws SQLException {
			return tar.supportsTableCorrelationNames();
		}

		public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
			return tar.supportsTransactionIsolationLevel(level);
		}

		public boolean supportsTransactions() throws SQLException {
			return tar.supportsTransactions();
		}

		public boolean supportsUnion() throws SQLException {
			return tar.supportsUnion();
		}

		public boolean supportsUnionAll() throws SQLException {
			return tar.supportsUnionAll();
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			return Converter.P
					.convert(VersionAdapter.invoke(tar, VersionAdapter.find(tar.getClass(), "unwrap", Class.class), iface), iface);
		}

		public boolean updatesAreDetected(int type) throws SQLException {
			return tar.updatesAreDetected(type);
		}

		public boolean usesLocalFilePerTable() throws SQLException {
			return tar.usesLocalFilePerTable();
		}

		public boolean usesLocalFiles() throws SQLException {
			return tar.usesLocalFiles();
		}

		/**
		 * 包装结果集
		 * @param rs 结果集
		 * @return
		 * @throws SQLException
		 */
		protected ResultSet wrap(ResultSet rs) throws SQLException {
			return new EssentialResultSet(rs, new EssentialStatement(rs.getStatement(), EssentialConnection.this));
		}

	}
}
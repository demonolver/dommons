/*
 * @(#)JDBCTester.java     2012-2-1
 */
package org.dommons.db;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeSet;

import org.dommons.db.jdbc.ShowableConnection;

/**
 * @author Demon 2012-2-1
 */
public class JDBCTester {

	public static void main(String[] args) {
		try {
			// testSQL();
			testString();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	protected static void testSQL() throws SQLException {
		Driver driver = new com.mysql.jdbc.Driver();

		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");

		Connection conn = new ShowableConnection(driver.connect("jdbc:mysql://127.0.0.1:3306/scm", prop));
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			pstat = conn.prepareStatement("update sys_company set group_alias = ? where com_uid = ?;");
			for (int i = 0; i < 5; i++) {
				pstat.setString(1, "demon080126");
				pstat.setString(2, String.valueOf(i));
				pstat.addBatch();
			}
			// pstat.execute();
			// rs = pstat.getResultSet();
			// int count = 0;
			// while (rs.next()) {
			// count++;
			// }
			// System.out.println(count);
			pstat.executeBatch();
		} finally {
			if (rs != null) rs.close();
			if (pstat != null) pstat.close();
			if (conn != null) conn.close();
		}
	}

	protected static void testString() throws Exception {
		Driver driver = new com.mysql.jdbc.Driver();

		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");

		Collection<Integer> es = new TreeSet();
		Connection conn = new ShowableConnection(driver.connect("jdbc:mysql://127.0.0.1:3306/scm", prop));
		try {
			for (int i = 10000; i < 200000; i++) {
				String sql = "insert into st set num=" + i + ", st='" + (char) i + "'";
				if (!execute(conn, sql)) es.add(i);
			}
		} finally {
			if (conn != null) conn.close();
		}
		if (es.isEmpty()) return;
		FileWriter w = new FileWriter(new File("x.txt"));
		try {
			for (Integer i : es) {
				w.append(String.valueOf(i)).append('\n');
			}
			w.flush();
		} finally {
			w.close();
		}
	}

	static boolean execute(Connection conn, String sql) {
		Statement stat = null;
		try {
			try {
				stat = conn.createStatement();
				return stat.executeUpdate(sql) > 0;
			} finally {
				if (stat != null) stat.close();
			}
		} catch (SQLException e) {
			System.out.println(e.toString());
			return false;
		}
	}
}

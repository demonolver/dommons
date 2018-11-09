/*
 * @(#)ClosableContainer.java     2012-2-1
 */
package org.dommons.db.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.dommons.core.Silewarner;

/**
 * 可关闭 JDBC 项容器
 * @author Demon 2012-2-1
 */
abstract class ClosableContainer {

	private Collection list;
	private volatile boolean closing = false;

	/**
	 * 添加子元素
	 * @param closable 子元素
	 */
	public <T> T add(T closable) {
		if (closable != null) {
			if (list == null) list = new HashSet(2);
			list.add(closable);
		}
		return closable;
	}

	/**
	 * 关闭子元素
	 */
	public void closeChildren() {
		closing = true;
		try {
			if (list != null) {
				for (Iterator it = list.iterator(); it.hasNext(); it.remove()) {
					Object closable = it.next();
					try {
						close(closable);
					} catch (RuntimeException e) {
						Silewarner.warn(this.getClass(), e);
					}
				}
			}
		} finally {
			closing = false;
		}
	}

	/**
	 * 移除子元素
	 * @param closable 子元素
	 */
	public void remove(Object closable) {
		if (list != null && !closing) list.remove(closable);
	}

	/**
	 * 关闭
	 * @param closable 可关闭元素
	 */
	protected void close(Object closable) {
		try {
			if (closable instanceof Connection) {
				((Connection) closable).close();
			} else if (closable instanceof Statement) {
				((Statement) closable).close();
			} else if (closable instanceof ResultSet) {
				((ResultSet) closable).close();
			}
		} catch (SQLException e) {
			Silewarner.warn(this.getClass(), e);
		}
	}
}

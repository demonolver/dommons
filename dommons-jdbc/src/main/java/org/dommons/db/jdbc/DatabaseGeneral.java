/*
 * @(#)DatabaseGeneral.java     2022-09-30
 */
package org.dommons.db.jdbc;

import java.io.Serializable;

/**
 * 数据库基础信息
 * @author demon 2022-09-30
 */
public class DatabaseGeneral implements Serializable {

	private static final long serialVersionUID = 6130275063685448784L;

	/** 数据库名称 */
	protected String name;
	/** 数据库类型 */
	protected String type;
	/** 数据库版本 */
	protected String version;

	public DatabaseGeneral() {
		super();
	}

	public DatabaseGeneral(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public DatabaseGeneral setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public DatabaseGeneral setType(String type) {
		this.type = type;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public DatabaseGeneral setVersion(String version) {
		this.version = version;
		return this;
	}
}

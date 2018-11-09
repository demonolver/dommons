/*
 * @(#)SQLDateConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.sql.Date;

/**
 * SQL 日期转换器
 * @author Demon 2011-10-21
 */
public class SQLDateConverter extends DateTimeConverter<Date> {

	protected Date createDate(java.util.Date date) {
		return new Date(date.getTime());
	}
}

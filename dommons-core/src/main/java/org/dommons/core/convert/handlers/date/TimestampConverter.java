/*
 * @(#)TimestampConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 时间戳转换器
 * @author Demon 2011-10-21
 */
public class TimestampConverter extends DateTimeConverter<Timestamp> {

	protected Timestamp createDate(Date date) {
		return new Timestamp(date.getTime());
	}
}

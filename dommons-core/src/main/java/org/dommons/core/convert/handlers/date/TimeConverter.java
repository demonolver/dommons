/*
 * @(#)TimeConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.sql.Time;
import java.util.Date;

/**
 * 时分秒转换器
 * @author Demon 2011-10-21
 */
public class TimeConverter extends DateTimeConverter<Time> {

	protected Time createDate(Date date) {
		return new Time(date.getTime());
	}
}

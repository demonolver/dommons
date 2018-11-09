/*
 * @(#)DateConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.util.Date;

/**
 * 日期转换器
 * @author Demon 2011-10-21
 */
public class DateConverter extends DateTimeConverter<Date> {

	protected Date createDate(Date date) {
		return date;
	}
}

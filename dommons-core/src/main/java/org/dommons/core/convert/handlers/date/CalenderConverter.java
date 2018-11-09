/*
 * @(#)CalenderConverter.java     2011-10-21
 */
package org.dommons.core.convert.handlers.date;

import java.util.Calendar;
import java.util.Date;

import org.dommons.core.Environments;

/**
 * 日历转换器
 * @author Demon 2011-10-21
 */
public class CalenderConverter extends DateTimeConverter<Calendar> {

	protected Calendar createDate(Date date) {
		Calendar cal = Calendar.getInstance(Environments.defaultTimeZone(), Environments.defaultLocale());
		cal.setTime(date);
		return cal;
	}
}

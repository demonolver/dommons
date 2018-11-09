/*
 * @(#)AndroidLogFactory.java     2014-4-14
 */
package org.dommons.android.log;

import org.dommons.core.string.Stringure;
import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * 安卓应用日志工厂
 * @author Demon 2014-4-14
 */
public class AndroidLogFactory extends LoggerFactory {

	protected Logger createLogger(Class clazz) {
		return createLogger(clazz == null ? Stringure.empty : clazz.getCanonicalName());
	}

	protected Logger createLogger(String name) {
		return new LoggerWAndroid(name);
	}
}

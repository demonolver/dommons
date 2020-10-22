/*
 * @(#)LogbackFactory.java     2020-10-21
 */
package org.dommons.log.logback;

import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * logback 工厂
 * @author demon 2020-10-21
 */
public class LogbackFactory extends LoggerFactory {

	@Override
	protected Logger createLogger(Class clazz) {
		return new LoggerWBack((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(clazz));
	}

	@Override
	protected Logger createLogger(String name) {
		return new LoggerWBack((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(name));
	}

}

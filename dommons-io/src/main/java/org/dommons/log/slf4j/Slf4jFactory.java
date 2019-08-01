/*
 * @(#)Slf4jFactory.java     2018-04-25
 */
package org.dommons.log.slf4j;

import org.dommons.log.Logger;
import org.dommons.log.LoggerFactory;

/**
 * slf4j 工厂
 * @author demon 2018-04-25
 */
public class Slf4jFactory extends LoggerFactory {

	protected Logger createLogger(Class clazz) {
		return new LoggerWSlf4j(org.slf4j.LoggerFactory.getLogger(clazz));
	}

	protected Logger createLogger(String name) {
		return new LoggerWSlf4j(org.slf4j.LoggerFactory.getLogger(name));
	}

}

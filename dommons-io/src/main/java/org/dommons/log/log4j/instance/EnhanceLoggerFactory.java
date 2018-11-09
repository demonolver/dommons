/*
 * @(#)EnhanceLoggerFactory.java     2011-11-14
 */
package org.dommons.log.log4j.instance;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * 加强 log4j 日志工厂
 * @author Demon 2011-11-14
 */
public class EnhanceLoggerFactory implements LoggerFactory {

	public Logger makeNewLoggerInstance(String name) {
		return new EnhanceLogger(name);
	}
}

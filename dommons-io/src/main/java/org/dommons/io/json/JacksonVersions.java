/*
 * @(#)JacksonVersions.java     2018-06-11
 */
package org.dommons.io.json;

import org.dommons.core.util.beans.ObjectInstantiators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

/**
 * @author demon 2018-06-11
 */
class JacksonVersions {

	@SuppressWarnings("deprecation")
	protected static PropertyNamingStrategyBase snake() {
		try {
			return ObjectInstantiators.newInstance(Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy",
				false, ObjectMapper.class.getClassLoader()));
		} catch (Throwable t) {
			return new com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy();
		}
	}
}

/*
 * @(#)JacksonVersions.java     2018-06-11
 */
package org.dommons.io.json;

import org.dommons.core.util.beans.ObjectInstantiators;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;

/**
 * 版本适配
 * @author demon 2018-06-11
 */
class JacksonVersions {

	protected static PropertyNamingStrategyBase snake() {
		try {
			return (PropertyNamingStrategyBase) PropertyNamingStrategy.SNAKE_CASE;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			return ObjectInstantiators
					.newInstance(Class.forName("com.fasterxml.jackson.databind.PropertyNamingStrategy$LowerCaseWithUnderscoresStrategy",
						false, PropertyNamingStrategy.class.getClassLoader()));
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
}

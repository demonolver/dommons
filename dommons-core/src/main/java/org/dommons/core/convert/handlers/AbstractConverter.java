/*
 * @(#)AbstractConverter.java     2011-11-1
 */
package org.dommons.core.convert.handlers;

import org.dommons.core.convert.ConvertHandler;

/**
 * 抽象类型转换器
 * @author Demon 2011-11-1
 */
public abstract class AbstractConverter<S, T> implements ConvertHandler<S, T> {

	public boolean equals(Object o) {
		return o != null && this.getClass().equals(o.getClass());
	}

	public int hashCode() {
		return this.getClass().getName().hashCode();
	}
}

/*
 * @(#)ListConverter.java     2016-1-18
 */
package org.dommons.core.convert.handlers.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dommons.core.convert.handlers.AbstractConverter;
import org.dommons.core.util.Arrayard;

/**
 * 列表型转换器
 * @author Demon 2016-1-18
 */
public class ListConverter extends AbstractConverter<Object, List> {

	public List convert(Object obj, Class<? extends Object> source, Class<List> target) {
		if (Collection.class.isAssignableFrom(source)) {
			return new ArrayList((Collection) obj);
		} else if (source.isArray()) {
			return Arrayard.asList(obj);
		}
		return null;
	}
}

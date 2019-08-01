/*
 * @(#)JsonCollectionType.java     2018-6-11
 */
package org.dommons.io.json;

import java.lang.reflect.TypeVariable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeBase;
import com.fasterxml.jackson.databind.type.TypeBindings;

/**
 * 集合 JSON 类型转换器
 * @author demon 2018-6-11
 */
abstract class JsonCollectionType extends TypeBase {

	private static final long serialVersionUID = -2036181218910588301L;

	private static boolean r = true;

	public static JavaType collection(Class<?> rawType, JavaType elem) {
		if (!r) return null;
		try {
			TypeVariable<?>[] vars = rawType.getTypeParameters();
			TypeBindings bindings;
			if ((vars == null) || (vars.length != 1)) bindings = TypeBindings.emptyBindings();
			else bindings = TypeBindings.create(rawType, elem);
			return CollectionType.construct(rawType, bindings, _bogusSuperClass(rawType), null, elem);
		} catch (Throwable t) {
			r = false;
			return null;
		}
	}

	protected JsonCollectionType(TypeBase base) {
		super(base);
	}
}

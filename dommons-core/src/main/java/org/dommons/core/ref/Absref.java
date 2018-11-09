/*
 * @(#)AbsRef.java     2018-08-22
 */
package org.dommons.core.ref;

import java.lang.ref.Reference;

/**
 * 抽象对象引用
 * @author demon 2018-08-22
 */
abstract class Absref<T> implements Ref<T> {

	protected final Reference<T> ref;

	protected Absref(Reference<T> ref) {
		assert ref != null;
		this.ref = ref;
	}

	public T get() {
		return ref.get();
	}
}

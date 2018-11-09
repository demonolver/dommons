/*
 * @(#)Strongref.java     2018-08-22
 */
package org.dommons.core.ref;

/**
 * 强引用 使用中不做释放
 * @author demon 2018-08-22
 */
public class Strongref<T> implements Ref<T> {

	private final T referent;

	public Strongref(T referent) {
		this.referent = referent;
	}

	public T get() {
		return referent;
	}
}

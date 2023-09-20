/*
 * @(#)Weakref.java     2018-08-22
 */
package org.dommons.core.ref;

import java.lang.ref.WeakReference;

/**
 * 虚引用 内存回收时释放
 * @author demon 2018-08-22
 */
public class Weakref<T> extends Absref<T> {

	public Weakref(T referent) {
		super(new WeakReference(referent));
	}
}

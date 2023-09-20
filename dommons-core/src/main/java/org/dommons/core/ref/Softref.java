/*
 * @(#)Softref.java     2018-08-22
 */
package org.dommons.core.ref;

import java.lang.ref.SoftReference;

/**
 * 软引用 内存不足时释放
 * @author demon 2018-08-22
 */
public class Softref<T> extends Absref<T> {

	public Softref(T referent) {
		super(new SoftReference(referent));
	}
}

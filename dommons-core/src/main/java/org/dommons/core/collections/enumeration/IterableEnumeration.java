/*
 * @(#)IterableEnumeration.java     2012-7-5
 */
package org.dommons.core.collections.enumeration;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * 枚举迭代器
 * @author Demon 2012-7-5
 */
public abstract class IterableEnumeration<E> implements Enumeration<E>, Iterator<E> {

	static Reference<IterableEnumeration> empty;

	/**
	 * 空枚举
	 * @return 枚举实例
	 */
	public static IterableEnumeration empty() {
		IterableEnumeration e = empty == null ? null : empty.get();
		if (e == null) {
			e = new IterableEnumeration() {
				public boolean hasNext() {
					return false;
				}

				public Object next() {
					return null;
				}

				public void remove() {}
			};
			empty = new SoftReference(e);
		}
		return e;
	}

	public boolean hasMoreElements() {
		return hasNext();
	}

	public E nextElement() {
		return next();
	}
}

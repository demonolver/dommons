/*
 * @(#)Strongref.java     2018-08-22
 */
package org.dommons.core.ref;

/**
 * 强引用 使用中不做释放
 * @author demon 2018-08-22
 */
public class Strongref<T> implements Ref<T> {

	/** 空对象引用 */
	public static final Strongref empty = new Strongref(null);

	/**
	 * 生成强引用
	 * @param referent 引用对象
	 * @return 强引用
	 */
	public static <T> Strongref<T> ref(T referent) {
		if (referent == null) return empty;
		return new Strongref(referent);
	}

	private final T referent;

	public Strongref(T referent) {
		this.referent = referent;
	}

	public T get() {
		return referent;
	}
}

/*
 * @(#)Strongref.java     2018-08-22
 */
package org.dommons.core.ref;

import java.io.Serializable;

/**
 * 强引用 使用中不做释放
 * @author demon 2018-08-22
 */
public class Strongref<T> implements Modref<T>, Serializable {

	private static final long serialVersionUID = 5097156840789700965L;

	/** 空对象引用 */
	public static final Ref empty = new Ref() {
		@Override
		public Object get() {
			return null;
		}
	};

	/**
	 * 生成强引用
	 * @param referent 引用对象
	 * @return 强引用
	 */
	public static <T> Strongref<T> ref(T referent) {
		return new Strongref(referent);
	}

	private volatile T referent;

	public Strongref() {
		super();
	}

	public Strongref(T referent) {
		this();
		this.referent = referent;
	}

	public T get() {
		return referent;
	}

	@Override
	public void set(T value) {
		this.referent = value;
	}

}

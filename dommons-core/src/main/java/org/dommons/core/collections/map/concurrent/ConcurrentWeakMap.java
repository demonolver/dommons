/*
 * @(#)ConcurrentWeakMap.java     2018-08-01
 */
package org.dommons.core.collections.map.concurrent;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

/**
 * 线程安全弱引用映射表
 * @author demon 2018-08-01
 */
public class ConcurrentWeakMap<K, V> extends ConcurrentMapWrapper<K, V> {

	private static final long serialVersionUID = 3503329831784603237L;

	public ConcurrentWeakMap() {
		super(new WeakHashMap());
	}

	@Override
	protected Lock readLock() {
		return lock.writeLock();
	}
}

/*
 * @(#)ConcurrentSoftMap.java     2018-10-29
 */
package org.dommons.core.collections.map.concurrent;

import java.util.concurrent.locks.Lock;

import org.dommons.core.collections.map.ref.SoftHashMap;

/**
 * 线程安全软引用映射表
 * @author demon 2018-10-29
 */
public class ConcurrentSoftMap<K, V> extends ConcurrentMapWrapper<K, V> {

	private static final long serialVersionUID = -2304594653282810324L;

	public ConcurrentSoftMap() {
		super(new SoftHashMap());
	}

	@Override
	protected Lock readLock() {
		return lock.writeLock();
	}
}

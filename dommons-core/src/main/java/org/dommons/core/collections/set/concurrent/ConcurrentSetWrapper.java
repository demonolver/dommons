/*
 * @(#)ConcurrentSetWrapper.java     2018-10-29
 */
package org.dommons.core.collections.set.concurrent;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import org.dommons.core.collections.collection.concurrent.AbsConcurrentCollectionWrapper;

/**
 * 并发 Set 包装
 * @author demon 2018-10-29
 */
public class ConcurrentSetWrapper<E> extends AbsConcurrentCollectionWrapper<E, Set<E>> implements Set<E> {

	private static final long serialVersionUID = -4876600619894725697L;

	public ConcurrentSetWrapper(Set<E> tar) {
		super(tar);
	}

	public ConcurrentSetWrapper(Set<E> tar, ReadWriteLock lock) {
		super(tar, lock);
	}
}

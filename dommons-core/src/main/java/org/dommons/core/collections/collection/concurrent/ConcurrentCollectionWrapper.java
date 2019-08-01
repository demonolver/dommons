/*
 * @(#)ConcurrentCollectionWrapper.java     2018-10-29
 */
package org.dommons.core.collections.collection.concurrent;

import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 并发数据集包装
 * @author demon 2018-10-29
 */
public class ConcurrentCollectionWrapper<T> extends AbsConcurrentCollectionWrapper<T, Collection<T>> {

	private static final long serialVersionUID = 3944928356542152535L;

	public ConcurrentCollectionWrapper(Collection<T> tar) {
		super(tar);
	}

	public ConcurrentCollectionWrapper(Collection<T> tar, ReadWriteLock lock) {
		super(tar, lock);
	}
}

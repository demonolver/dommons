/*
 * @(#)EmptyCache.java     2019-08-01
 */
package org.dommons.core.cache;

/**
 * 空缓存实现
 * @author demon 2019-08-01
 */
public class EmptyCache<K, V> extends DataCacheMap<K, V> {

	public static final EmptyCache empty = new EmptyCache();

	protected EmptyCache() {}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void clear() {

	}
}

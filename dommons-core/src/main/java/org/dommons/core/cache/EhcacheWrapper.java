/*
 * @(#)DataCacheWrapper.java     2017-06-15
 */
package org.dommons.core.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * Ehcache 缓存包装
 * @author demon 2017-06-15
 */
public class EhcacheWrapper<K, V> implements DataCache<K, V> {

	/**
	 * 包装缓存体
	 * @param cache Ehcache 缓存
	 * @return 数据缓存体
	 */
	public static <K, V> DataCache<K, V> wrap(Cache cache) {
		return cache == null ? null : new EhcacheWrapper(cache);
	}

	protected final Cache cache;

	protected EhcacheWrapper(Cache cache) {
		this.cache = cache;
	}

	public void clear() {
		cache.removeAll();
	}

	public V get(Object key) {
		if (key == null) return null;
		Element ele = cache.get(key);
		return ele == null ? null : (V) ele.getObjectValue();
	}

	public V remove(Object key) {
		if (key != null) {
			V v = get(key);
			if (cache.remove(key) && v != null) return v;
		}
		return null;
	}

	public void set(K key, V value) {
		if (key == null) return;
		else if (value == null) remove(key);
		else cache.put(new Element(key, value));
	}
}

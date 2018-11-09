/*
 * @(#)DataCacheMap.java     2017-06-15
 */
package org.dommons.core.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 抽象数据缓存映射集
 * @author demon 2017-06-15
 */
public abstract class DataCacheMap<K, V> implements Map<K, V>, DataCache<K, V> {

	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.EMPTY_SET;
	}

	public boolean isEmpty() {
		return false;
	}

	public Set<K> keySet() {
		return Collections.EMPTY_SET;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		if (m != null) {
			for (Entry<? extends K, ? extends V> en : m.entrySet())
				put(en.getKey(), en.getValue());
		}
	}

	public void set(K key, V value) {
		put(key, value);
	}

	public int size() {
		return 0;
	}

	public Collection<V> values() {
		return Collections.EMPTY_LIST;
	}
}

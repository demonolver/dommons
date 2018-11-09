/*
 * @(#)DataCache.java     2017-06-15
 */
package org.dommons.core.cache;

/**
 * 数据缓存
 * @author demon 2017-06-15
 */
public interface DataCache<K, V> {

	/**
	 * 清空缓存
	 */
	public void clear();

	/**
	 * 获取缓存数据
	 * @param key 缓存键
	 * @return 缓存数据
	 */
	public V get(Object key);

	/**
	 * 移除缓存
	 * @param key 缓存键
	 * @return 缓存数据
	 */
	public V remove(Object key);

	/**
	 * 设置缓存数据
	 * @param key 缓存键
	 * @param value 缓存数据
	 */
	public void set(K key, V value);
}

/*
 * @(#)DualMap.java     2011-10-19
 */
package org.dommons.core.collections.map.dual;

import java.util.Map;

/**
 * 双向映射表
 * @author Demon 2011-10-19
 */
public interface DualMap<K, V> extends Map<K, V> {

	/**
	 * 获取键数据
	 * @param value 值数据
	 * @return 键数据
	 */
	public K getKey(Object value);

	/**
	 * 获取值数据
	 * @param key 键数据
	 * @return 值数据
	 */
	public V getValue(Object key);

	/**
	 * 键值反转
	 * @return 反转后列表
	 */
	public DualMap<V, K> inverse();

	/**
	 * 根据键移除数据
	 * @param key 键数据
	 * @return 值数据
	 */
	public V removeKey(Object key);

	/**
	 * 根据值移除数据
	 * @param value 值数据
	 * @return 键数据
	 */
	public K removeValue(Object value);
}

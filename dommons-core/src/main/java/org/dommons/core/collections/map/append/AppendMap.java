/*
 * @(#)AppendMap.java     2011-10-19
 */
package org.dommons.core.collections.map.append;

import java.util.Map;

/**
 * 可追加映射表
 * @author Demon 2011-10-19
 */
public interface AppendMap<K, V, M extends Map<K, V>> extends Map<K, V> {

	/**
	 * 添加值
	 * @param key 键数据
	 * @param value 值数据
	 * @return 可追加映射表
	 */
	AppendMap<K, V, M> append(K key, V value);

	/**
	 * 转换为原映射表
	 * @return 原映射表
	 */
	M entity();

	/**
	 * 移除值
	 * @param keys 键值集
	 * @return 可追加映射表
	 */
	AppendMap<K, V, M> remove(Object... keys);
}

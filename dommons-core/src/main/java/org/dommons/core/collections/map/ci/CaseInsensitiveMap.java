/*
 * @(#)CaseInsensitiveMap.java     2011-10-18
 */
package org.dommons.core.collections.map.ci;

import java.util.Map;

/**
 * 无视键值大小写的映射表
 * @author Demon 2011-10-18
 */
public interface CaseInsensitiveMap<V> extends Map<String, V> {

	/**
	 * 是否默认以无视键值大小写的方式添加新数据
	 * @return 是、否
	 */
	boolean defaultWithCaseInsensitive();

	/**
	 * 获取忽略大小写的真实键值
	 * @param key 原键值
	 * @return 真实键值
	 */
	String getCaseInsensitivekey(String key);

	/**
	 * 加入数据，键值区分大小写
	 * @param key 键
	 * @param value 值
	 * @return 原值
	 */
	V putNoCaseInsensitive(String key, V value);

	/**
	 * 加入数据，键值无视大小写
	 * @param key 键
	 * @param value 值
	 * @return 原值
	 */
	V putWithCaseInsensitive(String key, V value);

	/**
	 * 移除所有相似键值的数据
	 * @param key 键
	 * @return 是否移除
	 */
	boolean removeAll(Object key);
}

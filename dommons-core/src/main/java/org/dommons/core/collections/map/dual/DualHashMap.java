/*
 * @(#)DualHashMap.java     2011-10-19
 */
package org.dommons.core.collections.map.dual;

import java.util.HashMap;
import java.util.Map;

/**
 * 双向哈希表
 * @author Demon 2011-10-19
 */
public class DualHashMap<K, V> extends AbsDualMap<K, V> {

	private static final long serialVersionUID = 5588129734537123303L;

	/**
	 * 构造函数
	 */
	public DualHashMap() {
		super(new HashMap(), new HashMap());
	}

	/**
	 * 构造函数
	 * @param normalMap 键-值对表
	 * @param reverseMap 值-键对表
	 * @param inverseMap 反向双向表
	 */
	protected DualHashMap(Map<K, V> normalMap, Map<V, K> reverseMap, DualMap<V, K> inverseMap) {
		super(normalMap, reverseMap, inverseMap);
	}

	protected DualMap<V, K> createInverse(Map<V, K> reverseMap, Map<K, V> normalMap, DualMap<K, V> current) {
		return new DualHashMap(normalMap, reverseMap, current);
	}
}

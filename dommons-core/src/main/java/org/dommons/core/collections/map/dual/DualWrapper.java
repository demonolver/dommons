/*
 * @(#)DualWrapper.java     2011-10-19
 */
package org.dommons.core.collections.map.dual;

import java.util.Map;

import org.dommons.core.Assertor;

/**
 * 双向映射表包装
 * @author Demon 2011-10-19
 */
public class DualWrapper<K, V> extends AbsDualMap<K, V> {

	private static final long serialVersionUID = -3682763177328451556L;

	/**
	 * 双向表包装 将两个键-值类型相互相反的对应表包装为一个可双向查询对应值的对应表
	 * @param normalMap 正向表
	 * @param reverseMap 反向表
	 * @return 双向映射表
	 */
	public static <K, V> DualMap<K, V> wrap(Map<K, V> normalMap, Map<V, K> reverseMap) {
		Assertor.F.notNull(normalMap);
		Assertor.F.notNull(reverseMap);
		return new DualWrapper(normalMap, reverseMap);
	}

	/**
	 * 构造函数
	 * @param normalMap 键-值对表
	 * @param reverseMap 值-键对表
	 */
	public DualWrapper(Map<K, V> normalMap, Map<V, K> reverseMap) {
		super(normalMap, reverseMap);
	}

	/**
	 * 构造函数
	 * @param normalMap 键-值对表
	 * @param reverseMap 值-键对表
	 * @param inverseMap 反向双向表
	 */
	protected DualWrapper(Map<K, V> normalMap, Map<V, K> reverseMap, DualMap<V, K> inverseMap) {
		super(normalMap, reverseMap, inverseMap);
	}

	protected DualMap<V, K> createInverse(Map<V, K> reverseMap, Map<K, V> normalMap, DualMap<K, V> current) {
		return new DualWrapper(normalMap, reverseMap, current);
	}
}

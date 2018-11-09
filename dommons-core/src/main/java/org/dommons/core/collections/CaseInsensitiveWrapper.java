/*
 * @(#)CaseInsensitiveWrapper.java     2011-10-18
 */
package org.dommons.core.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.dommons.core.Assertor;
import org.dommons.core.collections.map.ci.AbsCaseInsensitiveMap;
import org.dommons.core.collections.map.ci.CaseInsensitiveMap;
import org.dommons.core.collections.set.AbsSet;

/**
 * 忽略键值大小写包装 支持多个大小写不同的键并存，查找时无视大小写，查找最相似的
 * @author Demon 2011-10-18
 */
public class CaseInsensitiveWrapper implements Serializable {

	private static final long serialVersionUID = -1551014544601699087L;

	/**
	 * 无视键值大小写包装
	 * @param <T> 值类型
	 * @param map 目标映射表
	 * @return 新映射表
	 */
	public static <T> CaseInsensitiveMap<T> wrap(Map<String, T> map) {
		return wrap(map, true);
	}

	/**
	 * 无视键值大小写包装
	 * @param <T> 值类型
	 * @param map 目标映射表
	 * @param caseInsensitive 是否默认写入时无视大小写
	 * @return 新映射表
	 */
	public static <T> CaseInsensitiveMap<T> wrap(Map<String, T> map, boolean caseInsensitive) {
		return wrap(map, null, caseInsensitive);
	}

	/**
	 * 无视键值大小写包装
	 * @param <T> 值类型
	 * @param map 目标映射表
	 * @param keyIndex 键索引
	 * @return 新映射表
	 */
	public static <T> CaseInsensitiveMap<T> wrap(Map<String, T> map, Map<String, Collection<String>> keyIndex) {
		return wrap(map, keyIndex, true);
	}

	/**
	 * 无视键值大小写包装
	 * @param <T> 值类型
	 * @param map 目标映射表
	 * @param keyIndex 键索引
	 * @param caseInsensitive 是否默认写入时无视大小写
	 * @return 新映射表
	 */
	public static <T> CaseInsensitiveMap<T> wrap(Map<String, T> map, Map<String, Collection<String>> keyIndex, boolean caseInsensitive) {
		Assertor.F.notNull(map, "The target map is must not be null");
		if (map instanceof CaseInsensitiveMap) return CaseInsensitiveMap.class.cast(map);
		return new CIMapWrapper(map, keyIndex, caseInsensitive);
	}

	/**
	 * 包装成忽略大小写无重复数据集
	 * @param set 目标数据集
	 * @return 新数据集
	 */
	public static Set<String> wrap(Set<String> set) {
		Assertor.F.notNull(set, "The target set is must not be null");
		return AbsSet.wrap(set, false);
	}

	/**
	 * 包装成线程安全的忽略大小写无重复数据集
	 * @param set 目标数据集
	 * @return 新数据集
	 */
	public static Set<String> wrapWithSynchronized(Set<String> set) {
		Assertor.F.notNull(set, "The target set is must not be null");
		return AbsSet.wrap(set, true);
	}

	/**
	 * 忽略键值大小写的映射表包装
	 * @param <V> 值类型
	 * @author demon 2016-11-24
	 */
	protected static class CIMapWrapper<V> extends AbsCaseInsensitiveMap<V> {

		private static final long serialVersionUID = 2460623490683063400L;

		/**
		 * 构造函数
		 * @param map 目标映射表
		 * @param keyIndex 键索引
		 * @param caseInsensitive 是否默认写入无视大小写
		 */
		CIMapWrapper(Map<String, V> map, Map<String, Collection<String>> keyIndex, boolean caseInsensitive) {
			super(map, keyIndex, caseInsensitive);
		}
	}
}

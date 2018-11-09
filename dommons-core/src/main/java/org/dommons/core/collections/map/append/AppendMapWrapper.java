/*
 * @(#)AppendMapWrapper.java     2011-10-19
 */
package org.dommons.core.collections.map.append;

import java.io.Serializable;
import java.util.Map;

import org.dommons.core.collections.map.AbsMapWrapper;

/**
 * 可追加映射表包装
 * @author Demon 2011-10-19
 */
public class AppendMapWrapper<K, V, M extends Map<K, V>> extends AbsMapWrapper<K, V> implements AppendMap<K, V, M>, Serializable {

	private static final long serialVersionUID = 4547344129686591300L;

	/**
	 * 包装
	 * @param map 映射表
	 * @return 可追加映射表
	 */
	public static <K, V, M extends Map<K, V>> AppendMap<K, V, M> wrap(M map) {
		if (map == null) return null;
		if (map instanceof AppendMap) return (AppendMap) map;
		return new AppendMapWrapper(map);
	}

	protected AppendMapWrapper(M map) {
		super(map);
	}

	public AppendMap<K, V, M> append(K key, V value) {
		synchronized (this) {
			put(key, value);
		}
		return this;
	}

	public M entity() {
		synchronized (this) {
			return (M) tar();
		}
	}

	public AppendMap<K, V, M> remove(Object... keys) {
		if (keys != null) {
			for (Object k : keys) {
				synchronized (this) {
					remove(k);
				}
			}
		}
		return this;
	}
}

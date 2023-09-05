/*
 * @(#)Mapped.java     2017-09-25
 */
package org.dommons.core.collections.map;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.dommons.core.collections.map.concurrent.ConcurrentMapWrapper;
import org.dommons.core.convert.Converter;
import org.dommons.core.util.beans.ObjectInstantiator;
import org.dommons.core.util.beans.ObjectInstantiator.ObjectCreator;
import org.dommons.core.util.beans.ObjectInstantiators;

/**
 * 映射表工具类
 * @author demon 2017-09-25
 */
public abstract class Mapped {

	/**
	 * 使映射表线程安全
	 * @param map 映射表
	 * @return 线程安全映射表
	 */
	public static <K, V> ConcurrentMap<K, V> concurrent(Map<K, V> map) {
		if (map == null) return null;
		else if (map instanceof ConcurrentMap) return (ConcurrentMap) map;
		else return new ConcurrentMapWrapper(map);
	}

	/**
	 * 触达子元素
	 * @param map 可并发映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(ConcurrentMap<? super K, V> map, K key, Class<C> instCls) {
		return touch(map, key, instCls, false);
	}

	/**
	 * 触达子元素
	 * @param map 可并发映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @param syn 是否同步
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(ConcurrentMap<? super K, V> map, K key, Class<C> instCls, boolean syn) {
		if (map == null || key == null || instCls == null) return null;
		Object v = map.get(key), nv = null;
		touch: try {
			if (v != null && instCls.isInstance(v)) {
				break touch;
			} else if (v == null) {
				Object old = map.putIfAbsent(key, (C) (nv = v = ObjectInstantiators.newInstance(instCls)));
				if (old != null) v = old;
			}
			if (!instCls.isInstance(v)) {
				if (syn) {
					synchronized (map) {
						return touch(map, key, instCls, false);
					}
				} else {
					if (nv == null) nv = ObjectInstantiators.newInstance(instCls);
					map.put(key, (V) (v = nv));
				}
			}
		} catch (Exception e) {
			Converter.F.convert(e, RuntimeException.class);
		}
		return (S) v;
	}

	/**
	 * 触达子元素
	 * @param map 可并发映射表
	 * @param key 键值名
	 * @param creator 元素创建器
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(ConcurrentMap<? super K, V> map, K key, ObjectCreator<C> creator) {
		return touch(map, key, creator, false);
	}

	/**
	 * 触达子元素
	 * @param map 可并发映射表
	 * @param key 键值名
	 * @param creator 元素创建器
	 * @param syn 是否同步
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(ConcurrentMap<? super K, V> map, K key, ObjectCreator<C> creator, boolean syn) {
		if (map == null || key == null || creator == null) return null;
		Object v = map.get(key), nv = null;
		touch: try {
			if (v != null && creator.isInstance(v)) {
				break touch;
			} else if (v == null) {
				Object old = map.putIfAbsent(key, (C) (nv = v = creator.newInstance()));
				if (old != null) v = old;
			}

			if (!creator.isInstance(v)) {
				if (syn) {
					synchronized (map) {
						return touch(map, key, creator, false);
					}
				} else {
					if (nv == null) nv = creator.newInstance();
					map.put(key, (V) (v = nv));
				}
			}
		} catch (Exception e) {
			Converter.F.convert(e, RuntimeException.class);
		}
		return (S) v;
	}

	/**
	 * 触达子元素
	 * @param map 映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(Map<? super K, V> map, K key, Class<C> instCls) {
		if (map == null || key == null || instCls == null) return null;
		if (map instanceof ConcurrentMap) return touch((ConcurrentMap<? super K, V>) map, key, instCls);
		Object v = map.get(key);
		try {
			if (v == null || !instCls.isInstance(v)) map.put(key, (C) (v = ObjectInstantiators.newInstance(instCls)));
		} catch (Exception e) {
			Converter.F.convert(e, RuntimeException.class);
		}
		return (S) v;
	}

	/**
	 * 触达子元素
	 * @param map 映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @param syn 是否同步
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(Map<? super K, V> map, K key, Class<C> instCls, boolean syn) {
		if (map == null || key == null || instCls == null) return null;
		if (map instanceof ConcurrentMap) return touch((ConcurrentMap<? super K, V>) map, key, instCls, syn);
		Object v = map.get(key);
		if (v != null && instCls.isInstance(v)) return (S) v;
		if (syn) {
			synchronized (map) {
				return touch(map, key, instCls);
			}
		} else {
			return touch(map, key, instCls);
		}
	}

	/**
	 * 触达子元素
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param <S> 目标结果类型
	 * @param <C> 元素实现类型
	 * @param map 映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @param instantiator 元素实例化器
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(
			Map<? super K, V> map,
			K key,
			Class<C> instCls,
			ObjectInstantiator<C> instantiator) {
		if (map == null || key == null || instCls == null || instantiator == null) return null;
		return touch(map, key, ObjectInstantiators.creator(instCls, instantiator));
	}

	/**
	 * 触达子元素
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @param <S> 目标结果类型
	 * @param <C> 元素实现类型
	 * @param map 映射表
	 * @param key 键值名
	 * @param instCls 元素实现类
	 * @param instantiator 元素实例化器
	 * @param syn 是否同步
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(
			Map<? super K, V> map,
			K key,
			Class<C> instCls,
			ObjectInstantiator<C> instantiator,
			boolean syn) {
		if (map == null || key == null || instCls == null || instantiator == null) return null;
		return touch(map, key, ObjectInstantiators.creator(instCls, instantiator), syn);
	}

	/**
	 * 触达子元素
	 * @param map 映射表
	 * @param key 键值名
	 * @param creator 元素创建器
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(Map<? super K, V> map, K key, ObjectCreator<C> creator) {
		if (map == null || key == null || creator == null) return null;
		if (map instanceof ConcurrentMap) return touch((ConcurrentMap<? super K, V>) map, key, creator);
		Object v = map.get(key);
		if (v == null || !creator.isInstance(v)) map.put(key, (C) (v = creator.newInstance()));
		return (S) v;
	}

	/**
	 * 触达子元素
	 * @param map 映射表
	 * @param key 键值名
	 * @param creator 元素创建器
	 * @param syn 是否同步
	 * @return 元素实例
	 */
	public static <K, V, S extends V, C extends S> S touch(Map<? super K, V> map, K key, ObjectCreator<C> creator, boolean syn) {
		if (map == null || key == null || creator == null) return null;
		if (map instanceof ConcurrentMap) return touch((ConcurrentMap<? super K, V>) map, key, creator, syn);
		Object v = map.get(key);
		if (v != null && creator.isInstance(v)) return (S) v;
		if (syn) {
			synchronized (map) {
				return touch(map, key, creator);
			}
		} else {
			return touch(map, key, creator);
		}
	}
}

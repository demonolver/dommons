/*
 * @(#)ThreadContent.java     2012-7-5
 */
package org.dommons.core.util.thread;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.dommons.core.collections.enumeration.CollectionEnumeration;
import org.dommons.core.convert.Converter;

/**
 * 线程上下文
 * @author Demon 2012-7-5
 */
public final class ThreadContent<T> {

	private static ThreadLocal<Map<ThreadContent, Map>> local = new ThreadLocal();

	private static ThreadContent def;

	/**
	 * 获取默认线程上下文
	 * @return 线程上下文
	 */
	public static ThreadContent content() {
		return def != null ? def : (def = new ThreadContent());
	}

	public ThreadContent() {
	}

	/**
	 * 清除上下文中所有变量
	 */
	public void clear() {
		Map vars = map().remove(this);
		if (vars != null) vars.clear();
	}

	/**
	 * 获取变量值
	 * @return 变量值
	 */
	public T get() {
		return get((String) null);
	}

	/**
	 * 获取变量值
	 * @param target 目标类型
	 * @return 变量值
	 * @throws ClassCastException
	 */
	public <E> E get(Class<E> target) throws ClassCastException {
		return get(null, target);
	}

	/**
	 * 获取变量值
	 * @param key 变量名
	 * @return 变量值
	 */
	public T get(String key) {
		return vars().get(key);
	}

	/**
	 * 获取变量值
	 * @param key 变量名
	 * @param target 目标类型
	 * @return 变量值
	 * @throws ClassCastException
	 */
	public <E> E get(String key, Class<E> target) throws ClassCastException {
		return Converter.P.convert(get(key), target);
	}

	/**
	 * 变量名集
	 * @return 变量名集
	 */
	public Enumeration<String> keys() {
		return CollectionEnumeration.create(vars().keySet());
	}

	/**
	 * 移除变量
	 * @return 变量值
	 */
	public T remove() {
		return remove(null);
	}

	/**
	 * 移除变量
	 * @param key 变量名
	 * @return 变量值
	 */
	public T remove(String key) {
		return vars().remove(key);
	}

	/**
	 * 设置变量值
	 * @param key 变量名
	 * @param value 变量值
	 * @return 原变量值
	 */
	public T set(String key, T value) {
		return vars().put(key, value);
	}

	/**
	 * 设置变量值
	 * @param value 变量值
	 * @return 原变量值
	 * @see #get()
	 * @see #remove()
	 */
	public T set(T value) {
		return set(null, value);
	}

	/**
	 * 获取线程变量映射表
	 * @return 映射表
	 */
	private Map<ThreadContent, Map> map() {
		Map<ThreadContent, Map> map = local.get();
		if (map == null) local.set(map = new HashMap());
		return map;
	}

	/**
	 * 获取变量集
	 * @return 变量集
	 */
	private Map<String, T> vars() {
		Map<ThreadContent, Map> map = map();
		Map<String, T> vars = map.get(this);
		if (vars == null) map.put(this, vars = new HashMap());
		return vars;
	}
}

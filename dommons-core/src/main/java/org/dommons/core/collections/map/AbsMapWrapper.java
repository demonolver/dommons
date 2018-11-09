/*
 * @(#)AbsMapWrapper.java     2012-6-25
 */
package org.dommons.core.collections.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 抽象映射表包装
 * @author Demon 2012-6-25
 */
public abstract class AbsMapWrapper<K, V> implements Map<K, V>, Serializable {

	private static final long serialVersionUID = -4141422378008365892L;

	private transient Map<K, V> tar;

	protected AbsMapWrapper(Map<K, V> tar) {
		if (tar == null) throw new NullPointerException();
		this.tar = tar;
	}

	public void clear() {
		tar.clear();
	}

	public boolean containsKey(Object key) {
		return tar.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return tar.containsValue(value);
	}

	public Set<Entry<K, V>> entrySet() {
		return tar.entrySet();
	}

	public boolean equals(Object o) {
		return tar.equals(o);
	}

	public V get(Object key) {
		return tar.get(key);
	}

	public int hashCode() {
		return tar.hashCode();
	}

	public boolean isEmpty() {
		return tar.isEmpty();
	}

	public Set<K> keySet() {
		return tar.keySet();
	}

	public V put(K key, V value) {
		return tar.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		if (t != null) tar.putAll(t);
	}

	public V remove(Object key) {
		return tar.remove(key);
	}

	public int size() {
		return tar.size();
	}

	public String toString() {
		return tar.toString();
	}

	public Collection<V> values() {
		return tar.values();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	protected void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		tar = (Map) s.readObject();
	}

	/**
	 * 获取目标
	 * @return 目标实体
	 */
	protected final Map<K, V> tar() {
		return tar;
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	protected void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(tar);
	}
}
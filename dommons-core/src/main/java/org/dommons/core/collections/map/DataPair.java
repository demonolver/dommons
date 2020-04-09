/*
 * @(#)DataPair.java     2012-6-25
 */
package org.dommons.core.collections.map;

import java.io.Serializable;
import java.util.Map.Entry;

import org.dommons.core.Assertor;
import org.dommons.core.convert.Converter;

/**
 * 数据对
 * @author Demon 2012-6-25
 */
public class DataPair<K, V> implements Entry<K, V>, Serializable {

	private static final long serialVersionUID = -4414550409528314933L;

	/**
	 * 创建数据对
	 * @param entry 数据元素项
	 * @return 数据对
	 */
	public static <K, V> DataPair<K, V> create(Entry<K, V> entry) {
		return entry == null ? new DataPair(null, null) : new DataPair(entry.getKey(), entry.getValue());
	}

	/**
	 * 创建数据对
	 * @param key 数据键
	 * @param value 数据值
	 * @return 数据对
	 */
	public static <K, V> DataPair<K, V> create(K key, V value) {
		return new DataPair(key, value);
	}

	private transient K k;
	private transient V v;

	/**
	 * 构造函数
	 */
	public DataPair() {
		super();
	}

	/**
	 * 构造函数
	 * @param key 数据键
	 * @param value 数据值
	 */
	public DataPair(K key, V value) {
		this();
		this.k = key;
		this.v = value;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof Entry)) return false;
		Entry e = (Entry) o;
		return Assertor.P.equals(k, e.getKey()) && Assertor.P.equals(v, e.getValue());
	}

	/**
	 * 获取数据键
	 * @return 数据键
	 */
	public K getKey() {
		return k;
	}

	/**
	 * 获取数据值
	 * @return 数据值
	 */
	public V getValue() {
		return v;
	}

	public int hashCode() {
		return hash(k) ^ hash(v);
	}

	/**
	 * 设置数据键
	 * @param k 数据键
	 */
	public void setKey(K k) {
		this.k = k;
	}

	/**
	 * 设置数据值
	 * @param v 数据值
	 * @return 原数据值
	 */
	public V setValue(V v) {
		V o = this.v;
		this.v = v;
		return o;
	}

	public String toString() {
		return new StringBuilder(64).append(Converter.P.convert(k, String.class)).append('=').append(Converter.P.convert(v, String.class))
				.toString();
	}

	/**
	 * 获取对象哈希值
	 * @param o 对象
	 * @return 哈希值
	 */
	private int hash(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		k = (K) s.readObject();
		v = (V) s.readObject();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(k);
		s.writeObject(v);
	}
}

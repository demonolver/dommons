/*
 * @(#)SoftHashMap.java     2018-10-29
 */
package org.dommons.core.collections.map.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;

import org.dommons.core.util.Arrayard;

/**
 * 软引用哈希映射表
 * @author demon 2018-10-29
 */
public class SoftHashMap<K, V> extends ReferenceHashMap<K, V> {

	public SoftHashMap() {
		super();
	}

	public SoftHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public SoftHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public SoftHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	@Override
	protected ReferenceEntry<K, V> createEntry(K key, V value, ReferenceQueue queue, int hash, ReferenceEntry<K, V> next) {
		return new SoftEntry(key, value, queue, hash, next);
	}

	/**
	 * 软引用元素值
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2018-10-30
	 */
	private static class SoftElement<K, V> extends SoftReference<Object[]> implements ReferenceElement<K, V> {

		private final int hash;

		public SoftElement(K key, V value, ReferenceQueue queue, int hash) {
			super(new Object[] { key, value }, queue);
			this.hash = hash;
		}

		@Override
		public int hash() {
			return hash;
		}

		public K key() {
			return key(get());
		}

		public K key(Object[] o) {
			return (K) Arrayard.get(o, 0);
		}

		public V value() {
			return value(get());
		}

		public V value(Object[] o) {
			return (V) Arrayard.get(o, 1);
		}
	}

	/**
	 * 软引用元素项
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2018-10-29
	 */
	private static class SoftEntry<K, V> extends ReferenceEntry<K, V> {

		public SoftEntry(K key, V value, ReferenceQueue queue, int hash, ReferenceEntry<K, V> next) {
			super(key, value, queue, hash, next);
		}

		@Override
		protected ReferenceElement<K, V> create(K key, V value, ReferenceQueue queue, int hash) {
			return new SoftElement(key, value, queue, hash);
		}
	}
}

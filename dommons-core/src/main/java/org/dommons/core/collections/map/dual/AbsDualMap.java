/*
 * @(#)AbsDualMap.java     2011-10-19
 */
package org.dommons.core.collections.map.dual;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 抽象双向映射表
 * @author Demon 2011-10-19
 */
abstract class AbsDualMap<K, V> extends AbstractMap<K, V> implements DualMap<K, V>, Serializable {

	private static final long serialVersionUID = 2664499793569312187L;

	/** 键-值对表 */
	private transient Map<K, V> normalMap;

	/** 值-键对表 */
	private transient Map<V, K> reverseMap;

	/** 反向双向表 */
	private transient DualMap<V, K> inverseMap;

	/** 值集合实例 */
	private transient Collection<V> values;

	/** 键集合实例 */
	private transient Set<K> keys;

	/** 键-值集合实例 */
	private transient Set<Entry<K, V>> entries;

	/**
	 * 构造函数
	 * @param normalMap 键-值对表
	 * @param reverseMap 值-键对表
	 */
	public AbsDualMap(Map<K, V> normalMap, Map<V, K> reverseMap) {
		this(normalMap, reverseMap, null);
		init();
	}

	/**
	 * 构造函数
	 * @param normalMap 键-值对表
	 * @param reverseMap 值-键对表
	 * @param inverseMap 反向双向表
	 */
	protected AbsDualMap(Map<K, V> normalMap, Map<V, K> reverseMap, DualMap<V, K> inverseMap) {
		if (normalMap == reverseMap) throw new IllegalArgumentException("The two map is must not be same!");
		this.normalMap = normalMap;
		this.reverseMap = reverseMap;
		this.inverseMap = inverseMap;
	}

	public void clear() {
		normalMap.clear();
		reverseMap.clear();
	}

	public boolean containsKey(Object key) {
		if (key == null) return false;
		return normalMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		if (value == null) return false;
		return reverseMap.containsKey(value);
	}

	public Set<Entry<K, V>> entrySet() {
		if (entries == null) entries = new DualEntries();
		return entries;
	}

	public V get(Object key) {
		return getValue(key);
	}

	public K getKey(Object value) {
		if (value == null) return null;
		return reverseMap.get(value);
	}

	public V getValue(Object key) {
		if (key == null) return null;
		return normalMap.get(key);
	}

	public DualMap<V, K> inverse() {
		if (inverseMap == null) inverseMap = createInverse(reverseMap, normalMap, this);
		return inverseMap;
	}

	public boolean isEmpty() {
		return normalMap.isEmpty();
	}

	public Set<K> keySet() {
		if (keys == null) keys = new DualKeys();
		return keys;
	}

	public V put(K key, V value) {
		if (key == null || value == null) throw new NullPointerException();
		V oldValue = normalMap.remove(key);
		if (oldValue != null) reverseMap.remove(oldValue);
		K oldKey = reverseMap.get(value);
		if (oldKey != null) normalMap.remove(oldKey);
		normalMap.put(key, value);
		reverseMap.put(value, key);
		return oldValue;
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		if (map == null) return;
		for (Entry<? extends K, ? extends V> en : map.entrySet()) {
			put(en.getKey(), en.getValue());
		}
	}

	public V remove(Object key) {
		return removeKey(key);
	}

	public V removeKey(Object key) {
		if (key == null) return null;
		V value = normalMap.remove(key);
		if (value != null) reverseMap.remove(value);
		return value;
	}

	public K removeValue(Object value) {
		if (value == null) return null;
		K key = reverseMap.remove(value);
		if (key != null) normalMap.remove(key);
		return key;
	}

	public int size() {
		return normalMap.size();
	}

	public Collection<V> values() {
		if (values == null) values = new DualValues();
		return values;
	}

	/**
	 * 创建反向双向映射表
	 * @param reverseMap 反向映射表
	 * @param normalMap 正常映射表
	 * @param current 当前双向映射表
	 * @return 反向双向映射表
	 */
	protected abstract DualMap<V, K> createInverse(Map<V, K> reverseMap, Map<K, V> normalMap, DualMap<K, V> current);

	/**
	 * 初始化
	 */
	protected void init() {
		Object[][] values = new Object[normalMap.size() + reverseMap.size()][2];
		int i = 0;
		for (Entry<K, V> en : normalMap.entrySet()) {
			Object[] value = values[i++];
			value[0] = en.getKey();
			value[1] = en.getValue();
		}
		for (Entry<V, K> en : reverseMap.entrySet()) {
			Object[] value = values[i++];
			value[0] = en.getValue();
			value[1] = en.getKey();
		}
		normalMap.clear();
		reverseMap.clear();
		for (Object[] value : values) {
			put((K) value[0], (V) value[1]);
		}
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		normalMap = (Map) s.readObject();
		reverseMap = (Map) s.readObject();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(normalMap);
		s.writeObject(reverseMap);
	}

	/**
	 * 双向表元素集
	 * @author Demon 2011-10-19
	 */
	protected class DualEntries extends AbstractSet<Entry<K, V>> {

		public void clear() {
			AbsDualMap.this.clear();
		}

		public boolean contains(Object o) {
			if (!(o instanceof Entry)) return false;
			Entry en = (Entry) o;
			V value = getValue(en.getKey());
			return value == null ? false : value.equals(en.getValue());
		}

		public Iterator<Entry<K, V>> iterator() {
			return new DualEntryIterator();
		}

		public boolean remove(Object o) {
			if (!(o instanceof Entry)) return false;
			Entry en = (Entry) o;
			return removeKey(en.getKey()) != null;
		}

		public int size() {
			return AbsDualMap.this.size();
		}
	}

	/**
	 * 双向表元素
	 * @author Demon 2011-10-19
	 */
	protected class DualEntry implements Entry<K, V> {

		private final Entry<K, V> target; // 目标元素

		protected DualEntry(Entry<K, V> en) {
			this.target = en;
		}

		public K getKey() {
			return AbsDualMap.this.getKey(target.getValue());
		}

		public V getValue() {
			return target.getValue();
		}

		public V setValue(V value) {
			if (value == null) throw new NullPointerException();
			return AbsDualMap.this.put(target.getKey(), value);
		}
	}

	/**
	 * 元素迭代器
	 * @author Demon 2011-10-19
	 */
	protected class DualEntryIterator extends DualIterator<Entry<K, V>> {
		public Entry<K, V> next() {
			return super.nextEntry();
		}
	}

	/**
	 * 双向表迭代器
	 * @param <E> 元素类型
	 * @author Demon 2011-10-19
	 */
	protected abstract class DualIterator<E> implements Iterator<E> {

		private final Iterator<Entry<K, V>> target;
		private Entry<K, V> last;

		protected DualIterator() {
			this.target = normalMap.entrySet().iterator();
		}

		public boolean hasNext() {
			return target.hasNext();
		}

		public void remove() {
			if (last == null) return;
			reverseMap.remove(last.getValue());
			target.remove();
		}

		protected Entry<K, V> nextEntry() {
			return new DualEntry(last = target.next());
		}
	}

	/**
	 * 键集合迭代器
	 * @author Demon 2011-10-19
	 */
	protected class DualKeyIterator extends DualIterator<K> {
		public K next() {
			return super.nextEntry().getKey();
		}
	}

	/**
	 * 双向表键集合
	 * @author Demon 2011-10-19
	 */
	protected class DualKeys extends AbstractSet<K> {

		public void clear() {
			AbsDualMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsKey(o);
		}

		public Iterator<K> iterator() {
			return new DualKeyIterator();
		}

		public boolean remove(Object o) {
			return removeKey(o) != null;
		}

		public int size() {
			return AbsDualMap.this.size();
		}
	}

	/**
	 * 值集合迭代器
	 * @author Demon 2011-10-19
	 */
	protected class DualValueIterator extends DualIterator<V> {
		public V next() {
			return super.nextEntry().getValue();
		}
	}

	/**
	 * 双向表值集合
	 * @author Demon 2011-10-19
	 */
	protected class DualValues extends AbstractSet<V> {

		public void clear() {
			AbsDualMap.this.clear();
		}

		public boolean contains(Object o) {
			return AbsDualMap.this.containsValue(o);
		}

		public Iterator<V> iterator() {
			return new DualValueIterator();
		}

		public boolean remove(Object o) {
			return removeValue(o) != null;
		}

		public int size() {
			return AbsDualMap.this.size();
		}
	}
}

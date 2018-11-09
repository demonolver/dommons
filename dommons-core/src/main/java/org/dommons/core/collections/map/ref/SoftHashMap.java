/*
 * @(#)SoftHashMap.java     2018-10-29
 */
package org.dommons.core.collections.map.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.dommons.core.util.Arrayard;

/**
 * 软引用哈希映射表
 * @author demon 2018-10-29
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> {

	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	private static final Object NULL_KEY = new Object();

	/**
	 * 执行匹配
	 * @param o1 对象1
	 * @param o2 对象2
	 * @return 是否相同
	 */
	static boolean eq(Object o1, Object o2) {
		return Arrayard.equals(o1, o2);
	}

	/**
	 * 计算哈希值
	 * @param h 原哈希值
	 * @return 哈希值
	 */
	static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * 计算哈希表序号
	 * @param h 哈希值
	 * @param len 哈希表数量
	 * @return 序号
	 */
	static int indexFor(int h, int len) {
		return h & (len - 1);
	}

	/**
	 * 伪装空键值
	 * @param key 键值
	 * @return 伪装后键值
	 */
	private static Object maskNull(Object key) {
		return (key == null ? NULL_KEY : key);
	}

	/**
	 * 揭露空键值伪装
	 * @param key 伪装后键值
	 * @return 真实键值
	 */
	private static <K> K unmaskNull(Object key) {
		return (K) (key == NULL_KEY ? null : key);
	}

	private SoftEntry[] table;
	private int size;
	private int threshold;
	private final float loadFactor;
	private final ReferenceQueue queue;

	private volatile int modCount;

	transient volatile Set<Map.Entry<K, V>> entrySet = null;
	transient volatile Set<K> keySet = null;
	transient volatile Collection<V> values = null;

	public SoftHashMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public SoftHashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public SoftHashMap(int initialCapacity, float loadFactor) {
		initialCapacity = Math.min(Math.max(1, initialCapacity), MAXIMUM_CAPACITY);
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) loadFactor = DEFAULT_LOAD_FACTOR;

		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		this.table = new SoftEntry[capacity];
		this.loadFactor = loadFactor;
		this.threshold = (int) (capacity * loadFactor);
		this.queue = new ReferenceQueue();
	}

	public SoftHashMap(Map<? extends K, ? extends V> m) {
		this(Math.max((int) ((m == null ? 0 : m.size()) / DEFAULT_LOAD_FACTOR) + 1, 16), DEFAULT_LOAD_FACTOR);
		if (m != null) putAll(m);
	}

	@Override
	public void clear() {
		while (queue.poll() != null);

		modCount++;
		Entry[] tab = table;
		for (int i = 0; i < tab.length; ++i)
			tab[i] = null;
		size = 0;

		while (queue.poll() != null);
	}

	@Override
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		SoftEntry[] tab = getTable();
		for (int i = tab.length; i-- > 0;)
			for (SoftEntry e = tab[i]; e != null; e = e.next)
				if (eq(value, e.getValue())) return true;
		return false;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<K, V>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	@Override
	public V get(Object key) {
		SoftEntry<K, V> e = getEntry(key);
		return e == null ? null : e.getValue();
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<K> keySet() {
		Set<K> ks = keySet;
		return (ks != null ? ks : (keySet = new KeySet()));
	}

	@Override
	public V put(K key, V value) {
		K k = (K) maskNull(key);
		int h = hash(k.hashCode());
		SoftEntry[] tab = getTable();
		int i = indexFor(h, tab.length);
		for (SoftEntry<K, V> e = tab[i]; e != null; e = e.next) {
			if (h == e.hash() && eq(k, e.key())) return e.setValue(value);
		}
		modCount++;
		SoftEntry<K, V> e = tab[i];
		tab[i] = new SoftEntry(k, value, queue, h, e);
		if (++size >= threshold) resize(tab.length * 2);
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m == null) return;
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0) return;

		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY) targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = table.length;
			while (newCapacity < targetCapacity)
				newCapacity <<= 1;
			if (newCapacity > table.length) resize(newCapacity);
		}

		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	@Override
	public V remove(Object key) {
		Object k = maskNull(key);
		int h = hash(k.hashCode());
		SoftEntry[] tab = getTable();
		int i = indexFor(h, tab.length);
		SoftEntry<K, V> prev = tab[i];
		SoftEntry<K, V> e = prev;

		while (e != null) {
			SoftEntry<K, V> next = e.next;
			if (h == e.hash() && eq(k, e.key())) {
				modCount++;
				size--;
				if (prev == e) tab[i] = next;
				else prev.next = next;
				return e.getValue();
			}
			prev = e;
			e = next;
		}

		return null;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<V> values() {
		Collection<V> vs = values;
		return (vs != null ? vs : (values = new Values()));
	}

	/**
	 * 获取元素项
	 * @param key 键值
	 * @return 元素项
	 */
	SoftEntry<K, V> getEntry(Object key) {
		Object k = maskNull(key);
		int h = hash(k.hashCode());
		SoftEntry[] tab = getTable();
		int index = indexFor(h, tab.length);
		SoftEntry<K, V> e = tab[index];
		while (e != null && !(e.hash() == h && eq(k, e.key())))
			e = e.next;
		return e;
	}

	Entry<K, V> removeMapping(Object o) {
		if (o == null || !(o instanceof Map.Entry)) return null;
		SoftEntry[] tab = getTable();
		Map.Entry entry = (Map.Entry) o;
		Object k = maskNull(entry.getKey());
		int h = hash(k.hashCode());
		int i = indexFor(h, tab.length);
		SoftEntry<K, V> prev = tab[i];
		SoftEntry<K, V> e = prev;

		while (e != null) {
			SoftEntry<K, V> next = e.next;
			if (h == e.hash() && e.equals(entry)) {
				modCount++;
				size--;
				if (prev == e) tab[i] = next;
				else prev.next = next;
				return e;
			}
			prev = e;
			e = next;
		}

		return null;
	}

	/**
	 * 调整哈希表
	 * @param newCapacity 新哈希表大小
	 */
	void resize(int newCapacity) {
		SoftEntry[] oldTable = getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		SoftEntry[] newTable = new SoftEntry[newCapacity];
		transfer(oldTable, newTable);
		table = newTable;

		if (size >= threshold / 2) {
			threshold = (int) (newCapacity * loadFactor);
		} else {
			expungeStaleEntries();
			transfer(newTable, oldTable);
			table = oldTable;
		}
	}

	/**
	 * 抹去已释放元素集
	 */
	private void expungeStaleEntries() {
		SoftElement<K, V> e;
		while ((e = (SoftElement<K, V>) queue.poll()) != null) {
			int h = e.hash;
			int i = indexFor(h, table.length);

			SoftEntry<K, V> prev = table[i];
			SoftEntry<K, V> p = prev;
			while (p != null) {
				SoftEntry<K, V> next = p.next;
				if (p.match(e)) {
					if (prev == p) table[i] = next;
					else prev.next = next;
					p.next = null; // Help GC
					size--;
					break;
				}
				prev = p;
				p = next;
			}
		}
	}

	/**
	 * 获取哈希表
	 * @return 哈希表
	 */
	private SoftEntry[] getTable() {
		expungeStaleEntries();
		return table;
	}

	/**
	 * 迁移哈希表
	 * @param src 源哈希表
	 * @param dest 目标哈希表
	 */
	private void transfer(SoftEntry[] src, SoftEntry[] dest) {
		for (int j = 0; j < src.length; ++j) {
			SoftEntry<K, V> e = src[j];
			src[j] = null;
			while (e != null) {
				SoftEntry<K, V> next = e.next;
				Object key = e.key();
				if (key == null) {
					e.next = null; // Help GC
					size--;
				} else {
					int i = indexFor(e.hash(), dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}

	/**
	 * 元素迭代器
	 * @author demon 2018-10-29
	 */
	private class EntryIterator extends SoftIterator<Map.Entry<K, V>> {
		public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	/**
	 * 元素项集
	 * @author demon 2018-10-29
	 */
	private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		public void clear() {
			SoftHashMap.this.clear();
		}

		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry e = (Map.Entry) o;
			Entry candidate = getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		public boolean remove(Object o) {
			return removeMapping(o) != null;
		}

		public int size() {
			return SoftHashMap.this.size();
		}

		public Object[] toArray() {
			return deepCopy().toArray();
		}

		public <T> T[] toArray(T[] a) {
			return deepCopy().toArray(a);
		}

		/**
		 * 深度复制
		 * @return 元素列表
		 */
		private List<Map.Entry<K, V>> deepCopy() {
			List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(size());
			for (Map.Entry<K, V> e : this)
				list.add(new AbstractMap.SimpleEntry<K, V>(e));
			return list;
		}
	}

	/**
	 * 软引用键迭代器
	 * @author demon 2018-10-29
	 */
	private class KeyIterator extends SoftIterator<K> {
		public K next() {
			return nextEntry().getKey();
		}
	}

	/**
	 * 软引用键集
	 * @author demon 2018-10-29
	 */
	private class KeySet extends AbstractSet<K> {
		public void clear() {
			SoftHashMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsKey(o);
		}

		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		public boolean remove(Object o) {
			if (containsKey(o)) {
				SoftHashMap.this.remove(o);
				return true;
			} else return false;
		}

		public int size() {
			return SoftHashMap.this.size();
		}
	}

	/**
	 * 软引用元素值
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2018-10-30
	 */
	private static class SoftElement<K, V> extends SoftReference<Object[]> {

		private final int hash;

		public SoftElement(K key, V value, ReferenceQueue queue, int hash) {
			super(new Object[] { key, value }, queue);
			this.hash = hash;
		}

		/**
		 * 获取键名
		 * @return 键名
		 */
		public K key() {
			return key(get());
		}

		/**
		 * 获取值
		 * @return 值
		 */
		public V value() {
			return value(get());
		}

		/**
		 * 提取键名
		 * @param o 引用项
		 * @return 键名
		 */
		protected K key(Object[] o) {
			return (K) Arrayard.get(o, 0);
		}

		/**
		 * 提取值
		 * @param o 引用项
		 * @return 值
		 */
		protected V value(Object[] o) {
			return (V) Arrayard.get(o, 1);
		}
	}

	/**
	 * 软引用元素项
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2018-10-29
	 */
	private static class SoftEntry<K, V> implements Map.Entry<K, V> {
		private final ReferenceQueue queue;
		private volatile SoftElement<K, V> ele;
		private SoftEntry<K, V> next;

		public SoftEntry(K key, V value, ReferenceQueue queue, int hash, SoftEntry<K, V> next) {
			this.queue = queue;
			this.ele = new SoftElement(key, value, queue, hash);
			this.next = next;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey(), k2 = e.getKey();
			if (eq(k1, k2)) {
				Object v1 = getValue(), v2 = e.getValue();
				if (eq(v1, v2)) return true;
			}
			return false;
		}

		@Override
		public K getKey() {
			return SoftHashMap.unmaskNull(key());
		}

		@Override
		public V getValue() {
			return ele.value();
		}

		public int hashCode() {
			Object[] o = ele.get();
			Object k = ele.key(o);
			Object v = ele.value(o);
			return ((k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode()));
		}

		/**
		 * 获取键名
		 * @return 键名
		 */
		public K key() {
			return ele.key();
		}

		@Override
		public V setValue(V newValue) {
			Object[] o = ele.get();
			if (o == null) return null;
			V oldValue = ele.value(o);
			if (!eq(oldValue, newValue)) ele = new SoftElement(ele.key(o), newValue, queue, ele.hash);
			return oldValue;
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}

		/**
		 * 获取哈希值
		 * @return 哈希值
		 */
		protected int hash() {
			return ele.hash;
		}

		/**
		 * 匹配软引用值
		 * @param te 目标软引用值
		 * @return 是、否
		 */
		private boolean match(SoftElement<K, V> te) {
			return ele == te;
		}
	}

	/**
	 * 迭代器
	 * @param <T> 迭代目标类型
	 * @author demon 2018-10-29
	 */
	private abstract class SoftIterator<T> implements Iterator<T> {

		int index;
		SoftEntry<K, V> entry = null;
		SoftEntry<K, V> lastReturned = null;
		int expectedModCount;

		Object nextKey = null;
		Object currentKey = null;

		SoftIterator() {
			index = (size() != 0 ? table.length : 0);
			expectedModCount = modCount;
		}

		public boolean hasNext() {
			SoftEntry[] t = table;

			while (nextKey == null) {
				SoftEntry<K, V> e = entry;
				int i = index;
				while (e == null && i > 0)
					e = t[--i];
				entry = e;
				index = i;
				if (e == null) {
					currentKey = null;
					return false;
				}
				nextKey = e.key(); // hold on to key in strong ref
				if (nextKey == null) entry = entry.next;
			}
			return true;
		}

		public void remove() {
			if (lastReturned == null) throw new IllegalStateException();
			if (modCount != expectedModCount) throw new ConcurrentModificationException();

			SoftHashMap.this.remove(currentKey);
			expectedModCount = modCount;
			lastReturned = null;
			currentKey = null;
		}

		/**
		 * 下个元素项
		 * @return 元素项
		 */
		protected SoftEntry<K, V> nextEntry() {
			if (modCount != expectedModCount) throw new ConcurrentModificationException();
			if (nextKey == null && !hasNext()) throw new NoSuchElementException();

			lastReturned = entry;
			entry = entry.next;
			currentKey = nextKey;
			nextKey = null;
			return lastReturned;
		}
	}

	/**
	 * 软引用值迭代器
	 * @author demon 2018-10-29
	 */
	private class ValueIterator extends SoftIterator<V> {
		public V next() {
			return nextEntry().getValue();
		}
	}

	/**
	 * 软引用值集
	 * @author demon 2018-10-29
	 */
	private class Values extends AbstractCollection<V> {
		public void clear() {
			SoftHashMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		public int size() {
			return SoftHashMap.this.size();
		}
	}
}

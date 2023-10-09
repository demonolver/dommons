/*
 * @(#)ReferenceHashMap.java     2023-10-09
 */
package org.dommons.core.collections.map.ref;

import java.lang.ref.ReferenceQueue;
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
import java.util.concurrent.locks.Lock;

import org.dommons.core.util.Arrayard;

/**
 * 引用绑定哈希映射表
 * @author demon 2023-10-09
 */
public abstract class ReferenceHashMap<K, V> extends AbstractMap<K, V> {

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

	private ReferenceEntry[] table;
	private int size;
	private int threshold;
	private final float loadFactor;
	private final ReferenceQueue queue;

	private volatile int modCount;

	transient volatile Set<Map.Entry<K, V>> entrySet = null;
	transient volatile Set<K> keySet = null;
	transient volatile Collection<V> values = null;

	public ReferenceHashMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	public ReferenceHashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public ReferenceHashMap(int initialCapacity, float loadFactor) {
		initialCapacity = Math.min(Math.max(1, initialCapacity), MAXIMUM_CAPACITY);
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) loadFactor = DEFAULT_LOAD_FACTOR;

		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		this.table = new ReferenceEntry[capacity];
		this.loadFactor = loadFactor;
		this.threshold = (int) (capacity * loadFactor);
		this.queue = new ReferenceQueue();
	}

	public ReferenceHashMap(Map<? extends K, ? extends V> m) {
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
		ReferenceEntry[] tab = getTable();
		for (int i = tab.length; i-- > 0;)
			for (ReferenceEntry e = tab[i]; e != null; e = e.next)
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
		ReferenceEntry<K, V> e = getEntry(key);
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
		ReferenceEntry[] tab = getTable();
		int i = indexFor(h, tab.length);
		for (ReferenceEntry<K, V> e = tab[i]; e != null; e = e.next) {
			if (h == e.hash() && eq(k, e.key())) return e.setValue(value);
		}
		modCount++;
		ReferenceEntry<K, V> e = tab[i];
		tab[i] = createEntry(k, value, queue, h, e);
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
		ReferenceEntry[] tab = getTable();
		int i = indexFor(h, tab.length);
		ReferenceEntry<K, V> prev = tab[i];
		ReferenceEntry<K, V> e = prev;

		while (e != null) {
			ReferenceEntry<K, V> next = e.next;
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
	 * 构建引用元素项
	 * @param key 键名
	 * @param value 值
	 * @param queue 引用释放队列
	 * @param hash 哈希值
	 * @param next 下一元素项
	 * @return 引用元素项
	 */
	protected abstract ReferenceEntry<K, V> createEntry(K key, V value, ReferenceQueue queue, int hash, ReferenceEntry<K, V> next);

	/**
	 * 获取释放元素抹去锁
	 * @return 并发锁
	 */
	protected Lock expungeLock() {
		return null;
	}

	/**
	 * 是否检查需抹去的元素
	 * @return 是、否
	 */
	protected boolean needExpungeStale() {
		return true;
	}

	/**
	 * 获取元素项
	 * @param key 键值
	 * @return 元素项
	 */
	ReferenceEntry<K, V> getEntry(Object key) {
		Object k = maskNull(key);
		int h = hash(k.hashCode());
		ReferenceEntry[] tab = getTable();
		int index = indexFor(h, tab.length);
		ReferenceEntry<K, V> e = tab[index];
		while (e != null && !(e.hash() == h && eq(k, e.key())))
			e = e.next;
		return e;
	}

	Entry<K, V> removeMapping(Object o) {
		if (o == null || !(o instanceof Map.Entry)) return null;
		ReferenceEntry[] tab = getTable();
		Map.Entry entry = (Map.Entry) o;
		Object k = maskNull(entry.getKey());
		int h = hash(k.hashCode());
		int i = indexFor(h, tab.length);
		ReferenceEntry<K, V> prev = tab[i];
		ReferenceEntry<K, V> e = prev;

		while (e != null) {
			ReferenceEntry<K, V> next = e.next;
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
		ReferenceEntry[] oldTable = getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		ReferenceEntry[] newTable = new ReferenceEntry[newCapacity];
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
		if (!needExpungeStale()) return;
		for (ReferenceElement<K, V> e; (e = (ReferenceElement<K, V>) queue.poll()) != null;) {
			Lock lock = expungeLock();
			if (lock != null) lock.lock();
			try {
				int h = e.hash();
				int i = indexFor(h, table.length);

				ReferenceEntry<K, V> prev = table[i];
				ReferenceEntry<K, V> p = prev;
				while (p != null) {
					ReferenceEntry<K, V> next = p.next;
					if (p.match(e) || p.isEvicted()) {
						if (prev == p) table[i] = next;
						else prev.next = next;
						p.next = null; // Help GC
						size--;
						break;
					}
					prev = p;
					p = next;
				}
			} finally {
				if (lock != null) lock.unlock();
			}
		}
	}

	/**
	 * 获取哈希表
	 * @return 哈希表
	 */
	private ReferenceEntry[] getTable() {
		expungeStaleEntries();
		return table;
	}

	/**
	 * 迁移哈希表
	 * @param src 源哈希表
	 * @param dest 目标哈希表
	 */
	private void transfer(ReferenceEntry[] src, ReferenceEntry[] dest) {
		for (int j = 0; j < src.length; ++j) {
			ReferenceEntry<K, V> e = src[j];
			src[j] = null;
			while (e != null) {
				ReferenceEntry<K, V> next = e.next;
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
	 * 引用元素项实体
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2023-10-09
	 */
	protected interface ReferenceElement<K, V> {
		/**
		 * 获取引用项实体
		 * @return 引用项实体
		 */
		Object[] get();

		/**
		 * 获取哈希值
		 * @return 哈希值
		 */
		int hash();

		/**
		 * 获取键名
		 * @return 键名
		 */
		K key();

		/**
		 * 提取键名
		 * @param o 引用项
		 * @return 键名
		 */
		K key(Object[] o);

		/**
		 * 获取值
		 * @return 值
		 */
		V value();

		/**
		 * 提取值
		 * @param o 引用项
		 * @return 值
		 */
		V value(Object[] o);
	}

	/**
	 * 引用元素项
	 * @param <K> 键类型
	 * @param <V> 值类型
	 * @author demon 2018-10-29
	 */
	protected static abstract class ReferenceEntry<K, V> implements Map.Entry<K, V> {
		private final ReferenceQueue queue;
		private volatile ReferenceElement<K, V> ele;
		private ReferenceEntry<K, V> next;

		public ReferenceEntry(K key, V value, ReferenceQueue queue, int hash, ReferenceEntry<K, V> next) {
			this.queue = queue;
			this.ele = create(key, value, queue, hash);
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
			return ReferenceHashMap.unmaskNull(key());
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
			if (!eq(oldValue, newValue)) ele = create(ele.key(o), newValue, queue, ele.hash());
			return oldValue;
		}

		public String toString() {
			return getKey() + "=" + getValue();
		}

		/**
		 * 构建引用元素
		 * @param key 键名
		 * @param value 值
		 * @param queue 引用释放队列
		 * @param hash 哈希值
		 * @return 引用元素
		 */
		protected abstract ReferenceElement<K, V> create(K key, V value, ReferenceQueue queue, int hash);

		/**
		 * 获取哈希值
		 * @return 哈希值
		 */
		protected int hash() {
			return ele.hash();
		}

		/**
		 * 是否被驱逐
		 * @return 是、否
		 */
		private boolean isEvicted() {
			return ele.get() == null;
		}

		/**
		 * 匹配引用值
		 * @param te 目标引用值
		 * @return 是、否
		 */
		private boolean match(ReferenceElement<K, V> te) {
			return ele == te;
		}
	}

	/**
	 * 抽象引用迭代器
	 * @param <T> 迭代目标类型
	 * @author demon 2018-10-29
	 */
	private abstract class AbstractReferenceIterator<T> implements Iterator<T> {

		int index;
		ReferenceEntry<K, V> entry = null;
		ReferenceEntry<K, V> lastReturned = null;
		int expectedModCount;

		Object nextKey = null;
		Object currentKey = null;

		AbstractReferenceIterator() {
			index = (size() != 0 ? table.length : 0);
			expectedModCount = modCount;
		}

		public boolean hasNext() {
			ReferenceEntry[] t = table;

			while (nextKey == null) {
				ReferenceEntry<K, V> e = entry;
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

			ReferenceHashMap.this.remove(currentKey);
			expectedModCount = modCount;
			lastReturned = null;
			currentKey = null;
		}

		/**
		 * 下个元素项
		 * @return 元素项
		 */
		protected ReferenceEntry<K, V> nextEntry() {
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
	 * 元素迭代器
	 * @author demon 2018-10-29
	 */
	private class EntryIterator extends AbstractReferenceIterator<Map.Entry<K, V>> {
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
			ReferenceHashMap.this.clear();
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
			return ReferenceHashMap.this.size();
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
				list.add(new AbstractMap.SimpleEntry(e));
			return list;
		}
	}

	/**
	 * 引用键迭代器
	 * @author demon 2018-10-29
	 */
	private class KeyIterator extends AbstractReferenceIterator<K> {
		public K next() {
			return nextEntry().getKey();
		}
	}

	/**
	 * 引用键集
	 * @author demon 2018-10-29
	 */
	private class KeySet extends AbstractSet<K> {
		public void clear() {
			ReferenceHashMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsKey(o);
		}

		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		public boolean remove(Object o) {
			if (containsKey(o)) {
				ReferenceHashMap.this.remove(o);
				return true;
			} else return false;
		}

		public int size() {
			return ReferenceHashMap.this.size();
		}
	}

	/**
	 * 引用值迭代器
	 * @author demon 2018-10-29
	 */
	private class ValueIterator extends AbstractReferenceIterator<V> {
		public V next() {
			return nextEntry().getValue();
		}
	}

	/**
	 * 引用值集
	 * @author demon 2018-10-29
	 */
	private class Values extends AbstractCollection<V> {
		public void clear() {
			ReferenceHashMap.this.clear();
		}

		public boolean contains(Object o) {
			return containsValue(o);
		}

		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		public int size() {
			return ReferenceHashMap.this.size();
		}
	}
}
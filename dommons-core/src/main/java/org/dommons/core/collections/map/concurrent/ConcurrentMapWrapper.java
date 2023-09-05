/*
 * @(#)ConcurrentMapWrapper.java     2018-08-01
 */
package org.dommons.core.collections.map.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dommons.core.collections.collection.concurrent.ConcurrentCollectionWrapper;
import org.dommons.core.collections.map.AbsMapWrapper;
import org.dommons.core.collections.set.concurrent.ConcurrentSetWrapper;
import org.dommons.core.util.Arrayard;

/**
 * 线程安全映射表包装
 * @author demon 2018-08-01
 */
public class ConcurrentMapWrapper<K, V> extends AbsMapWrapper<K, V> implements ConcurrentMap<K, V>, Serializable {

	private static final long serialVersionUID = 4705262725651297800L;

	protected transient ReadWriteLock lock;

	transient SubWrapCreator<K, Set<K>> keySet;
	transient SubWrapCreator<V, Collection<V>> values;
	transient SubWrapCreator<Map.Entry<K, V>, Set<Map.Entry<K, V>>> entrySet;

	public ConcurrentMapWrapper(Map<K, V> map) {
		super(map);
		this.lock = new ReentrantReadWriteLock();
	}

	public void clear() {
		Lock l = writeLock();
		l.lock();
		try {
			super.clear();
		} finally {
			l.unlock();
		}
	}

	public boolean containsKey(Object key) {
		Lock l = readLock();
		l.lock();
		try {
			return super.containsKey(key);
		} finally {
			l.unlock();
		}
	}

	public boolean containsValue(Object value) {
		Lock l = readLock();
		l.lock();
		try {
			return super.containsValue(value);
		} finally {
			l.unlock();
		}
	}

	public Set<Entry<K, V>> entrySet() {
		SubWrapCreator<Map.Entry<K, V>, Set<Map.Entry<K, V>>> sc = entrySet;
		if (sc == null) {
			entrySet = (sc = new SubWrapCreator<Map.Entry<K, V>, Set<Entry<K, V>>>() {
				@Override
				protected Set<Entry<K, V>> sub() {
					return ConcurrentMapWrapper.super.entrySet();
				}

				@Override
				protected Set<Entry<K, V>> wrap(Set<Entry<K, V>> sub) {
					return new ConcurrentSetWrapper<Map.Entry<K, V>>(sub, lock);
				}
			});
		}
		return sc.wrap();
	}

	public V get(Object key) {
		Lock l = readLock();
		l.lock();
		try {
			return super.get(key);
		} finally {
			l.unlock();
		}
	}

	public boolean isEmpty() {
		Lock l = readLock();
		l.lock();
		try {
			return super.isEmpty();
		} finally {
			l.unlock();
		}
	}

	public Set<K> keySet() {
		SubWrapCreator<K, Set<K>> sc = keySet;
		if (sc == null) {
			keySet = (sc = new SubWrapCreator<K, Set<K>>() {
				@Override
				protected Set<K> sub() {
					return ConcurrentMapWrapper.super.keySet();
				}

				@Override
				protected Set<K> wrap(Set<K> sub) {
					return new ConcurrentSetWrapper(sub, lock);
				}
			});
		}
		return sc.wrap();
	}

	public V put(K key, V value) {
		Lock l = writeLock();
		l.lock();
		try {
			return super.put(key, value);
		} finally {
			l.unlock();
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		Lock l = writeLock();
		l.lock();
		try {
			super.putAll(m);
		} finally {
			l.unlock();
		}
	}

	public V putIfAbsent(K key, V value) {
		Lock l = writeLock();
		l.lock();
		try {
			V v = super.get(key);
			if (v == null) super.put(key, value);
			return v;
		} finally {
			l.unlock();
		}
	}

	public V remove(Object key) {
		Lock l = writeLock();
		l.lock();
		try {
			return super.remove(key);
		} finally {
			l.unlock();
		}
	}

	public boolean remove(Object key, Object value) {
		if (value == null) return false;
		Lock l = writeLock();
		l.lock();
		try {
			V v = super.get(key);
			if (Arrayard.equals(v, value)) return super.remove(key) != null;
			return false;
		} finally {
			l.unlock();
		}
	}

	public V replace(K key, V value) {
		Lock l = writeLock();
		l.lock();
		try {
			if (!super.containsKey(key)) return null;
			return super.put(key, value);
		} finally {
			l.unlock();
		}
	}

	public boolean replace(K key, V oldValue, V newValue) {
		if (oldValue == null || newValue == null) return false;
		Lock l = writeLock();
		l.lock();
		try {
			V v = super.get(key);
			if (!Arrayard.equals(v, oldValue)) return false;
			super.put(key, newValue);
			return true;
		} finally {
			l.unlock();
		}
	}

	public int size() {
		Lock l = readLock();
		l.lock();
		try {
			return super.size();
		} finally {
			l.unlock();
		}
	}

	public Collection<V> values() {
		SubWrapCreator<V, Collection<V>> sc = values;
		if (sc == null) {
			values = (sc = new SubWrapCreator<V, Collection<V>>() {
				@Override
				protected Collection<V> sub() {
					return ConcurrentMapWrapper.super.values();
				}

				@Override
				protected Collection<V> wrap(Collection<V> sub) {
					return new ConcurrentCollectionWrapper(sub, lock);
				}
			});
		}
		return sc.wrap();
	}

	/**
	 * 获取读取锁
	 * @return 读取锁
	 */
	protected Lock readLock() {
		return lock.readLock();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	protected void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		super.readObject(s);
		lock = (ReadWriteLock) s.readObject();
	}

	/**
	 * 获取写入锁
	 * @return 写入锁
	 */
	protected Lock writeLock() {
		return lock.writeLock();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	protected void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		super.writeObject(s);
		s.writeObject(lock);
	}

	/**
	 * 子包装构建器
	 * @param <E> 元素类型
	 * @param <T> 子集类型
	 * @author demon 2018-10-29
	 */
	protected abstract class SubWrapCreator<E, T extends Collection<E>> {

		private transient T last;
		private transient T wrap;

		public T wrap() {
			T sub = sub();
			wrap: {
				if (wrap != null && sub == last) break wrap;
				wrap = wrap(sub);
				last = sub;
			}
			return wrap;
		}

		protected abstract T sub();

		protected abstract T wrap(T sub);
	}
}

/*
 * @(#)AbsCaseInsensitiveMap.java     2011-10-18
 */
package org.dommons.core.collections.map.ci;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 忽略键值大小写的映射表包装 支持多个大小写不同的键并存，查找时无视大小写，查找最相似的
 * @author Demon 2011-10-18
 */
public abstract class AbsCaseInsensitiveMap<V> extends AbstractMap<String, V> implements CaseInsensitiveMap<V>, Serializable {

	private static final long serialVersionUID = -4208450415202237192L;

	/**
	 * 克隆映射表
	 * @param map 原map对象
	 * @return 克隆对象 原对象为<code>null</code>则返回<code>null</code>
	 * @throws CloneNotSupportedException 不支持克隆
	 */
	protected static Map cloneMap(Map map) throws CloneNotSupportedException {
		if (map == null) return null;
		Map target = null;
		// 支持克隆，调用克隆方法
		if (map instanceof Cloneable) {
			try {
				Method method = null;
				try {
					method = map.getClass().getMethod("clone");
				} catch (NoSuchMethodException e) {
					method = map.getClass().getDeclaredMethod("clone");
					method.setAccessible(true);
				}
				target = (Map) method.invoke(map);
			} catch (Throwable e) {
				// ignore
			}
		}

		// 重新实例化并导入数据
		if (target == null) {
			try {
				target = map.getClass().newInstance();
				target.putAll(map);
			} catch (Exception e) {
				// ignore
			}
		}

		// 无法克隆抛出异常
		if (target == null) throw new CloneNotSupportedException();
		return target;
	}

	/**
	 * 比较键值
	 * @param source 源键值
	 * @param target 目标键值
	 * @return 结果差距
	 */
	static int compareKeys(String source, String target) {
		int compare = 0;
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char ch1 = source.charAt(i);
			char ch2 = target.charAt(i);
			if (ch1 == ch2) continue;
			compare = (compare * len) + i + 1;
		}
		return compare;
	}

	/**
	 * 查找键值
	 * @param list 键集合
	 * @param key 键值
	 * @return 真实键值
	 */
	static String find(Collection<String> list, String key) {
		String res = null;
		if (list.contains(key)) return key;
		int compare = 0;
		for (String value : list) {
			int tmp = compareKeys(key, value);
			if (compare == 0 || tmp < compare) {
				compare = tmp;
				res = value;
			}
		}
		return res;
	}

	private final boolean caseInsensitive;
	private transient Map<String, Collection<String>> index;
	private transient Map<String, V> target;

	/**
	 * 构造函数
	 * @param map 目标映射表
	 * @param keyIndex 键索引
	 * @param caseInsensitive 是否默认写入无视大小写
	 */
	public AbsCaseInsensitiveMap(Map<String, V> map, Map<String, Collection<String>> keyIndex, boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		this.target = map;
		if (keyIndex == null) {
			try {
				keyIndex = cloneMap(map);
				keyIndex.clear();
			} catch (CloneNotSupportedException e) {
				keyIndex = new HashMap();
			}
		}
		this.index = keyIndex;
		init();
	}

	public void clear() {
		target.clear();
		synchronized (index) {
			index.clear();
		}
	}

	public boolean containsKey(Object key) {
		Object k = findKey(key);
		return target.containsKey(k == null ? key : k);
	}

	public boolean defaultWithCaseInsensitive() {
		return caseInsensitive;
	}

	public Set<Entry<String, V>> entrySet() {
		return target.entrySet();
	}

	public V get(Object key) {
		Object k = findKey(key);
		return target.get(k == null ? key : k);
	}

	public String getCaseInsensitivekey(String key) {
		String fKey = findKey(key);
		return fKey == null ? key : fKey;
	}

	public int hashCode() {
		return target.hashCode();
	}

	public Set<String> keySet() {
		return target.keySet();
	}

	public V put(String key, V value) {
		return defaultWithCaseInsensitive() ? putWithCaseInsensitive(key, value) : putNoCaseInsensitive(key, value);
	}

	public V putNoCaseInsensitive(String key, V value) {
		importKey(key);
		return target.put(key, value);
	}

	public V putWithCaseInsensitive(String key, V value) {
		return putNoCaseInsensitive(getCaseInsensitivekey(key), value);
	}

	public V remove(Object key) {
		Object o = convertKey(key);
		Collection<String> keys = index.get(o);
		if (keys == null) return null;
		synchronized (keys) {
			key = find(keys, (String) key);
			if (key != null) keys.remove(key);
			if (keys.isEmpty()) {
				synchronized (index) {
					index.remove(o);
				}
			}
		}
		return target.remove(key);
	}

	public boolean removeAll(Object key) {
		Object o = convertKey(key);
		Collection<String> keys = null;
		synchronized (index) {
			keys = index.remove(o);
		}
		if (keys == null) return false;
		boolean modified = false;
		int old = target.size();
		for (String k : keys) {
			modified = target.remove(k) != null;
		}
		return modified || old > target.size();
	}

	public String toString() {
		return target.toString();
	}

	public Collection<V> values() {
		return target.values();
	}

	/**
	 * 键值数据转换
	 * @param <T> 类型
	 * @param key 键值数据
	 * @return 转换后值
	 */
	protected <T> T convertKey(T key) {
		if (key == null) return null;
		if (key instanceof String) {
			return (T) ((String) key).toUpperCase();
		} else {
			return key;
		}
	}

	/**
	 * 查找真实键
	 * @param key 键数据
	 * @return 真实键 不存在返回<code>null</code>
	 */
	protected <K> K findKey(K key) {
		Object c = convertKey(key);
		if (c == null) return null;
		Collection<String> list = index.get(c);
		if (list != null && key instanceof String) {
			synchronized (list) {
				return (K) find(list, (String) key);
			}
		}
		return null;
	}

	/**
	 * 添加映射表键
	 * @param key 键数据
	 */
	protected void importKey(String key) {
		String c = convertKey(key);
		if (c == null) return;
		Collection<String> keys = this.index.get(c);
		if (keys == null) {
			synchronized (this.index) {
				keys = this.index.get(c);
				if (keys == null) {
					keys = new HashSet();
					this.index.put(c, keys);
				}
			}
		}
		synchronized (keys) {
			keys.add(key);
		}
	}

	/**
	 * 初始化 转换原有键值
	 */
	private void init() {
		index.clear();
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		target = (Map) s.readObject();
		index = (Map) s.readObject();
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(target);
		s.writeObject(index);
	}
}

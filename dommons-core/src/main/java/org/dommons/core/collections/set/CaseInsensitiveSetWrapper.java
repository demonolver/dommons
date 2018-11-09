/*
 * @(#)CaseInsensitiveSetWrapper.java     2012-3-19
 */
package org.dommons.core.collections.set;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 忽略大小写无重复数据集包装
 * @author Demon 2012-3-19
 */
class CaseInsensitiveSetWrapper extends AbstractCollection<String> implements Set<String>, Serializable {

	private static final long serialVersionUID = -3523834107274246343L;

	protected transient Set<String> tar;
	private transient Map<String, String> index;
	protected final boolean syn;

	protected CaseInsensitiveSetWrapper(Set<String> set, boolean syn) {
		this.tar = set;
		this.index = new HashMap();
		this.syn = syn;
	}

	public boolean add(String e) {
		if (syn) {
			synchronized (index) {
				return addElement(e);
			}
		} else {
			return addElement(e);
		}
	}

	public boolean addAll(Collection<? extends String> c) {
		if (c == null) return false;
		boolean mod = false;
		if (syn) {
			synchronized (index) {
				for (Iterator<? extends String> it = c.iterator(); it.hasNext();) {
					if (addElement(it.next())) mod = true;
				}
			}
		} else {
			for (Iterator<? extends String> it = c.iterator(); it.hasNext();) {
				if (addElement(it.next())) mod = true;
			}
		}
		return mod;
	}

	public void clear() {
		if (syn) {
			synchronized (index) {
				tar.clear();
				index.clear();
			}
		} else {
			tar.clear();
			index.clear();
		}
	}

	public boolean contains(Object o) {
		Object k = convertKey(o);
		String t = index.get(k);

		if (syn) {
			synchronized (index) {
				if (t == null) t = index.get(k);
				return tar.contains(t == null ? o : t);
			}
		} else {
			return tar.contains(t == null ? o : t);
		}
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CaseInsensitiveSetWrapper)) {
			return false;
		} else if (this == obj) {
			return true;
		}
		CaseInsensitiveSetWrapper w = (CaseInsensitiveSetWrapper) obj;
		return tar.equals(w.tar);
	}

	public int hashCode() {
		return tar.hashCode();
	}

	public boolean isEmpty() {
		return tar.isEmpty();
	}

	public Iterator<String> iterator() {
		return new CaseInsensitiveSetIterator(tar.iterator());
	}

	public boolean remove(Object o) {
		boolean r = removeElement(o);
		if (!r && syn) {
			synchronized (o) {
				r = removeElement(o);
			}
		}
		return r;
	}

	public boolean removeAll(Collection<?> c) {
		if (c == null) return false;
		boolean mod = false;
		if (syn) {
			synchronized (index) {
				for (Iterator it = c.iterator(); it.hasNext();) {
					if (removeElement(it.next())) mod = true;
				}
			}
		} else {
			for (Iterator it = c.iterator(); it.hasNext();) {
				if (removeElement(it.next())) mod = true;
			}
		}
		return mod;
	}

	public int size() {
		return tar.size();
	}

	public Object[] toArray() {
		return tar.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return tar.toArray(a);
	}

	public String toString() {
		return tar.toString();
	}

	/**
	 * 添加元素
	 * @param s 元素值
	 * @return 是否添加成功
	 */
	protected boolean addElement(String s) {
		String key = convertKey(s);
		if (index.containsKey(key)) return false;
		index.put(key, s);
		return tar.add(s);
	}

	/**
	 * 键值数据转换
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
	 * 移除元素
	 * @param o 元素值
	 * @return 是否移除
	 */
	protected boolean removeElement(Object o) {
		String t = index.remove(convertKey(o));
		return t == null ? tar.remove(o) : tar.remove(t);
	}

	/**
	 * 序列化读取
	 * @param s 序列化输入流
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		tar = (Set<String>) s.readObject();

		index = new HashMap();
		for (Iterator<String> it = tar.iterator(); it.hasNext();) {
			String e = it.next();
			index.put(convertKey(e), e);
		}
	}

	/**
	 * 序列化写入
	 * @param s 序列化输出流
	 * @throws java.io.IOException
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(tar);
	}

	/**
	 * 忽略大小写无重复数据集迭代器
	 * @author Demon 2012-3-19
	 */
	protected class CaseInsensitiveSetIterator implements Iterator<String> {

		private final Iterator<String> it;
		private String last;

		protected CaseInsensitiveSetIterator(Iterator<String> it) {
			this.it = it;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public String next() {
			return last = it.next();
		}

		public void remove() {
			if (syn) {
				synchronized (index) {
					it.remove();
					index.remove(last);
				}
			} else {
				it.remove();
				index.remove(last);
			}
		}
	}
}

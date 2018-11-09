/*
 * @(#)Beandata.java     2017-07-24
 */
package org.dommons.core.util.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.convert.Converter;

/**
 * 对象数据
 * @author demon 2017-07-24
 */
public class Beandata extends AbstractMap<String, Object> implements Serializable {

	private static final long serialVersionUID = 3232796266993829116L;

	/**
	 * 转换数据对象
	 * @param obj 目标对象
	 * @return 对象数据
	 */
	public static Beandata cast(Object obj) {
		if (obj == null) return null;
		else if (obj instanceof Beandata) return (Beandata) obj;
		else return new Beandata(obj);
	}

	private transient Object entity;
	private transient Map<String, Object> index;

	private transient Map<String, BeanEntry> ems;

	protected Beandata(Object entity) {
		entity(entity);
	}

	public void clear() {}

	public boolean containsKey(Object key) {
		return index.containsKey(key);
	}

	/**
	 * 创建新数据对象
	 * @param type 目标类型
	 * @return 数据对象
	 */
	public <O> O create(Class<O> type) {
		return BeanProperties.newInstance(type, entity);
	}

	/**
	 * 获取数据对象实体
	 * @return 数据对象
	 */
	public <O> O entity() {
		return (O) entity;
	}

	public Set<Entry<String, Object>> entrySet() {
		return new Entries();
	}

	public Object get(Object property) {
		Object x = index.get(property);
		if (x != null && x instanceof BeanProperty && !(entity instanceof Map)) {
			BeanProperty p = (BeanProperty) x;
			return p.get(entity);
		} else {
			return x;
		}
	}

	/**
	 * 获取属性值
	 * @param property 属性名
	 * @param type 值类型
	 * @return 属性值
	 */
	public <R> R get(String property, Class<R> type) {
		return Converter.P.convert(get(property), type);
	}

	public Set<String> keySet() {
		return index.keySet();
	}

	public Object put(String key, Object value) {
		Object x = index.get(key);

		if (x != null && x instanceof BeanProperty && !(entity instanceof Map)) {
			BeanProperty p = (BeanProperty) x;
			Object old = p.get(entity);
			p.set(entity, value);
			return old;
		} else {
			return index.put(key, value);
		}
	}

	public Object remove(Object key) {
		if (!(key instanceof CharSequence)) return null;
		return put(String.valueOf(key), null);
	}

	/**
	 * @param property
	 * @param value
	 */
	public void set(String property, Object value) {
		put(property, value);
	}

	public int size() {
		return index.size();
	}

	/**
	 * 获取属性值
	 * @param property 属性名
	 * @return 属性值
	 */
	public <R> R value(String property) {
		return (R) get(property);
	}

	/**
	 * 获取元素项
	 * @param property 属性名
	 * @return 元素项
	 */
	protected BeanEntry entry(String property) {
		if (ems == null) {
			synchronized (this) {
				if (ems == null) ems = new ConcurrentSoftMap();
			}
		}
		BeanEntry be = ems.get(property);
		if (be == null) ems.put(property, be = new BeanEntry(property));
		return be;
	}

	/**
	 * 设置对象实体
	 * @param entity 对象实体
	 */
	void entity(Object entity) {
		this.entity = entity;
		if (entity instanceof Map) {
			this.index = (Map) entity;
		} else {
			BeanProperty[] ps = BeanProperties.properties(entity.getClass());
			this.index = new LinkedHashMap();
			for (BeanProperty p : ps)
				this.index.put(p.getName(), p);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		entity(s.readObject());
	}

	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeObject(entity);
	}

	/**
	 * 数据对象元素项
	 * @author demon 2017-07-25
	 */
	protected class BeanEntry implements Entry<String, Object> {

		private final String property;

		protected BeanEntry(String property) {
			this.property = property;
		}

		public String getKey() {
			return property;
		}

		public Object getValue() {
			return get(property);
		}

		public Object setValue(Object value) {
			return put(property, value);
		}
	}

	/**
	 * 元素项集
	 * @author demon 2017-07-25
	 */
	protected class Entries extends AbstractSet<Entry<String, Object>> {

		public void clear() {
			Beandata.this.clear();
		}

		public Iterator<Entry<String, Object>> iterator() {
			return new EntryIterator(keySet().iterator());
		}

		public boolean remove(Object o) {
			Beandata.this.remove(o);
			return false;
		}

		public int size() {
			return Beandata.this.size();
		}
	}

	/**
	 * 元素项迭代器
	 * @author demon 2017-07-25
	 */
	protected class EntryIterator implements Iterator<Entry<String, Object>> {

		protected final Iterator<String> it;

		private String current;

		protected EntryIterator(Iterator<String> it) {
			this.it = it;
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public Entry<String, Object> next() {
			current = it.next();
			return entry(current);
		}

		public void remove() {
			if (current != null) Beandata.this.remove(current);
		}
	}
}

/*
 * @(#)DataBean.java     2012-7-17
 */
package org.dommons.bean.handler;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.bean.transition.DommonTransition;
import org.dommons.core.collections.map.CaseInsensitiveHashMap;
import org.dommons.core.convert.Converter;

/**
 * 对象数据
 * @author Demon 2012-7-17
 */
class DataBean<E> extends DastractBean<E> {

	static final Map<Class, PropertyDescriptor[]> propertiesCache = new WeakHashMap();
	static final Map<Class, Map<String, Field>> fieldsCache = new WeakHashMap();

	static final Pattern pname = Pattern.compile("[\\$_A-Za-z][\\$_A-Za-z0-9]*");
	static final Pattern iname = Pattern.compile("([\\$_A-Za-z][\\$_A-Za-z0-9]*)\\[([0-9]+)\\]");
	static final Pattern numeric = Pattern.compile("\\[([0-9]+)\\]");

	/**
	 * 导入成员集
	 * @param cls 类
	 * @param map 映射关系
	 */
	private static void innerFields(Class cls, Map<String, Field> map) {
		if (cls == null || Object.class.equals(cls)) return;
		innerFields(cls.getSuperclass(), map);
		Field[] fields = cls.getFields();
		if (fields != null) {
			for (Field field : fields) {
				map.put(field.getName(), field);
			}
		}
	}

	private final Class clazz;

	private Map<String, Field> fields;
	private Map<String, PropertyDescriptor> properties;
	private Map<String, IndexedPropertyDescriptor> indexes;

	/**
	 * 构造函数
	 * @param tar 目标对象
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 * @param clazz 对象类型
	 */
	public DataBean(E tar, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent, Class clazz) {
		super(tar, transition, builder, parent);
		this.clazz = clazz;
	}

	protected DastractProperty lookup(String name) {
		load();
		if (pname.matcher(name).matches()) {
			return property(name);
		} else {
			Matcher m = iname.matcher(name);
			if (m.matches()) return index(m.group(1), m.group(2));
		}
		return null;
	}

	protected DastractProperty lookup(String head, PropertyToken token) {
		load();
		String part = token.part();
		Matcher m = numeric.matcher(part);
		if (m.matches()) {
			return (head == null || !pname.matcher(head).matches()) ? null : index(head, m.group(1));
		} else {
			return property(part);
		}
	}

	/**
	 * 加载子元素
	 */
	void load() {
		if (fields == null || properties == null || indexes == null) {
			// 加载成员集
			fields = fieldsCache.get(clazz);
			if (fields == null) {
				Map<String, Field> map = new CaseInsensitiveHashMap(false);
				innerFields(clazz, map);
				synchronized (fieldsCache) {
					fieldsCache.put(clazz, fields = map);
				}
			}

			// 加载属性集
			PropertyDescriptor[] descriptors = propertiesCache.get(clazz);
			if (descriptors == null) {
				try {
					descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
				} catch (IntrospectionException e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
				synchronized (propertiesCache) {
					propertiesCache.put(clazz, descriptors);
				}
			}

			Map<String, PropertyDescriptor> ps = new CaseInsensitiveHashMap(false);
			Map<String, IndexedPropertyDescriptor> is = new CaseInsensitiveHashMap(false);
			if (descriptors != null) {
				for (PropertyDescriptor descriptor : descriptors) {
					if (descriptor instanceof IndexedPropertyDescriptor) {
						is.put(descriptor.getName(), (IndexedPropertyDescriptor) descriptor);
						if (descriptor.getReadMethod() == null && descriptor.getWriteMethod() == null) continue;
					}
					ps.put(descriptor.getName(), descriptor);
				}
			}
			properties = ps;
			indexes = is;
		}
	}

	/**
	 * 查找索引属性项
	 * @param name 属性名
	 * @param index 索引
	 * @return 索引属性
	 */
	private IndexProperty index(String name, String index) {
		IndexedPropertyDescriptor property = indexes.get(name);
		if (property != null) return new IndexProperty(name, property, Converter.P.convert(index, int.class));
		return null;
	}

	/**
	 * 查找属性项
	 * @param name 属性名
	 * @return 属性项
	 */
	private DastractProperty property(String name) {
		// 优先查找属性
		PropertyDescriptor property = properties.get(name);
		if (property != null) return new Property(name, property);
		Field field = fields.get(name);
		if (field != null) return new FieldProperty(name, field);
		return null;
	}

	/**
	 * 数据对象成员属性
	 * @author Demon 2012-7-17
	 */
	class FieldProperty extends DastractProperty<E> {

		private final Field field;

		private Type type;
		private volatile boolean accessible;

		/**
		 * 构造函数
		 * @param name 属性名
		 * @param field 成员
		 */
		protected FieldProperty(String name, Field field) {
			super(DataBean.this.transition, DataBean.this, name);
			this.field = field;
			accessible = false;
			type = null;
		}

		public boolean readable() {
			return true;
		}

		public boolean writable() {
			return !Modifier.isFinal(field.getModifiers());
		}

		protected Object getter() {
			try {
				accessible();
				return field.get(tar);
			} catch (Exception e) {
				throw Converter.P.convert(e, RuntimeException.class);
			}
		}

		protected Type readableType() {
			return getType();
		}

		protected Object setter(Object value) {
			value = transition.transform(value, getType());
			accessible();
			try {
				field.set(tar, value);
			} catch (Exception e) {
				throw Converter.P.convert(e, RuntimeException.class);
			}
			return value;
		}

		protected Type writableType() {
			return getType();
		}

		/**
		 * 置为可访问
		 */
		private void accessible() {
			if (!accessible) field.setAccessible(accessible = true);
		}

		/**
		 * 获取成员类型
		 * @return 类型
		 */
		private Type getType() {
			if (type == null) type = field.getGenericType();
			return type;
		}
	}

	/**
	 * 对象索引属性
	 * @author Demon 2012-7-18
	 */
	class IndexProperty extends DastractProperty<E> {

		private final int index;

		private final IndexedPropertyDescriptor property;

		private Method[] read;
		private Method[] write;

		/**
		 * 构造函数
		 * @param name 属性名
		 * @param property 索引属性描述
		 * @param index 索引值
		 */
		protected IndexProperty(String name, IndexedPropertyDescriptor property, int index) {
			super(DataBean.this.transition, DataBean.this, name);
			this.property = property;
			this.index = index;
		}

		public boolean readable() {
			return read() != null;
		}

		public boolean writable() {
			return write() != null;
		}

		protected Object getter() {
			Method method = read();
			if (method != null) {
				try {
					return method.invoke(tar, index);
				} catch (InvocationTargetException e) {
					throw Converter.P.convert(e.getTargetException(), RuntimeException.class);
				} catch (Exception e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
			}
			return null;
		}

		protected Class readableType() {
			Method method = read();
			return method == null ? Object.class : method.getReturnType();
		}

		protected Object setter(Object value) {
			Method method = write();
			if (method != null) {
				value = transition.transform(value, method.getParameterTypes()[1]);
				try {
					method.invoke(tar, index, value);
				} catch (InvocationTargetException e) {
					throw Converter.P.convert(e.getTargetException(), RuntimeException.class);
				} catch (Exception e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
			}
			return value;
		}

		protected Class writableType() {
			Method method = write();
			return method == null ? Object.class : method.getParameterTypes()[1];
		}

		/**
		 * 获取读取方式
		 * @return 读取方式
		 */
		private Method read() {
			if (read == null) {
				read = new Method[] { property.getIndexedReadMethod() };
				if (read[0] != null) read[0].setAccessible(true);
			}
			return read[0];
		}

		/**
		 * 获取写入方式
		 * @return 写入方式
		 */
		private Method write() {
			if (write == null) {
				write = new Method[] { property.getIndexedWriteMethod() };
				if (write[0] != null) write[0].setAccessible(true);
			}
			return write[0];
		}
	}

	/**
	 * 对象属性
	 * @author Demon 2012-7-17
	 */
	class Property extends DastractProperty<E> {

		private final PropertyDescriptor property;

		private Method[] read;
		private Method[] write;

		/**
		 * 构造函数
		 * @param name 属性名
		 * @param property 属性项描述
		 */
		protected Property(String name, PropertyDescriptor property) {
			super(DataBean.this.transition, DataBean.this, name);
			this.property = property;
		}

		public boolean readable() {
			return read() != null;
		}

		public boolean writable() {
			return write() != null;
		}

		protected Object getter() {
			Method method = read();
			if (method != null) {
				try {
					return method.invoke(tar);
				} catch (InvocationTargetException e) {
					throw Converter.P.convert(e.getTargetException(), RuntimeException.class);
				} catch (Exception e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
			}
			return null;
		}

		protected Class readableType() {
			Method method = read();
			return method == null ? Object.class : method.getReturnType();
		}

		protected Object setter(Object value) {
			Method method = write();
			if (method != null) {
				value = transition.transform(value, method.getParameterTypes()[0]);
				try {
					method.invoke(tar, value);
				} catch (InvocationTargetException e) {
					throw Converter.P.convert(e.getTargetException(), RuntimeException.class);
				} catch (Exception e) {
					throw Converter.P.convert(e, RuntimeException.class);
				}
			}
			return value;
		}

		protected Class writableType() {
			Method method = write();
			return method == null ? Object.class : method.getParameterTypes()[0];
		}

		/**
		 * 获取读取方式
		 * @return 读取方式
		 */
		private Method read() {
			if (read == null) {
				read = new Method[] { property.getReadMethod() };
				if (read[0] != null) read[0].setAccessible(true);
			}
			return read[0];
		}

		/**
		 * 获取写入方式
		 * @return 写入方式
		 */
		private Method write() {
			if (write == null) {
				write = new Method[] { property.getWriteMethod() };
				if (write[0] != null) write[0].setAccessible(true);
			}
			return write[0];
		}
	}
}

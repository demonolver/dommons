/*
 * @(#)PropertyDescriptors.java     2013-6-14
 */
package org.dommons.core.util;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.util.beans.BeanProperties;

/**
 * 属性描述集
 * @author Demon 2013-6-14
 */
public class PropertyDescriptors {

	static Map<Class, PropertyDescriptor[]> cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));

	/**
	 * 复制对象
	 * @param src 源数据
	 * @param tar 目标数据
	 * @deprecated {@link BeanProperties#copy(Object, Object)}
	 */
	public static void copy(Object src, Object tar) {
		BeanProperties.copy(src, tar);
	}

	/**
	 * 获取属性描述集
	 * @param clazz 目标对象类
	 * @return 属性集
	 */
	public static PropertyDescriptor[] descriptors(Class clazz) {
		if (clazz == null) return null;
		PropertyDescriptor[] descriptors = cache.get(clazz);
		if (descriptors == null) cache.put(clazz, descriptors = new PropertyDescriptors(clazz).descriptors());
		return descriptors;
	}

	/**
	 * 获取属性集
	 * @param obj 数据对象
	 * @return 属性集
	 * @deprecated {@link BeanProperties#properties(Object)}
	 */
	public static Map properties(Object obj) {
		return BeanProperties.properties(obj);
	}

	protected final Class clazz;
	protected final Map<String, PropertyDescriptor> descriptors;

	private Collection<String> ms;

	protected PropertyDescriptors(Class clazz) {
		this.clazz = clazz;
		this.descriptors = new LinkedHashMap();
		this.ms = new HashSet();
	}

	/**
	 * 获取属性描述集
	 * @return 描述集
	 */
	public PropertyDescriptor[] descriptors() {
		fetch(clazz);
		return descriptors.values().toArray(new PropertyDescriptor[descriptors.size()]);
	}

	/**
	 * 查找类属性集
	 * @param clazz 类型
	 */
	protected void fetch(Class clazz) {
		Method[] methods = clazz.getMethods();
		if (methods != null) {
			for (Method m : methods) {
				if (Modifier.isStatic(m.getModifiers()) || !ms.add(feature(m))) continue;
				inner(m);
			}
		}

		Class parent = clazz.getSuperclass();
		if (parent != null) fetch(parent);
		else if (!Object.class.equals(clazz)) fetch(Object.class);

		Class[] inters = clazz.getInterfaces();
		if (inters != null) {
			for (Class inter : inters) {
				if (inter != null) fetch(inter);
			}
		}
	}

	/**
	 * 添加方法
	 * @param method 方法
	 */
	protected void inner(Method method) {
		String n = method.getName(), s = n.toLowerCase();
		if (s.length() <= 3 && !s.startsWith("is")) return;
		Class rt = method.getReturnType();
		Class[] pts = method.getParameterTypes();

		switch (pts.length) {
		case 0:
			if (s.startsWith("is") && isBoolean(rt)) getter(n, 2, method, false);
			else if (s.startsWith("get")) getter(n, 3, method, false);
			break;
		case 1:
			if (s.startsWith("is") && isBoolean(rt) && isInt(pts[0])) getter(n, 2, method, true);
			else if (s.startsWith("get") && isInt(pts[0])) getter(n, 3, method, true);
			else if (s.startsWith("set")) setter(n, method, false);
			break;
		case 2:
			if (s.startsWith("set") && isInt(pts[0])) setter(n, method, true);
			break;
		}
	}

	/**
	 * 注册属性读取方法
	 * @param n 方法名
	 * @param prefix 前缀
	 * @param m 方法
	 * @param index 是否索引属性
	 */
	void getter(String n, int prefix, Method m, boolean index) {
		try {
			String k = Introspector.decapitalize(n.substring(prefix));
			PropertyDescriptor d = descriptors.get(k);
			Method r = null, w = null;
			if (d == null) descriptors.put(k,
				d = index ? new IndexedPropertyDescriptor(k, clazz, null, null, null, null) : new PropertyDescriptor(k, clazz, null, null));
			reg: if (index) {
				IndexedPropertyDescriptor i = null;
				if (!IndexedPropertyDescriptor.class.isInstance(d)) {
					i = new IndexedPropertyDescriptor(k, clazz);
					i.setReadMethod(d.getReadMethod());
					i.setWriteMethod(d.getWriteMethod());
				} else {
					i = (IndexedPropertyDescriptor) d;
				}

				if ((r = i.getIndexedReadMethod()) != null) {
					if (isUpper(n, prefix)) break reg;
					if ((w = i.getIndexedWriteMethod()) == null) break reg;
					Class wt = w.getParameterTypes()[1], mt = m.getReturnType();
					if (!mt.isAssignableFrom(wt)) break reg;
					Class rt = r.getReturnType();
					if (mt.isAssignableFrom(rt)) break reg;
				}
				i.setIndexedReadMethod(m);
			} else {
				w = d.getWriteMethod();
				Class mt = m.getReturnType();
				if ((r = d.getReadMethod()) != null) {
					if (isUpper(n, prefix)) break reg;
					if (w == null) break reg;
					Class wt = w.getParameterTypes()[0];
					if (!mt.isAssignableFrom(wt)) break reg;
					Class rt = r.getReturnType();
					if (mt.isAssignableFrom(rt)) break reg;
				} else if (w != null) {
					if (!w.getParameterTypes()[0].isAssignableFrom(mt)) break reg;
				}
				d.setReadMethod(m);
			}
		} catch (IntrospectionException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * 注册属性写入方法
	 * @param n 方法名
	 * @param m 方法
	 * @param index 是否索引属性
	 */
	void setter(String n, Method m, boolean index) {
		try {
			String k = Introspector.decapitalize(n.substring(3));
			PropertyDescriptor d = descriptors.get(k);
			Method r = null, w = null;
			if (d == null) descriptors.put(k,
				d = index ? new IndexedPropertyDescriptor(k, clazz, null, null, null, null) : new PropertyDescriptor(k, clazz, null, null));
			reg: if (index) {
				IndexedPropertyDescriptor i = null;
				if (!IndexedPropertyDescriptor.class.isInstance(d)) {
					i = new IndexedPropertyDescriptor(k, clazz);
					i.setReadMethod(d.getReadMethod());
					i.setWriteMethod(d.getWriteMethod());
				} else {
					i = (IndexedPropertyDescriptor) d;
				}
				if ((w = i.getIndexedWriteMethod()) != null) {
					if (isUpper(n, 3)) break reg;
					if ((r = i.getIndexedReadMethod()) == null) break reg;
					Class rt = r.getReturnType(), mt = m.getParameterTypes()[1];
					if (!rt.isAssignableFrom(mt)) break reg;
					Class wt = w.getParameterTypes()[1];
					if (wt.isAssignableFrom(mt)) break reg;
				}
				i.setIndexedWriteMethod(m);
			} else {
				Class mt = m.getParameterTypes()[0];
				r = d.getReadMethod();
				if ((w = d.getWriteMethod()) != null) {
					if (isUpper(n, 3)) break reg;
					if (r == null) break reg;
					Class rt = r.getReturnType();
					if (!rt.isAssignableFrom(mt)) break reg;
					Class wt = w.getParameterTypes()[0];
					if (wt.isAssignableFrom(mt)) break reg;
				} else if (r != null) {
					if (!mt.isAssignableFrom(r.getReturnType())) break reg;
				}
				d.setWriteMethod(m);
			}
		} catch (IntrospectionException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * 生成特征串
	 * @param cls 类型
	 * @return 特征串
	 */
	private String feature(Class cls) {
		StringBuilder buf = new StringBuilder();
		while (cls.isArray()) {
			cls = cls.getComponentType();
			buf.append("[]");
		}
		buf.insert(0, cls.getName());
		return buf.toString();
	}

	/**
	 * 生成特征串
	 * @param m 方法
	 * @return 特征串
	 */
	private String feature(Method m) {
		StringBuilder buf = new StringBuilder();
		buf.append(feature(m.getReturnType())).append(' ').append(m.getName()).append('(');
		Class[] types = m.getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			if (i > 0) buf.append(',');
			buf.append(feature(types[i]));
		}
		return buf.append(')').toString();
	}

	/**
	 * 是否布尔类型
	 * @param cls 类型
	 * @return 是、否
	 */
	private boolean isBoolean(Class cls) {
		return Boolean.class.equals(cls) || boolean.class.equals(cls);
	}

	/**
	 * 是否整型
	 * @param cls 类型
	 * @return 是、否
	 */
	private boolean isInt(Class cls) {
		return Integer.class.equals(cls) || int.class.equals(cls) || Long.class.equals(cls) || long.class.equals(cls)
				|| short.class.equals(cls) || Short.class.equals(cls);
	}

	/**
	 * 是否大写
	 * @param str 字符串
	 * @param end 截止
	 * @return 是、否
	 */
	private boolean isUpper(String str, int end) {
		int len = str == null ? 0 : str.length();
		for (int i = 0; i < len && i < end; i++) {
			if (Character.isUpperCase(str.charAt(i))) return true;
		}
		return false;
	}
}

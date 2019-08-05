/*
 * @(#)BeanProperties.java     2016-12-01
 */
package org.dommons.core.util.beans;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.dommons.core.Silewarner;
import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.util.Arrayard;

/**
 * 数据对象属性集
 * @author demon 2016-12-01
 */
public final class BeanProperties {

	static Map<Class, BeanProperty[]> cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));

	/**
	 * 对象子类型适配
	 * @param tar 对象实例
	 * @param cls 类型
	 * @return 目标类型实例 非子类返回<code>null</code>
	 */
	public static <P, T extends P> T cast(P tar, Class<T> cls) {
		if (tar == null || cls == null) return (T) tar;
		else if (cls.isInstance(tar)) return cls.cast(tar);
		else if (tar.getClass().isAssignableFrom(cls)) return BeanProperties.newInstance(cls, tar);
		else return null;
	}

	/**
	 * 克隆数据
	 * @param obj 数据对象
	 * @return 新数据对象
	 */
	public static <O> O clone(O obj) {
		if (obj == null) return null;
		if (obj instanceof Cloneable) {
			try {
				Method m = null;
				try {
					m = obj.getClass().getMethod("clone");
				} catch (NoSuchMethodException e) {
					m = Object.class.getDeclaredMethod("clone");
				}
				if (!m.isAccessible()) m.setAccessible(true);
				return (O) m.invoke(obj);
			} catch (Throwable t) {
			}
		}
		return newInstance((Class<O>) obj.getClass(), obj);
	}

	/**
	 * 复制对象
	 * @param src 源数据
	 * @param tar 目标数据
	 */
	public static void copy(Object src, Object tar) {
		if (src == null || tar == null) return;
		if (tar instanceof Map) {
			Map tm = (Map) tar;
			if (src instanceof Map) tm.putAll((Map) src);
			else load(tm, src);
		} else {
			BeanProperty[] ps = properties(tar.getClass());
			if (src instanceof Map) load(tar, ps, (Map) src);
			else load(tar, ps, src);
		}
	}

	/**
	 * 新建数据对象
	 * @param clazz 数据对象类
	 * @param src 源数据
	 * @return 新数据对象
	 */
	public static <O> O newInstance(Class<O> clazz, Object src) {
		if (clazz == null) return null;
		O n = ObjectInstantiators.newInstance(clazz);
		copy(src, n);
		return n;
	}

	/**
	 * 获取属性集
	 * @param clazz 对象
	 * @return 属性集
	 */
	public static BeanProperty[] properties(Class clazz) {
		if (clazz == null) return null;
		BeanProperty[] ps = cache.get(clazz);
		if (ps == null) cache.put(clazz, ps = new BeanProperties(clazz).properties());
		return ps;
	}

	/**
	 * 获取属性集
	 * @param obj 数据对象
	 * @return 属性集
	 */
	public static Map<String, Object> properties(Object obj) {
		if (obj == null) return null;
		Map ps = new LinkedHashMap();
		copy(obj, ps);
		return ps;
	}

	/**
	 * 生成属性名
	 * @param name 属性名
	 * @return 属性名
	 */
	static String decapitalize(String name) {
		if (name == null || name.length() == 0) return name;
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) return name;
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 生成特征串
	 * @param cls 类型
	 * @return 特征串
	 */
	private static String feature(Class cls) {
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
	private static String feature(Method m) {
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
	 * 复制到映射表
	 * @param tm 映射表
	 * @param src 源数据
	 */
	private static void load(Map tm, Object src) {
		BeanProperty[] ps = properties(src.getClass());
		for (BeanProperty p : ps) {
			String name = p.getName();
			if ("class".equals(name)) continue;
			Method m = p.getReadMethod();
			if (m == null) continue;
			Throwable tt = null;
			try {
				tm.put(name, m.invoke(src));
			} catch (InvocationTargetException e) {
				tt = e.getTargetException();
			} catch (Throwable t) {
				tt = t;
			} finally {
				if (tt != null) Silewarner.warn(src.getClass(), name, tt);
			}
		}
	}

	/**
	 * 从映射表复制
	 * @param tar 目标数据
	 * @param ps 属性集
	 * @param sm 映射表
	 */
	private static void load(Object tar, BeanProperty[] ps, Map sm) {
		for (BeanProperty pd : ps) {
			Method m = pd.getWriteMethod();
			if (m == null) continue;
			String name = pd.getName();
			Object v = sm.get(name);
			if (v == null && !sm.containsKey(name)) continue;
			Throwable tt = null;
			try {
				if (!m.isAccessible()) m.setAccessible(true);
				m.invoke(tar, Converter.P.convert(v, m.getParameterTypes()[0]));
			} catch (InvocationTargetException e) {
				tt = e.getTargetException();
			} catch (Throwable t) {
				tt = t;
			} finally {
				if (tt != null) Silewarner.warn(tar.getClass(), name, tt);
			}
		}
	}

	/**
	 * 复制数据对象
	 * @param tar 目标数据
	 * @param ps 属性集
	 * @param src 源数据对象
	 */
	private static void load(Object tar, BeanProperty[] ps, Object src) {
		Map<String, BeanProperty> pm = null;
		if (!tar.getClass().equals(src.getClass())) {
			pm = new HashMap();
			BeanProperty[] sds = properties(src.getClass());
			for (BeanProperty pd : sds) {
				String name = pd.getName();
				if ("class".equals(name) || pd.getReadMethod() == null) continue;
				pm.put(name, pd);
			}
		}
		for (BeanProperty pd : ps) {
			String name = pd.getName();
			BeanProperty sp = pm == null ? pd : pm.get(name);
			if (sp == null) continue;
			Method wm = pd.getWriteMethod(), rm = sp.getReadMethod();
			if (wm == null || rm == null) continue;
			Throwable tt = null;
			try {
				Object v = rm.invoke(src);
				Class wt = wm.getParameterTypes()[0];
				if (v == null && wt.isPrimitive()) continue;
				wm.invoke(tar, Converter.P.convert(v, wt));
			} catch (InvocationTargetException e) {
				tt = e.getTargetException();
			} catch (Throwable t) {
				tt = t;
			} finally {
				if (tt != null) Silewarner.warn(tar.getClass(), pd.getName(), tt);
			}
		}
	}

	protected final Class clazz;

	protected final Map<String, Collection<Method>> rs;
	protected final Map<String, Collection<Method>> ws;

	protected BeanProperties(Class clazz) {
		this.clazz = clazz;
		this.rs = new HashMap();
		this.ws = new HashMap();
	}

	/**
	 * 获取属性集
	 * @return 属性集
	 */
	public BeanProperty[] properties() {
		fetch(clazz);
		Map<String, BeanProperty> ps = new TreeMap();
		for (Entry<String, Collection<Method>> en : this.rs.entrySet()) {
			Collection<Method> rs = en.getValue(), ws = this.ws.remove(en.getKey());
			BeanProperty p = property(en.getKey(), rs, ws);
			ps.put(en.getKey(), p);
		}

		for (Entry<String, Collection<Method>> en : this.ws.entrySet()) {
			BeanProperty p = property(en.getKey(), null, en.getValue());
			ps.put(en.getKey(), p);
		}
		return Arrayard.toArray(ps.values(), BeanProperty.class);
	}

	/**
	 * 查找类属性集
	 * @param clazz 类型
	 */
	protected void fetch(Class clazz) {
		Map<String, Method> ms = new HashMap();
		methods(clazz, ms);

		for (Iterator<Method> it = ms.values().iterator(); it.hasNext();) {
			if (!inner(it.next(), true)) continue;
			it.remove();
		}

		for (Method m : ms.values())
			inner(m, false);
	}

	/**
	 * 追加方法
	 * @param m 方法
	 * @param getter 是否追加设置方法
	 * @return 是否追加属性
	 */
	protected boolean inner(Method m, boolean getter) {
		String n = m.getName(), s = n.toLowerCase();
		if (s.length() <= 3 && !s.startsWith("is")) return false;
		Class rt = m.getReturnType();
		Class[] pts = m.getParameterTypes();
		switch (pts.length) {
		case 0:
			if (s.startsWith("is") && isBoolean(rt) && getter) getter(n, 2, m);
			else if (s.startsWith("get") && getter) getter(n, 3, m);
			else return false;
			break;
		case 1:
			if (s.startsWith("set") && !getter) setter(n, m);
			else return false;
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * 生成属性项
	 * @param n 属性名
	 * @param rs 读取方法集
	 * @param ws 写入方法集
	 * @return 属性项
	 */
	protected BeanProperty property(String n, Collection<Method> rs, Collection<Method> ws) {
		BeanProperty p = new BeanProperty(n, null, null);
		Map<Class, Method> rm = new HashMap(), wm = new HashMap();
		if (rs != null) {
			for (Method m : rs) {
				if (p.getReadMethod() == null) p.setReadMethod(m);
				rm.put(m.getReturnType(), m);
			}
		}
		if (ws != null) {
			for (Method m : ws) {
				if (p.getWriteMethod() == null) p.setWriteMethod(m);
				Class wt = m.getParameterTypes()[0];
				wm.put(wt, m);
			}
		}
		if (rm.size() > 1 && wm.size() > 0) {
			int x = 0;
			Method m = null;
			for (Method cm : rm.values()) {
				int cx = match(cm.getReturnType(), wm.keySet());
				if (cx > x) {
					x = cx;
					m = cm;
				}
			}
			p.setReadMethod(m);
		}
		if (wm.size() > 1 && rm.size() > 0) {
			int x = 0;
			Method m = null;
			for (Method cm : wm.values()) {
				int cx = match(cm.getParameterTypes()[0], rm.keySet());
				if (cx > x) {
					x = cx;
					m = cm;
				}
			}
			p.setWriteMethod(m);
		}
		return p;
	}

	/**
	 * 注册属性读取方法
	 * @param n 方法名
	 * @param prefix 前缀
	 * @param m 方法
	 */
	void getter(String n, int prefix, Method m) {
		String k = decapitalize(n.substring(prefix));
		Collection<Method> ms = rs.get(k);
		if (ms == null) rs.put(k, ms = new ArrayList());
		else if (!ms.isEmpty() && isUpper(n, prefix)) return;
		ms.add(m);
	}

	int match(Class c, Collection<Class> ws) {
		int x = 0;
		for (Class tc : ws) {
			if (tc.isAssignableFrom(c)) x++;
			if (c.isAssignableFrom(tc)) x++;
		}
		return x;
	}

	/**
	 * 获取方法集
	 * @param c 类型
	 * @param ms 方法集
	 */
	void methods(Class c, Map<String, Method> ms) {
		Method[] methods = c.getMethods();
		if (methods != null) {
			for (Method m : methods) {
				if (Modifier.isStatic(m.getModifiers())) continue;
				Class[] pts = m.getParameterTypes();
				if (pts != null && pts.length > 1) continue;
				String f = feature(m);
				if (ms.containsKey(f)) continue;
				ms.put(f, m);
			}
		}

		Class parent = c.getSuperclass();
		if (parent != null) methods(parent, ms);
		else if (!Object.class.equals(c)) methods(Object.class, ms);

		Class[] inters = c.getInterfaces();
		if (inters != null) {
			for (Class inter : inters) {
				if (inter != null) methods(inter, ms);
			}
		}
	}

	/**
	 * 注册属性写入方法
	 * @param n 方法名
	 * @param m 方法
	 */
	void setter(String n, Method m) {
		String k = decapitalize(n.substring(3));
		Collection<Method> ms = ws.get(k);
		if (ms == null) ws.put(k, ms = new ArrayList());
		else if (!ms.isEmpty() && isUpper(n, 3)) return;
		ms.add(m);
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

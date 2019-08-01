/*
 * @(#)ThreadLocalMapCloner.java     2016-10-17
 */
package org.dommons.core.util.thread;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.util.Arrayard;

/**
 * 线程上下文克隆器
 * @author demon 2016-10-17
 */
class ThreadLocalMapCloner {

	static Ref<Constructor> cref;
	static Ref<Field> table;
	static Ref<Method> set;
	static Ref<Field> value;

	/**
	 * 克隆上下文
	 * @param tc 上下文
	 * @return 新线程
	 */
	public static Object clone(Object tc) {
		Object clone = null;
		if (tc != null) {
			try {
				Class cls = tc.getClass();
				Object table = table(tc);
				int len = Arrayard.length(table);
				if (len > 0) {
					int x = 0;
					for (int i = 0; i < len; i++) {
						Object entry = Array.get(table, i);
						if (entry == null) continue;
						if (x++ == 0) clone = create(entry, cls);
						else set(clone, entry);
					}
				}
			} catch (Throwable t) { // ignored
				e(t);
			}
		}
		return clone;
	}

	/**
	 * 处理异常
	 * @param t 异常信息
	 */
	protected static void e(Throwable t) {
		t.printStackTrace();
	}

	/**
	 * 获取上下构造函数
	 * @param tc 上下文
	 * @return 构造函数
	 */
	static Constructor constructor(Class tc) {
		Constructor c = cref == null ? null : cref.get();
		try {
			if (c == null) {
				c = tc.getDeclaredConstructor(ThreadLocal.class, Object.class);
				c.setAccessible(true);
				cref = new Softref(c);
			}
		} catch (Throwable t) {
			e(t);
		}
		return c;
	}

	/**
	 * 创建线程
	 * @param entry
	 * @param cls 线程类型
	 * @return 线程
	 */
	static Object create(Object entry, Class cls) {
		Constructor c = constructor(cls);
		try {
			if (c != null) {
				Object k = key(entry), v = value(entry);
				if (k != null && v != null) return c.newInstance(k, v);
			}
		} catch (Throwable t) {
			e(t);
		}
		return null;
	}

	/**
	 * 获取键值
	 * @param entry 变量项
	 * @return 变量键值
	 */
	static Object key(Object entry) {
		return entry == null ? null : ((Reference) entry).get();
	}

	/**
	 * 获取上下文设置方法
	 * @param tc 上下文
	 * @return 方法
	 */
	static Method method(Class tc) {
		Method m = set == null ? null : set.get();
		try {
			if (m == null) {
				m = tc.getDeclaredMethod("set", ThreadLocal.class, Object.class);
				m.setAccessible(true);
				set = new Softref(m);
			}
		} catch (Throwable t) {
			e(t);
		}
		return m;
	}

	/**
	 * 设置上下文值
	 * @param tc 上下文
	 * @param entry 数据值
	 */
	static void set(Object tc, Object entry) {
		if (tc == null) return;
		Method m = set == null ? null : set.get();
		try {
			if (m == null) {
				m = tc.getClass().getDeclaredMethod("set", ThreadLocal.class, Object.class);
				m.setAccessible(true);
				set = new Softref(m);
			}
			Object k = key(entry), v = value(entry);
			if (k != null && v != null) m.invoke(tc, k, v);
		} catch (Throwable t) {
			e(t);
		}
	}

	/**
	 * 获取上下文数据表
	 * @param tc 上下文
	 * @return 数据表
	 */
	static Object table(Object tc) {
		Field f = table == null ? null : table.get();
		Object tb = null;
		try {
			if (f == null) {
				f = tc.getClass().getDeclaredField("table");
				f.setAccessible(true);
				table = new Softref(f);
			}
			tb = f.get(tc);
		} catch (Throwable t) {
			e(t);
		}
		return tb;
	}

	/**
	 * 获取变量值
	 * @param entry 变量项
	 * @return 变量值
	 */
	static Object value(Object entry) {
		if (entry == null) return null;
		Field f = value == null ? null : value.get();
		Object v = null;
		try {
			if (f == null) {
				f = entry.getClass().getDeclaredField("value");
				f.setAccessible(true);
				value = new Softref(f);
			}
			v = f.get(entry);
		} catch (Throwable t) {
			e(t);
		}
		return v;
	}
}

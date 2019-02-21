/*
 * @(#)ObjectInstantiators.java     2017-03-01
 */
package org.dommons.core.util.beans;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.collections.map.ci.CaseInsensitiveHashMap;
import org.dommons.core.collections.map.ci.CaseInsensitiveMap;
import org.dommons.core.collections.map.concurrent.ConcurrentWeakMap;
import org.dommons.core.collections.stack.LinkedStack;
import org.dommons.core.collections.stack.Stack;
import org.dommons.core.convert.Converter;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.ref.Strongref;
import org.dommons.core.string.Stringure;

/**
 * 数据对象构造器
 * @author demon 2017-03-01
 */
public class ObjectInstantiators {

	private static final Map<Class, Ref<Constructor>> cmap = new ConcurrentWeakMap();
	private static final Map<Class, org.dommons.core.util.beans.ObjectInstantiator> imap = new ConcurrentWeakMap();

	private static Ref<ClassObjectInstantiator> iref;

	/**
	 * 获取对象实例化器
	 * @param clazz 对象类型
	 * @return 实例化器
	 */
	public static <T> ObjectInstantiator<T> instantiator(final Class<T> clazz) {
		ObjectInstantiator in = imap.get(clazz);
		if (in == null) {
			in = new ObjectInstantiator<T>() {
				@Override
				public T newInstance() {
					return ObjectInstantiators.newInstance(clazz);
				}

			};
			imap.put(clazz, in);
		}
		return in;
	}

	/**
	 * 新建对象实例
	 * @param clazz 对象类型
	 * @return 对象实例
	 */
	public static <O> O newInstance(Class clazz) {
		Ref<Constructor> r = cmap.get(clazz);
		if (r == null) {
			Constructor c = null;
			if (!isAbstract(clazz)) c = defaultConstructor(clazz);
			else c = abstractConstructor(clazz);
			cmap.put(clazz, r = new Strongref(c));
		}
		Throwable nt = null;
		try {
			if (r.get() != null) return (O) r.get().newInstance();
		} catch (Throwable t) {
			nt = t;
		}
		if (Serializable.class.isAssignableFrom(clazz) && !isAbstract(clazz)) return newSerializable(clazz);
		if (nt != null) throw Converter.F.convert(nt, RuntimeException.class);
		else throw new UnsupportedOperationException();
	}

	/**
	 * 获取抽象类构造方法
	 * @param clazz 类型
	 * @return 构造方法
	 */
	protected static Constructor abstractConstructor(Class clazz) {
		if (CaseInsensitiveMap.class.isAssignableFrom(clazz)) return constructor(clazz, CaseInsensitiveHashMap.class);
		else if (ConcurrentMap.class.isAssignableFrom(clazz)) return constructor(clazz, ConcurrentHashMap.class);
		else if (Map.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedHashMap.class);
		else if (BlockingQueue.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedBlockingQueue.class);
		else if (Queue.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedList.class);
		else if (Stack.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedStack.class);
		else if (Set.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedHashSet.class);
		else if (Collection.class.isAssignableFrom(clazz)) return constructor(clazz, LinkedList.class);
		return null;
	}

	/**
	 * 获取默认构造方法
	 * @param clazz 类型
	 * @return 构造方法
	 */
	protected static Constructor defaultConstructor(Class clazz) {
		try {
			Constructor c = clazz.getDeclaredConstructor();
			if (!Modifier.isPublic(c.getModifiers()) || !Modifier.isPublic(clazz.getModifiers())) c.setAccessible(true);
			return c;
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 查找方法
	 * @param name 方法名
	 * @param types 参数类型集
	 * @return 方法
	 */
	protected static Method method(String name, Class... types) {
		try {
			Method m = ObjectStreamClass.class.getDeclaredMethod(name, types);
			m.setAccessible(true);
			return m;
		} catch (Throwable t) {
			throw Converter.F.convert(t, RuntimeException.class);
		}
	}

	/**
	 * 生成可序列化对象实例
	 * @param clazz 类型
	 * @return 对象实例
	 */
	protected static <O> O newSerializable(Class clazz) {
		ClassObjectInstantiator instantiator = iref == null ? null : iref.get();
		if (instantiator == null) {
			String jvm = Stringure.trim(System.getProperty("java.vm.name")).toLowerCase();
			if (jvm.startsWith("dalvik")) instantiator = new AndroidObjectInstantiator();
			else instantiator = new DefaultObjectInstantiator();
			iref = new Softref(instantiator);
		}
		return instantiator.newInstance(clazz);
	}

	/**
	 * 尝试实现类
	 * @param clazz 类型
	 * @param c 实现类型
	 * @return 构造方法
	 */
	static Constructor constructor(Class clazz, Class c) {
		return clazz.isAssignableFrom(c) ? defaultConstructor(c) : null;
	}

	/**
	 * 是否抽象类
	 * @param clazz 类型
	 * @return 构造方式
	 */
	static boolean isAbstract(Class clazz) {
		return clazz == null || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * 对象构造器
	 * @author demon 2017-03-02
	 */
	protected static interface ClassObjectInstantiator {
		/**
		 * 创建新对象
		 * @param clazz 对象类型
		 * @return 数据对象
		 */
		public <O> O newInstance(Class clazz);
	}

	/**
	 * 抽象流类型对象构造器
	 * @author demon 2017-03-02
	 */
	protected static abstract class ObjectStreamClassInstantiator implements ClassObjectInstantiator {

		public ObjectStreamClassInstantiator() {
			super();

		}

		/**
		 * @param type
		 * @return
		 */
		protected abstract ObjectStreamClass lookup(Class type);
	}

	/**
	 * Android 环境对象构造器
	 * @author demon 2017-03-02
	 */
	static class AndroidObjectInstantiator implements ClassObjectInstantiator {

		protected final Map<Class, ObjectStreamClass> map;
		private Method lmethod;
		private Method method;

		public AndroidObjectInstantiator() {
			super();
			lmethod = method("lookupAny", Class.class);
			method = method("newInstance", Class.class);
			map = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));
		}

		public <O> O newInstance(Class clazz) {
			ObjectStreamClass osc = map.get(clazz);
			if (osc == null) map.put(clazz, osc = lookup(clazz));

			try {
				return (O) clazz.cast(method.invoke(osc, clazz));
			} catch (Throwable t) {
				throw Converter.F.convert(t, RuntimeException.class);
			}
		}

		protected ObjectStreamClass lookup(Class type) {
			try {
				return (ObjectStreamClass) lmethod.invoke(null, type);
			} catch (Throwable t) {
				throw Converter.F.convert(t, RuntimeException.class);
			}
		}
	}

	/**
	 * 默认对象构造器
	 * @author demon 2017-03-02
	 */
	static class DefaultObjectInstantiator extends ObjectStreamClassInstantiator {

		protected final Map<Class, ObjectStreamClass> map;
		private Method method;

		public DefaultObjectInstantiator() {
			super();
			method = method("newInstance");
			map = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));
		}

		public <O> O newInstance(Class clazz) {
			ObjectStreamClass osc = map.get(clazz);
			if (osc == null) map.put(clazz, osc = lookup(clazz));

			try {
				return (O) clazz.cast(method.invoke(osc));
			} catch (Throwable t) {
				throw Converter.F.convert(t, RuntimeException.class);
			}
		}

		protected ObjectStreamClass lookup(Class type) {
			return ObjectStreamClass.lookup(type);
		}
	}
}

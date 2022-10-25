/*
 * @(#)ProxyFactory.java     2017-12-25
 */
package org.dommons.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.dommons.core.collections.map.concurrent.ConcurrentWeakMap;
import org.dommons.core.util.Arrayard;

/**
 * 代理工厂
 * @author demon 2017-12-25
 */
public class ProxyFactory {

	static final Map<String, Boolean> existCache = new ConcurrentWeakMap();
	static final ConcurrentMap<ClassLoader, Map<String, Class[]>> classCache = new ConcurrentHashMap();

	/**
	 * 查找类
	 * @param cls 类名
	 * @return 类
	 */
	public static Class findClass(String cls) {
		return findClass(cls, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 查找类
	 * @param cls 类名
	 * @param loader 类加载器
	 * @return 类
	 */
	public static Class findClass(String cls, ClassLoader loader) {
		Map<String, Class[]> cache = classCache.get(loader);
		if (cache == null) {
			Map<String, Class[]> old = classCache.putIfAbsent(loader, cache = new ConcurrentWeakMap());
			if (old != null) cache = old;
		}
		Class[] c = cache.get(cls);
		if (c == null) {
			try {
				c = new Class[] { Class.forName(cls, false, loader) };
			} catch (Throwable t) {
				c = new Class[1];
			}
			cache.put(cls, c);
		}
		return c[0];
	}

	/**
	 * 是否代理类型
	 * @param clazz 类型
	 * @return 是否代理
	 */
	public static boolean isProxyClass(Class<?> clazz) {
		if (clazz == null) return false;
		if (existClass("org.springframework.cglib.proxy.Enhancer") && SpringProxy.isProxyClass(clazz)) return true;
		else if (existClass("net.sf.cglib.proxy.Enhancer") && CglibProxy.isProxyClass(clazz)) return true;
		else if (existClass("javassist.util.proxy.ProxyFactory") && JavassistProxy.isProxyClass(clazz)) return true;
		return Proxy.isProxyClass(clazz);
	}

	/**
	 * 创建代理实例
	 * @param h 代理处理器
	 * @param sc 父数
	 * @param interfaces 接口类集
	 * @return 类实例
	 */
	public static <O> O newInstance(InvocationHandler h, Class sc, Class... interfaces) {
		if (h == null) return null;
		if (existClass("org.springframework.cglib.proxy.Enhancer")) return SpringProxy.create(h, sc, interfaces);
		else if (existClass("net.sf.cglib.proxy.Enhancer")) return CglibProxy.create(h, sc, interfaces);
		else if (sc != null) throw new UnsupportedOperationException("unable to create proxy with super class");
		else if (Arrayard.isEmpty(interfaces)) return null;
		else return (O) Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, h);
	}

	/**
	 * 是否存在类
	 * @param cls 类名
	 * @return 是、否
	 */
	static boolean existClass(String cls) {
		Boolean ex = existCache.get(cls);
		if (ex == null) {
			ex = Boolean.FALSE;
			if (findClass(cls, Thread.currentThread().getContextClassLoader()) != null) ex = Boolean.TRUE;
			else if (findClass(cls, ProxyFactory.class.getClassLoader()) != null) ex = Boolean.TRUE;
			existCache.put(cls, ex);
		}
		return Boolean.TRUE.equals(ex);
	}
}

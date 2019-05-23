/*
 * @(#)ProxyFactory.java     2017-12-25
 */
package org.dommons.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.dommons.core.util.Arrayard;

/**
 * 代理工厂
 * @author demon 2017-12-25
 */
public class ProxyFactory {

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
		try {
			return Class.forName(cls, false, loader);
		} catch (ClassNotFoundException e) {
			return null;
		}
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
		if (findClass(cls, Thread.currentThread().getContextClassLoader()) != null) return true;
		else if (findClass(cls, ProxyFactory.class.getClassLoader()) != null) return true;
		return false;
	}
}

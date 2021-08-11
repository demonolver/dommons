/*
 * @(#)JavassistProxy.java     2021-08-11
 */
package org.dommons.core.proxy;

/**
 * Javassist 代理实现
 * @author demon 2021-08-11
 */
class JavassistProxy {

	/**
	 * 是否代理类
	 * @param clazz 类
	 * @return 是否代理
	 */
	public static boolean isProxyClass(Class<?> clazz) {
		return ProxyFactory.isProxy(clazz);
	}
}

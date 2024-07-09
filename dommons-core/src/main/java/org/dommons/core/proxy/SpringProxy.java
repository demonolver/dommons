/*
 * @(#)SpringProxy.java     2017-12-25
 */
package org.dommons.core.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.dommons.core.util.Arrayard;
import org.springframework.cglib.proxy.Enhancer;

/**
 * Spring 代理生成
 * @author demon 2017-12-25
 */
class SpringProxy {

	/**
	 * 生成代理
	 * @param h 代理处理器
	 * @param sc 父类
	 * @param interfaces 接口类
	 * @return 代理实例
	 */
	public static <O> O create(InvocationHandler h, Class sc, Class... interfaces) {
		Enhancer en = new Enhancer();
		if (sc != null) en.setSuperclass(sc);
		if (!Arrayard.isEmpty(interfaces)) en.setInterfaces(interfaces);
		en.setCallback(new SpringHandler(h));
		return (O) en.create();
	}

	/**
	 * 是否代理类
	 * @param clazz 类
	 * @return 是否代理
	 */
	public static boolean isProxyClass(Class<?> clazz) {
		return Enhancer.isEnhanced(clazz);
	}

	/**
	 * 获取代理处理器
	 * @param proxy 代理实例
	 * @return 代理处理器
	 */
	public static InvocationHandler getInvocationHandler(Object proxy) {
		try {
			Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
			field.setAccessible(true);
			Object h = field.get(proxy);
			if (h instanceof SpringHandler) return ((SpringHandler) h).h;
		} catch (Throwable t) { // ignored
		}
		return null;
	}

	/**
	 * Spring 代理处理器
	 * @author demon 2017-12-25
	 */
	protected static class SpringHandler implements org.springframework.cglib.proxy.InvocationHandler {
		private InvocationHandler h;

		public SpringHandler(InvocationHandler h) {
			this.h = h;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return h.invoke(proxy, method, args);
		}
	}
}

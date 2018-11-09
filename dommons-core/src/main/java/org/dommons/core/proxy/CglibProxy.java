/*
 * @(#)CglibProxy.java     2017-12-25
 */
package org.dommons.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.dommons.core.util.Arrayard;

import net.sf.cglib.proxy.Enhancer;

/**
 * CGLib 代理生成
 * @author demon 2017-12-25
 */
class CglibProxy {

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
		en.setCallback(new CglibHandler(h));
		return (O) en.create();
	}

	/**
	 * CGlib 代理处理器
	 * @author demon 2017-12-25
	 */
	protected static class CglibHandler implements net.sf.cglib.proxy.InvocationHandler {
		protected InvocationHandler h;

		public CglibHandler(InvocationHandler h) {
			this.h = h;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return h.invoke(proxy, method, args);
		}
	}
}

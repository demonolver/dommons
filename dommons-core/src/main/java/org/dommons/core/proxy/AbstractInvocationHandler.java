/*
 * @(#)AbstractInvocationHandler.java     2018-06-15
 */
package org.dommons.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 抽象代理执行器
 * @author demon 2018-06-15
 */
public abstract class AbstractInvocationHandler implements InvocationHandler {

	/**
	 * 执行 Object 通用方法
	 * @param method 方法
	 * @param args 参数集
	 * @return 结果
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected Object invokeNative(Method method, Object... args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (method != null && Object.class.equals(method.getDeclaringClass())) return method.invoke(this, args);
		return null;
	}
}

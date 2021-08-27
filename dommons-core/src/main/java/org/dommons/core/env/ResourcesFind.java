/*
 * @(#)ResourcesFind.java     2021-08-26
 */
package org.dommons.core.env;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.dommons.core.Environments;

/**
 * 资源查找
 * @author demon 2021-08-26
 */
public class ResourcesFind {

	/**
	 * 获取资源集
	 * @param cl 类加载
	 * @param name 资源名
	 * @return 资源集
	 * @throws IOException
	 */
	public static Enumeration<URL> getResources(ClassLoader cl, String name) throws IOException {
		return get().resources(cl, name);
	}

	/**
	 * 获取资源
	 * @param cl 类加载
	 * @param name 资源名
	 * @return 资源路径
	 */
	public static URL getResource(ClassLoader cl, String name) {
		return get().resource(cl, name);
	}

	/**
	 * 获取资源查找实例
	 * @return 资源查找实例
	 */
	protected static ResourcesFind get() {
		try {
			Class c = Environments.findClass("org.dommons.android.env.AndroidResources");
			if (c != null) return (ResourcesFind) c.newInstance();
		} catch (Throwable t) { // ignored
		}
		return new ResourcesFind();
	}

	protected ResourcesFind() {
		super();
	}

	protected Enumeration<URL> resources(ClassLoader cl, String name) throws IOException {
		if (cl == null) return ClassLoader.getSystemResources(name);
		else return cl.getResources(name);
	}

	protected URL resource(ClassLoader cl, String name) {
		if (cl == null) return ClassLoader.getSystemResource(name);
		else return cl.getResource(name);
	}
}

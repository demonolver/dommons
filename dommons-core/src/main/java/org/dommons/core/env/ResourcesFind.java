/*
 * @(#)ResourcesFind.java     2021-08-26
 */
package org.dommons.core.env;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.dommons.core.Environments;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Strongref;

/**
 * 资源查找
 * @author demon 2021-08-26
 */
public class ResourcesFind {

	static Ref<Boolean> $status;
	static Ref<Class> $cache;

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
	 * 获取资源查找实例
	 * @return 资源查找实例
	 */
	protected static ResourcesFind get() {
		Boolean s = $status == null ? null : $status.get();
		Class c = null;
		if (s == null) {
			c = getAndroidClass();
			$status = new Strongref(s = Boolean.valueOf(c != null));
		}
		if (Boolean.TRUE.equals(s)) {
			try {
				if (c != null) c = getAndroidClass();
				if (c != null) return (ResourcesFind) c.newInstance();
			} catch (Throwable t) { // ignored
			}
		}
		return new ResourcesFind();
	}

	static Class getAndroidClass() {
		Class c = $cache == null ? null : $cache.get();
		if (c == null) {
			try {
				c = Environments.findClass("org.dommons.android.env.AndroidResources");
			} catch (Throwable t) { // ignored
			}
			$cache = new Strongref(c);
		}
		return c;
	}

	protected ResourcesFind() {
		super();
	}

	protected URL resource(ClassLoader cl, String name) {
		if (cl == null) return ClassLoader.getSystemResource(name);
		else return cl.getResource(name);
	}

	protected Enumeration<URL> resources(ClassLoader cl, String name) throws IOException {
		if (cl == null) return ClassLoader.getSystemResources(name);
		else return cl.getResources(name);
	}
}

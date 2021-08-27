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
	 * @param cl
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static Enumeration<URL> getResources(ClassLoader cl, String name) throws IOException {
		return get().resources(cl, name);
	}

	/**
	 * 获取资源
	 * @param cl 
	 * @param name
	 * @return
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
		return cl.getResources(name);
	}

	protected URL resource(ClassLoader cl, String name) {
		return cl.getResource(name);
	}
}

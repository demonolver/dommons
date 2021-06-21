/*
 * @(#)_EnvFactory.java     2018-09-19
 */
package org.dommons.core.env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;

import org.dommons.core.proxy.ProxyFactory;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.core.util.beans.ObjectInstantiators;

/**
 * 环境配置工厂
 * @author demon 2018-09-19
 */
public final class _EnvFactory {

	static Properties properties;

	/**
	 * 获取环境配置集
	 * @return 配置集
	 */
	public static Properties properties() {
		if (properties == null) {
			synchronized (_EnvFactory.class) {
				if (properties == null) properties = new _EnvFactory().load();
			}
		}
		return properties;
	}

	protected _EnvFactory() {

	}

	/**
	 * 加载配置
	 * @return 配置集
	 */
	public Properties load() {
		Properties sys = System.getProperties();
		String[] loaders = loaders();
		if (loaders != null) {
			for (String l : loaders) {
				Properties p = load(l, sys);
				if (p != null) return p;
			}
		}
		return new DefaultEnvLoader().load(sys);
	}

	/**
	 * 加载扩展配置
	 * @param cl 加载器类名
	 * @param defaults 默认配置
	 * @return 配置集
	 */
	protected Properties load(String cl, Properties defaults) {
		try {
			Class c = ProxyFactory.findClass(cl);
			if (c != null && EnvironmentLoader.class.isAssignableFrom(c)) {
				EnvironmentLoader loader = ObjectInstantiators.newInstance(c);
				return loader.load(defaults);
			}
		} catch (Throwable t) { // ignored
		}
		return null;
	}

	protected String[] loaders() {
		Collection<String> ls = new LinkedHashSet();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> en = null;
			if (cl == null) en = ClassLoader.getSystemResources("META-INF/" + EnvironmentLoader.class.getName());
			else en = cl.getResources("META-INF/" + EnvironmentLoader.class.getName());
			while (en != null && en.hasMoreElements()) {
				URL u = en.nextElement();
				if (u == null) continue;
				try {
					read(u, ls);
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
		}
		return Arrayard.toArray(ls, String.class);
	}

	private void read(URL u, Collection<String> ls) throws IOException {
		InputStream is = null;
		try {
			is = u.openStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			for (String s = null; (s = r.readLine()) != null;) {
				if (!Stringure.isEmpty(s)) ls.add(Stringure.trim(s));
			}
		} finally {
			if (is != null) is.close();
		}
	}
}

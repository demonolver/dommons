/*
 * @(#)NLSFactory.java     2017-01-06
 */
package org.dommons.io.nls;

import java.util.Map;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.proxy.ProxyFactory;
import org.dommons.core.string.Stringure;

/**
 * 多语言包构造工厂
 * @author demon 2017-01-06
 */
public final class NLSFactory {

	private static final Map<Class, NLS> cache = new ConcurrentSoftMap();

	/**
	 * @param pack
	 * @param name
	 * @param cls
	 * @return
	 */
	public static <N extends NLS> N create(Package pack, String name, Class<N> cls) {
		return create(bundleName(pack, name), cls);
	}

	/**
	 * 多语言信息包
	 * @param bundleName 资源包名
	 * @param cls 类型
	 * @return 多语言信息包
	 */
	public static <N extends NLS> N create(String bundleName, Class<N> cls) {
		if (cls == null) return null;
		NLS nls = cache.get(cls);
		if (nls == null) {
			nls = create(NLSBundle.get(bundleName), cls);
			if (nls != null) {
				synchronized (cache) {
					cache.put(cls, nls);
				}
			}
		}
		return (N) nls;
	}

	/**
	 * 转换为资源包名
	 * @param pack 资源包
	 * @param name 资源名
	 * @return 资源名称
	 */
	static String bundleName(Package pack, String name) {
		StringBuilder buf = new StringBuilder();
		if (pack != null) buf.append(pack.getName());
		String n = Stringure.trim(name);
		int len = n.length();
		if (len > 0) {
			if (buf.length() > 0) buf.append('.');
			boolean s = false;
			for (int i = 0; i < len; i++) {
				char c = n.charAt(i);
				if (s) s = false;
				else if (c == '.') buf.append('\\');
				else if (c == '\\') s = true;
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * 创建多语言信息包
	 * @param bundle 多语言包
	 * @param cls 类型
	 * @return 多语言信息包
	 */
	static <N extends NLS> N create(NLSBundle bundle, Class<N> cls) {
		if (bundle == null) return null;
		Class sc = null, ic = null;
		if (cls.isInterface()) ic = cls;
		else sc = cls;
		return ProxyFactory.newInstance(new NLSInvoker(bundle), sc, ic);
	}

	private NLSFactory() {
	}
}

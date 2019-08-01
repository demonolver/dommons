/*
 * @(#)NLSInvoker.java     2017-01-09
 */
package org.dommons.io.nls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;

/**
 * 多语言信息反射获取器
 * @author demon 2017-01-09
 */
class NLSInvoker implements InvocationHandler {

	protected final NLSBundle bundle;
	private Map<String, NLSItem> iCache;

	public NLSInvoker(NLSBundle bundle) {
		this.bundle = bundle;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		if ("currentLocale".equals(name)) return NLSLocal.get();
		else if (nativeMethod(name, method)) return message(method.getReturnType(), Converter.F.convert(args[0], String.class));
		else return message(method.getReturnType(), name, args);
	}

	/**
	 * 获取信息
	 * @param rt 信息
	 * @param key 键值
	 * @param args 参数集
	 * @return 信息
	 */
	protected Object message(Class rt, String key, Object... args) {
		Locale l = NLSLocal.get();
		Object[] ps = null;
		if (args != null && args.length > 0) {
			int s = 0;
			if (args[0] instanceof Locale) {
				l = (Locale) args[0];
				s = 1;
			}
			if (args.length > s) {
				if (args.length - s == 1 && Arrayard.isArray(args[s])) {
					ps = new Object[Arrayard.length(args[s])];
					System.arraycopy(args[s], 0, ps, 0, ps.length);
				} else {
					ps = new Object[args.length - s];
					System.arraycopy(args, s, ps, 0, args.length - s);
				}
			}
		}
		return result(rt, l, key, ps);
	}

	/**
	 * 获取结果
	 * @param rt 结果类型
	 * @param l 语言环境
	 * @param key 键值
	 * @param ps 参数集
	 * @return 结果
	 */
	Object result(Class rt, Locale l, String key, Object... ps) {
		if (rt == null) {
		} else if (rt.isAssignableFrom(String.class)) {
			if (ps == null) return bundle.get(l, key);
			else return bundle.format(l, key).format(ps);
		} else if (rt.isAssignableFrom(NLSItem.class)) {
			if (ps == null) return item(l, key);
			else return new NLSItem(l, bundle, key, ps);
		}
		return null;
	}

	/**
	 * 生成多语言信息项
	 * @param l 语言环境
	 * @param key 键值
	 * @return 多语言信息项
	 */
	private NLSItem item(Locale l, String key) {
		String k = Stringure.join(':', l, key);
		if (iCache == null) iCache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));
		NLSItem item = iCache.get(k);
		if (item == null) iCache.put(k, item = new NLSItem(l, bundle, key));
		return item;
	}

	private boolean nativeMethod(String name, Method method) {
		if (!"get".equals(name) && !"item".equals(name)) return false;
		if (method == null) return false;
		Class[] pts = method.getParameterTypes();
		if (pts == null || pts.length != 1) return false;
		return String.class.equals(pts[0]);
	}
}

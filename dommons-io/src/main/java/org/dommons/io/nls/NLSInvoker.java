/*
 * @(#)NLSInvoker.java     2017-01-09
 */
package org.dommons.io.nls;

import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
		if (method.getDeclaringClass().equals(Object.class)) return doNativeObject(method, args, proxy);
		else if (method.getDeclaringClass().equals(AbstractNLS.class)) return doAbstract(method, args, proxy);
		String name = method.getName();
		if ("currentLocale".equals(name)) return NLSLocal.get();
		else if (nativeMethod(name, method))
			return message(method.getReturnType(), Converter.F.convert(Arrayard.get(args, 0), String.class));
		else return message(method.getReturnType(), name, args);
	}

	/**
	 * 执行抽象类方法
	 * @param method 方法体
	 * @param args 参数集
	 * @param proxy 代理对象实体
	 * @return 结果
	 */
	protected Object doAbstract(Method method, Object[] args, Object proxy) {
		if (method.getName().equals("startEvaluate") && PrintStream.class.equals(Arrayard.get(method.getParameterTypes(), 0)))
			return doEvaluate(args[0], proxy);
		return null;
	}

	/**
	 * 执行原生方法
	 * @param method 方法体
	 * @param args 参数集
	 * @param proxy 代理对象实体
	 * @return 结果
	 */
	protected Object doNativeObject(Method method, Object[] args, Object proxy) {
		Class<?> type = method.getReturnType();
		if ("toString".equals(method.getName()) && String.class.equals(type)) {
			return proxy.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
		} else if ("hashCode".equals(method.getName()) && int.class.equals(type)) {
			return this.hashCode();
		}
		return Converter.F.convert(null, type);
	}

	/**
	 * 获取信息
	 * @param rt 信息
	 * @param key 键值
	 * @param args 参数集
	 * @return 信息
	 */
	protected Object message(Class rt, String key, Object... args) {
		if (key == null || key.trim().isEmpty()) return null;
		Locale l = null;
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

	void doEvaluate(Class type, PrintStream out, Set<String> set) {
		if (NLS.class.equals(type) || Object.class.equals(type)) return;
		eval: if (type.isInterface() && NLS.class.isAssignableFrom(type)) {
			Method[] ms = type.getMethods();
			if (ms == null) break eval;
			for (Method m : ms) {
				if (m.getDeclaringClass().equals(NLS.class)) continue;
				String name = m.getName();
				if (!set.add(name) || nativeMethod(name, m)) continue;
				String v = bundle.get(null, name);
				if (name.equals(v)) out.println(name + " not found");
			}
		}
		Class[] is = type.getInterfaces();
		if (is == null) return;
		for (Class itype : is) {
			if (!set.add("type:" + itype.getName())) continue;
			doEvaluate(itype, out, set);
		}
	}

	Object doEvaluate(Object arg, Object proxy) {
		if (arg instanceof PrintStream) {
			PrintStream out = (PrintStream) arg;
			doEvaluate(proxy.getClass(), out, new HashSet());
		}
		return null;
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

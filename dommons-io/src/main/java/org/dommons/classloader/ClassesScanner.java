/*
 * @(#)ClassesScanner.java     2017-08-11
 */
package org.dommons.classloader;

import java.net.URL;

import org.dommons.core.string.Stringure;
import org.dommons.io.Pathfinder;

/**
 * 类扫描器
 * @author demon 2017-08-11
 */
public class ClassesScanner {

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param cl 类加载器
	 * @param filter 过滤器
	 * @param listener 扫描监听器
	 */
	public static void startScan(String base, ClassLoader cl, ClassScanFilter filter, ClassScanListener listener) {
		startScan(base, cl, filter, listener, null);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param cl 类加载器
	 * @param filter 过滤器
	 * @param listener 扫描监听器
	 * @param error 扫描异常监听器
	 */
	public static void startScan(
			String base, ClassLoader cl, ClassScanFilter filter, ClassScanListener listener, ClassErrorListener error) {
		base = Stringure.trim(base);
		if (base.isEmpty()) return;
		new ClassesScanner(base, cl).classScanFilter(filter).scan(listener, error);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param cl 类加载器
	 * @param listener 扫描监听器
	 */
	public static void startScan(String base, ClassLoader cl, ClassScanListener listener) {
		startScan(base, cl, listener, null);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param cl 类加载器
	 * @param listener 扫描监听器
	 * @param error 扫描异常监听器
	 */
	public static void startScan(String base, ClassLoader cl, ClassScanListener listener, ClassErrorListener error) {
		startScan(base, cl, null, listener, error);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param listener 扫描监听器
	 */
	public static void startScan(String base, ClassScanListener listener) {
		startScan(base, null, listener);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param listener 扫描监听器
	 * @param error 扫描异常监听器
	 */
	public static void startScan(String base, ClassScanListener listener, ClassErrorListener error) {
		startScan(base, null, listener, error);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param listener 扫描监听器
	 * @param error 扫描异常监听器
	 * @param filter 扫描异常监听器
	 */
	public static void startScan(String base, ClassScanListener listener, ClassErrorListener error, ClassScanFilter filter) {
		startScan(base, null, filter, listener, error);
	}

	/**
	 * 开始扫描
	 * @param base 基础包
	 * @param listener 扫描监听器
	 * @param filter 扫描过滤器
	 */
	public static void startScan(String base, ClassScanListener listener, ClassScanFilter filter) {
		startScan(base, null, filter, listener);
	}

	private final String base;
	private final ClassLoader cl;

	private ClassScanFilter filter;

	protected ClassesScanner(String base, ClassLoader cl) {
		this.cl = cl != null ? cl : Thread.currentThread().getContextClassLoader();
		this.base = base;
	}

	/**
	 * 执行扫描
	 * @param listener 监听器
	 */
	public void scan(ClassScanListener listener) {
		scan(listener, null);
	}

	/**
	 * 执行扫描
	 * @param listener 监听器
	 * @param error 异常监听器
	 */
	public void scan(ClassScanListener listener, ClassErrorListener error) {
		if (listener == null) return;
		String x = base.replace('.', '/');
		String regex = regex(x);
		URL[] us = Pathfinder.getResources(cl, regex);
		for (URL u : us) {
			if (filter != null && filter.onFilter(u)) continue;
			scan(u, x, listener, error);
		}
	}

	/**
	 * 设置类扫描过滤器
	 * @param filter 过滤器
	 * @return 类扫描器
	 */
	protected ClassesScanner classScanFilter(ClassScanFilter filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * 执行扫描
	 * @param u 路径
	 * @param head 前缀
	 * @param listener 监听器
	 * @param error 异常监听器
	 */
	protected void scan(URL u, String head, ClassScanListener listener, ClassErrorListener error) {
		String p = u.getProtocol(), ph = Stringure.subString(u.getPath(), 0, -6);
		Class cls = null;
		c: if ("file".equals(p)) {
			ph = ph.replace('\\', '/');
			int s = 0;
			do {
				s = ph.indexOf(head, s);
				if (s < 0) break;
				String n = ph.substring(s++).replace('/', '.');
				try {
					cls = loadClass(n);
				} catch (Throwable t) {
					if (error != null) error.onError(n, t);
				}
			} while (cls == null);
		} else if ("jar".equals(p)) {
			int i = ph.indexOf("!/");
			if (i < 0) break c;
			String n = ph.substring(i + 2).replace('/', '.');
			try {
				cls = loadClass(n);
			} catch (Throwable t) {
				if (error != null) error.onError(n, t);
			}
		}
		if (cls != null) listener.onScan(cls);
	}

	/**
	 * 加载类定义
	 * @param cls 类名
	 * @return 类定义
	 * @throws Throwable
	 */
	private Class loadClass(String cls) throws Throwable {
		if (filter != null && filter.onFilter(cls)) return null;
		try {
			return Class.forName(cls, false, cl);
		} catch (ClassNotFoundException e) { // ignored
		}
		return null;
	}

	/**
	 * 生成扫描路径表达式
	 * @param base 基础包
	 * @return 表达式
	 */
	private String regex(String base) {
		StringBuilder buf = new StringBuilder().append(base);
		if (buf.charAt(buf.length() - 1) != '/') buf.append('/');
		buf.append("**/*.class");
		return buf.toString();
	}

	/**
	 * 扫描异常监听器
	 * @author demon 2017-08-22
	 */
	public interface ClassErrorListener {
		/**
		 * 响应类扫描异常
		 * @param cls 类名
		 * @param t 异常
		 */
		public void onError(String cls, Throwable t);
	}

	/**
	 * 类扫描扫描过滤器
	 * @author demon 2017-11-02
	 */
	public interface ClassScanFilter {
		/**
		 * 是否过滤类名
		 * @param className 类名
		 * @return 是否过滤 <code>true</code> 不做扫描
		 */
		public boolean onFilter(String className);

		/**
		 * 是否过滤类文件路径
		 * @param u 文件路径
		 * @return 是否过滤 <code>true</code> 不做扫描
		 */
		public boolean onFilter(URL u);
	}

	/**
	 * 扫描监听器
	 * @author demon 2017-08-11
	 */
	public interface ClassScanListener {

		/**
		 * 响应类被扫描
		 * @param cls 类
		 */
		public void onScan(Class<?> cls);
	}
}

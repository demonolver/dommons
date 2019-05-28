/*
 * @(#)CommonEnvironment.java     2011-10-17
 */
package org.dommons.core;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.env._EnvFactory;
import org.dommons.core.string.Stringure;

/**
 * 通用工具集环境
 * @author Demon 2011-10-17
 */
public final class Environments {

	static final Map<String, Object> cache = new ConcurrentSoftMap();

	private static long[] time = new long[3];

	/**
	 * 获取默认字符集
	 * @return 字符集
	 */
	public static Charset defaultCharset() {
		final String key = "file.encoding";
		return getSpecial(key, Charset.class, new SpecialProvider<Charset>() {
			public Charset get() {
				String encoding = getProperty(key);
				try {
					if (encoding != null) return Charset.forName(encoding);
				} catch (Throwable e) {
					Silewarner.warn(Environments.class, "Find default charset [" + encoding + "]", e);
				}
				return Charset.defaultCharset();
			}
		});
	}

	/**
	 * 获取默认语言区域
	 * @return 语言区域
	 */
	public static Locale defaultLocale() {
		final String key = "user.locale";
		return getSpecial(key, Locale.class, new SpecialProvider<Locale>() {
			public Locale get() {
				String language = getProperty(key);
				try {
					if (language != null) {
						String lang = Stringure.trim(language).replace('-', '_');
						Locale[] ls = Locale.getAvailableLocales();
						for (Locale l : ls) {
							if (Stringure.equalsIgnoreCase(lang, l.getCountry())
									|| Stringure.equalsIgnoreCase(lang, Stringure.join('_', l.getCountry(), l.getLanguage()))) {
								return l;
							}
						}
						return (Locale) Locale.class.getField(language.toUpperCase()).get(null);
					}
				} catch (Throwable e) {
					Silewarner.warn(Environments.class, "Find default locale [" + language + "] fail", e);
				}
				return Locale.getDefault();
			}
		});
	}

	/**
	 * 获取默认时区
	 * @return 时区
	 */
	public static TimeZone defaultTimeZone() {
		final String key = "user.timezone";
		return getSpecial(key, TimeZone.class, new SpecialProvider<TimeZone>() {
			public TimeZone get() {
				String zoneID = getProperty(key);
				return zoneID == null ? TimeZone.getDefault() : TimeZone.getTimeZone(zoneID);
			}
		});
	}

	/**
	 * 查找目标类
	 * @param name 类名
	 * @return 类
	 */
	public static Class findClass(String name) {
		try {
			return Class.forName(name, false, Environments.class.getClassLoader());
		} catch (Throwable t) {
		}
		try {
			return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * 获取属性集
	 * @return 属性集
	 */
	public static Properties getProperties() {
		return _EnvFactory.properties();
	}

	/**
	 * 获取属性值
	 * @param key 属性键名
	 * @return 属性值
	 */
	public static String getProperty(String key) {
		return getProperties().getProperty(key);
	}

	/**
	 * 获取属性值
	 * @param key 属性键名
	 * @param def 默认值
	 * @return 属性值
	 */
	public static String getProperty(String key, String def) {
		return getProperties().getProperty(key, def);
	}

	/**
	 * 是否视窗系统
	 * @return 是、否
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os == null) return false;
		os = os.toLowerCase();
		return os.contains("window");
	}

	/**
	 * 获取 Java 版本
	 * @return 版本
	 */
	public static double javaVersion() {
		String[] ks = { "java.specification.version", "java.version" };
		for (final String key : ks) {
			Double v = getSpecial(key, Double.class, new SpecialProvider<Double>() {
				public Double get() {
					String jv = getProperty(key);
					if (Stringure.isEmpty(jv)) return null;
					try {
						Matcher m = Pattern.compile("[0-9]+(\\.[0-9]+)?").matcher(jv);
						if (m.find()) return Converter.F.convert(m.group(), double.class);
					} catch (Throwable e) {
						Silewarner.warn(Environments.class, "Get java version [" + jv + "]", e);
					}
					return null;
				}
			});
			if (v != null) return Converter.F.convert(v, double.class);
		}
		return 0;
	}

	/**
	 * 获取类纳秒时间
	 * @return 类纳秒时间
	 */
	public static long similarNano() {
		synchronized (time) {
			long now = System.currentTimeMillis();
			if (now > time[0]) {
				time[0] = now;
				time[1] = System.nanoTime();
				time[2] = now * 1000000;
			}
			return time[2] + (System.nanoTime() - time[1]);
		}
	}

	/**
	 * 线程休眠
	 * @param time 休眠时长
	 */
	public static void sleep(long time) {
		sleep(time, TimeUnit.MILLISECONDS);
	}

	/**
	 * 线程休眠
	 * @param time 休眠时长
	 * @param unit 时长单位
	 */
	public static void sleep(long time, TimeUnit unit) {
		if (unit == null) unit = TimeUnit.MILLISECONDS;
		time = unit.toMillis(time);
		for (long s = System.currentTimeMillis(), n = s;;) {
			try {
				Thread.sleep(time - n + s);
				break;
			} catch (Throwable t) { // ignored
			}
			n = System.currentTimeMillis();
			if (n - s >= time) break;
		}
	}

	/**
	 * 等待对象唤醒
	 * @param tar 目标对象
	 */
	public static void wait(Object tar) {
		wait(tar, 0);
	}

	/**
	 * 等待对象唤醒
	 * @param tar 目标对象
	 * @param time 等待时长
	 */
	public static void wait(Object tar, long time) {
		wait(tar, time, null);
	}

	/**
	 * 等待对象唤醒
	 * @param tar 目标对象
	 * @param time 等待时长
	 * @param unit 时长单位
	 */
	public static void wait(Object tar, long time, TimeUnit unit) {
		if (tar == null) return;
		if (unit == null) unit = TimeUnit.MILLISECONDS;
		time = unit.toMillis(time);
		for (long s = System.currentTimeMillis(), n = s;;) {
			try {
				synchronized (tar) {
					if (time > 0) tar.wait(time - n + s);
					else tar.wait();
					break;
				}
			} catch (Throwable t) { // ignored
			}
			n = System.currentTimeMillis();
			if (time > 0 && n - s >= time) break;
		}
	}

	/**
	 * 获取特殊值
	 * @param key 键名
	 * @param type 值类型
	 * @param provider 特殊值提供器
	 * @return 值
	 */
	protected static <V> V getSpecial(String key, Class<V> type, SpecialProvider<V> provider) {
		V v = Converter.F.convert(cache.get(key), type);
		get: if (v == null) {
			v = provider.get();
			if (v == null) break get;
			cache.put(key, v);
		}
		return v;
	}

	/**
	 * 特殊参数值提供器
	 * @param <V>
	 * @author demon 2018-09-19
	 */
	static interface SpecialProvider<V> {
		/**
		 * 获取特殊值
		 * @return 值
		 */
		V get();
	}
}

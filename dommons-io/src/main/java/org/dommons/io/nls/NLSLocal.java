/*
 * @(#)NLSLocal.java     2017-01-09
 */
package org.dommons.io.nls;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.dommons.core.Environments;
import org.dommons.core.Silewarner;
import org.dommons.core.collections.map.ci.CaseInsensitiveHashMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.prop.Bundles;

/**
 * 当前语言环境
 * @author demon 2017-01-09
 */
public class NLSLocal {

	static final ThreadLocal<String[]> local = new ThreadLocal();

	static Ref<Map<String, Locale>> ref;
	static Ref<String[]> dlref;

	/**
	 * 生成可接受语言环境集
	 * @return
	 */
	public static String acceptLanguage() {
		String[] ss = locales();
		return Stringure.join(',', ss).replace('_', '-').toLowerCase();
	}

	/**
	 * 解析可接受语言环境集
	 * @param lang 语言环境参数
	 */
	public static Locale[] acceptLanguage(String lang) {
		lang = Stringure.trim(lang);
		Collection<Locale> ls = new LinkedHashSet();
		if (!Stringure.isEmpty(lang)) {
			String[] xs = lang.split("(\\s*,\\s*)+");
			for (String x : xs) {
				String[] ss = Stringure.split(x, ';');
				for (String s : ss) {
					Locale l = parse(s.replace('-', '_'));
					if (l != null) ls.add(l);
				}
			}
		}
		return ls.isEmpty() ? null : Arrayard.toArray(ls, Locale.class);
	}

	/**
	 * 获取默认语言环境集
	 * @return 语言环境集
	 */
	public static String[] defaultLocales() {
		String[] ds = dlref == null ? null : dlref.get();
		if (ds == null) dlref = new Softref(ds = locales(Environments.defaultLocale(), Locale.SIMPLIFIED_CHINESE));
		return ds;
	}

	/**
	 * 获取当前语言环境
	 * @return 语言环境
	 */
	public static Locale get() {
		String[] ls = local.get();
		if (ls != null) {
			for (String l : ls) {
				Locale x = locale(l);
				if (x != null) return x;
			}
		}
		return Environments.defaultLocale();
	}

	/**
	 * 获取当前语言环境集
	 * @return 语言环境集
	 */
	public static String[] locales() {
		String[] ls = local.get();
		if (ls == null) ls = defaultLocales();
		return ls;
	}

	/**
	 * 转换语言环境集
	 * @param ls 语言环境集
	 * @return 语言环境串集
	 */
	public static String[] locales(Locale... ls) {
		Collection<String> x = new LinkedHashSet(), xl = new LinkedHashSet();
		if (ls != null) {
			for (Locale l : ls) {
				if (l == null) continue;
				x.add(Stringure.join('_', l.getLanguage(), l.getCountry()));
				x.add(l.getLanguage());
			}
			x.remove(Stringure.empty);
			xl.remove(Stringure.empty);
			x.addAll(xl);
		}
		if (x.isEmpty()) return null;
		return Arrayard.toArray(x, String.class);
	}

	/**
	 * 解析语言环境
	 * @param lang 语言环境名
	 * @return 语言环境
	 */
	public static Locale parse(String lang) {
		Locale l = ls().get(Stringure.trim(lang));
		try {
			if (l == null) l = Locale.forLanguageTag(lang.replace('_', '-'));
		} catch (Throwable t) { // ignored
		}
		return l == null ? null : new Locale(l.getLanguage(), l.getCountry(), l.getVariant());
	}

	/**
	 * 注册当前语言环境
	 * @param ls 语言环境
	 */
	public static void registerLocale(Locale... ls) {
		if (ls == null || ls.length < 1) local.remove();
		else local.set(locales(ls));
	}

	/**
	 * 获取可用语言环境集
	 * @return 语言环境集
	 */
	static Map<String, Locale> ls() {
		Map<String, Locale> map = ref == null ? null : ref.get();
		if (map == null) {
			synchronized (local) {
				Map<String, String[]> sm = scripts();
				map = new CaseInsensitiveHashMap<Locale>(true);
				Locale[] ls = Locale.getAvailableLocales();
				for (Locale l : ls) {
					String key = Stringure.join('_', l.getLanguage(), l.getCountry());
					map.put(key, l);
					String[] ss = sm.get(key.toLowerCase());
					if (ss != null) {
						for (String s : ss)
							map.put(s, l);
					}
				}
				ref = new Softref(map);
			}
		}
		return map;
	}

	/**
	 * 生成语言环境
	 * @param l 语言环境
	 * @return 语言环境
	 */
	private static Locale locale(String l) {
		String[] s = Stringure.split(l, '_');
		if (s.length > 2) return new Locale(s[0], s[1], s[2]);
		else if (s.length > 1) return new Locale(s[0], s[1]);
		else if (s.length > 0) return new Locale(s[0]);
		else return null;
	}

	/**
	 * 获取特殊语言环境对应集 (为了兼容一些 c 语言环境的语言参数)
	 * @return 对应集
	 */
	private static Map<String, String[]> scripts() {
		Map<String, String[]> map = new HashMap();
		ld: try {
			Properties ps = Bundles.loadByClassPath(null, "org/dommons/io/nls/locale.scripts");
			if (ps == null) break ld;
			Enumeration<Object> ks = ps.keys();
			if (ks == null) break ld;
			while (ks.hasMoreElements()) {
				String k = Converter.F.convert(ks.nextElement(), String.class);
				if (Stringure.isEmpty(k)) continue;
				String v = Stringure.trim(ps.getProperty(k));
				if (Stringure.isEmpty(v)) continue;
				String[] vs = v.split("(\\s*[,;:]\\s*)+");
				Collection<String> xvs = new HashSet();
				for (String x : vs)
					xvs.add(Stringure.trim(x).toLowerCase());
				xvs.remove(Stringure.empty);
				map.put(k.toLowerCase(), Arrayard.toArray(xvs, String.class));
			}
		} catch (IOException e) {
			Silewarner.warn(NLSLocal.class, e.toString());
		}
		return map;
	}
}

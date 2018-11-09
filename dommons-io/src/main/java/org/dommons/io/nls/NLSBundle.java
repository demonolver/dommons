/*
 * @(#)NLSBundle.java     2017-01-06
 */
package org.dommons.io.nls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.collections.map.Mapped;
import org.dommons.core.collections.map.ci.CaseInsensitiveHashMap;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.format.text.MessageFormat;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.Pathfinder;
import org.dommons.io.prop.Bundles;

/**
 * 多语言包
 * @author demon 2017-01-06
 */
class NLSBundle {

	static Map<String, NLSBundle> bs = new ConcurrentSoftMap();

	/**
	 * 获取多语言包
	 * @param bundleName 多语言包名
	 * @return 多语言包
	 */
	public static NLSBundle get(String bundleName) {
		String[] ss = Stringure.split(convertBundle(bundleName), "\\s*[\\\\\\/]+\\s*");
		StringBuilder buf = new StringBuilder();
		String last = null;
		for (String s : ss) {
			s = Stringure.trim(s);
			if (s.isEmpty()) continue;
			if (buf.length() > 0) buf.append('/');
			buf.append(last = s);
		}
		String name = buf.toString();
		NLSBundle b = bs.get(name);
		if (b == null) {
			b = new NLSBundle(name, links(name, last), bundles(name, last));
			bs.put(name, b);
		}
		return b;
	}

	/**
	 * 查找多语言文件集
	 * @param name 文件路径名
	 * @param last 文件名
	 * @return 文件集
	 */
	static Map<String, Collection<URL>> bundles(String name, String last) {
		URL[] us = Pathfinder.getResources(name + "*.properties");
		Map<String, Collection<URL>> map = new CaseInsensitiveHashMap();
		int pre = last.length();
		Pattern p1 = Pattern.compile("[\\/\\\\]([^\\/\\\\]+)\\.properties$"),
				p2 = Pattern.compile("[a-z]+([\\.\\_][a-z]+)?", Pattern.CASE_INSENSITIVE);
		for (URL u : us) {
			String p = u.getPath();
			Matcher m = p1.matcher(p);
			if (!m.find()) continue;
			String x = m.group(1);
			if (!x.startsWith(last)) continue;
			String px = Stringure.trim(Stringure.subString(x, pre));
			l: if (!px.isEmpty()) {
				if (px.charAt(0) == '.' || px.charAt(0) == '_') {
					px = Stringure.subString(px, 1);
					if (p2.matcher(px).matches()) {
						px = px.replace('.', '_');
						break l;
					}
				}
				continue;
			}
			Mapped.touch(map, px, LinkedList.class).add(u);
		}
		return map;
	}

	/**
	 * 转换资源包名
	 * @param bundleName 资源包名
	 * @return 转换后资源包名
	 */
	static String convertBundle(String bundleName) {
		StringBuilder buffer = new StringBuilder();
		int len = bundleName.length();
		for (int i = 0; i < len; i++) {
			char ch = bundleName.charAt(i);
			switch (ch) {
			case '\\':
				buffer.append(bundleName.charAt(++i));
				break;
			case '.':
				buffer.append('/');
				break;

			default:
				buffer.append(ch);
				break;
			}
		}
		return buffer.toString();
	}

	/**
	 * 获取语言映射集
	 * @param name 文件路径名
	 * @param last 文件名
	 * @return 映射集
	 */
	static Map<String, String> links(String name, String last) {
		URL[] us = Pathfinder.getResources(name + ".link");
		Map<String, String> map = new CaseInsensitiveHashMap(true);
		for (URL u : us) {
			try {
				Bundles.loadContent(map, u);
			} catch (IOException e) { // ignored
			}
		}
		return map;
	}

	private final String name;

	private final Map<String, Collection<URL>> us;
	private final Map<String, String> links;

	private Map<String, Map<String, String>> cs;
	private Map<String, MessageFormat> fs;

	public NLSBundle(String name, Map<String, String> links, Map<String, Collection<URL>> us) {
		this.name = Stringure.trim(name);
		this.us = us;
		this.links = links;
		this.cs = new ConcurrentSoftMap();
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof NLSBundle)) return false;
		NLSBundle b = (NLSBundle) obj;
		return name.equals(b.name);
	}

	/**
	 * 获取信息模板
	 * @param locale 语言环境
	 * @param key 键值
	 * @return 信息模板格式
	 */
	public MessageFormat format(Locale locale, String key) {
		String str = get(locale, key);
		if (fs == null) fs = new ConcurrentSoftMap();
		MessageFormat f = fs.get(str);
		if (f == null) fs.put(str, f = MessageFormat.compile(str));
		return f;
	}

	/**
	 * 获取信息
	 * @param locale 语言环境
	 * @param key 键值
	 * @return 信息
	 */
	public String get(Locale locale, String key) {
		key = Stringure.trim(key);
		if (Stringure.isEmpty(key)) return null;
		Collection<String> ls = new LinkedHashSet();
		if (locale != null) Arrayard.addAll(ls, NLSLocal.locales(locale));
		Arrayard.addAll(ls, NLSLocal.locales());
		ls.add(Stringure.empty);
		Arrayard.addAll(ls, NLSLocal.defaultLocales());
		String x = null;
		for (String l : ls) {
			x = load(l, key);
			if (x != null) break;
		}
		return (x == null) ? key : x;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return name;
	}

	/**
	 * 加载信息
	 * @param s 语言环境项
	 * @param key 信息键值
	 * @return 信息
	 */
	protected String load(String s, String key) {
		if (s == null) return null;
		Map<String, String> map = contexts(s);
		for (int i = 0; i < 2; i++) {
			String x = map.get(key);
			if (x != null) return x;
			if (key.contains("_")) key = key.replace('_', '.');
			else break;
		}
		return null;
	}

	/**
	 * 获取内容集
	 * @param s 语言环境
	 * @return 内容集
	 */
	Map<String, String> contexts(String s) {
		return contexts(s, new HashSet());
	}

	/**
	 * 获取内容集
	 * @param s 语言环境
	 * @param xs 已查找集 （防止重复嵌套）
	 * @return 内容集
	 */
	Map<String, String> contexts(String s, Collection<String> xs) {
		Map<String, String> map = cs.get(s);
		if (map == null) {
			map = new ConcurrentHashMap();
			Collection<URL> u = us.get(s);
			if (u != null) {
				load(u, map);
			} else if (links != null) {
				String t = links.get(s);
				if (!Stringure.isEmpty(t) && xs.add(t)) return contexts(t);
			}
			cs.put(s, map);
		}
		return map;
	}

	/**
	 * 加载信息文件集
	 * @param us 文件集
	 * @param map 内容集
	 */
	void load(Collection<URL> us, Map<String, String> map) {
		for (URL u : us) {
			try {
				InputStream is = null;
				try {
					is = u.openStream();
					Bundles.loadContent(map, is);
				} finally {
					if (is != null) is.close();
				}
			} catch (IOException e) { // ignored
			}
		}
	}
}

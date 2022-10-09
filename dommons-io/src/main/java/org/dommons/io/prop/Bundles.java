/*
 * @(#)Bundles.java     2011-10-25
 */
package org.dommons.io.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.dommons.core.Assertor;
import org.dommons.core.convert.Converter;
import org.dommons.core.number.Radix64.Radix64Digits;
import org.dommons.core.string.Charician;
import org.dommons.core.string.Stringure;
import org.dommons.io.Pathfinder;
import org.dommons.io.file.ContentWriter;
import org.dommons.io.file.FileRoboter;

/**
 * 属性集工具
 * @author Demon 2011-10-25
 */
public final class Bundles extends Radix64Digits {

	/**
	 * 将Properties存储的实际内容转换为显示内容
	 * @param context 实际内容
	 * @return 实际内容
	 */
	public static String decode(String context) {
		if (context == null) return null;
		int len = context.length();
		if (len == 0) return context;
		StringBuilder outBuffer = new StringBuilder(len);

		char[] in = context.toCharArray();
		for (int x = 0; x < len; x++) {
			char aChar = in[x];
			if (aChar == '\\') {
				if (x >= len - 1) break;
				aChar = in[++x];
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					if (x >= len - 4) break;
					for (int i = 0; i < 4; i++) {
						aChar = Character.toUpperCase(in[++x]);
						if (aChar < 0 || aChar >= converts.length) throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						int digit = converts[aChar];
						if (digit < 0 || digit >= 16) throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						value = (value << 4) + digit;
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't') aChar = '\t';
					else if (aChar == 'r') aChar = '\r';
					else if (aChar == 'n') aChar = '\n';
					else if (aChar == 'f') aChar = '\f';
					outBuffer.append(aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}

		return outBuffer.toString();
	}

	/**
	 * 将内容转换为Properties存储的实际内容
	 * @param context 内容
	 * @return 实际内容
	 */
	public static String encode(String context) {
		if (context == null) return null;
		int len = context.length();
		if (len == 0) return context;
		int bufLen = len * 2;
		if (bufLen < 0) bufLen = Integer.MAX_VALUE;
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = context.charAt(x);
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
			case ' ':
				outBuffer.append('\\');
				outBuffer.append(' ');
				break;
			case '\t':
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n':
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r':
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f':
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			case '=': // Fall through
			case ':': // Fall through
			case '#': // Fall through
			case '!':
				outBuffer.append('\\');
				outBuffer.append(aChar);
				break;
			default:
				if ((aChar < 0x20) || (aChar > 0x7e)) Charician.ascII(aChar, outBuffer);
				else outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	/**
	 * 获取首个有效属性值
	 * @param prop 属性集
	 * @param keys 键值集
	 * @return 属性值
	 */
	public static String get(Map prop, String... keys) {
		return get(prop, null, keys);
	}

	/**
	 * 获取有效属性值
	 * @param prop 属性集
	 * @param def 默认值
	 * @param keys 键值集
	 * @return 属性值
	 */
	public static String get(Map prop, String def, String[] keys) {
		if (keys == null) return def;
		String str = null;
		for (String k : keys) {
			str = getProperty(prop, k);
			if (str != null) break;
		}
		if (str == null) str = def;
		return str == null ? str : Stringure.convertVariables(str, prop);
	}

	/**
	 * 获取boolean型属性值
	 * @param prop 属性集
	 * @param def 默认值
	 * @param keys 键值集
	 * @return boolean值
	 */
	public static boolean getBoolean(Map prop, boolean def, String... keys) {
		String value = get(prop, keys);
		Boolean b = Converter.F.convert(value, Boolean.class);
		return b == null ? def : b.booleanValue();
	}

	/**
	 * 获取boolean型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return boolean值
	 */
	public static boolean getBoolean(Map prop, String key) {
		return Converter.F.convert(getString(prop, key), boolean.class);
	}

	/**
	 * 获取boolean型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认值
	 * @return boolean值
	 */
	public static boolean getBoolean(Map prop, String key, boolean def) {
		String value = getString(prop, key);
		try {
			return value == null ? def : Converter.P.convert(value, boolean.class);
		} catch (ClassCastException e) {
			return def;
		}
	}

	/**
	 * 获取boolean型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param criterion 判断标准
	 * @return boolean值
	 */
	public static boolean getBoolean(Map prop, String key, String criterion) {
		return criterion == null ? getString(prop, key) == null : criterion.equalsIgnoreCase(getString(prop, key));
	}

	/**
	 * 获取boolean型属性值
	 * @param prop 属性集
	 * @param criterion 判断标准
	 * @param keys 键值集
	 * @return boolean值
	 */
	public static boolean getBoolean(Map prop, String criterion, String... keys) {
		return criterion == null ? get(prop, keys) == null : criterion.equalsIgnoreCase(get(prop, keys));
	}

	/**
	 * 获取浮点型属性值
	 * @param prop 属性集
	 * @param def 默认值
	 * @param keys 键值集
	 * @return 浮点值
	 */
	public static double getDouble(Map prop, double def, String... keys) {
		String value = get(prop, keys);
		Number no = Converter.F.convert(value, Number.class);
		return no == null ? def : no.doubleValue();
	}

	/**
	 * 获取浮点型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 浮点值 无此属性时返回<code>0</code>
	 */
	public static double getDouble(Map prop, String key) {
		return getDouble(prop, key, 0);
	}

	/**
	 * 获取浮点型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认值
	 * @return 浮点值
	 */
	public static double getDouble(Map prop, String key, double def) {
		Number no = getNumber(prop, key);
		return no == null ? def : no.doubleValue();
	}

	/**
	 * 获取整型属性值
	 * @param prop 属性集
	 * @param def 默认值
	 * @param keys 键值集
	 * @return 整型值
	 */
	public static int getInteger(Map prop, int def, String... keys) {
		return (int) getLong(prop, def, keys);
	}

	/**
	 * 获取整型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 整型值 无此属性时返回<code>0</code>
	 */
	public static int getInteger(Map prop, String key) {
		return getInteger(prop, key, 0);
	}

	/**
	 * 获取整型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认值
	 * @return 整型值
	 */
	public static int getInteger(Map prop, String key, int def) {
		return (int) getLong(prop, key, def);
	}

	/**
	 * 获取长整型属性值
	 * @param prop 属性集
	 * @param def 默认值
	 * @param keys 键值集
	 * @return 长整型值
	 */
	public static long getLong(Map prop, long def, String... keys) {
		String v = get(prop, keys);
		Long l = Converter.F.convert(v, Long.class);
		return l == null ? def : l.longValue();
	}

	/**
	 * 获取长整型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 长整型值 无此属性时返回<code>0</code>
	 */
	public static long getLong(Map prop, String key) {
		return getLong(prop, key, 0);
	}

	/**
	 * 获取长整型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认值
	 * @return 长整型值
	 */
	public static long getLong(Map prop, String key, long def) {
		Number no = getNumber(prop, key);
		return no == null ? def : no.longValue();
	}

	/**
	 * 获取属性内容
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 字符串值 如无此属性则返回<code>null</code>
	 */
	public static String getProperty(Map prop, String key) {
		Assertor.F.notNull(prop);
		Assertor.F.notNull(key);

		if (prop instanceof Properties) {
			return ((Properties) prop).getProperty(key);
		} else {
			Object val = prop.get(key);
			return val != null && val instanceof String ? (String) val : null;
		}
	}

	/**
	 * 获取属性内容
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认内容
	 * @return 字符串值 如无此属性则返回<code>null</code>
	 */
	public static String getProperty(Map prop, String key, String def) {
		Assertor.F.notNull(prop);
		Assertor.F.notNull(key);

		if (prop instanceof Properties) {
			return ((Properties) prop).getProperty(key, def);
		} else {
			Object val = prop.get(key);
			return val != null && val instanceof String ? (String) val : def;
		}
	}

	/**
	 * 获取字符型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 字符串 如无此属性则返回<code>null</code>
	 */
	public static String getString(Map prop, String key) {
		return getString(prop, key, null);
	}

	/**
	 * 获取字符型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @param def 默认
	 * @return 字符串值 如无此属性则返回<code>null</code>
	 */
	public static String getString(Map prop, String key, String def) {
		String str = getProperty(prop, key, def);
		return str == null ? str : Stringure.convertVariables(str, prop);
	}

	/**
	 * 获取属性集所有键值
	 * @param prop 属性集
	 * @return 键值数组
	 */
	public static String[] keys(Properties prop) {
		Assertor.F.notNull(prop);

		Collection<String> list = new ArrayList(prop.size());
		for (Enumeration en = prop.propertyNames(); en.hasMoreElements();) {
			Object key = en.nextElement();
			if (!(key instanceof String)) continue;
			list.add((String) key);
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 读取属性集内容
	 * @param file 文件对象
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(File file) throws IOException {
		return load(null, file);
	}

	/**
	 * 读取属性集内容
	 * @param is 输入流
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(InputStream is) throws IOException {
		return load(null, is);
	}

	/**
	 * 读取属性集内容
	 * @param defaults 默认属性集
	 * @param is 文件对象
	 * @return 新属性集
	 * @throws IOException 文件读取出错
	 */
	public static Properties load(Map defaults, InputStream is) throws IOException {
		Properties def = null;
		if (defaults != null) {
			if (defaults instanceof Properties) {
				def = (Properties) defaults;
			} else {
				def = new Properties();
				def.putAll(defaults);
			}
		}
		Properties prop = new Properties(def);
		loadContent(prop, is);
		return prop;
	}

	/**
	 * 读取属性集内容
	 * @param defaults 默认属性集
	 * @param path 文件路径
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(Map defaults, String path) throws IOException {
		return load(defaults, Pathfinder.findPath(path));
	}

	/**
	 * 读取属性集内容
	 * @param defaults 默认属性集
	 * @param url 文件URL
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(Map defaults, URL url) throws IOException {
		Assertor.F.notNull(url);
		InputStream is = url.openStream();
		try {
			return load(defaults, is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 读取属性集内容
	 * @param defaults 默认属性集
	 * @param file 文件对象
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(Properties defaults, File file) throws IOException {
		Assertor.F.notNull(file);

		InputStream is = new FileInputStream(file);
		try {
			return load(defaults, is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 读取属性集内容
	 * @param path 路径
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(String path) throws IOException {
		return load(null, path);
	}

	/**
	 * 读取属性集内容
	 * @param url 文件URL
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties load(URL url) throws IOException {
		return load(null, url);
	}

	/**
	 * 读取属性集内容
	 * @param clazz 相关类
	 * @param path 资源路径
	 * @return 新属性集
	 * @throws IOException 文件不存在，或读取出错
	 */
	public static Properties loadByClassPath(Class clazz, String path) throws IOException {
		return load(Pathfinder.getResource(clazz, path));
	}

	/**
	 * 读取文件内容并解析为map结构
	 * @param is 文件流
	 * @return 内容结果
	 * @throws IOException 文件读取出错
	 */
	public static Map<String, String> loadContent(InputStream is) throws IOException {
		Assertor.F.notNull(is);

		Map<String, String> map = new HashMap<String, String>();
		loadContent(map, is);

		return map;
	}

	/**
	 * 读取文件内容并解析为映射表
	 * @param map 备存映射表
	 * @param is 文件流
	 * @throws IOException 文件读取出错
	 */
	public static void loadContent(Map map, InputStream is) throws IOException {
		Assertor.F.notNull(is);
		Assertor.F.notNull(map);

		new BundlerLoader(map).load(is);
	}

	/**
	 * 读取文件内容并解析为映射表
	 * @param map 备存映射表
	 * @param url 文件路径
	 * @throws IOException
	 */
	public static void loadContent(Map map, URL url) throws IOException {
		Assertor.F.notNull(url);
		Assertor.F.notNull(map);

		InputStream is = null;
		try {
			is = url.openStream();
			new BundlerLoader(map).load(is);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 保存属性集
	 * @param prop 属性集
	 * @param file 文件对象
	 * @throws IOException 保存出错
	 */
	public static void store(final Map prop, File file) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");

		FileRoboter.write(file, new ContentWriter() {
			public void write(OutputStream out) throws IOException {
				store(prop, out);
			}
		});
	}

	/**
	 * 保存属性集
	 * @param prop 属性集
	 * @param out 输出对象
	 * @throws IOException
	 */
	public static void store(Map prop, OutputStream out) throws IOException {
		Assertor.F.notNull(prop, "The properties must not be null!");
		Assertor.F.notNull(out, "The output stream must not be null!");

		Properties p = null;
		if (prop instanceof Properties) {
			p = (Properties) prop;
		} else {
			p = new Properties();
			p.putAll(prop);
		}
		p.store(out, null);
	}

	/**
	 * 保存属性集
	 * @param prop 属性集
	 * @param path 保存文件路径
	 * @throws IOException 保存出错
	 */
	public static void store(Map prop, String path) throws IOException {
		store(prop, Pathfinder.findFile(path));
	}

	/**
	 * 保存属性集
	 * @param prop 属性集
	 * @param url 保存文件路径 保存出错
	 * @throws IOException
	 */
	public static void store(Map prop, URL url) throws IOException {
		store(prop, Pathfinder.getFile(url));
	}

	/**
	 * 获取数字型属性值
	 * @param prop 属性集
	 * @param key 属性键值
	 * @return 数字型值 如无此属性或值不为数字型则返回<code>null</code>
	 */
	private static Number getNumber(Map prop, String key) {
		String value = getString(prop, key);
		if (value != null) {
			try {
				return Converter.P.convert(value, Number.class);
			} catch (ClassCastException e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * 构造函数
	 */
	protected Bundles() {}

	/**
	 * 属性集内容读取器
	 * @author Demon 2011-10-25
	 */
	static class BundlerLoader extends Properties {

		private static final long serialVersionUID = -5189392331260443705L;

		private final Map target;

		/**
		 * 构造函数
		 * @param target 目标
		 */
		public BundlerLoader(Map target) {
			super();
			this.target = target;
		}

		public synchronized Object put(Object key, Object value) {
			return target.put(key, value);
		}
	}
}

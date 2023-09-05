/*
 * @(#)AbstractLocaleConverter.java     2012-6-25
 */
package org.dommons.core.convert.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.dommons.core.Environments;
import org.dommons.core.collections.map.ci.CaseInsensitiveHashMap;
import org.dommons.core.env.ResourcesFind;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;

/**
 * 抽象多语言区域转换器
 * @author Demon 2012-6-25
 */
public abstract class AbstractLocaleConverter {

	/** 语言区域映射表缓存 */
	protected static Ref<Map<String, Locale>> ref;

	/**
	 * 加载资源集
	 * @param clz 资源类
	 * @param res 资源名
	 * @return 属性集
	 * @throws IOException
	 */
	protected static Properties load(Class clz, String res) throws IOException {
		Enumeration<URL> en = ResourcesFind.getResources(clz.getClassLoader(), toResourceName(clz, res));
		Properties props = new Properties();
		if (en != null) {
			while (en.hasMoreElements()) {
				try {
					InputStream in = en.nextElement().openStream();
					try {
						props.load(in);
					} finally {
						if (in != null) in.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return props;
	}

	/**
	 * 获取语言区域
	 * @param locale 语言区域名称
	 * @return 语言区域
	 */
	protected static Locale locale(String locale) {
		Locale l = locales().get(locale);
		return l == null ? Environments.defaultLocale() : l;
	}

	/**
	 * 获取语言区域集
	 * @return 语言区域集
	 */
	private static Map<String, Locale> locales() {
		Map<String, Locale> locales = ref == null ? null : ref.get();
		if (locales == null) {
			locales = new CaseInsensitiveHashMap(true);
			for (Locale locale : Locale.getAvailableLocales()) {
				locales.put(locale.toString(), locale);
			}
			ref = new Softref(locales);
		}
		return locales;
	}

	/**
	 * 转换资源路径
	 * @param clz 资源类
	 * @param res 资源名
	 * @return 资源路径
	 */
	private static String toResourceName(Class clz, String res) {
		if (res.length() > 0 && res.charAt(0) == '/') return res.substring(1);

		String qualifiedClassName = clz.getName();
		int classIndex = qualifiedClassName.lastIndexOf('.');
		if (classIndex == -1) return res; // from a default package
		return qualifiedClassName.substring(0, classIndex + 1).replace('.', '/') + res;
	}
}
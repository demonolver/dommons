/*
 * @(#)SQLDialect.java     2012-2-11
 */
package org.dommons.db.jdbc;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.dommons.core.convert.Converter;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.io.Pathfinder;
import org.dommons.io.prop.Bundles;

/**
 * SQL 方言
 * @author Demon 2012-2-11
 */
class SQLDialect {

	static final String path = "classpath:org/dommons/db/jdbc.dialect.properties";
	static final String defaultType = "default";

	private static Ref<Map> prop;

	/**
	 * 构造函数
	 * @param type 数据源类型
	 * @param version 数据源版本
	 * @param content 内容类型
	 * @return 模板串
	 */
	public static String getSQLTemplate(String type, String version, String content) {
		Map prop = getProperties();
		String value = null;
		if (type != null) {
			type = type.toLowerCase();
			if (version != null) {
				version = version.toLowerCase();
				value = Bundles.getProperty(prop, type + '.' + version + '.' + content);
			}
			if (value == null) value = Bundles.getProperty(prop, type + '.' + content);
		}
		if (value == null) value = Bundles.getProperty(prop, defaultType + '.' + content);
		if (value == null) throw new IllegalArgumentException(type + '.' + version + '.' + content);
		return value;
	}

	/**
	 * 获取属性集
	 * @return 属性集
	 */
	protected static Map getProperties() {
		try {
			Map m = prop == null ? null : prop.get();
			if (m == null) {
				m = new HashMap();
				URL u = Pathfinder.findPath(path);
				if (u != null) Bundles.loadContent(m, u);
				prop = new Softref(m);
			}
			return m;
		} catch (IOException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
	}
}

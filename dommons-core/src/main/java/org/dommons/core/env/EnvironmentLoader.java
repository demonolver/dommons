/*
 * @(#)EnvironmentLoader.java     2018-09-19
 */
package org.dommons.core.env;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 环境加载器
 * @author demon 2018-09-19
 */
public abstract class EnvironmentLoader {

	protected static final Collection<EnvItem> envs;

	static {
		List<EnvItem> items = new ArrayList();
		items.add(new EnvItem("org/dommons", "dommons.*\\.properties"));
		items.add(new EnvItem("META-INF/dommons", ".*\\.properties"));
		items.add(new EnvItem("META-INF", "dommons.*\\.properties"));
		items.add(new EnvItem("dommons.properties"));
		items.add(new EnvItem("system.properties"));
		envs = Collections.unmodifiableList(items);
	}

	/**
	 * 创建新属性集
	 * @param prop 父属性集
	 * @return 新属性集
	 */
	protected static Properties create(Properties prop) {
		return new EnvProp(prop);
	}

	/**
	 * 加载默认环境变量集
	 * @param defaults 默认配置
	 * @return 配置集
	 */
	protected static Properties loadEnvs(Properties defaults) {
		return new DefaultEnvLoader().load(defaults);
	}

	/**
	 * 加载配置集
	 * @param defaults 默认配置
	 * @return 配置集
	 */
	public abstract Properties load(Properties defaults);

	/**
	 * 加载属性
	 * @param prop 属性集
	 * @param is 文件
	 * @throws IOException
	 */
	protected void load(Properties prop, InputStream is) throws IOException {
		if (prop == null || is == null) return;
		try {
			prop.load(is);
		} finally {
			is.close();
		}
	}

	/**
	 * 转换路径
	 * @param url 路径地址
	 * @return 路径
	 */
	protected String path(URL url) {
		String path = url.getPath();
		try {
			return URLDecoder.decode(path, "utf8");
		} catch (IOException e) {
			return path;
		}
	}

	/**
	 * 环境项
	 * @author demon 2018-09-19
	 */
	protected static class EnvItem {

		public final String parent;
		public final String pattern;

		public EnvItem(String path) {
			this(path, null);
		}

		public EnvItem(String parent, String pattern) {
			this.parent = parent;
			this.pattern = pattern;
		}
	}

	/**
	 * 环境配置集
	 * @author demon 2018-09-19
	 */
	protected static class EnvProp extends Properties {

		private static final long serialVersionUID = 5017196406559259600L;

		public EnvProp(Properties defaults) {
			super(defaults);
		}

		public synchronized Object get(Object key) {
			return (key != null && key instanceof String) ? getProperty(String.valueOf(key)) : super.get(key);
		}
	}
}

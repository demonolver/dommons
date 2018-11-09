/*
 * @(#)NLSContents.java     2011-10-25
 */
package org.dommons.io.message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.collections.CaseInsensitiveWrapper;
import org.dommons.core.collections.stack.ArrayStack;
import org.dommons.core.collections.stack.Stack;
import org.dommons.core.string.Stringure;
import org.dommons.io.Pathfinder;
import org.dommons.io.prop.Bundles;
import org.dommons.log.LoggerFactory;

/**
 * 国际化信息内容
 * @author Demon 2011-10-25
 */
class NLSContents {

	static final char[] Separators = { '_', '.' };
	static final String[] BUNDLE_SUFFIX = { ".properties", ".contents" };

	/**
	 * 读取内容
	 * @param pack 资源包
	 * @param name 资源名
	 * @return 信息内容
	 */
	public static NLSContents load(Package pack, String name) {
		return load(pack, name, (Locale) null);
	}

	/**
	 * 读取内容
	 * @param pack 资源包
	 * @param name 资源名
	 * @param locale 语言区域
	 * @return 信息内容
	 */
	public static NLSContents load(Package pack, String name, Locale locale) {
		return load(toBundleName(pack, name), locale);
	}

	/**
	 * 读取内容
	 * @param pack 资源包
	 * @param name 资源名
	 * @param nl 语言区域名
	 * @return 信息内容
	 */
	public static NLSContents load(Package pack, String name, String nl) {
		return load(toBundleName(pack, name), nl);
	}

	/**
	 * 读取内容
	 * @param bundleName 资源包名
	 * @return 信息内容
	 */
	public static NLSContents load(String bundleName) {
		return load(bundleName, (Locale) null);
	}

	/**
	 * 读取内容
	 * @param bundleName 资源包名
	 * @param locale 语言区域
	 * @return 信息内容
	 */
	public static NLSContents load(String bundleName, Locale locale) {
		if (locale == null) locale = Environments.defaultLocale();
		return load(bundleName, locale.toString());
	}

	/**
	 * 读取内容
	 * @param bundleName 资源包名
	 * @param nl 语言区域
	 * @return 信息内容
	 */
	public static NLSContents load(String bundleName, String nl) {
		Assertor.F.notEmpty(bundleName, "The bundle name is must not be empty");
		if (Stringure.isEmpty(nl)) nl = Environments.defaultLocale().toString();
		return new NLSContents(bundleName, nl);
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
	 * 转换为资源包名
	 * @param pack 资源包
	 * @param name 资源名
	 * @return 资源名称
	 */
	static String toBundleName(Package pack, String name) {
		Assertor.F.notNull(pack);
		StringBuilder buffer = new StringBuilder();
		buffer.append(pack.getName());
		String n = Stringure.trim(name);
		int len = n.length();
		if (len > 0) {
			buffer.append('.');
			boolean s = false;
			for (int i = 0; i < len; i++) {
				char c = n.charAt(i);
				if (s) {
					buffer.append(c);
					s = false;
				} else {
					switch (c) {
					case '.':
						buffer.append('\\');
						break;
					case '\\':
						s = true;
						break;
					}
					buffer.append(c);
				}
			}
		}
		return buffer.toString();
	}

	private Map<String, String> contentMap;
	private final String bundleName;
	private final String nl;

	/**
	 * 构造函数
	 * @param bundleName 资源包名
	 * @param nl 语言区域
	 */
	protected NLSContents(String bundleName, String nl) {
		this.bundleName = bundleName;
		this.nl = nl;
	}

	/**
	 * 查找消息内容
	 * @param key 内容键值
	 * @return 消息内容
	 */
	public String getMessage(String key) {
		if (Stringure.isEmpty(key)) return null;
		init();
		return contentMap.get(key.trim());
	}

	/**
	 * 初始化
	 */
	protected void init() {
		if (contentMap != null) return;
		synchronized (this) {
			if (contentMap != null) return;
			Map<String, String> map = CaseInsensitiveWrapper.wrap(new HashMap(), false);

			String nl = this.nl;
			String name = convertBundle(bundleName);
			Stack<String> stack = new ArrayStack(8);
			while (true) {
				innerBundle(stack, name, nl, true);
				int lastSeparator = nl.lastIndexOf(Separators[0]);
				if (lastSeparator == -1) break;
				nl = nl.substring(0, lastSeparator);
			}
			innerBundle(stack, name, "", false);

			loadContents(stack, map);
			this.contentMap = map;
		}
	}

	/**
	 * 加入资源包
	 * @param stack 包集合堆栈
	 * @param bundle 资源包
	 * @param nl 语言区域
	 * @param separator 是否包含连接符
	 */
	protected void innerBundle(Stack<String> stack, String bundle, String nl, boolean separator) {
		for (String suffix : BUNDLE_SUFFIX) {
			if (separator) {
				for (char sep : Separators) {
					stack.push(bundle + sep + nl + suffix);
				}
			} else {
				stack.push(bundle + nl + suffix);
			}
		}
	}

	/**
	 * 读取内容
	 * @param stack 包集合堆栈
	 * @param map 内容映射表
	 */
	protected void loadContents(Stack<String> stack, Map<String, String> map) {
		while (!stack.isEmpty()) {
			String path = stack.pop();
			for (URL url : Pathfinder.getResources(path)) {
				try {
					InputStream in = url.openStream();
					try {
						Bundles.loadContent(map, in);
					} finally {
						if (in != null) in.close();
					}
				} catch (IOException e) {
					LoggerFactory.getInstance().getLogger(NLSContents.class).error(e, "load nls content fail!");
				}
			}
		}
	}
}

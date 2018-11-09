/*
 * @(#)NLSMessages.java     2011-10-25
 */
package org.dommons.io.message;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.util.Arrayard;

/**
 * 国际化的消息内容集
 * @author Demon 2011-10-25
 */
public final class NLSMessages extends AbstractMessages {

	// 使用弱引用的映射来做缓存
	private static final ConcurrentMap<String, NLSMessages> cache = new ConcurrentSoftMap();

	/**
	 * 获取消息内容集实例
	 * @param pack 资源包
	 * @param name 资源名
	 * @return 消息内容集
	 */
	public static NLSMessages getInstance(Package pack, String name) {
		return getInstance(NLSContents.toBundleName(pack, name));
	}

	/**
	 * 获取消息内容集实例
	 * @param pack 资源包
	 * @param name 资源名
	 * @param locale 语言区域
	 * @return 消息内容集
	 */
	public static NLSMessages getInstance(Package pack, String name, Locale locale) {
		return getInstance(NLSContents.toBundleName(pack, name), locale);
	}

	/**
	 * 获取消息内容集实例
	 * @param pack 资源包
	 * @param name 资源名
	 * @param nl 语言区域名
	 * @return 消息内容表
	 */
	public static NLSMessages getInstance(Package pack, String name, String nl) {
		return getInstance(NLSContents.toBundleName(pack, name), nl);
	}

	/**
	 * 获取消息内容集实例
	 * @param bundleName 消息资源包名
	 * @return 消息内容集
	 */
	public static NLSMessages getInstance(String bundleName) {
		return getInstance(bundleName, Environments.getProperty("common.nls", Environments.defaultLocale().toString()));
	}

	/**
	 * 获取消息内容集实例
	 * @param bundleName 消息资源包名
	 * @param locale 语言区域
	 * @return 消息内容集
	 */
	public static NLSMessages getInstance(String bundleName, Locale locale) {
		Assertor.F.notNull(locale, "The locale is must not be null");
		return getInstance(bundleName, locale.toString());
	}

	/**
	 * 获取消息内容集实例
	 * @param bundleName 消息资源包名
	 * @param nl 语言区域名 如: "en", "zh_CN"
	 * @return 消息内容表
	 */
	public static NLSMessages getInstance(String bundleName, String nl) {
		Assertor.F.notEmpty(bundleName, "The name of message bundle is must not be empty");
		Assertor.F.notEmpty(nl, "The nl is must not be empty");
		String name = bundleName + NLSContents.Separators[0] + nl;
		NLSMessages messages = cache.get(name);
		if (messages == null) {
			messages = cache.get(name);
			if (messages == null) {
				messages = new NLSMessages(NLSContents.load(bundleName, nl));
				NLSMessages old = cache.putIfAbsent(name, messages);
				messages = Arrayard.theOne(old, messages);
			}
		}
		return messages;
	}

	private final NLSContents contents;

	/**
	 * 构造函数
	 * @param contents 信息内容
	 */
	protected NLSMessages(NLSContents contents) {
		this.contents = contents;
	}

	public String getMessage(String key) {
		Assertor.F.notEmpty(key, "The key is must not be empty!");

		// 读取内容信息
		String message = contents.getMessage(key);

		// 检查内容是否存在
		if (message == null) {
			if (Boolean.getBoolean("SYSTEM_DEBUG")) {
				// 调试模式下,不存在内容以键值代替
				message = key;
			} else {
				throw new IllegalArgumentException("Not found the message which mapping for this key! Key : " + key);
			}
		}

		return message;
	}
}

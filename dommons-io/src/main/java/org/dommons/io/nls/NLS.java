/*
 * @(#)NLS.java     2017-01-06
 */
package org.dommons.io.nls;

import java.util.Locale;

import org.dommons.core.env.ProguardIgnore;

/**
 * 多语言信息接口
 * @author demon 2017-01-06
 * @see NLSFactory#create(String, Class)
 * @see NLSFactory#create(Package, String, Class)
 */
public interface NLS extends ProguardIgnore {

	/**
	 * 获取当前语言环境
	 * @return 语言环境
	 */
	public Locale currentLocale();

	/**
	 * 获取信息
	 * @param key 信息键值
	 * @return 信息
	 */
	public String get(String key);

	/**
	 * 获取多语言信息项
	 * @param key 信息键值
	 * @return 信息项
	 */
	public NLSItem item(String key);
}

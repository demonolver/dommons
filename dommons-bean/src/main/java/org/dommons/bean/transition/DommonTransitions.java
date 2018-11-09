/*
 * @(#)DommonTransitions.java     2012-7-19
 */
package org.dommons.bean.transition;

import java.net.URL;

import org.dommons.bean.handler.DommonBeanBuilder;

/**
 * 通用数据对象转换集
 * @author Demon 2012-7-19
 */
public final class DommonTransitions {

	/**
	 * 构建默认转换器
	 * @param builder 通用数据对象构建器
	 * @return 转换器
	 */
	public static DommonTransition defaultTransition(DommonBeanBuilder builder) {
		return new DefaultTransition(builder);
	}

	/**
	 * 构建转换器
	 * @param url 配置路径
	 * @param builder 通用数据对象构建器
	 * @return 转换器
	 */
	public static DommonTransition load(URL url, DommonBeanBuilder builder) {
		// TODO
		return null;
	}

	private DommonTransitions() {
	}
}

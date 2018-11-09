/*
 * @(#)DommonBeanFactory.java     2012-7-12
 */
package org.dommons.bean;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;

import org.dommons.bean.handler.DommonBeanBuilder;
import org.dommons.bean.handler.DommonBeanBuilder.DommonTransitionProvider;
import org.dommons.bean.transition.DommonTransition;
import org.dommons.bean.transition.DommonTransitions;

/**
 * 通用数据对象处理器
 * @author Demon 2012-7-12
 */
public final class DommonBeanician extends DommonTransitionProvider {

	static Reference<DommonBeanician> ref;

	/**
	 * 获取默认处理器实例
	 * @return 处理器实例
	 */
	public static DommonBeanician defaultance() {
		DommonBeanician instance = ref == null ? null : ref.get();
		if (instance == null) ref = new WeakReference(instance = new DommonBeanician(null));
		return instance;
	}

	protected final DommonTransition transition;
	protected final DommonBeanBuilder builder;

	/**
	 * 构造函数
	 * @param url 定义文件路径
	 */
	public DommonBeanician(URL url) {
		this.builder = createBuilder();
		transition = url == null ? DommonTransitions.defaultTransition(builder) : DommonTransitions.load(url, builder);
	}

	/**
	 * 转换数据对象
	 * @param bean 源数据对象
	 * @param type 目标类型
	 * @return 目标数据对象
	 */
	public <T> T convert(Object bean, Class<T> type) {
		return transition.transform(bean, type);
	}

	/**
	 * 复制数据对象
	 * @param source 源数据对象
	 * @param target 目标数据对象
	 * @return 目标数据对象
	 */
	public <T> T copy(Object source, T target) {
		transition.duplicate(source, target);
		return target;
	}

	/**
	 * 加载数据对象为通用数据对象
	 * @param bean 目标数据对象
	 * @return 通用数据对象
	 */
	public <E> DommonBean<E> load(E bean) {
		return builder.load(bean);
	}

	protected DommonTransition getTransition() {
		return transition;
	}
}

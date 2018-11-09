/*
 * @(#)DommonProperty.java     2012-7-13
 */
package org.dommons.bean;

/**
 * 通用数据属性项
 * @author Demon 2012-7-13
 */
public interface DommonProperty {

	/**
	 * 是否可读
	 * @return 是、否
	 */
	public boolean readable();

	/**
	 * 是否可写
	 * @return 是、否
	 */
	public boolean writable();

	/**
	 * 获取属性值
	 * @return 属性值
	 */
	public Object get();

	/**
	 * 获取属性值
	 * @param type 目标类型
	 * @return 属性值
	 * @throws ClassCastException
	 */
	public <T> T get(Class<T> type) throws ClassCastException;

	/**
	 * 获取属性名
	 * @return 属性名
	 */
	public String getName();

	/**
	 * 设置属性值
	 * @param value
	 */
	public void set(Object value);
}

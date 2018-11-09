/*
 * @(#)DommonBean.java     2012-7-12
 */
package org.dommons.bean;

/**
 * 通用数据对象
 * @author Demon 2012-7-12
 */
public interface DommonBean<E> {

	/**
	 * 数据对象实体
	 * @return 对象实体
	 */
	public E entity();

	/**
	 * 获取属性值
	 * @param property 属性名
	 * @return 属性值
	 */
	public Object get(String property);

	/**
	 * 获取属性值
	 * @param property 属性名
	 * @param type 值类型
	 * @return 属性值
	 */
	public <P> P get(String property, Class<P> type) throws ClassCastException;

	/**
	 * 设置属性值
	 * @param property 属性名
	 * @param value 属性值
	 * @throws ClassCastException
	 */
	public void set(String property, Object value) throws ClassCastException;
	
	/**
	 * 获取属性项
	 * @param name 属性名
	 * @return 属性项
	 */
	public DommonProperty getProperty(String name);
}

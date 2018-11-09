/*
 * @(#)XAttribute.java     2011-11-1
 */
package org.dommons.dom.bean;


/**
 * XML 属性项
 * @author Demon 2011-11-1
 */
public interface XAttribute extends XBean {

	/**
	 * 获取属性名
	 * @return 属性名
	 */
	String getName();

	/**
	 * 获取内容
	 * @return 内容
	 */
	String getValue();

	/**
	 * 设置属性内容
	 * @param value 内容
	 * @return 属性项
	 */
	XAttribute setValue(String value);
}
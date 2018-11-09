/*
 * @(#)XElement.java     2011-11-1
 */
package org.dommons.dom.bean;

import java.util.List;

/**
 * XML 叶子节点
 * @author Demon 2011-11-1
 */
public interface XElement extends XNode<XElement> {

	/**
	 * 添加属性项
	 * @param attribute 属性项
	 * @return 当前节点对象
	 */
	XElement addAttribute(XAttribute attribute);

	/**
	 * 添加注释
	 * @param comment 注释
	 * @return 当前节点对象
	 */
	XElement addComment(String comment);

	/**
	 * 获取属性内容
	 * @param name 属性名
	 * @return 属性内容
	 */
	String attribute(String name);

	/**
	 * 获取属性集
	 * @return 属性集
	 */
	List<XAttribute> attributes();

	/**
	 * 获取节点名
	 * @return 节点名
	 */
	String getName();

	/**
	 * 获取命名空间
	 * @return 命名空间
	 */
	String getNamespace();

	/**
	 * 获取内容
	 * @return 内容
	 */
	String getText();

	/**
	 * 是否含有指定属性项
	 * @param name 属性名
	 * @return 是、否
	 */
	boolean hasAttribute(String name);

	/**
	 * 获取父节点
	 * @return 父节点
	 */
	XElement parent();

	/**
	 * 移除属性项
	 * @param attribute 属性项
	 * @return 是、否
	 */
	boolean remove(XAttribute attribute);

	/**
	 * 移除属性项
	 * @param name 属性名
	 * @return 被移除属性项
	 */
	XAttribute removeAttribute(String name);

	/**
	 * 设置属性
	 * @param name 属性名
	 * @param value 属性内容
	 * @return 节点对象
	 */
	XElement setAttribute(String name, String value);

	/**
	 * 设置CData内容
	 * @param text 内容
	 * @return 节点对象
	 */
	XElement setCData(String text);

	/**
	 * 设置内容
	 * @param text 内容
	 * @return 节点对象
	 */
	XElement setText(String text);

	/**
	 * 获取去除空格的内容
	 * @return 内容
	 */
	String textTrim();
}
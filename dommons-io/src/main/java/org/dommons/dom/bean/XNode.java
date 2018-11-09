/*
 * @(#)XNode.java     2011-11-1
 */
package org.dommons.dom.bean;

import java.util.List;

/**
 * XML 节点对象
 * @author Demon 2011-11-1
 */
public interface XNode<N extends XNode> extends XBean {

	/**
	 * 添加子节点
	 * @param child 子节点
	 * @return 当前父节点
	 */
	N add(XElement child);

	/**
	 * 添加子节点
	 * @param child 子节点
	 * @return 父节点
	 * @deprecated {@link #add(XElement)}
	 */
	N addElement(XElement child);

	/**
	 * 获取子元素集
	 * @return 子元素集
	 */
	List<XBean> children();

	/**
	 * 获取注释集
	 * @return 注释集
	 */
	List<XComment> comments();

	/**
	 * 获取指定名称的子节点
	 * @param names 子节点名称顺序集
	 * @return 子节点
	 */
	XElement element(String... names);

	/**
	 * 获取叶子节点集
	 * @return 叶子节点集
	 */
	List<XElement> elements();

	/**
	 * 获取指定名称的子节点集
	 * @param names 子节点名称顺序集
	 * @return 子节点集
	 */
	List<XElement> elements(String... names);

	/**
	 * 获取指定名称子节点文本
	 * @param names 子节点名称顺序集
	 * @return 文本
	 */
	String elementText(String... names);

	/**
	 * 创建子节点
	 * @param name 子节点名
	 * @return 子节点
	 */
	XElement newChild(String name);

	/**
	 * 移除注释
	 * @param comment 注释
	 * @return 是否移除成功
	 */
	boolean remove(XComment comment);

	/**
	 * 移除叶子节点
	 * @param element 节点
	 * @return 是否移除成功
	 */
	boolean remove(XElement element);

	/**
	 * 替换节点
	 * @param oldEle 原节点
	 * @param newEle 新节点
	 * @return 新节点
	 */
	XElement replace(XElement oldEle, XElement newEle);
}

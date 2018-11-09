/*
 * @(#)XBean.java     2011-11-1
 */
package org.dommons.dom.bean;

/**
 * XML 内容对象基本接口
 * @author Demon 2011-11-1
 */
public interface XBean extends Cloneable, XType {

	/**
	 * 获取节点类型
	 * @return 类型
	 */
	int type();
}

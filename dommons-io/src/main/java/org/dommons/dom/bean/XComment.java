/*
 * @(#)XComment.java     2011-11-1
 */
package org.dommons.dom.bean;

/**
 * XML 注释
 * @author Demon 2011-11-1
 */
public interface XComment extends XBean {

	/**
	 * 获取注释内容
	 * @return 内容
	 */
	String getContext();
}

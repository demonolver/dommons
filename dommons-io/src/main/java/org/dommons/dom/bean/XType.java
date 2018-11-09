/*
 * @(#)XType.java     2012-3-13
 */
package org.dommons.dom.bean;

/**
 * XML 类型宏定义
 * @author Demon 2012-3-13
 */
public interface XType {

	/** 文档类型 */
	final int Type_Document = 3;

	/** 元素类型 */
	final int Type_Element = 2;

	/** 文本类型 */
	final int Type_Text = 32;

	/** 注释类型 */
	final int Type_Comment = 128;

	/** 属性类型 */
	final int Type_Attribute = 8;
}

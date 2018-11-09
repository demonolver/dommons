/*
 * @(#)XDom4jAttribute.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dommons.dom.bean.XAttribute;

/**
 * dom4j XML 属性项
 * @author Demon 2011-11-1
 */
public class XDom4jAttribute extends XDom4jBean implements XAttribute {

	/** 目标属性项 */
	private final Attribute attribute;

	/**
	 * 构造函数
	 * @param attribute 目标属性项
	 */
	protected XDom4jAttribute(Attribute attribute) {
		if (attribute == null) throw new NullPointerException();
		this.attribute = attribute;
	}

	public String getName() {
		return attribute.getName();
	}

	public String getValue() {
		return attribute.getText().trim();
	}

	public XAttribute setValue(String value) {
		attribute.setText(value);
		return this;
	}

	public int type() {
		return Type_Attribute;
	}

	/**
	 * 获取目标属性项
	 * @return 属性项
	 */
	protected Attribute attribute() {
		return attribute;
	}

	protected Node target() {
		return attribute();
	}
}
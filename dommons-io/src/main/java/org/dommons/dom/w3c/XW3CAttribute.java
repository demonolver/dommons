/*
 * @(#)XW3CAttribute.java     2011-11-1
 */
package org.dommons.dom.w3c;

import org.dommons.dom.bean.XAttribute;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * w3c XML 属性项
 * @author Demon 2011-11-1
 */
public class XW3CAttribute extends XW3CBean implements XAttribute {

	/** 目标属性项 */
	private final Attr attribute;

	protected XW3CAttribute(Attr attribute) {
		if (attribute == null) throw new NullPointerException();
		this.attribute = attribute;
	}

	public String getName() {
		return attribute.getName();
	}

	public String getValue() {
		return attribute.getValue().trim();
	}

	public XAttribute setValue(String value) {
		attribute.setValue(value);
		return this;
	}

	public String toString() {
		return new StringBuilder().append(getName()).append('=').append('"').append(getValue()).append('"').toString();
	}

	public int type() {
		return Type_Attribute;
	}

	/**
	 * 获取目标属性项
	 * @return 目标属性项
	 */
	protected Attr attribute() {
		return attribute;
	}

	protected Node target() {
		return attribute();
	}
}
/*
 * @(#)XDom4jElement.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Comment;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dommons.core.string.Stringure;
import org.dommons.dom.bean.XAttribute;
import org.dommons.dom.bean.XBean;
import org.dommons.dom.bean.XComment;
import org.dommons.dom.bean.XElement;

/**
 * dom4j XML 叶子节点
 * @author Demon 2011-11-1
 */
public class XDom4jElement extends XDom4jNode<XElement> implements XElement {

	static final String DEFAULT_NAMESPACE_ATTRIBUTE = "xmlns";

	private final Element element; // 目标节点

	protected XDom4jElement(Element element) {
		if (element == null) throw new NullPointerException();
		this.element = element;
	}

	public XElement add(XElement child) {
		if (child != null && child instanceof XDom4jElement) {
			XDom4jElement ele = (XDom4jElement) child;
			this.element.add(ele.element());
		}
		return this;
	}

	public XElement addAttribute(XAttribute attribute) {
		if (attribute != null && attribute instanceof XDom4jAttribute) {
			XDom4jAttribute attr = (XDom4jAttribute) attribute;
			element.add(attr.attribute());
		}
		return this;
	}

	public XElement addComment(String comment) {
		element.addComment(comment);
		return this;
	}

	public XElement appendElement(String name) {
		return new XDom4jElement(element.addElement(name));
	}

	public String attribute(String name) {
		Attribute a = findAttribute(name);
		return a == null ? null : a.getValue();
	}

	public List<XAttribute> attributes() {
		List<XAttribute> list = new ArrayList();
		for (Iterator<Attribute> it = element.attributeIterator(); it.hasNext();) {
			list.add(new XDom4jAttribute(it.next()));
		}
		return list;
	}

	public List<XBean> children() {
		return XDom4jDocument.toChildren(element.nodeIterator());
	}

	public List<XComment> comments() {
		List<XComment> list = new ArrayList();
		for (Iterator<Node> it = element.nodeIterator(); it.hasNext();) {
			Node node = it.next();
			if (node.getNodeType() == Node.COMMENT_NODE) list.add(new XDom4jComment((Comment) node));
		}
		return list;
	}

	public String getName() {
		return element.getName();
	}

	public String getNamespace() {
		return element.getNamespaceURI();
	}

	public String getText() {
		return element.getText();
	}

	public boolean hasAttribute(String name) {
		return findAttribute(name) != null;
	}

	public XElement newChild(String name) {
		Element ele = element.addElement(name);
		return ele == null ? null : new XDom4jElement(ele);
	}

	public XElement parent() {
		Element parent = element.getParent();
		return parent == null ? null : new XDom4jElement(parent);
	}

	public boolean remove(XAttribute attribute) {
		if (attribute == null || !(attribute instanceof XDom4jAttribute)) return false;
		XDom4jAttribute attr = (XDom4jAttribute) attribute;
		return element.remove(attr.attribute());
	}

	public boolean remove(XComment comment) {
		if (comment == null || !(comment instanceof XDom4jComment)) return false;
		XDom4jComment com = (XDom4jComment) comment;
		return this.element.remove(com.comment());
	}

	public boolean remove(XElement element) {
		if (element == null || !(element instanceof XDom4jElement)) return false;
		XDom4jElement ele = (XDom4jElement) element;
		return this.element.remove(ele.element());
	}

	public XAttribute removeAttribute(String name) {
		Attribute attr = element.attribute(name);
		if (attr != null) element.remove(attr);
		return attr == null ? null : new XDom4jAttribute(attr);
	}

	public XElement replace(XElement oldEle, XElement newEle) {
		if (oldEle == null || !(oldEle instanceof XDom4jElement)) return null;
		if (newEle == null) {
			remove(oldEle);
			return null;
		} else if (newEle instanceof XDom4jElement) {
			XDom4jElement old = (XDom4jElement) oldEle;
			XDom4jElement current = (XDom4jElement) newEle;
			element.remove(old.element());
			element.add(current.element());
			return current;
		} else {
			return null;
		}
	}

	public XElement setAttribute(String name, String value) {
		if (DEFAULT_NAMESPACE_ATTRIBUTE.equals(name)) {
			element.setQName(DocumentHelper.createQName(element.getName(), DocumentHelper.createNamespace("", value)));
		} else {
			element.addAttribute(name, value);
		}
		return this;
	}

	public XElement setCData(String text) {
		Collection<Node> list = new ArrayList();
		for (Iterator it = element.nodeIterator(); it.hasNext();) {
			Node node = (Node) it.next();
			switch (node.getNodeType()) {
			case Node.CDATA_SECTION_NODE:
				// case ENTITY_NODE:
			case Node.ENTITY_REFERENCE_NODE:
			case Node.TEXT_NODE:
				list.add(node);
			default:
				break;
			}
		}
		for (Node node : list) {
			element.remove(node);
		}
		element.addCDATA(text);
		return this;
	}

	public XElement setText(String text) {
		element.setText(text);
		return this;
	}

	public String textTrim() {
		return element.getTextTrim();
	}

	public int type() {
		return Type_Element;
	}

	/**
	 * 获取目标节点实例
	 * @return 节点实例
	 */
	protected Element element() {
		return element;
	}

	/**
	 * 查找属性项
	 * @param name 属性名
	 * @return 属性项
	 */
	protected Attribute findAttribute(String name) {
		Iterator<Attribute> it = element.attributeIterator();
		if (it == null) return null;
		for (; it.hasNext();) {
			Attribute a = it.next();
			if (a == null) continue;
			String key = Stringure.join(':', a.getNamespacePrefix(), a.getName());
			if (key.equals(name)) return a;
		}
		return null;
	}

	protected Branch target() {
		return element();
	}
}
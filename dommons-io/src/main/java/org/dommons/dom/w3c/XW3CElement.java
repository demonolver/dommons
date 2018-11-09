/*
 * @(#)XW3CElement.java     2011-11-1
 */
package org.dommons.dom.w3c;

import java.util.ArrayList;
import java.util.List;

import org.dommons.core.Assertor;
import org.dommons.core.string.Stringure;
import org.dommons.dom.bean.XAttribute;
import org.dommons.dom.bean.XComment;
import org.dommons.dom.bean.XElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * w3c XML 叶子节点
 * @author Demon 2011-11-1
 */
public class XW3CElement extends XW3CNode<XElement> implements XElement {

	private final Element element; // 目标叶子节点

	protected XW3CElement(Element element) {
		if (element == null) throw new NullPointerException();
		this.element = element;
	}

	public XElement add(XElement child) {
		if (child != null && child instanceof XW3CElement) {
			XW3CElement ele = (XW3CElement) child;
			this.element.appendChild(ele.element());
		}
		return this;
	}

	public XElement addAttribute(XAttribute attribute) {
		if (attribute != null && attribute instanceof XW3CAttribute) element.appendChild(((XW3CAttribute) attribute).attribute());
		return this;
	}

	public XElement addComment(String comment) {
		Document doc = element.getOwnerDocument();
		Comment com = doc.createComment(comment);
		element.appendChild(com);
		return this;
	}

	public String attribute(String name) {
		return element.getAttribute(name);
	}

	public String attribute(String name, String ns) {
		return element.getAttribute(ns + ':' + name);
	}

	public List<XAttribute> attributes() {
		NamedNodeMap map = element.getAttributes();
		int len = map.getLength();
		List<XAttribute> list = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			Node node = map.item(i);
			if (node instanceof Attr) list.add(new XW3CAttribute((Attr) node));
		}
		return list;
	}

	public String getName() {
		return element.getNodeName();
	}

	public String getNamespace() {
		return element.getNamespaceURI();
	}

	public String getText() {
		return element.getTextContent();
	}

	public boolean hasAttribute(String name) {
		return element.hasAttribute(name);
	}

	public XElement newChild(String name) {
		Element ele = element.getOwnerDocument().createElement(name);
		element.appendChild(ele);
		return ele == null ? null : new XW3CElement(ele);
	}

	public XElement parent() {
		Node node = element.getParentNode();
		return (node == null || !(node instanceof Element)) ? null : new XW3CElement((Element) node);
	}

	public boolean remove(XAttribute attribute) {
		if (attribute == null) return false;
		this.element.removeAttribute(attribute.getName());
		return true;
	}

	public boolean remove(XComment comment) {
		if (comment == null || !(comment instanceof XW3CComment)) return false;
		XW3CComment com = (XW3CComment) comment;
		return this.element.removeChild(com.comment()) != null;
	}

	public boolean remove(XElement element) {
		if (element == null || !(element instanceof XW3CElement)) return false;
		XW3CElement ele = (XW3CElement) element;
		return this.element.removeChild(ele.element()) != null;
	}

	public XAttribute removeAttribute(String name) {
		if (Assertor.P.empty(name)) return null;
		Attr attr = element.getAttributeNode(name);
		if (attr != null) {
			element.removeAttribute(name);
			return new XW3CAttribute(attr);
		} else {
			return null;
		}
	}

	public XElement replace(XElement oldEle, XElement newEle) {
		if (oldEle == null || !(oldEle instanceof XW3CElement)) return null;
		if (newEle == null) {
			remove(oldEle);
			return null;
		} else if (newEle instanceof XW3CElement) {
			XW3CElement old = (XW3CElement) oldEle;
			XW3CElement current = (XW3CElement) newEle;
			element.replaceChild(current.element(), old.element());
			return current;
		} else {
			return null;
		}
	}

	public XElement setAttribute(String name, String value) {
		element.setAttribute(name, value);
		return this;
	}

	public XElement setCData(String text) {
		Document doc = element.getOwnerDocument();
		element.appendChild(doc.createCDATASection(text));
		return this;
	}

	public XElement setText(String text) {
		element.setTextContent(text);
		return this;
	}

	public String textTrim() {
		return Stringure.trim(getText());
	}

	public int type() {
		return Type_Element;
	}

	protected NodeList childs() {
		return element.getChildNodes();
	}

	/**
	 * 获取目标节点
	 * @return 目标节点
	 */
	protected Element element() {
		return element;
	}

	protected Node target() {
		return element();
	}
}
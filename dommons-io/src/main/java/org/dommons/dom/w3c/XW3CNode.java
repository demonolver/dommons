/*
 * @(#)XW3CNode.java     2011-11-1
 */
package org.dommons.dom.w3c;

import java.util.ArrayList;
import java.util.List;

import org.dommons.dom.bean.XBean;
import org.dommons.dom.bean.XComment;
import org.dommons.dom.bean.XElement;
import org.dommons.dom.bean.XNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * w3c XML 抽象结构体
 * @author Demon 2011-11-1
 */
public abstract class XW3CNode<N extends XNode> extends XW3CBean implements XNode<N> {

	/**
	 * 获取子节点
	 * @param list 子节点集
	 * @param name 子节点名
	 * @return 子节点
	 */
	static Element element(NodeList list, String name) {
		int len = list.getLength();
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (node instanceof Element && node.getNodeName().equals(name)) return (Element) node;
		}
		return null;
	}

	public N addElement(XElement child) {
		return add(child);
	}

	public List<XBean> children() {
		NodeList nodes = childs();
		int len = nodes.getLength();
		List<XBean> list = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case Node.ATTRIBUTE_NODE:
				list.add(new XW3CAttribute((Attr) node));
				break;
			case Node.COMMENT_NODE:
				list.add(new XW3CComment((Comment) node));
				break;
			case Node.DOCUMENT_NODE:
				list.add(new XW3CDocument((Document) node));
				break;
			case Node.ELEMENT_NODE:
				list.add(new XW3CElement((Element) node));
				break;
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				list.add(new XW3CText((Text) node));
				break;
			}
		}
		return list;
	}

	public List<XComment> comments() {
		List<XComment> result = new ArrayList();
		NodeList list = childs();
		if (list != null) {
			int len = list.getLength();
			for (int i = 0; i < len; i++) {
				Node node = list.item(i);
				if (node instanceof Comment) result.add(new XW3CComment((Comment) node));
			}
		}
		return result;
	}

	public XElement element(String... names) {
		Element ele = element(names, 0, 0);
		return ele == null ? null : new XW3CElement(ele);
	}

	public List<XElement> elements() {
		List<XElement> result = new ArrayList();
		NodeList list = childs();
		if (list != null) {
			int len = list.getLength();
			for (int i = 0; i < len; i++) {
				Node node = list.item(i);
				if (node instanceof Element) result.add(new XW3CElement((Element) node));
			}
		}
		return result;
	}

	public List<XElement> elements(String... names) {
		Element ele = element(names, 0, -1);
		List<XElement> result = new ArrayList();
		NodeList list = ele != null ? ele.getChildNodes() : (names.length == 1 ? childs() : null);
		int len = list == null ? 0 : list.getLength();
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (node instanceof Element && node.getNodeName().equals(names[names.length - 1])) result.add(new XW3CElement((Element) node));
		}
		return result;
	}

	public String elementText(String... names) {
		XElement ele = element(names);
		return ele == null ? null : ele.textTrim();
	}

	/**
	 * 获取子项集合
	 * @return 子节点列表
	 */
	protected abstract NodeList childs();

	/**
	 * 获取子节点
	 * @param names 子节点名称顺序集
	 * @param start 起始
	 * @param end 截止
	 * @return 子节点
	 */
	protected Element element(String[] names, int start, int end) {
		int len = names == null ? 0 : names.length;
		if (start < 0) start = len + start;
		if (end <= 0) end = len + end;
		Element ele = null;
		for (int i = start; i < len && i < end; i++) {
			ele = ele == null ? element(childs(), names[i]) : element(ele.getChildNodes(), names[i]);
			if (ele == null) break;
		}
		return ele;
	}
}
/*
 * @(#)XDom4jNode.java     2013-3-6
 *
 * Copyright (c) 2011-2013 杭州湖畔网络技术有限公司 
 * 保留所有权利 
 * 本软件为杭州湖畔网络技术有限公司所有及包含机密信息，须遵守其相关许可证条款进行使用。
 * Copyright (c) 2011-2013 HUPUN Network Technology CO.,LTD.
 * All rights reserved.
 * This software is the confidential and proprietary information of HUPUN
 * Network Technology CO.,LTD("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with HUPUN.
 * Website：http://www.hupun.com
 */
package org.dommons.dom.dom4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Branch;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dommons.dom.bean.XElement;
import org.dommons.dom.bean.XNode;

/**
 * dom4j XML 节点
 * @author Demon 2013-3-6
 */
abstract class XDom4jNode<N extends XNode> extends XDom4jBean implements XNode<N> {

	/**
	 * 获取子节点
	 * @param parent 父节点
	 * @param name 子节点名
	 * @return 节点
	 */
	private static Element element(Branch parent, String name) {
		for (Iterator<Node> it = parent.nodeIterator(); it.hasNext();) {
			Node node = it.next();
			if (node.getNodeType() == Node.ELEMENT_NODE && name.equals(node.getName())) return (Element) node;
		}
		return null;
	}

	public N addElement(XElement child) {
		return add(child);
	}

	public XElement element(String... names) {
		Branch ele = element(names, 0, 0);
		return ele != null && ele instanceof Element ? new XDom4jElement((Element) ele) : null;
	}

	public List<XElement> elements() {
		List<XElement> list = new ArrayList();
		for (Iterator<Node> it = target().nodeIterator(); it.hasNext();) {
			Node node = it.next();
			if (node.getNodeType() == Node.ELEMENT_NODE) list.add(new XDom4jElement((Element) node));
		}
		return list;
	}

	public List<XElement> elements(String... names) {
		Branch ele = element(names, 0, -1);
		List<XElement> list = new ArrayList();
		if (ele != null) {
			for (Iterator<Node> it = ele.nodeIterator(); it.hasNext();) {
				Node node = it.next();
				if (node.getNodeType() == Node.ELEMENT_NODE && names[names.length - 1].equals(node.getName()))
					list.add(new XDom4jElement((Element) node));
			}
		}
		return list;
	}

	public String elementText(String... names) {
		XElement ele = element(names);
		return ele == null ? null : ele.textTrim();
	}

	/**
	 * 获取子节点集
	 * @param names 子节点名称顺序集
	 * @param start 起始
	 * @param end 截止
	 * @return 子节点
	 */
	protected Branch element(String[] names, int start, int end) {
		int len = names == null ? 0 : names.length;
		if (start < 0) start = len + start;
		if (end <= 0) end = len + end;
		Branch ele = start < end ? null : target();
		for (int i = start; i < len && i < end; i++) {
			ele = ele == null ? element(target(), names[i]) : element(ele, names[i]);
			if (ele == null) break;
		}
		return ele;
	}

	protected abstract Branch target();
}

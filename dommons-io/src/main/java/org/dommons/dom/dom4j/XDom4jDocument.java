/*
 * @(#)XDom4jDocument.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CharacterData;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dommons.dom.bean.XBean;
import org.dommons.dom.bean.XComment;
import org.dommons.dom.bean.XDocument;
import org.dommons.dom.bean.XElement;
import org.dommons.dom.bean.XFormat;

/**
 * dom4j XML 文档
 * @author Demon 2011-11-1
 */
public class XDom4jDocument extends XDom4jNode<XDocument> implements XDocument {

	/**
	 * 解析文档格式
	 * @param format 文档格式
	 * @return 输出格式
	 */
	static OutputFormat parseFormat(XFormat format) {
		if (format == null) return OutputFormat.createCompactFormat();
		OutputFormat out = new OutputFormat();
		out.setNewLineAfterDeclaration(false);
		out.setTrimText(true);
		if (format.isIndent()) {
			out.setIndent(true);
			out.setNewlines(true);
			out.setIndent("    ");
		} else {
			out.setIndent(false);
			out.setNewlines(false);
		}
		if (format.getEncoding() != null) out.setEncoding(format.getEncoding());

		return out;
	}

	/**
	 * 转换子节点集
	 * @param it 节点迭代器
	 * @return 节点集
	 */
	static List<XBean> toChildren(Iterator<Node> it) {
		List<XBean> list = new ArrayList();
		for (; it.hasNext();) {
			Node node = it.next();
			switch (node.getNodeType()) {
			case Node.ELEMENT_NODE:
				list.add(new XDom4jElement((Element) node));
				break;
			case Node.ATTRIBUTE_NODE:
				list.add(new XDom4jAttribute((Attribute) node));
				break;
			case Node.COMMENT_NODE:
				list.add(new XDom4jComment((Comment) node));
				break;
			case Node.DOCUMENT_NODE:
				list.add(new XDom4jDocument((Document) node));
				break;
			case Node.TEXT_NODE:
			case Node.CDATA_SECTION_NODE:
				list.add(new XDom4jText((CharacterData) node));
				break;
			}
		}
		return list;
	}

	private final Document doc; // 目标文档

	protected XDom4jDocument(Document doc) {
		if (doc == null) throw new NullPointerException();
		this.doc = doc;
	}

	public XDocument add(XElement child) {
		if (child != null && child instanceof XDom4jElement) {
			XDom4jElement ele = (XDom4jElement) child;
			doc.add(ele.element());
		}
		return this;
	}

	public XDocument addComment(String comment) {
		doc.addComment(comment);
		return this;
	}

	public List<XBean> children() {
		return toChildren(doc.nodeIterator());
	}

	public List<XComment> comments() {
		List<XComment> list = new ArrayList();
		for (Iterator<Node> it = doc.nodeIterator(); it.hasNext();) {
			Node node = it.next();
			if (node.getNodeType() == Node.COMMENT_NODE) list.add(new XDom4jComment((Comment) node));
		}
		return list;
	}

	public XElement createElement(String name) {
		return new XDom4jElement(DocumentHelper.createElement(name));
	}

	public String getEncoding() {
		return doc.getXMLEncoding();
	}

	public XDom4jElement getRootElement() {
		return rootElement();
	}

	public XDom4jElement newChild(String name) {
		Element ele = doc.addElement(name);
		return ele == null ? null : new XDom4jElement(ele);
	}

	public boolean remove(XComment comment) {
		if (comment == null || !(comment instanceof XDom4jComment)) return false;
		XDom4jComment com = (XDom4jComment) comment;
		return doc.remove(com.comment());
	}

	public boolean remove(XElement element) {
		if (element == null || !(element instanceof XDom4jElement)) return false;
		XDom4jElement ele = (XDom4jElement) element;
		return doc.remove(ele.element());
	}

	public XElement replace(XElement oldEle, XElement newEle) {
		if (oldEle == null || !(oldEle instanceof XDom4jElement)) return null;
		if (newEle == null) {
			remove(oldEle);
			return null;
		} else if (newEle instanceof XDom4jElement) {
			XDom4jElement old = (XDom4jElement) oldEle;
			XDom4jElement current = (XDom4jElement) newEle;
			doc.remove(old.element());
			doc.add(current.element());
			return current;
		} else {
			return null;
		}
	}

	public XDom4jElement rootElement() {
		Element root = doc.getRootElement();
		return root == null ? null : new XDom4jElement(root);
	}

	public void store(OutputStream out, XFormat format) throws IOException {
		XMLWriter writer = new XDom4jWriter(parseFormat(format));
		writer.setOutputStream(out);
		writer.write(doc);
	}

	public void store(Writer writer, XFormat format) throws IOException {
		XMLWriter w = new XDom4jWriter(parseFormat(format));
		w.setWriter(writer);
		w.write(doc);
	}

	public int type() {
		return Type_Document;
	}

	/**
	 * 获取目标xml文档
	 * @return xml文档
	 */
	protected Document document() {
		return doc;
	}

	protected Branch target() {
		return document();
	}
}
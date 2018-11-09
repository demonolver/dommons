/*
 * @(#)XW3CDocument.java     2011-11-1
 */
package org.dommons.dom.w3c;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dommons.dom.bean.XComment;
import org.dommons.dom.bean.XDocument;
import org.dommons.dom.bean.XElement;
import org.dommons.dom.bean.XFormat;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * w3c XML 文档
 * @author Demon 2011-11-1
 */
public class XW3CDocument extends XW3CNode<XDocument> implements XDocument {

	/**
	 * 解析文档格式
	 * @param format 文档格式
	 * @throws TransformerConfigurationException
	 */
	static Transformer parseFormat(XFormat format) throws TransformerConfigurationException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer ts = null;
		if (format != null) {
			if (format.isIndent()) {
				factory.setAttribute("indent-number", Integer.valueOf(4));
				ts = factory.newTransformer();
				ts.setOutputProperty(OutputKeys.INDENT, "yes");
			} else {
				ts = factory.newTransformer();
			}
			if (format.getEncoding() != null) ts.setOutputProperty(OutputKeys.ENCODING, format.getEncoding());
		} else {
			ts = factory.newTransformer();
		}
		return ts;
	}

	private final Document doc; // 目标文档

	protected XW3CDocument(Document doc) {
		if (doc == null) throw new NullPointerException();
		this.doc = doc;
	}

	public XDocument add(XElement child) {
		if (child != null && child instanceof XW3CElement) {
			XW3CElement ele = (XW3CElement) child;
			doc.appendChild(ele.element());
		}
		return this;
	}

	public XW3CDocument addComment(String comment) {
		Comment com = doc.createComment(comment);
		doc.appendChild(com);
		return this;
	}

	public XW3CElement createElement(String name) {
		return new XW3CElement(doc.createElement(name));
	}

	public String getEncoding() {
		return null;
	}

	public XW3CElement getRootElement() {
		return rootElement();
	}

	public XElement newChild(String name) {
		Element ele = doc.createElement(name);
		doc.appendChild(ele);
		return ele == null ? null : new XW3CElement(ele);
	}

	public boolean remove(XComment comment) {
		if (comment == null || !(comment instanceof XW3CComment)) return false;
		XW3CComment com = (XW3CComment) comment;
		return doc.removeChild(com.comment()) != null;
	}

	public boolean remove(XElement element) {
		if (element == null || !(element instanceof XW3CElement)) return false;
		XW3CElement ele = (XW3CElement) element;
		return doc.removeChild(ele.element()) != null;
	}

	public XW3CElement replace(XElement oldEle, XElement newEle) {
		if (oldEle == null || !(oldEle instanceof XW3CElement)) return null;
		if (newEle == null) {
			remove(oldEle);
			return null;
		} else if (newEle instanceof XW3CElement) {
			XW3CElement old = (XW3CElement) oldEle;
			XW3CElement current = (XW3CElement) newEle;
			doc.replaceChild(current.element(), old.element());
			return current;
		} else {
			return null;
		}
	}

	public XW3CElement rootElement() {
		Element root = doc.getDocumentElement();
		return root == null ? null : new XW3CElement(root);
	}

	public void store(OutputStream out, XFormat format) throws IOException {
		store(new StreamResult(out), format);
	}

	public void store(Writer writer, XFormat format) throws IOException {
		store(new StreamResult(writer), format);
	}

	public Document target() {
		return doc;
	}

	public int type() {
		return Type_Document;
	}

	protected NodeList childs() {
		return doc.getChildNodes();
	}

	/**
	 * 保存文档
	 * @param sr 文档输出项
	 * @param format 输出选项
	 * @throws IOException
	 */
	protected void store(StreamResult sr, XFormat format) throws IOException {
		DOMSource doms = new DOMSource(doc);
		Transformer ts;
		try {
			ts = parseFormat(format);
			ts.transform(doms, sr);
		} catch (TransformerException e) {
			IOException io = new IOException(e.getMessage());
			io.initCause(e.getException());
			io.setStackTrace(e.getStackTrace());
			throw io;
		}
	}
}
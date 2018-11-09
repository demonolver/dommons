/*
 * @(#)XErrorHandler.java     2011-11-1
 */
package org.dommons.dom.base;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML 错误处理器
 * @author Demon 2011-11-1
 */
public class XErrorHandler implements ErrorHandler {

	/** 处理器实例 */
	public static XErrorHandler instance = new XErrorHandler();

	/**
	 * 解析异常
	 * @param e 解析时异常
	 * @return 新异常
	 */
	public static SAXException parse(SAXParseException e) {
		String systemID = e.getSystemId();
		if (systemID == null) systemID = "";
		String message = "Error on line " + e.getLineNumber() + " of document [" + systemID + "] : " + e.getMessage();
		SAXException sax = new SAXException(message, e.getException());
		sax.setStackTrace(e.getStackTrace());
		return sax;
	}

	protected XErrorHandler() {
	}

	public void error(SAXParseException e) throws SAXException {
		throw parse(e);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		throw parse(e);
	}

	public void warning(SAXParseException e) throws SAXException {
		throw parse(e);
	}
}
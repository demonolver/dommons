/*
 * @(#)XDom4jWriter.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import java.io.UnsupportedEncodingException;

import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * dom4j 文件写入器 修正了空命名空间和中文乱码的问题
 * @author Demon 2011-11-1
 */
public class XDom4jWriter extends XMLWriter {

	public XDom4jWriter(OutputFormat format) throws UnsupportedEncodingException {
		super(format);
	}

	protected int defaultMaximumAllowedCharacter() {
		int character = super.defaultMaximumAllowedCharacter();
		return character > 0 ? character : 0x7e;
	}

	protected boolean isNamespaceDeclaration(Namespace ns) {
		return super.isNamespaceDeclaration(ns) && ns != Namespace.NO_NAMESPACE;
	}
}
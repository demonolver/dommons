/*
 * @(#)XFormat.java     2011-11-1
 */
package org.dommons.dom.bean;

import java.io.Serializable;

/**
 * XML 文档格式
 * @author Demon 2011-11-1
 */
public final class XFormat implements Serializable, Cloneable {

	private static final long serialVersionUID = 6322898200949872710L;

	/** 是否缩进 */
	private boolean indent;
	/** 字符集 */
	private String encoding;

	public XFormat clone() {
		try {
			return (XFormat) super.clone();
		} catch (CloneNotSupportedException e) {
			XFormat format = new XFormat();
			format.indent = indent;
			format.encoding = encoding;
			return format;
		}
	}

	/**
	 * 获取文档指定的字符集
	 * @return 字符集
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * 是否缩进
	 * @return 是、否
	 */
	public boolean isIndent() {
		return indent;
	}

	/**
	 * 设置文档指定的字符集
	 * @param encoding 字符集
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 设置是否缩进
	 * @param indent 是、否
	 */
	public void setIndent(boolean indent) {
		this.indent = indent;
	}
}
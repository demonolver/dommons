/*
 * @(#)XDocument.java     2011-11-1
 */
package org.dommons.dom.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * XML 文档对象
 * @author Demon 2011-11-1
 */
public interface XDocument extends XNode<XDocument> {

	/**
	 * 添加注释
	 * @param comment 注释
	 * @return 文档对象
	 */
	XDocument addComment(String comment);

	/**
	 * 创建新节点
	 * @param name 节点名
	 * @return 节点
	 */
	XElement createElement(String name);

	/**
	 * 获取文档字符集
	 * @return 字符集
	 */
	String getEncoding();

	/**
	 * 获取根叶子节点
	 * @return 根叶子节点
	 * @deprecated {@link #rootElement()}
	 */
	XElement getRootElement();

	/**
	 * 获取根节点
	 * @return 根节点
	 */
	XElement rootElement();

	/**
	 * 保存文档
	 * @param out 输出流
	 * @param format 输出格式选项
	 * @throws IOException
	 */
	void store(OutputStream out, XFormat format) throws IOException;

	/**
	 * 保存文档
	 * @param writer 写入器
	 * @param format 输出格式选项
	 * @throws IOException
	 */
	void store(Writer writer, XFormat format) throws IOException;
}
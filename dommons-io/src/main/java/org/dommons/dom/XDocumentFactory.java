/*
 * @(#)XDocumentFactory.java     2011-11-1
 */
package org.dommons.dom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.dommons.dom.bean.XDocument;
import org.xml.sax.SAXException;

/**
 * XML 文档工厂
 * @author Demon 2011-11-1
 */
public abstract class XDocumentFactory implements XValidation {

	private static XDocumentFactory instance;

	/**
	 * 获取工厂实例
	 * @return 工厂实例
	 */
	public static XDocumentFactory getInstance() {
		if (instance == null) {
			synchronized (XDocumentFactory.class) {
				if (instance == null) instance = new XDocumentFactoryBuilder().build();
			}
		}
		return instance;
	}

	/**
	 * 创建新 XML 文档
	 * @return XML 文档
	 */
	public abstract XDocument create();

	/**
	 * 解析 XML
	 * @param file XML 文件
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(File file) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param file XML 文件
	 * @param validation 校验文件
	 * @param type 校验类型{@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(File file, URL validation, int type) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param is 输入流
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(InputStream is) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param is 输入流
	 * @param validation 校验文件流
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(InputStream is, InputStream validation, int type) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param is 输入流
	 * @param validation 格式校验文件
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(InputStream is, URL validation, int type) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param reader 内容读取器
	 * @return XML 文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public abstract XDocument parse(Reader reader) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param reader 内容读取器
	 * @param validation 格式校验文件
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException
	 * @throws IOException
	 */
	public abstract XDocument parse(Reader reader, URL validation, int type) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param url 文件URL
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(URL url) throws SAXException, IOException;

	/**
	 * 解析 XML
	 * @param url 文件URL
	 * @param validation 格式校验文件
	 * @param type 校验类型 {@link XValidation}
	 * @return XML 文档对象
	 * @throws SAXException 解析出错
	 * @throws IOException 读取出错
	 */
	public abstract XDocument parse(URL url, URL validation, int type) throws SAXException, IOException;
}
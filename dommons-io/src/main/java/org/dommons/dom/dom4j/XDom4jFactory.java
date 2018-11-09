/*
 * @(#)XDom4jFactory.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dommons.core.Assertor;
import org.dommons.dom.XDocumentFactory;
import org.dommons.dom.base.XEntityResolver;
import org.dommons.dom.base.XErrorHandler;
import org.dommons.dom.bean.XDocument;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * dom4j XML 工厂
 * @author Demon 2011-11-1
 */
public class XDom4jFactory extends XDocumentFactory {

	static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
	static final String SCHEMA_FULLCHECK = "http://apache.org/xml/features/validation/schema-full-checking";

	public XDocument create() {
		return wrap(DocumentHelper.createDocument());
	}

	public XDocument parse(File file) throws SAXException, IOException {
		return wrap(read(file, reader()));
	}

	public XDocument parse(File file, URL validation, int type) throws SAXException, IOException {
		if (type == NONE) return parse(file);
		return wrap(read(file, createValidateReader(validation, type)));
	}

	public XDocument parse(InputStream is) throws SAXException, IOException {
		return wrap(read(is, reader()));
	}

	public XDocument parse(InputStream is, InputStream validation, int type) throws SAXException, IOException {
		if (type == NONE) parse(is);
		return wrap(read(is, createValidateReader(validation, type)));
	}

	public XDocument parse(InputStream is, URL validation, int type) throws SAXException, IOException {
		if (type == NONE) return parse(is);
		return wrap(read(is, createValidateReader(validation, type)));
	}

	public XDocument parse(Reader reader) throws SAXException, IOException {
		return wrap(read(reader, reader()));
	}

	public XDocument parse(Reader reader, URL validation, int type) throws SAXException, IOException {
		if (type == NONE) return parse(reader);
		return wrap(read(reader, createValidateReader(validation, type)));
	}

	public XDocument parse(URL url) throws SAXException, IOException {
		return wrap(read(url, reader()));
	}

	public XDocument parse(URL url, URL validation, int type) throws SAXException, IOException {
		if (type == NONE) return parse(url);
		return wrap(read(url, createValidateReader(validation, type)));
	}

	/**
	 * 解析异常
	 * @param e 原异常
	 * @return 新异常
	 * @throws IOException
	 * @throws RuntimeException
	 */
	protected SAXException parseException(DocumentException e) throws IOException, RuntimeException {
		Throwable cause = e.getNestedException();
		if (cause instanceof SAXParseException) {
			return XErrorHandler.parse((SAXParseException) cause);
		} else if (cause instanceof IOException) {
			throw (IOException) cause;
		} else if (cause instanceof RuntimeException) {
			throw (RuntimeException) cause;
		} else if (cause instanceof SAXException) {
			return (SAXException) cause;
		} else {
			SAXException sax = new SAXException(cause.getMessage());
			sax.initCause(cause.getCause());
			sax.setStackTrace(cause.getStackTrace());
			return sax;
		}
	}

	/**
	 * 包装文档实例
	 * @param doc 文档实例
	 * @return 文档实例
	 */
	protected XDocument wrap(Document doc) {
		return new XDom4jDocument(doc);
	}

	/**
	 * 创建带校验的读取器
	 * @param validation 校验文件
	 * @param type 校验类型
	 * @return 读取器
	 * @throws SAXException
	 */
	SAXReader createValidateReader(InputStream validation, int type) throws SAXException {
		Assertor.F.notNull(validation, "The input stream of validation file is must not be null!");
		SAXReader reader = new SAXReader(true);
		reader.setErrorHandler(XErrorHandler.instance);
		reader.setEntityResolver(new XEntityResolver(validation));

		if (type == XSD) {
			reader.setFeature(SCHEMA_VALIDATION, true);
			reader.setFeature(SCHEMA_FULLCHECK, true);
		}
		XDom4jHelper.configure(reader);

		return reader;
	}

	/**
	 * 创建带校验的读取器
	 * @param validation 校验文件
	 * @param type 校验类型
	 * @return 读取器
	 * @throws SAXException
	 */
	SAXReader createValidateReader(URL validation, int type) throws SAXException {
		Assertor.F.notNull(validation, "The url of validation file is must not be null!");
		SAXReader reader = new SAXReader(true);
		reader.setErrorHandler(XErrorHandler.instance);
		reader.setEntityResolver(new XEntityResolver(validation));

		if (type == XSD) {
			reader.setFeature(SCHEMA_VALIDATION, true);
			reader.setFeature(SCHEMA_FULLCHECK, true);
		}
		XDom4jHelper.configure(reader);

		return reader;
	}

	/**
	 * 读取 XML 内容
	 * @param file XML 文件
	 * @param reader 读取器
	 * @return XML 文档
	 * @throws SAXException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	Document read(File file, SAXReader reader) throws SAXException, IOException, RuntimeException {
		Assertor.F.notNull(file, "The xml file is must not be null!");
		try {
			return reader.read(file);
		} catch (DocumentException e) {
			throw parseException(e);
		}
	}

	/**
	 * 读取 XML 内容
	 * @param is 输入流
	 * @param reader 读取器
	 * @return XML 文档
	 * @throws SAXException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	Document read(InputStream is, SAXReader reader) throws SAXException, IOException, RuntimeException {
		Assertor.F.notNull(is, "The input stream of xml file is must not be null!");
		try {
			return reader.read(is);
		} catch (DocumentException e) {
			throw parseException(e);
		}
	}

	/**
	 * 读取 XML 内容
	 * @param rd 内容读取器
	 * @param reader 读取器
	 * @return XML 文档
	 * @throws SAXException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	Document read(Reader rd, SAXReader reader) throws SAXException, IOException, RuntimeException {
		Assertor.F.notNull(rd, "The input stream of xml file is must not be null!");
		try {
			return reader.read(rd);
		} catch (DocumentException e) {
			throw parseException(e);
		}
	}

	/**
	 * 读取 XML 内容
	 * @param url XML 路径
	 * @param reader 读取器
	 * @return XML 文档
	 * @throws SAXException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	Document read(URL url, SAXReader reader) throws SAXException, IOException, RuntimeException {
		Assertor.F.notNull(url, "The url of xml file is must not be null!");
		try {
			return reader.read(url);
		} catch (DocumentException e) {
			throw parseException(e);
		}
	}

	private SAXReader reader() throws SAXException {
		return XDom4jHelper.reader();
	}
}
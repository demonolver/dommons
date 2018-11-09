/*
 * @(#)XW3CFactory.java     2011-11-1
 */
package org.dommons.dom.w3c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.dommons.dom.XDocumentFactory;
import org.dommons.dom.base.XEntityResolver;
import org.dommons.dom.base.XErrorHandler;
import org.dommons.dom.bean.XDocument;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * w3c XML 工厂
 * @author Demon 2011-11-1
 */
public class XW3CFactory extends XDocumentFactory {

	static final String SCHEMA_KEY = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

	public XW3CDocument create() {
		return wrap(createBuilder(DocumentBuilderFactory.newInstance()).newDocument());
	}

	public XDocument parse(File file) throws SAXException, IOException {
		Assertor.F.notNull(file, "The xml file is must not be null!");
		return wrap(createBuilder(DocumentBuilderFactory.newInstance()).parse(file));
	}

	public XDocument parse(File file, URL validation, int type) throws SAXException, IOException {
		Assertor.F.notNull(file, "The xml file is must not be null!");
		if (type == NONE) return parse(file);
		return wrap(createValidateBuilder(validation, type).parse(file));
	}

	public XW3CDocument parse(InputStream is) throws SAXException, IOException {
		Assertor.F.notNull(is, "The input stream of xml is must not be null!");
		return wrap(createBuilder(DocumentBuilderFactory.newInstance()).parse(is));
	}

	public XDocument parse(InputStream is, InputStream validation, int type) throws SAXException, IOException {
		Assertor.F.notNull(is, "The input stream of xml is must not be null!");
		if (type == NONE) return parse(is);
		return wrap(createValidateBuilder(validation, type).parse(is));
	}

	public XW3CDocument parse(InputStream is, URL validation, int type) throws SAXException, IOException {
		Assertor.F.notNull(is, "The input stream of xml is must not be null!");
		if (type == NONE) return parse(is);
		return wrap(createValidateBuilder(validation, type).parse(is));
	}

	public XDocument parse(Reader reader) throws SAXException, IOException {
		Assertor.F.notNull(reader, "The input stream of xml is must not be null!");
		return wrap(createBuilder(DocumentBuilderFactory.newInstance()).parse(new InputSource(reader)));
	}

	public XDocument parse(Reader reader, URL validation, int type) throws SAXException, IOException {
		Assertor.F.notNull(reader, "The input stream of xml is must not be null!");
		if (type == NONE) return parse(reader);
		return wrap(createValidateBuilder(validation, type).parse(new InputSource(reader)));
	}

	public XW3CDocument parse(URL url) throws SAXException, IOException {
		Assertor.F.notNull(url, "The url of xml is must not be null!");
		InputStream is = url.openStream();
		try {
			return wrap(createBuilder(DocumentBuilderFactory.newInstance()).parse(is));
		} finally {
			if (is != null) is.close();
		}
	}

	public XW3CDocument parse(URL url, URL validation, int type) throws SAXException, IOException {
		Assertor.F.notNull(url, "The url of xml is must not be null!");
		if (type == NONE) return parse(url);
		InputStream is = url.openStream();
		try {
			return wrap(createValidateBuilder(validation, type).parse(is));
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 包装文档实例
	 * @param doc 文档实例
	 * @return 文档实例
	 */
	protected XW3CDocument wrap(Document doc) {
		return new XW3CDocument(doc);
	}

	/**
	 * 创建构建器
	 * @param factory 构建工厂
	 * @return 构建器
	 */
	DocumentBuilder createBuilder(DocumentBuilderFactory factory) {
		try {
			configure(factory);
			return factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建带校验的构建器
	 * @param validation 校验文件
	 * @param type 校验类型
	 * @return 构建器
	 */
	DocumentBuilder createValidateBuilder(InputStream validation, int type) {
		Assertor.F.notNull(validation, "The input stream of validation file is must not be null!");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(true);
		if (type == XSD) {
			factory.setNamespaceAware(true);
			factory.setAttribute(SCHEMA_KEY, SCHEMA_LANGUAGE);
		}
		DocumentBuilder builder = createBuilder(factory);
		builder.setErrorHandler(XErrorHandler.instance);
		builder.setEntityResolver(new XEntityResolver(validation));
		return builder;
	}

	/**
	 * 创建带校验的构建器
	 * @param validation 校验文件
	 * @param type 校验类型
	 * @return 构建器
	 */
	DocumentBuilder createValidateBuilder(URL validation, int type) {
		Assertor.F.notNull(validation, "The url of validation file is must not be null!");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		if (type == XSD) {
			factory.setNamespaceAware(true);
			factory.setAttribute(SCHEMA_KEY, SCHEMA_LANGUAGE);
		}
		DocumentBuilder builder = createBuilder(factory);
		builder.setErrorHandler(XErrorHandler.instance);
		builder.setEntityResolver(new XEntityResolver(validation));
		return builder;
	}

	/**
	 * 配置构建工厂
	 * @param factory 构建工厂
	 */
	private void configure(DocumentBuilderFactory factory) {
		try {
			if (!Converter.F.convert(Environments.getProperty("xml.entities.disabled", "Y"), boolean.class)) return;
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
		} catch (Throwable t) { // ignored
		}
	}
}
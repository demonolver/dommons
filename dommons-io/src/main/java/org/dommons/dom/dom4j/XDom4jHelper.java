/*
 * @(#)XDom4jHelper.java     2018-09-14
 */
package org.dommons.dom.dom4j;

import java.io.StringReader;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dommons.core.Environments;
import org.dommons.core.convert.Converter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Dom4j 帮助工具
 * @author demon 2018-09-14
 */
public class XDom4jHelper {

	/**
	 * 解析 XML 内容
	 * @param text XML 文本
	 * @return 内容
	 * @throws DocumentException
	 */
	public static Document parseText(String text) throws DocumentException {
		try {
			return parse(text);
		} catch (SAXException e) {
			return DocumentHelper.parseText(text);
		}
	}

	protected static Document parse(String text) throws DocumentException, SAXException {
		Document result = null;

		SAXReader reader = reader();
		String encoding = getEncoding(text);

		InputSource source = new InputSource(new StringReader(text));
		source.setEncoding(encoding);
		result = reader.read(source);

		// if the XML parser doesn't provide a way to retrieve the encoding,
		// specify it manually
		if (result.getXMLEncoding() == null) result.setXMLEncoding(encoding);
		return result;
	}

	static void configure(SAXReader reader) throws SAXException {
		if (!Converter.F.convert(Environments.getProperty("xml.entities.disabled", "Y"), boolean.class)) return;
		try {
			reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
			reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			reader.setIncludeExternalDTDDeclarations(false);
			reader.setIncludeInternalDTDDeclarations(false);
		} catch (Throwable t) { // ignored
		}
	}

	static SAXReader reader() throws SAXException {
		SAXReader reader = new SAXReader();
		configure(reader);
		return reader;
	}

	private static String getEncoding(String text) {
		String result = null;

		String xml = text.trim();

		if (xml.startsWith("<?xml")) {
			int end = xml.indexOf("?>");
			String sub = xml.substring(0, end);
			StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();

				if ("encoding".equals(token)) {
					if (tokens.hasMoreTokens()) {
						result = tokens.nextToken();
					}

					break;
				}
			}
		}

		return result;
	}
}

/*
 * @(#)XEntityResolver.java     2011-11-1
 */
package org.dommons.dom.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML 解析实体
 * @author Demon 2011-11-1
 */
public class XEntityResolver implements EntityResolver {

	private InputStream is;
	private URL url;

	public XEntityResolver(InputStream is) {
		if (is == null) throw new NullPointerException();
		this.is = is;
	}

	public XEntityResolver(URL url) {
		if (url == null) throw new NullPointerException();
		this.url = url;
	}

	public InputSource resolveEntity(String publicID, String systemID) throws SAXException, IOException {
		InputSource ins = new InputSource(is == null ? url.openStream() : is);
		ins.setPublicId(publicID);
		ins.setSystemId(systemID);
		return ins;
	}
}
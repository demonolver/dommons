/*
 * @(#)JarURLStreamHandler.java     2020-03-17
 */
package org.dommons.io.jarurl;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Jar 路径流处理
 * @author demon 2020-03-17
 */
class JarURLStreamHandler extends URLStreamHandler {

	private final JarEntryItem item;

	public JarURLStreamHandler(JarEntryItem item) {
		this.item = item;
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return item.match(u) ? new JarItemURLConnection(u, item) : null;
	}
}

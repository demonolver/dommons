/*
 * @(#)JarItemURLConnection.java     2020-03-17
 */
package org.dommons.io.jarurl;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar 文件子项路径连接
 * @author demon 2020-03-17
 */
class JarItemURLConnection extends JarURLConnection {

	private final JarEntryItem item;

	protected JarItemURLConnection(URL url, JarEntryItem item) throws MalformedURLException {
		super(url);
		this.item = item;
	}

	@Override
	public void connect() throws IOException {}

	@Override
	public String getEntryName() {
		return item.getEntryName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return item.getInputStream();
	}

	@Override
	public JarEntry getJarEntry() throws IOException {
		return item.getEntry();
	}

	@Override
	public JarFile getJarFile() throws IOException {
		return item.getJarFile();
	}

	@Override
	public URL getJarFileURL() {
		return item.getParent();
	}
}

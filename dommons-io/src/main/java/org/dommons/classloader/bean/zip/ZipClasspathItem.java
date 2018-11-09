/*
 * @(#)ZipClasspathItem.java     2011-10-20
 */
package org.dommons.classloader.bean.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;

import org.dommons.classloader.bean.ClasspathItem;
import org.dommons.classloader.util.ClasspathURLHandler;

/**
 * 压缩资源项
 * @author Demon 2011-10-20
 */
public class ZipClasspathItem extends ClasspathItem {

	private final ZipClasspathContainer parent;
	private final ZipEntry entry;

	/**
	 * 构造函数
	 * @param parent 容器
	 * @param entry 压缩项
	 */
	public ZipClasspathItem(ZipClasspathContainer parent, ZipEntry entry) {
		super(parent);
		this.parent = parent;
		this.entry = entry;
	}

	public long getLength() {
		return entry.getSize();
	}

	public boolean isClass() {
		return entry.getName().endsWith(".class");
	}

	public long lastModified() {
		return entry.getTime();
	}

	public InputStream openStream() throws IOException {
		return parent.getZip().getInputStream(entry);
	}

	protected URL createURL() {
		StringBuilder buffer = new StringBuilder(parent.getPath());
		buffer.append('!').append('/');
		buffer.append(entry.getName());
		try {
			return new URL("jar", null, -1, buffer.toString(), new ClasspathURLHandler(this));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	protected boolean isActive() {
		return true;
	}
}
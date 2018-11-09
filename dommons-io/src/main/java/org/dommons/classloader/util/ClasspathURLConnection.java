/*
 * @(#)ClasspathURLConnection.java     2011-10-20
 */
package org.dommons.classloader.util;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

import org.dommons.classloader.bean.ClasspathItem;

/**
 * 类路径连接
 * @author Demon 2011-10-20
 */
public class ClasspathURLConnection extends URLConnection {

	/**
	 * 反解字符串
	 * @param pStr 原字符串
	 * @return 字符串
	 */
	static String decode(String pStr) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < pStr.length();) {
			char c = pStr.charAt(i);

			if (c != '%') ++i;
			else {
				try {
					c = unescape(pStr, i);
					i += 3;

					if ((c & 0x80) != 0) {
						int j;
						switch (c >> '\4') {
						case 12:
						case 13:
							j = unescape(pStr, i);
							i += 3;
							c = (char) ((c & 0x1F) << '\6' | j & 0x3F);
							break;
						case 14:
							j = unescape(pStr, i);
							i += 3;
							int k = unescape(pStr, i);
							i += 3;
							c = (char) ((c & 0xF) << '\f' | (j & 0x3F) << 6 | k & 0x3F);

							break;
						default:
							throw new IllegalArgumentException();
						}
					}
				} catch (NumberFormatException localNumberFormatException) {
					throw new IllegalArgumentException();
				}
			}

			buf.append(c);
		}

		return buf.toString();
	}

	/**
	 * 反解 ASC 码
	 * @param pStr 字符串
	 * @param pInt 位置
	 * @return 字符
	 */
	private static char unescape(String pStr, int pInt) {
		return (char) Integer.parseInt(pStr.substring(pInt + 1, pInt + 3), 16);
	}

	private final ClasspathItem item;

	private Permission permission;

	/**
	 * 构造函数
	 * @param url 路径
	 * @param item 资源项
	 */
	public ClasspathURLConnection(URL url, ClasspathItem item) {
		super(url);
		this.item = item;
	}

	public void connect() throws IOException {
		// do nothing
	}

	public int getContentLength() {
		return (int) item.getLength();
	}

	public InputStream getInputStream() throws IOException {
		connect();
		return item.opeanStreamWithWrapper();
	}

	public long getLastModified() {
		return item.lastModified();
	}

	public Permission getPermission() throws IOException {
		if (permission == null) {
			String str = decode(url.getPath());
			if (File.separatorChar == '/') permission = new FilePermission(str, "read");
			else permission = new FilePermission(str.replace('/', File.separatorChar), "read");
		}
		return permission;
	}
}

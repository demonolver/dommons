/*
 * @(#)AndroidResources.java     2021-08-26
 */
package org.dommons.android.env;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.android.ContextSet;
import org.dommons.core.env.ProguardIgnore;
import org.dommons.core.env.ResourcesFind;

import android.content.Context;

/**
 * android 资源查找
 * @author demon 2021-08-26
 */
public class AndroidResources extends ResourcesFind implements ProguardIgnore {

	@Override
	protected URL resource(ClassLoader cl, String name) {
		if (!isClass(name)) {
			Context context = ContextSet.get();
			try {
				if (context != null) return get(context, name);
			} catch (IOException e) { // ignored
			}
		}
		return super.resource(cl, name);
	}

	@Override
	protected Enumeration<URL> resources(ClassLoader cl, String name) throws IOException {
		if (!isClass(name)) {
			Context context = ContextSet.get();
			if (context != null) {
				URL url = get(context, name);
				Collection<URL> list = new ArrayList<>(1);
				if (url != null) list.add(url);
				return new IteratorEnumeration<>(list.iterator());
			}
		}
		return super.resources(cl, name);
	}

	/**
	 * 获取资源
	 * @param context 应用上下文
	 * @param name 资源名
	 * @return 资源路径
	 * @throws IOException
	 */
	URL get(Context context, String name) throws IOException {
		ZipFile zip = null;
		try {
			File file = new File(context.getPackageCodePath());
			zip = new ZipFile(file);
			{
				ZipEntry entry = zip.getEntry(name);
				if (entry != null) return url(entry.getName(), file);
			}
			if (name.endsWith("/")) {
				for (Enumeration<? extends ZipEntry> en = zip.entries(); en.hasMoreElements();) {
					ZipEntry entry = en.nextElement();
					if (entry.getName().startsWith(name)) return url(name, file);
				}
			}
			return null;
		} finally {
			if (zip != null) zip.close();
		}
	}

	/**
	 * 是否 Java 类
	 * @param name 资源名
	 * @return 是、否
	 */
	boolean isClass(String name) {
		return name.endsWith(".class");
	}

	/**
	 * 生成 URL
	 * @param entry 压缩项名
	 * @param file apk 文件
	 * @return URL
	 * @throws IOException
	 */
	URL url(String entry, File file) throws IOException {
		StringBuilder buf = new StringBuilder();
		buf.append("file:").append(file.getAbsolutePath()).append("!/").append(entry);
		return new URL("jar", null, -1, buf.toString());
	}

	static class IteratorEnumeration<E> implements Enumeration<E> {

		private final Iterator<E> it;

		protected IteratorEnumeration(Iterator<E> it) {
			super();
			this.it = it;
		}

		@Override
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		@Override
		public E nextElement() {
			return it.next();
		}

	}
}

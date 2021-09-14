/*
 * @(#)Filenvironment.java     2015-7-7
 */
package org.dommons.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.dommons.core.Environments;
import org.dommons.core.env.ProguardIgnore;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.io.Pathfinder;

/**
 * 文件系统环境
 * @author Demon 2015-7-7
 */
public class Filenvironment implements ProguardIgnore {

	static Ref<Filenvironment> ref;

	/**
	 * 获取文件环境实例
	 * @return 文件环境实例
	 */
	public static Filenvironment instance() {
		Filenvironment f = ref == null ? null : ref.get();
		if (f == null) {
			URL[] us = Pathfinder.getResources("org/dommons/io/*.file.env");
			if (us != null) {
				for (URL u : us) {
					f = create(u);
					if (f != null) break;
				}
			}
			if (f == null) {
				Class c = Environments.findClass("org.dommons.android.io.AndroidFiles");
				f = create(c);
			}
			if (f == null) f = new Filenvironment();
			ref = new Softref(f);
		}
		return f;
	}

	/**
	 * 创建文件系统环境
	 * @param c 类
	 * @return 文件系统环境
	 */
	static Filenvironment create(Class c) {
		try {
			if (c != null && Filenvironment.class.isAssignableFrom(c)) return (Filenvironment) c.newInstance();
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * 创建文件系统环境
	 * @param u 文件路径
	 * @return 文件系统环境
	 */
	static Filenvironment create(URL u) {
		try {
			BufferedReader r = null;
			InputStream is = null;
			try {
				is = u.openStream();
				r = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while (!Stringure.isEmpty(line = r.readLine())) {
					Class c = Environments.findClass(line);
					Filenvironment f = create(c);
					if (f != null) return f;
				}
			} finally {
				if (is != null) is.close();
				if (r != null) r.close();
			}
		} catch (IOException e) {
		}
		return null;
	}

	public Filenvironment() {
		super();
	}

	/**
	 * 获取缓存文件
	 * @param name 文件名
	 * @return 缓存文件
	 */
	public File cacheFile(String name) {
		return new File(Environments.getProperty("java.io.tmpdir"), name);
	}

	/**
	 * 获取运行包位置
	 * @return 位置
	 */
	public String getLocation() {
		return Pathfinder.getPath(getClass().getProtectionDomain().getCodeSource().getLocation());
	}
}

/*
 * @(#)AndroidEnvLoader.java     2018-09-19
 */
package org.dommons.android.env;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.core.Silewarner;
import org.dommons.core.collections.map.DataPair;
import org.dommons.core.collections.map.Mapped;
import org.dommons.core.env.EnvironmentLoader;
import org.dommons.core.string.Stringure;

/**
 * android 环境加载器
 * @author demon 2018-09-19
 */
public class AndroidEnvLoader extends EnvironmentLoader {

	public Properties load(Properties defaults) {
		String path = apk();
		if (Stringure.isEmpty(path)) return null;
		try {
			ZipFile zip = new ZipFile(new File(path));
			try {
				return load(defaults, zip);
			} finally {
				zip.close();
			}
		} catch (IOException e) {
			Silewarner.error(AndroidEnvLoader.class, null, e);
		}
		return null;
	}

	protected Properties load(Properties prop, ZipFile zip) {
		List<DataPair<String, Pattern>> ps = items();
		Map<Integer, Collection<ZipEntry>> index = new TreeMap();
		for (Enumeration<? extends ZipEntry> en = zip.entries(); en != null && en.hasMoreElements();) {
			ZipEntry ze = en.nextElement();
			Integer mi = match(ze, ps);
			if (mi == null) continue;
			Mapped.touch(index, mi, LinkedList.class).add(ze);
		}
		return load(prop, zip, index.values());
	}

	Properties load(Properties prop, ZipFile zip, Collection<Collection<ZipEntry>> zs) {
		for (Collection<ZipEntry> es : zs) {
			for (ZipEntry ze : es) {
				prop = create(prop);
				try {
					load(prop, zip.getInputStream(ze));
				} catch (IOException e) { // ignored
				}
			}
		}
		return null;
	}

	Integer match(ZipEntry ze, List<DataPair<String, Pattern>> ps) {
		String name = ze.getName();
		for (int i = 0; i < ps.size(); i++) {
			DataPair<String, Pattern> p = ps.get(i);
			Pattern pattern = p.getValue();
			if (pattern == null) {
				if (name.equals(p.getKey())) return i;
			} else if (name.startsWith(p.getKey())) {
				String n = name.substring(p.getKey().length());
				if (pattern.matcher(n).matches()) return i;
			}
		}
		return null;
	}

	private String apk() {
		String path = path(getClass().getProtectionDomain().getCodeSource().getLocation());
		if (Stringure.trim(path).toLowerCase().endsWith(".apk")) return path;
		return null;
	}

	private List<DataPair<String, Pattern>> items() {
		List<DataPair<String, Pattern>> list = new ArrayList();
		for (EnvItem item : envs) {
			Pattern p = item.pattern == null ? null : Pattern.compile(item.pattern);
			list.add(DataPair.create(item.parent, p));
		}
		return list;
	}
}

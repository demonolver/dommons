/*
 * @(#)LocalFileCache.java     2018-07-13
 */
package org.dommons.io.cache;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.DataCache;
import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.Pathfinder;
import org.dommons.io.net.UniQueness;

/**
 * 本地文件缓存
 * @author demon 2018-07-13
 */
public class LocalFileCache implements DataCache<String, String> {

	private final DiskStore disk;
	private final Map<String, String[]> tmp;

	public LocalFileCache(File file) {
		if (file == null) file = Pathfinder.cacheFile(UniQueness.generateHexUUID().toLowerCase());
		this.disk = new DiskStore(file);
		this.tmp = new MemcacheMap(TimeUnit.HOURS.toMillis(1), TimeUnit.HOURS.toMillis(12));
	}

	public LocalFileCache(String path) {
		this(Pathfinder.findFile(path));
	}

	public void clear() {
		disk.remove(null);
		this.tmp.clear();
	}

	public String get(Object key) {
		String k = key(key);
		String[] vs = null;
		vs = tmp.get(k);
		if (vs != null) return vs[0];
		String v = disk.read(k);
		tmp.put(k, vs = new String[] { v });
		return v;
	}

	public String remove(Object key) {
		String k = key(key);
		disk.remove(k);
		String[] vs = null;
		vs = tmp.remove(k);
		return Arrayard.get(vs, 0);
	}

	public void set(String key, String value) {
		if (value == null) {
			remove(key);
		} else {
			key = key(key);
			String[] v = new String[] { value };
			String[] last = null;
			last = tmp.put(key, v);
			if (!Arrayard.equals(v, last)) disk.write(key, value);
		}
	}

	protected String key(Object key) {
		return Stringure.trim(Converter.F.convert(key, String.class));
	}
}

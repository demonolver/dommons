/*
 * @(#)ThreadCacheMap.java     2018-09-05
 */
package org.dommons.core.util.thread;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.collections.map.Mapped;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.util.beans.ObjectInstantiator.ObjectCreator;
import org.dommons.core.util.beans.ObjectInstantiators;

/**
 * 线程绑定缓存
 * @author demon 2018-09-05
 */
public class ThreadCache<O> {

	private final Class<? super O> cls;
	private final Map<Long, O> cache;
	private final ThreadLocal<ThreadCache.ThreadGcHook> local;

	private Ref<CacheCreator> ref;

	public ThreadCache(Class<? super O> cls) {
		this.cls = cls;
		this.cache = new MemcacheMap(TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(24));
		this.local = new ThreadLocal();
	}

	/**
	 * 获取缓存项
	 * @return 缓存项
	 */
	public O get() {
		return Mapped.touch(cache, Long.valueOf(current()), creator());
	}

	/**
	 * 获取缓存构建器
	 * @return 缓存构建器
	 */
	private CacheCreator creator() {
		CacheCreator c = ref == null ? null : ref.get();
		if (c == null) ref = new Softref(c = new CacheCreator());
		return c;
	}

	/**
	 * 获取当前线程ID
	 * @return 线程ID
	 */
	private long current() {
		return Thread.currentThread().getId();
	}

	/**
	 * 缓存构建器
	 * @author demon 2018-09-05
	 */
	protected class CacheCreator implements ObjectCreator<O> {

		public boolean isInstance(Object o) {
			return cls.isInstance(o);
		}

		public O newInstance() {
			local.set(new ThreadGcHook(current()));
			return ObjectInstantiators.newInstance(cls);
		}
	}

	/**
	 * 线程回收钩子
	 * @author demon 2018-09-05
	 */
	private class ThreadGcHook {

		final long id;

		protected ThreadGcHook(long id) {
			this.id = id;
		}

		protected void finalize() throws Throwable {
			cache.remove(id);
		}
	}
}

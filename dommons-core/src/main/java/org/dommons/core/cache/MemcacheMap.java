/*
 * @(#)MemcacheMap.java     2017-06-15
 */
package org.dommons.core.cache;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;

import org.dommons.core.Environments;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.util.Arrayard;
import org.dommons.core.util.Randoms;

/**
 * 内存数据缓存映射集
 * @author demon 2017-06-15
 */
public class MemcacheMap<K, V> extends DataCacheMap<K, V> implements Serializable {

	private static final long serialVersionUID = -2629152255106817420L;

	protected final Map<K, CacheItem> map;
	private final long timeout;
	private final long max;
	private final Object k;

	public MemcacheMap() {
		this(5000L, Long.MAX_VALUE);
	}

	public MemcacheMap(long timeout) {
		this(timeout, Long.MAX_VALUE);
	}

	public MemcacheMap(long timeout, long max) {
		this(null, timeout, max);
	}

	public MemcacheMap(Map<?, ?> map, long timeout, long max) {
		this.map = map == null ? new ConcurrentSoftMap() : (Map) map;
		this.timeout = (timeout <= 0L ? 5000L : timeout);
		this.max = (max < this.timeout ? this.timeout : max);
		this.k = new Object();
	}

	public void clear() {
		map.clear();
	}

	public V get(Object key) {
		if (key == null) return null;
		return value(map.get(key), true, key);
	}

	public V put(K key, V value) {
		if (key == null) return null;
		else if (value == null) return remove(key);
		else return value($put(key, new CacheItem(value)));
	}

	public V remove(Object key) {
		if (key == null) return null;
		return value($remove(key));
	}

	/**
	 * 执行过期清理
	 * @return 是否清空
	 */
	protected boolean clean() {
		Object[] ks = Arrayard.toArray(map.keySet(), Object.class);
		if (ks != null) {
			for (Object k : ks) {
				if (k == null) continue;
				CacheItem item = map.get(k);
				if (item == null || item.over(System.currentTimeMillis())) $remove(k);
			}
		}
		return map.isEmpty();
	}

	/**
	 * 提取缓存值
	 * @param item 缓存项
	 * @return 缓存值
	 */
	protected V value(CacheItem item) {
		return value(item, false, null);
	}

	/**
	 * 提取缓存值
	 * @param item 缓存项
	 * @param b 是否检查过期
	 * @return 缓存值
	 */
	protected V value(CacheItem item, boolean b, Object key) {
		try {
			if (!WeakHashMap.class.isInstance(map)) MemClean.add(this.k, this);
		} catch (Throwable t) {
		}
		if (item != null && (!b || item.active())) {
			return item.get();
		} else {
			if (key != null) $remove(key);
			return null;
		}
	}

	/**
	 * 执行添加
	 * @param key 键值
	 * @param item 缓存项
	 * @return 原缓存项
	 */
	CacheItem $put(K key, CacheItem item) {
		if (map instanceof ConcurrentMap) {
			return map.put(key, item);
		} else {
			synchronized (k) {
				return map.put(key, item);
			}
		}
	}

	/**
	 * 执行移除
	 * @param key 键值
	 * @return 原缓存项
	 */
	CacheItem $remove(Object key) {
		if (map instanceof ConcurrentMap) {
			return map.remove(key);
		} else {
			synchronized (k) {
				return map.remove(key);
			}
		}
	}

	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		map.clear();
		s.defaultWriteObject();
	}

	/**
	 * 缓存项
	 * @author demon 2017-06-15
	 */
	protected class CacheItem {

		private final V v;
		private final long create;

		private volatile long time;

		protected CacheItem(V v) {
			this.v = v;
			create = (time = System.currentTimeMillis());
		}

		/**
		 * 是否有效
		 * @return 是、否
		 */
		public boolean active() {
			synchronized (this) {
				long n = System.currentTimeMillis();
				if (over(n)) return false;
				time = n;
			}
			return true;
		}

		/**
		 * 获取缓存值
		 * @return 缓存值
		 */
		public V get() {
			return v;
		}

		/**
		 * 是否超时
		 * @param n
		 * @return
		 */
		protected boolean over(long n) {
			return (n - time > timeout) || (n - create > max);
		}
	}

	/**
	 * 缓存清理
	 * @author demon 2019-02-25
	 */
	protected static class MemClean {
		/**
		 * 添加缓存
		 * @param key 键值
		 * @param map 缓存体
		 */
		public static void add(Object key, MemcacheMap map) {
			try {
				MemCleanThread ct = MemCleanThread.t();
				if (ct == null) return;
				if (ct.cs != null && !ct.cs.containsKey(key)) ct.cs.put(key, map);
				else if (Randoms.randomInteger(5) == 4) ct.notifyTo();
			} catch (Throwable t) { // ignored
			}
		}
	}

	/**
	 * 缓存内存清理
	 * @author demon 2017-09-12
	 */
	protected static class MemCleanThread implements Runnable, NotificationListener {

		static MemCleanThread t;
		static long limit = TimeUnit.SECONDS.toMillis(15);
		static Double $javaVersion;

		/**
		 * 获取清理器实例
		 * @return 清理器实例
		 */
		public static MemCleanThread t() {
			if (javaVersion() < 1.7) return null;
			if (t == null) {
				synchronized (MemCleanThread.class) {
					if (t == null) new MemCleanThread();
				}
			}
			return t;
		}

		static double javaVersion() {
			return $javaVersion != null ? $javaVersion.doubleValue() : ($javaVersion = Environments.javaVersion());

		}

		private final Map<Object, MemcacheMap> cs;
		private final AtomicLong time;
		private Thread thread;
		private volatile boolean act;

		protected MemCleanThread() {
			t = this;

			int x = 0;
			try {
				List<GarbageCollectorMXBean> list = ManagementFactory.getGarbageCollectorMXBeans();
				for (GarbageCollectorMXBean bean : list) {
					try {
						if (!(bean instanceof NotificationBroadcaster)) continue;
						((NotificationBroadcaster) bean).addNotificationListener(this, null, bean.getName());
						x++;
					} catch (Throwable t) { // ignored
					}
				}
			} catch (Throwable t) { // ignored
			}
			if (x < 1) cs = null;
			else cs = new ConcurrentHashMap();
			this.time = new AtomicLong(System.currentTimeMillis());
			this.act = true;
		}

		public void handleNotification(Notification notification, Object handback) {
			try {
				notifyTo();
			} catch (Throwable t) { // ignored
			}
		}

		public void run() {
			for (; thread != null && act;) {
				try {
					for (Iterator<MemcacheMap> it = cs.values().iterator(); it.hasNext();) {
						try {
							MemcacheMap m = it.next();
							if (m.clean()) it.remove();
							Environments.sleep(50);
						} catch (Throwable t) { // ignored
						}
					}
				} catch (Throwable t) { // ignored
				} finally {
					if (thread != null && act) {
						synchronized (this) {
							time.set(System.currentTimeMillis());
							Environments.wait(this);
						}
					}
				}
			}
			cs.clear();
		}

		/**
		 * 通知清理
		 */
		protected void notifyTo() {
			if (cs == null) return;
			long now = System.currentTimeMillis(), last = time.get();
			if (now - last < limit) return;
			else if (!time.compareAndSet(last, now)) return;
			synchronized (this) {
				if (thread == null) start();
				else this.notify();
			}
		}

		/**
		 * 启动线程
		 */
		protected void start() {
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					Thread t = null;
					synchronized (MemCleanThread.this) {
						act = false;
						t = thread;
						MemCleanThread.this.notify();
					}
					if (t == null) return;
					try {
						t.join();
					} catch (InterruptedException e) { // ignored
					}
				}
			}));
		}
	}
}

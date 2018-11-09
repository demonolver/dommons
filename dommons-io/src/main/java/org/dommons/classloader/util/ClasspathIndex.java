/*
 * @(#)ClasspathIndex.java     2011-10-20
 */
package org.dommons.classloader.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dommons.classloader.bean.ClasspathContainer;
import org.dommons.classloader.bean.ClasspathItem;

/**
 * 类路径索引
 * @author Demon 2011-10-20
 */
public class ClasspathIndex {

	private final Map<String, ClasspathItem> items;
	private final Map<String, Collection<String>> index;
	private final Map<String, String> map;

	private final ReadWriteLock locks;

	/**
	 * 构造函数
	 */
	protected ClasspathIndex() {
		items = new WeakHashMap();
		index = new WeakHashMap();
		map = new WeakHashMap();
		locks = new ReentrantReadWriteLock();
	}

	/**
	 * 添加资源项
	 * @param resName 资源名
	 * @param item 资源项
	 */
	public void addItem(String resName, ClasspathItem item) {
		Lock lock = locks.writeLock();
		lock.lock();
		try {
			String url = item.toString();
			items.put(url, item);
			map.put(resName, url);
			ClasspathContainer container = item.getContainer();
			String parent = container.toString();
			Collection<String> list = index.get(parent);
			if (list == null) list = new HashSet();
			list.add(url);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 清空索引
	 */
	public void clear() {
		Lock lock = locks.writeLock();
		lock.lock();
		try {
			map.clear();
			index.clear();
			items.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取资源项
	 * @param resName 资源名
	 * @return 资源项
	 */
	public ClasspathItem getItem(String resName) {
		ClasspathItem item = null;
		Lock lock = locks.readLock();
		lock.lock();
		try {
			String url = map.get(resName);
			if (url != null) item = items.get(url);
		} finally {
			lock.unlock();
		}
		if (item != null && !item.active()) {
			item = null;
			lock = locks.writeLock();
			lock.lock();
			try {
				items.remove(map.remove(resName));
			} finally {
				lock.unlock();
			}
		}
		return item;
	}

	/**
	 * 移除容器
	 * @param container 资源容器
	 */
	public void remove(ClasspathContainer container) {
		Lock lock = locks.writeLock();
		lock.lock();
		try {
			Collection<String> list = index.remove(container.toString());
			if (list != null) {
				for (String url : list) {
					items.remove(url);
				}
			}
		} finally {
			lock.unlock();
		}
	}
}

/*
 * @(#)DiskLock.java     2018-07-16
 */
package org.dommons.io.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dommons.core.string.Stringure;
import org.dommons.security.cipher.MD5Cipher;

/**
 * 磁盘锁定
 * @author demon 2018-07-16
 */
class DiskLock {

	static final Map<String, ReadWriteLock> locks = new ConcurrentHashMap<String, ReadWriteLock>();

	/**
	 * 转换标准键值
	 * @param key 缓存键值
	 * @return 标准键值
	 */
	protected byte[] key(String key) {
		return md5(Stringure.concat('K', key));
	}

	/**
	 * 获取读写锁
	 * @param key 键值
	 * @return 读写锁
	 */
	protected ReadWriteLock lock(String key) {
		ReadWriteLock rw = locks.get(key);
		if (rw == null) {
			synchronized (locks) {
				rw = locks.get(key);
				if (rw == null) locks.put(key, rw = new ReentrantReadWriteLock());
			}
		}
		return rw;
	}

	/**
	 * 生成 md5 值
	 * @param content 内容
	 * @return md5 值
	 */
	protected byte[] md5(String content) {
		return MD5Cipher.encode(Stringure.toBytes(content, "utf8"));
	}

}

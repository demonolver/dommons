/*
 * @(#)DiskStore.java     2018-07-13
 */
package org.dommons.io.cache;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;

import org.dommons.core.convert.Converter;
import org.dommons.security.coder.HexCoder;

/**
 * 磁盘读写器
 * @author demon 2018-07-13
 */
class DiskStore extends DiskLock {

	static final String m_rw = "rw";
	static final String m_r = "r";

	protected final File file;
	protected final byte[] key;
	protected final ReadWriteLock rw;

	public DiskStore(File file) {
		this.file = init(file);
		this.key = key(this.file);
		this.rw = lock(HexCoder.encodeBuffer(key).toLowerCase());
	}

	/**
	 * 读取内容
	 * @param key 缓存键值
	 * @return 内容值
	 */
	public String read(String key) {
		DiskFile df = getReadFile();
		if (df == null) return null;
		try {
			return df.read(key(key));
		} finally {
			if (df != null) df.close();
		}
	}

	/**
	 * 移除内容
	 * @param key 缓存键值
	 */
	public void remove(String key) {
		DiskFile df = getWriteFile();
		try {
			if (key == null) df.truncate();
			df.remove(key(key));
		} finally {
			if (df != null) df.close();
		}
	}

	/**
	 * 写入内容
	 * @param key 缓存键值
	 * @param value 内容值
	 */
	public void write(String key, String value) {
		DiskFile df = getWriteFile();
		try {
			df.write(key(key), value);
		} finally {
			if (df != null) df.close();
		}
	}

	/**
	 * 初始化
	 * @param file 文件
	 * @return 文件
	 */
	protected File init(File file) {
		file = file.getAbsoluteFile();
		file.getParentFile().mkdirs();
		try {
			if (!file.exists() || !file.isFile()) file.createNewFile();
		} catch (IOException e) {
			throw Converter.F.convert(e, RuntimeException.class);
		}
		return file;
	}

	/**
	 * 获取只读文件
	 * @return 磁盘文件
	 */
	DiskFile getReadFile() {
		try {
			if (!file.exists()) return null;
			return new DiskFile(file, rw.readLock(), m_r, key);
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * 获取写入文件
	 * @return 磁盘文件
	 */
	DiskFile getWriteFile() {
		return new DiskFile(file, rw.writeLock(), m_rw, key);
	}

	/**
	 * 生成文件键值
	 * @param file 文件
	 * @return 键值
	 */
	private byte[] key(File file) {
		return md5("1:" + file.getAbsolutePath());
	}
}

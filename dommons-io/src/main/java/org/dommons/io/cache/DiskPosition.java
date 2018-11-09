/*
 * @(#)DiskPosition.java     2018-07-16
 */
package org.dommons.io.cache;

import java.io.Serializable;

/**
 * 磁盘文件位置
 * @author demon 2018-07-16
 */
class DiskPosition implements Serializable {

	private static final long serialVersionUID = 3657654623959333770L;

	private long offset;
	private long length;
	private long idle;
	private byte separator;
	private byte[] content;
	private int index;

	/**
	 * 获取内容
	 * @return 内容
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * 获取空闲长度
	 * @return 空闲长度
	 */
	public long getIdle() {
		return idle;
	}

	/**
	 * 获取分组序号
	 * @return 分组序号
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * 获取长度
	 * @return 长度
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 获取起始位置
	 * @return 起始位置
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * 获取分割符
	 * @return 分割符
	 */
	public byte getSeparator() {
		return separator;
	}

	/**
	 * 设置内容
	 * @param content 内容
	 * @return 文件位置
	 */
	public DiskPosition setContent(byte[] content) {
		this.content = content;
		return this;
	}

	/**
	 * 设置空闲长度
	 * @param idle 空闲长度
	 * @return 文件位置
	 */
	public DiskPosition setIdle(long idle) {
		this.idle = idle;
		return this;
	}

	/**
	 * 设置分组序号
	 * @param index 分组序号
	 * @return 文件位置
	 */
	public DiskPosition setIndex(int index) {
		this.index = index;
		return this;
	}

	/**
	 * 设置长度
	 * @param length 长度
	 * @return 文件位置
	 */
	public DiskPosition setLength(long length) {
		this.length = length;
		return this;
	}

	/**
	 * 设置起始位置
	 * @param offset 起始位置
	 * @return 文件位置
	 */
	public DiskPosition setOffset(long offset) {
		this.offset = offset;
		return this;
	}

	/**
	 * 设置分割符
	 * @param separator 分割符
	 * @return 文件位置
	 */
	public DiskPosition setSeparator(byte separator) {
		this.separator = separator;
		return this;
	}
}

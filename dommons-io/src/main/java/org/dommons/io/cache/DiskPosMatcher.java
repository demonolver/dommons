/*
 * @(#)DiskPosMatcher.java     2018-07-16
 */
package org.dommons.io.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 文件位置匹配器
 * @author demon 2018-07-16
 */
class DiskPosMatcher {

	protected final RandomAccessFile file;
	protected final byte[] key;

	public DiskPosMatcher(RandomAccessFile file, byte[] key) {
		this.file = file;
		this.key = key;
	}

	/**
	 * 申请新位置空间
	 * @param s 起始位置
	 * @param l 位置长度
	 * @param len 内容长度
	 * @return 文件位置
	 * @throws IOException
	 */
	public DiskPosition apply(long s, long l, long len) throws IOException {
		file.seek(s);
		long x = 0, em = 0;
		for (long p = s, e = s + l; p < e; p++) {
			byte b = file.readByte();
			if (b == DiskFile.em) {
				if (em == 0) x = p;
				em++;
			} else {
				em = 0;
			}
			if (em >= len) break;
		}
		return em < len ? null : position(x, 0, null, em + x);
	}

	/**
	 * 匹配位置
	 * @param s 起始位置
	 * @param l 长度
	 * @return 文件位置
	 * @throws IOException
	 */
	public DiskPosition match(long s, long l) throws IOException {
		file.seek(s);
		long x = 0, em = 0, sep = 0;
		int m = 0;
		byte bsep = 0;
		boolean end = false;
		ByteArrayOutputStream bos = null;
		for (int i = 0; i < l; em = i + s, i++) {
			byte b = file.readByte();
			if (end) {
				if (b != DiskFile.em) break;
			} else if (sep > s) {
				if (bos == null) bos = new ByteArrayOutputStream();
				if (b == DiskFile.end) end = true;
				else bos.write(b);
			} else if (m == key.length) {
				if (b == DiskFile.eq || b == DiskFile.sep) {
					sep = s + i;
					bsep = b;
				} else {
					m = 0;
				}
			} else if (b == key[m]) {
				if (m++ == 0) x = s + i;
			} else {
				m = 0;
			}
		}
		return !end ? null : position(x, sep, bos.toByteArray(), em).setSeparator(bsep);
	}

	/**
	 * 生成文件位置
	 * @param offset 起始位置
	 * @param sep 分割符位置
	 * @param content 值内容
	 * @param p 空闲截止位置
	 * @return 文件位置
	 */
	private DiskPosition position(long offset, long sep, byte[] content, long p) {
		DiskPosition pos = new DiskPosition();
		pos.setOffset(offset);
		pos.setContent(content);
		if (sep > 0) {
			int len = content == null ? 0 : content.length;
			pos.setLength(sep - offset + len + 1);
		}
		pos.setIdle(p - offset - pos.getLength());
		return pos;
	}
}

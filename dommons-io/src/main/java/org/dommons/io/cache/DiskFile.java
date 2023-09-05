/*
 * @(#)DiskFile.java     2018-07-16
 */
package org.dommons.io.cache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;

import org.dommons.core.Silewarner;
import org.dommons.core.convert.Converter;
import org.dommons.core.number.Radix64;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.file.Zipper;
import org.dommons.security.coder.HexCoder;

/**
 * 磁盘文件
 * @author demon 2018-07-16
 */
class DiskFile implements Closeable {

	static Charset charset = Stringure.charset("utf-8", "gbk", "big5");

	static final int pn = 512;
	static final int ct = 64;
	static final int hc = 16;

	static final byte em = '*';
	static final byte end = '#';
	static final byte sep = ':';
	static final byte esc = '\\';
	static final byte eq = '=';

	private final Lock lock;
	private final RandomAccessFile file;
	private final byte[] key;

	public DiskFile(File file, Lock lock, String mode, byte[] key) {
		this.file = file(file, mode);
		this.key = key;
		this.lock = lock;
		this.lock.lock();
	}

	public void close() {
		try {
			DiskBinder.unbind(file);
			file.close();
		} catch (IOException e) {
			Silewarner.error(DiskFile.class, e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 读取内容
	 * @param key 键值
	 * @return 内容
	 */
	public String read(byte[] key) {
		try {
			if (!bind(false)) return null;
			key = key(key);
			DiskPosition pos = position(key);
			return pos == null ? null : value(pos);
		} catch (IOException e) {
			Silewarner.error(DiskFile.class, e);
			return null;
		}
	}

	/**
	 * 移除内容
	 * @param key 键值
	 */
	public void remove(byte[] key) {
		r: try {
			if (!bind(true)) break r;
			key = key(key);
			DiskPosition pos = position(key);
			if (pos != null) empty(pos);
			return;
		} catch (IOException e) {
			Silewarner.error(DiskFile.class, e);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * 清空
	 */
	public void truncate() {
		try {
			if (!bind(true)) return;
			capacity(1, hc * 2);
		} catch (IOException e) {
			Silewarner.error(DiskFile.class, e);
		}
	}

	/**
	 * 写入内容
	 * @param key 键值
	 * @param value 内容
	 */
	public void write(byte[] key, String value) {
		w: try {
			if (!bind(true)) break w;
			key = key(key);
			DiskPosition pos = position(key);
			f: {
				byte[] bs = content(key, value);
				if (pos != null && bs.length <= pos.getLength() + pos.getIdle()) {
					write(pos, bs);
					break f;
				}
				// 不存在或空间不足
				if (pos != null) empty(pos); // 清除原有内容
				// 申请新空间并写入内容
				pos = apply(key, bs.length);
				if (pos != null) write(pos, bs);
				else break w;
			}
			return;
		} catch (IOException e) {
			Silewarner.error(DiskFile.class, e);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * 占用文件
	 * @param write 是否写入
	 * @return 是否占用成功
	 * @throws IOException
	 */
	protected boolean bind(boolean write) throws IOException {
		boolean head = false;
		byte[] hs = null;
		if (file.length() > hc * 2) h: {
			long len = file.length() - hc * 2;
			if (len % pn != 0) break h;
			else if ((len / pn) > 4 && (len / pn - 4) % ct != 0) break h;
			hs = new byte[16];
			file.read(hs);
			if (Arrayard.equals(hs, key)) {
				head = true;
				if (!write) return true;
			}
		}
		b: {
			if (!head && write) {
				capacity(1, hc * 2);
				file.seek(0);
				file.write(key);
				file.write(DiskBinder.ems);
			} else if (!write) {
				break b;
			}
			DiskBinder.bind(file);
			return true;
		}
		return false;
	}

	/**
	 * 生成内容
	 * @param key 键值
	 * @param value 内容值
	 * @return 内容
	 * @throws IOException
	 */
	protected byte[] content(byte[] key, String value) throws IOException {
		int len = value.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(key.length + len * 2);
		bos.write(key);
		byte[] bs = Stringure.toBytes(value, charset);
		gz: {
			if (bs.length > Zipper.gzip_min_size) {
				byte[] gz = Zipper.gzip(bs);
				if (gz.length >= bs.length) break gz;
				bos.write(sep);
				bs = gz;
				break gz;
			}
			bos.write(eq);
		}
		write(bs, bos);
		bos.write(end);
		return bos.toByteArray();
	}

	/**
	 * 读取位置内容
	 * @param pos 文件位置
	 * @return 内容
	 * @throws IOException
	 */
	protected String value(DiskPosition pos) throws IOException {
		byte[] bs = pos.getContent();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length + 8);
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			if (b == esc) {
				if (i < bs.length - 2) {
					String hex = Stringure.toString(bs, i + 1, 2, charset);
					bos.write(Radix64.toInteger(hex, 16));
					i += 2;
				} else {
					break;
				}
			} else {
				bos.write(b);
			}
		}
		if (pos.getSeparator() == sep) bs = Zipper.gunzip(bos.toByteArray());
		else bs = bos.toByteArray();
		return Stringure.toString(bs, charset);
	}

	/**
	 * 申请新位置空间
	 * @param key 键值
	 * @param vl 内容长度
	 * @return 文件位置
	 * @throws IOException
	 */
	DiskPosition apply(byte[] key, long vl) throws IOException {
		DiskPosMatcher matcher = new DiskPosMatcher(file, key);
		long len = file.length(), base = hc * 2 + pn * 4;
		int p = -1;
		for (long o = hc * 2; len < base;) { // 从基础区获取空间
			DiskPosition pos = matcher.apply(o, len - o, vl);
			if (pos != null) {
				return pos.setIndex(p);
			} else {
				o += len - o;
				len = capacity((len - hc * 2) / pn * 2, len);
			}
		}
		{
			DiskPosition pos = matcher.apply(hc * 2, pn * 4, vl);
			if (pos != null) return pos.setIndex(p);
			else if (len == base) len = capacity(1, len);
		}
		p = hash(key);
		long c = 0, x = 1;
		for (;; c += x, x = x * 2) {
			long l = pn * x;
			if (l < vl) continue;
			long o = base + p * l + c * ct * pn;
			if (o >= len) len = capacity(c + x, len);
			DiskPosition pos = matcher.apply(o, l, vl);
			if (pos != null) return pos.setIndex(p);
		}
	}

	/**
	 * 清空位置内容
	 * @param pos 位置
	 * @throws IOException
	 */
	void empty(DiskPosition pos) throws IOException {
		file.seek(pos.getOffset());
		for (int s = 0; s < pos.getLength(); s++)
			file.writeByte(em);
	}

	/**
	 * 查找键值目标位置
	 * @param key 键值
	 * @return 位置
	 * @throws IOException
	 */
	DiskPosition position(byte[] key) throws IOException {
		DiskPosMatcher matcher = new DiskPosMatcher(file, key);
		long len = file.length(), base = hc * 2 + pn * 4;
		if (len < base) return matcher.match(hc * 2, len - hc * 2);
		{
			DiskPosition pos = matcher.match(hc * 2, pn * 4);
			if (pos != null) return pos.setIndex(-1);
		}
		int p = hash(key);
		long c = 0, x = 1;
		for (;; c += x, x = x * 2) {
			long l = pn * x;
			long o = base + p * l + c * ct * pn;
			if (o >= len) break;
			DiskPosition pos = matcher.match(o, l);
			if (pos != null) return pos.setIndex(p);
		}
		return null;
	}

	/**
	 * 写入位置内容
	 * @param pos 位置
	 * @param bs 内容
	 * @throws IOException
	 */
	void write(DiskPosition pos, byte[] bs) throws IOException {
		file.seek(pos.getOffset());
		file.write(bs);
		for (int o = bs.length + 1; o < pos.getLength(); o++)
			file.writeByte(em);
	}

	/**
	 * 执行扩容
	 * @param x 扩容
	 * @param cur 当前长度
	 * @return 新文件长度
	 * @throws IOException
	 */
	private long capacity(long x, long cur) throws IOException {
		long base = hc * 2 + pn * 4;
		long len = 0;
		if (cur >= base) len = base + ct * pn * x;
		else len = hc * 2 + pn * x;
		file.setLength(len);
		file.seek(cur);
		for (long s = cur; s < len; s++)
			file.writeByte(em);
		return len;
	}

	/**
	 * 生成文件
	 * @param file 目标文件
	 * @param mode 模式
	 * @return 文件
	 */
	private RandomAccessFile file(File file, String mode) {
		try {
			return new RandomAccessFile(file, mode);
		} catch (IOException e) {
			throw Converter.F.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 生成哈希值
	 * @param key 键值
	 * @return 哈希值
	 */
	private int hash(byte[] key) {
		int h = HexCoder.encodeBuffer(key).toLowerCase().hashCode();
		return Math.abs(h) % ct;
	}

	/**
	 * 转换键值
	 * @param key 键值
	 * @return 标准键值
	 * @throws IOException
	 */
	private byte[] key(byte[] key) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(key.length + 4);
		write(key, bos);
		return bos.toByteArray();
	}

	/**
	 * 写入内容
	 * @param bs 内容
	 * @param bos 字节流
	 * @throws IOException
	 */
	private void write(byte[] bs, ByteArrayOutputStream bos) throws IOException {
		for (byte b : bs) {
			switch (b) {
			case em:
			case esc:
			case eq:
			case sep:
			case end:
				bos.write('\\');
				bos.write(Radix64.toHex(b).toLowerCase().getBytes());
				break;

			default:
				bos.write(b);
				break;
			}
		}
	}
}

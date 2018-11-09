/*
 * @(#)AppendableOutputStream.java     2015-9-22
 */
package org.dommons.io.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.dommons.core.convert.Converter;

/**
 * 可追加输出流
 * @author Demon 2015-9-22
 */
public class AppendableOutputStream extends BufferedOutputStream {

	public AppendableOutputStream(OutputStream out) {
		super(out);
	}

	public AppendableOutputStream(OutputStream out, int size) {
		super(out, size);
	}

	/**
	 * 追加字节
	 * @param bs 字节
	 * @return 输出流
	 */
	public AppendableOutputStream append(byte[] bs) {
		if (bs != null && bs.length > 0) {
			try {
				write(bs);
			} catch (IOException e) {
				throw Converter.F.convert(e, RuntimeException.class);
			}
		}
		return this;
	}

	/**
	 * 追加字节
	 * @param bs 字节
	 * @param off 起始位置
	 * @param len 长度
	 * @return 输出流
	 */
	public AppendableOutputStream append(byte[] bs, int off, int len) {
		if (bs != null && bs.length > 0) {
			try {
				write(bs, off, len);
			} catch (IOException e) {
				throw Converter.F.convert(e, RuntimeException.class);
			}
		}
		return this;
	}

	/**
	 * 追加字节
	 * @param bs 字节
	 * @return 输出流
	 */
	public AppendableOutputStream append(int... bs) {
		if (bs != null && bs.length > 0) {
			byte[] ts = new byte[bs.length];
			for (int i = 0; i < bs.length; i++)
				ts[i] = (byte) bs[i];
			try {
				write(ts);
			} catch (IOException e) {
				throw Converter.F.convert(e, RuntimeException.class);
			}
		}
		return this;
	}
}

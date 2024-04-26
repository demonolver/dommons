/*
 * @(#)DiskBinder.java     2018-07-16
 */
package org.dommons.io.cache;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.dommons.core.Environments;
import org.dommons.core.util.Arrayard;
import org.dommons.io.net.UniQueness;
import org.dommons.security.coder.HexCoder;

/**
 * 磁盘文件占用器
 * @author demon 2018-07-16
 */
class DiskBinder {

	static final byte[] ems;
	static {
		ems = new byte[DiskFile.hc];
		for (int i = ems.length - 1; i >= 0; i--)
			ems[i] = DiskFile.em;
	}

	/**
	 * 占用磁盘文件
	 * @param file 文件
	 * @throws IOException
	 */
	public static boolean bind(RandomAccessFile file) throws IOException {
		byte[] x = HexCoder.decodeBuffer(UniQueness.generateHexUUID());
		byte[] bs = new byte[ems.length], old = null;
		long stat = System.currentTimeMillis(), s = stat;
		for (; System.currentTimeMillis() - s < 6000;) {
			file.seek(16);
			file.read(bs);
			if (Arrayard.equals(bs, x)) {
				return true;
			} else if (!Arrayard.equals(bs, ems)) dead: {
				if (!Arrayard.equals(old, bs)) {
					old = bs;
					stat = System.currentTimeMillis();
				} else {
					if (System.currentTimeMillis() - stat >= 1000) break dead;
				}
				Environments.sleep(200);
			}
			file.seek(16);
			file.write(x);
		}
		return false;
	}

	/**
	 * 释放磁盘文件
	 * @param file 文件
	 */
	public static void unbind(RandomAccessFile file) {
		try {
			file.seek(16);
			file.write(ems);
		} catch (IOException e) { // ignored
		}
	}

	static class FileFullIOException extends IOException {

		private static final long serialVersionUID = -6235915234109695599L;

		public FileFullIOException() {
			super();
		}
	}
}

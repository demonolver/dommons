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
	public static void bind(RandomAccessFile file) throws IOException {
		byte[] x = HexCoder.decodeBuffer(UniQueness.generateHexUUID());
		byte[] bs = new byte[ems.length], old = null;
		long stat = System.currentTimeMillis();
		for (;;) {
			file.seek(16);
			file.read(bs);
			if (Arrayard.equals(bs, x)) {
				return;
			} else if (!Arrayard.equals(bs, ems)) dead: {
				if (!Arrayard.equals(old, bs)) {
					old = bs;
					stat = System.currentTimeMillis();
				} else {
					if (System.currentTimeMillis() - stat > 3000) break dead;
				}
				Environments.sleep(500);
			}
			file.seek(16);
			file.write(x);
		}
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
}

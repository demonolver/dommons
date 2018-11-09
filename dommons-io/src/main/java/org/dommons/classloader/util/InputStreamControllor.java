/*
 * @(#)InputStreamController.java     2011-10-20
 */
package org.dommons.classloader.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 输入流控制器
 * @author Demon 2011-10-20
 */
public class InputStreamControllor {

	private final Map<String, InputStreamWrapper> streams;

	private volatile boolean closed;
	/**
	 * 构造函数
	 */
	public InputStreamControllor() {
		this.streams = new ConcurrentHashMap();
		closed = false;
	}

	/**
	 * 关闭
	 */
	public void close() {
		closed = true;
		InputStreamWrapper[] wrappers = streams.values().toArray(new InputStreamWrapper[streams.size()]);
		streams.clear();
		for (InputStreamWrapper wrapper : wrappers) {
			wrapper.forceClose();
		}
	}

	/**
	 * 是否已经关闭
	 * @return 是否
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * 包装输入流
	 * @param in 原输入流
	 * @return 输入流
	 */
	public InputStream wrap(InputStream in) {
		if (closed) {
			try {
				in.close();
			} catch (IOException e) {
			}
			throw new IllegalStateException();
		}
		InputStreamWrapper wrapper = new InputStreamWrapper(in);
		streams.put(wrapper.getUUID(), wrapper);
		return wrapper;
	}

	/**
	 * 移除流
	 * @param wrapper 流包装
	 */
	protected void remove(InputStreamWrapper wrapper) {
		streams.remove(wrapper.getUUID());
	}

	/**
	 * 输入流包装
	 * @author Demon 2011-10-20
	 */
	protected class InputStreamWrapper extends InputStream {

		private final InputStream in;
		private final String uuid;

		public InputStreamWrapper(InputStream in) {
			this.in = in;
			this.uuid = UUID.randomUUID().toString();
		}

		public int available() throws IOException {
			return in.available();
		}

		public void close() throws IOException {
			try {
				in.close();
			} finally {
				remove(this);
			}
		}

		/**
		 * 获取唯一序号
		 * @return 序号
		 */
		public String getUUID() {
			return uuid;
		}

		public synchronized void mark(int readlimit) {
			in.mark(readlimit);
		}

		public boolean markSupported() {
			return in.markSupported();
		}

		public int read() throws IOException {
			return in.read();
		}

		public int read(byte[] b) throws IOException {
			return in.read(b);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return in.read(b, off, len);
		}

		public synchronized void reset() throws IOException {
			in.reset();
		}

		public long skip(long n) throws IOException {
			return in.skip(n);
		}

		/**
		 * 强制关闭
		 */
		protected void forceClose() {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
}

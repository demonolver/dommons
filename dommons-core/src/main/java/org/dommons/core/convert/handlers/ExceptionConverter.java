/*
 * @(#)ExceptionConverter.java     2011-10-26
 */
package org.dommons.core.convert.handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import org.dommons.core.convert.ConvertHandler;

/**
 * 异常转换器
 * @author Demon 2011-10-26
 */
public final class ExceptionConverter implements ConvertHandler<Throwable, RuntimeException> {

	public RuntimeException convert(Throwable t, Class<? extends Throwable> source, Class<RuntimeException> target) {
		if (InvocationTargetException.class.isAssignableFrom(source)) {
			t = ((InvocationTargetException) t).getTargetException();
		} else if (ExecutionException.class.isAssignableFrom(source)) {
			t = ((ExecutionException) t).getCause();
		}
		return new RuntimeWrapper(t);
	}

	/**
	 * 运行时异常包装
	 * @author Demon 2011-10-26
	 */
	public static final class RuntimeWrapper extends RuntimeException {

		private static final long serialVersionUID = 4856129678599609531L;

		private transient Throwable tar;
		private transient String msg;
		private transient String type;

		/**
		 * 构造函数
		 * @param tar 目标异常
		 */
		protected RuntimeWrapper(Throwable tar) {
			this.tar = tar;
		}

		public boolean equals(Object o) {
			return super.equals(o) || (tar != null && tar.equals(o));
		}

		public Throwable fillInStackTrace() {
			if (tar == null) return null;
			return tar.fillInStackTrace();
		}

		public Throwable getCause() {
			return tar == null ? null : tar.getCause();
		}

		public String getLocalizedMessage() {
			return tar == null ? msg : tar.getLocalizedMessage();
		}

		public String getMessage() {
			return tar == null ? msg : tar.getMessage();
		}

		public StackTraceElement[] getStackTrace() {
			return tar == null ? super.getStackTrace() : tar.getStackTrace();
		}

		/**
		 * 获取目标异常实例
		 * @return 异常对象
		 */
		public Throwable getTargetThrowable() {
			return tar;
		}

		public int hashCode() {
			return tar == null ? super.hashCode() : tar.hashCode();
		}

		public synchronized Throwable initCause(Throwable throwable) {
			if (tar != null) {
				tar.initCause(throwable);
			} else {
				super.initCause(throwable);
			}
			return this;
		}

		public void printStackTrace() {
			if (tar != null) {
				tar.printStackTrace();
			} else {
				super.printStackTrace();
			}
		}

		public void printStackTrace(PrintStream err) {
			if (tar != null) {
				tar.printStackTrace(err);
			} else {
				super.printStackTrace(err);
			}
		}

		public void printStackTrace(PrintWriter err) {
			if (tar != null) {
				tar.printStackTrace(err);
			} else {
				super.printStackTrace(err);
			}
		}

		public void setStackTrace(StackTraceElement[] trace) {
			if (tar != null) {
				tar.setStackTrace(trace);
			} else {
				super.setStackTrace(trace);
			}
		}

		public String toString() {
			if (tar != null) {
				return tar.toString();
			} else {
				if (msg == null) {
					return type;
				} else {
					return new StringBuilder(type.length() + 2 + msg.length()).append(type).append(": ").append(msg).toString();
				}
			}
		}

		/**
		 * 读取对象
		 * @param s 读取域
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
			s.defaultReadObject();
			msg = s.readUTF();
			type = s.readUTF();
			try {
				tar = (Throwable) s.readObject();
			} catch (ClassNotFoundException e) {
			}
		}

		/**
		 * 写入对象
		 * @param s 写入域
		 * @throws IOException
		 */
		private void writeObject(final ObjectOutputStream s) throws IOException {
			msg = tar.getMessage();
			type = tar.getClass().getName();
			super.setStackTrace(tar.getStackTrace());

			s.defaultWriteObject();
			s.writeUTF(msg);
			s.writeUTF(type);
			s.writeObject(tar);
		}
	}
}

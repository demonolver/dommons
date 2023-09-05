/*
 * @(#)Silewarner.java     2011-11-1
 */
package org.dommons.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dommons.core.cache.MemcacheMap;
import org.dommons.core.string.Stringure;

/**
 * 静默警报器
 * @author Demon 2011-11-1
 */
public final class Silewarner {

	static final ThreadLocal<Boolean> local = new ThreadLocal();

	/**
	 * 记录错误
	 * @param cls 类
	 * @param msg 信息
	 */
	public static void error(Class cls, String msg) {
		error(cls, msg, null);
	}

	/**
	 * 记录错误
	 * @param cls 类
	 * @param msg 信息
	 * @param t 异常
	 */
	public static void error(Class cls, String msg, Throwable t) {
		{
			LoggerFactoryHandler handler = LoggerFactoryHandler.get(cls);
			if (handler != null && handler.error(msg, t)) return;
		}
		{
			Log4jHandler handler = Log4jHandler.get(cls);
			if (handler != null && handler.error(msg, t)) return;
		}
		warn(Level.SEVERE, cls, msg, t);
	}

	/**
	 * 记录错误
	 * @param cls 类
	 * @param t 异常
	 */
	public static void error(Class cls, Throwable t) {
		error(cls, null, t);
	}

	/**
	 * 是否正在报警
	 * @return 是、否
	 */
	public static boolean isWarning() {
		return Boolean.TRUE.equals(local.get());
	}

	/**
	 * 记录警告
	 * @param cls 类
	 * @param msg 信息
	 */
	public static void warn(Class cls, String msg) {
		warn(cls, msg, null);
	}

	/**
	 * 记录警告
	 * @param cls 类
	 * @param msg 信息
	 * @param t 异常
	 */
	public static void warn(Class cls, String msg, Throwable t) {
		{
			LoggerFactoryHandler handler = LoggerFactoryHandler.get(cls);
			if (handler != null && handler.warn(msg, t)) return;
		}
		{
			Log4jHandler handler = Log4jHandler.get(cls);
			if (handler != null && handler.warn(msg, t)) return;
		}
		warn(Level.WARNING, cls, msg, t);
	}

	/**
	 * 记录警告
	 * @param cls 类
	 * @param t 异常
	 */
	public static void warn(Class cls, Throwable t) {
		warn(cls, null, t);
	}

	/**
	 * 执行默认记录方式
	 * @param level 记录级别
	 * @param cls 目标类
	 * @param msg 信息
	 * @param t 异常
	 */
	protected static void warn(Level level, Class cls, String msg, Throwable t) {
		Logger l = Logger.getLogger(cls == null ? null : cls.getName());
		Throwable re = t == null ? new RuntimeException() : t;
		String s = null, m = null;
		for (StackTraceElement ste : re.getStackTrace()) {
			if (Silewarner.class.getName().equals(ste.getClassName())) continue;
			s = ste.getClassName();
			m = ste.getMethodName();
			break;
		}
		l.logp(level, s, m, Stringure.trim(msg), t);
	}

	/**
	 * 查找类
	 * @return 类实例
	 */
	static Class findClass(String name) {
		try {
			return Class.forName(name, false, Silewarner.class.getClassLoader());
		} catch (Throwable t) {
		}
		try {
			return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
		} catch (Throwable t) {
		}
		return null;
	}

	/**
	 * 查找方法
	 * @param cls 类
	 * @param name 方法名
	 * @param types 参数类型集
	 * @return 方法
	 */
	static Method findMethod(Class cls, String name, Class... types) {
		if (cls == null || Assertor.P.empty(name)) return null;
		try {
			Method m = cls.getMethod(name, types);
			if (!m.isAccessible()) m.setAccessible(true);
			return m;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	static void warn(Throwable t) {
		if (t instanceof InvocationTargetException) t = ((InvocationTargetException) t).getTargetException();
		Silewarner.warn(Level.WARNING, Silewarner.class, null, t);
	}

	/**
	 * 构造函数
	 */
	protected Silewarner() {}

	/**
	 * log4j 日志记录执行器
	 * @author Demon 2011-11-1
	 */
	static class Log4jHandler {

		static Map<Class, Log4jHandler> cache;

		/**
		 * 获取执行器实例
		 * @param cls 目标类
		 * @return 执行器实例
		 */
		public static Log4jHandler get(Class cls) {
			Log4jHandler h = cache == null ? null : cache.get(cls);
			if (h != null) return h;
			Object logger = getLogger(cls);
			if (logger == null) return null;
			if (cache == null) {
				synchronized (Log4jHandler.cache) {
					if (cache == null) cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));
				}
			}
			cache.put(cls, h = new Log4jHandler(logger));
			return h;
		}

		/**
		 * 获取 log4j 日志记录器实例
		 * @param cls 目标类
		 * @return 记录器实例
		 */
		static Object getLogger(Class cls) {
			Class tc = findClass("org.apache.log4j.Logger");
			Method m = findMethod(tc, "getLogger", Class.class);
			try {
				if (m != null) return m.invoke(null, cls);
			} catch (Throwable t) {
				Silewarner.warn(t);
			}
			return null;
		}

		private final Object inst;

		/**
		 * 构造函数
		 * @param instance log4j 日志记录实例
		 */
		protected Log4jHandler(Object instance) {
			this.inst = instance;
		}

		/**
		 * 记录错误信息
		 * @param msg 信息
		 * @param t 异常
		 * @return 是否记录成功
		 */
		public boolean error(String msg, Throwable t) {
			Method m = findMethod(inst.getClass(), "error", Object.class, Throwable.class);
			try {
				if (m != null) {
					m.invoke(inst, msg, t);
					return true;
				}
			} catch (Throwable e) {
				Silewarner.warn(e);
			}
			return false;
		}

		/**
		 * 记录警告信息
		 * @param msg 信息
		 * @param t 异常
		 * @return 是否记录成功
		 */
		public boolean warn(String msg, Throwable t) {
			Method m = findMethod(inst.getClass(), "warn", Object.class, Throwable.class);
			try {
				if (m != null) {
					m.invoke(inst, msg, t);
					return true;
				}
			} catch (Throwable e) {
				Silewarner.warn(e);
			}
			return false;
		}
	}

	/**
	 * 通知日志工厂日志记录执行器
	 * @author Demon 2014-4-14
	 */
	static class LoggerFactoryHandler {

		static Map<Class, LoggerFactoryHandler> cache;

		/**
		 * 获取记录器实例
		 * @param cls 目标类
		 * @return 记录器实例
		 */
		public static LoggerFactoryHandler get(Class cls) {
			LoggerFactoryHandler h = cache == null ? null : cache.get(cls);
			if (h != null) return h;
			Object logger = logger(cls);
			if (logger == null) return null;
			if (cache == null) {
				synchronized (LoggerFactoryHandler.class) {
					if (cache == null) cache = new MemcacheMap(TimeUnit.HOURS.toMillis(3), TimeUnit.HOURS.toMillis(24));
				}
			}
			cache.put(cls, h = new LoggerFactoryHandler(logger));
			return h;
		}

		/**
		 * 获取日志实例
		 * @param cls 目标类
		 * @return 日志实例
		 */
		static Object logger(Class cls) {
			Class tc = findClass("org.dommons.log.LoggerFactory");
			Method m = findMethod(tc, "getInstance");
			Object obj = null;
			try {
				if (m != null) obj = m.invoke(null);
			} catch (Throwable e) {
				Silewarner.warn(e);
			}
			if (obj == null) return null;
			m = findMethod(obj.getClass(), "getLogger", Class.class);

			try {
				if (m != null) return m.invoke(obj, cls);
			} catch (Throwable e) {
				Silewarner.warn(e);
			}
			return null;
		}

		private final Object inst;

		protected LoggerFactoryHandler(Object instance) {
			this.inst = instance;
		}

		/**
		 * 记录错误信息
		 * @param msg 信息
		 * @param t 异常
		 * @return 是否记录成功
		 */
		public boolean error(String msg, Throwable t) {
			local.set(Boolean.TRUE);
			try {
				Method m = findMethod(inst.getClass(), "error", Throwable.class, CharSequence.class);
				try {
					if (m != null) {
						m.invoke(inst, t, msg);
						return true;
					}
				} catch (Throwable e) {
					Silewarner.warn(e);
				}
				return false;
			} finally {
				local.remove();
			}
		}

		/**
		 * 记录警告信息
		 * @param msg 信息
		 * @param t 异常
		 * @return 是否记录成功
		 */
		public boolean warn(String msg, Throwable t) {
			local.set(Boolean.TRUE);
			try {
				Method m = findMethod(inst.getClass(), "warn", Throwable.class, CharSequence.class);
				try {
					if (m != null) {
						m.invoke(inst, t, msg);
						return true;
					}
				} catch (Throwable e) {
					Silewarner.warn(e);
				}
				return false;
			} finally {
				local.remove();
			}
		}
	}
}

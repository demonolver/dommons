/*
 * @(#)ThreadLocals.java     2017-10-09
 */
package org.dommons.core.util.thread;

import java.lang.reflect.Field;

import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;

/**
 * 线程上下文
 * @author demon 2017-10-09
 */
public class ThreadLocals {

	static Ref<Field> fref;

	/**
	 * 处理异常
	 * @param t 异常
	 */
	protected static void e(Throwable t) {
		ThreadLocalMapCloner.e(t);
	}

	/**
	 * 获取上下文属性
	 * @return 属性
	 */
	protected static Field field() {
		Field f = fref == null ? null : fref.get();
		if (f == null) {
			try {
				f = Thread.class.getDeclaredField("threadLocals");
				f.setAccessible(true);
				fref = new Softref(f);
			} catch (Throwable t) { // ignored
				e(t);
			}
		}
		return f;
	}
}

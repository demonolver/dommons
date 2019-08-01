/*
 * @(#)RunnableTask.java     2016-10-18
 */
package org.dommons.core.concurrent;

/**
 * 异步可执行任务
 * @author demon 2016-10-18
 */
public interface RunnableTask extends Runnable {

	/**
	 * 后置执行
	 * @param t 执行中异常
	 */
	public void afterExecute(Throwable t);

	/**
	 * 前置执行
	 */
	public void beforeExecute();

	/**
	 * 类型包装接口
	 * @author demon 2017-02-16
	 * @deprecated
	 */
	public interface TypeWrap {
		/**
		 * 获取类型
		 * @return 类型
		 */
		public java.lang.reflect.Type type();
	}
}

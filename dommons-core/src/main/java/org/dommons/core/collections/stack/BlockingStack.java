/*
 * @(#)BlockingStack.java     2011-10-18
 */
package org.dommons.core.collections.stack;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞堆栈
 * @author Demon 2011-10-18
 */
public interface BlockingStack<E> extends Stack<E> {

	/**
	 * 堆栈迁移
	 * @param c 目标集合体
	 * @return 迁移数量
	 */
	int drainTo(Collection<? super E> c);

	/**
	 * 堆栈迁移
	 * @param c 目标集合体
	 * @param maxElements 最大迁移数量
	 * @return 迁移数量
	 */
	int drainTo(Collection<? super E> c, int maxElements);

	/**
	 * 出栈
	 * @param timeout 超时时长
	 * @param unit 时间单位
	 * @return 出栈元素
	 * @throws InterruptedException
	 */
	E pop(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 加入栈
	 * @param o 数据
	 * @param timeout 超时时长
	 * @param unit 时间单位
	 * @return 是否加入成功
	 * @throws InterruptedException
	 */
	boolean push(E o, long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 加入元素 如栈已满则等待直到加入或线程被终止
	 * @param o 元素值
	 * @throws InterruptedException
	 */
	void put(E o) throws InterruptedException;

	/**
	 * 剩除空间
	 * @return 剩除空间
	 */
	int remainingCapacity();

	/**
	 * 取走首个元素 如栈为空则等待直到取得元素或线程被终止
	 * @return 元素值
	 * @throws InterruptedException
	 */
	E take() throws InterruptedException;
}

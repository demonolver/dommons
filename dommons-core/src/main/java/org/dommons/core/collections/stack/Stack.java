/*
 * @(#)Stack.java     2011-10-18
 */
package org.dommons.core.collections.stack;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 堆栈
 * @author Demon 2011-10-18
 */
public interface Stack<E> extends Collection<E> {

	/**
	 * 获得首个元素，不将其移出堆栈，如堆栈为空则抛出异常
	 * @return 元素项
	 * @throws NoSuchElementException
	 */
	E element() throws NoSuchElementException;

	/**
	 * 获取顺序迭代器
	 * @return 迭代器
	 */
	Iterator<E> iterator();

	/**
	 * 获得首个元素，不将其移出堆栈
	 * @return 元素项 堆栈为空时返回<code>null</code>
	 */
	E peek();

	/**
	 * 获得首个元素并将其移出堆栈
	 * @return 元素项 堆栈为空时返回<code>null</code>
	 */
	E pop();

	/**
	 * 将元素加入到堆栈中
	 * @param o 元素
	 * @return 是否加入
	 */
	boolean push(E o);

	/**
	 * 获得首个元素并将其移出堆栈，如堆栈为空则抛出异常
	 * @return 元素项
	 * @throws NoSuchElementException
	 */
	E remove() throws NoSuchElementException;

	/**
	 * 获取栈迭代器 以先入后出方式迭代器
	 * @return 迭代器
	 */
	Iterator<E> stackIterator();
}

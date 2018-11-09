/*
 * @(#)AbstractStack.java     2011-10-18
 */
package org.dommons.core.collections.stack;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.NoSuchElementException;

/**
 * 抽象堆栈实现类
 * @author Demon 2011-10-18
 */
public abstract class AbstractStack<E> extends AbstractCollection<E> implements Stack<E>, Serializable {

	private static final long serialVersionUID = -5054251262084422494L;

	public boolean add(E o) {
		return push(o);
	}

	public E element() throws NoSuchElementException {
		E x = peek();
		if (x != null) {
			return x;
		} else {
			throw new NoSuchElementException();
		}
	}

	public E remove() throws NoSuchElementException {
		E x = pop();
		if (x != null) {
			return x;
		} else {
			throw new NoSuchElementException();
		}
	}
}

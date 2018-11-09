/*
 * @(#)IterableGroup.java     2018-03-15
 */
package org.dommons.core.collections.iterable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 可迭代集合组
 * @author demon 2018-03-15
 */
public class IterableGroup<T> implements Iterable<T> {

	protected final List<Iterable<? extends T>> list;

	public IterableGroup() {
		super();
		list = new LinkedList();
	}

	public IterableGroup<T> append(Iterable<? extends T> sub) {
		if (sub != null && sub != this) list.add(sub);
		return this;
	}

	public Iterator<T> iterator() {
		return new GroupIterator();
	}

	/**
	 * 组迭代器
	 * @author demon 2018-03-15
	 */
	protected class GroupIterator implements Iterator<T> {

		private Iterator<? extends T> tar;
		private Iterator<Iterable<? extends T>> it;

		protected GroupIterator() {
			it = list.iterator();
		}

		public boolean hasNext() {
			boolean b = false;
			for (;;) {
				try {
					b = tar != null && tar.hasNext();
				} catch (RuntimeException e) { // ignored
				}
				if (!b && it.hasNext()) tar = it.next().iterator();
				else break;
			}
			return b;
		}

		public T next() {
			return tar == null ? null : tar.next();
		}

		public void remove() {
			if (tar == null) return;
			else tar.remove();
		}
	}
}

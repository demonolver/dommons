/*
 * @(#)TreeQueue.java     2011-10-19
 */
package org.dommons.core.collections.queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 树型队列
 * @author Demon 2011-10-19
 */
public class TreeQueue<E> extends AbstractQueue<E> implements Serializable {

	private static final long serialVersionUID = -3979283301657289230L;

	/** 红色 */
	protected static final boolean RED = false;
	/** 黑色 */
	protected static final boolean BLACK = true;

	/**
	 * 获取左子节点
	 * @param x 节点
	 * @return 左子节点
	 */
	static TreeQueue.Node leftOf(TreeQueue.Node x) {
		return x == null ? null : x.left;
	}

	/**
	 * 获取父节点
	 * @param x 节点
	 * @return 父元素
	 */
	static TreeQueue.Node parentOf(TreeQueue.Node x) {
		return x == null ? null : x.parent;
	}

	/**
	 * 获取右子节点
	 * @param x 节点
	 * @return 右子节点
	 */
	static TreeQueue.Node rightOf(TreeQueue.Node x) {
		return x == null ? null : x.right;
	}

	/**
	 * 设置节点颜色
	 * @param x 节点
	 * @param color 颜色
	 */
	static void setColor(TreeQueue.Node x, boolean color) {
		if (x != null) x.color = color;
	}

	private final Comparator<? super E> comparator;

	private int size = 0;

	private transient int modCount = 0;
	private transient Node root;
	private transient Node first;

	/**
	 * 构造函数
	 */
	public TreeQueue() {
		this(null);
	}

	/**
	 * 构造函数
	 * @param comparator 比较器
	 */
	public TreeQueue(Comparator<? super E> comparator) {
		this.comparator = comparator;
	}

	public void clear() {
		modCount++;
		size = 0;
		root = null;
		first = null;
	}

	public boolean contains(Object o) {
		Node node = getNode(o);
		return node == null ? false : node.contains((E) o);
	}

	public Iterator<E> iterator() {
		return new TreeIterator();
	}

	public boolean offer(E e) {
		if (e == null) throw new IllegalArgumentException();
		Node t = root;

		if (t == null) {
			root = new Node(e, null);
			first = root;
			return true;
		}

		while (true) {
			int cmp = compare(e, t.sample);
			if (cmp == 0) {
				t.addValue(e);
				return true;
			} else if (cmp < 0) {
				if (t.left != null) {
					t = t.left;
				} else {
					Node n = new Node(e, t);
					t.left = n;
					if (t == first) first = n;
					fixAfterInsertion(t.left);
					return true;
				}
			} else if (cmp > 0) {
				if (t.right != null) {
					t = t.right;
				} else {
					t.right = new Node(e, t);
					fixAfterInsertion(t.right);
					return true;
				}
			}
		}
	}

	public E peek() {
		return first == null ? null : first.sample;
	}

	public E poll() {
		if (first == null) return null;
		E e = first.removeValue(0);
		return e;
	}

	public boolean remove(Object o) {
		Node node = getNode(o);
		if (node == null) return false;
		if (!node.removeValue((E) o)) return false;
		return true;
	}

	public int size() {
		return size;
	}

	public Object[] toArray() {
		return toArray(new Object[size]);
	}

	public <T> T[] toArray(T[] a) {
		if (a.length < size) a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

		Node n = first;
		int index = 0;
		while (n != null) {
			System.arraycopy(n.elementDatas, 0, a, index, n.size);
			index += n.size;
			n = successor(n);
		}
		return a;
	}

	/**
	 * 比较
	 * @param e1 元素1
	 * @param e2 元素2
	 * @return 比较结果
	 */
	protected int compare(E e1, E e2) {
		if (comparator != null) return comparator.compare(e1, e2);
		else if (e1 != null) return ((Comparable) e1).compareTo(e2);
		else if (e2 != null) return -((Comparable) e2).compareTo(e1);
		else return 0;
	}

	/**
	 * 获取节点
	 * @param key 元素值
	 * @return 节点 不存在返回<code>null</code>
	 */
	protected Node getNode(Object key) {
		Node p = root;
		E e = (E) key;
		while (p != null) {
			int cmp = compare(e, p.sample);
			if (cmp == 0) return p;
			else if (cmp < 0) p = p.left;
			else p = p.right;
		}
		return null;
	}

	/**
	 * 移动队列头
	 */
	protected void moveFirst() {
		if (first != null && first.left == null) {
			Node p = null;
			for (Node t = first; t == leftOf(p = parentOf(t)); t = p) {
				if (p == root) return;
			}
		}
		Node p = root;
		if (p == null) {
			first = null;
			return;
		}
		for (; p.left != null; p = p.left);
		first = p;
	}

	/**
	 * 移除节点
	 * @param p 节点
	 */
	protected void removeNode(Node p) {
		if (p == null) return;

		if (first == p) first = successor(p);

		if (p.left != null && p.right != null) {
			Node s = successor(p);
			p.sample = s.sample;
			p.elementDatas = s.elementDatas;
			p.size = s.size;
			p = s;
		}

		Node replacement = (p.left != null ? p.left : p.right);

		if (replacement != null) {
			// 转接父节点
			replacement.parent = p.parent;
			if (p.parent == null) root = replacement;
			else if (p == p.parent.left) p.parent.left = replacement;
			else p.parent.right = replacement;

			// 清除节点链接
			p.left = p.right = p.parent = null;

			// 校准节点
			if (p.color == BLACK) fixAfterDeletion(replacement);
		} else if (p.parent == null) { // 删除当前唯一节点
			root = null;
		} else { // 叶子节点仅删除节点
			if (p.color == BLACK) fixAfterDeletion(p);

			if (p.parent != null) {
				if (p == p.parent.left) p.parent.left = null;
				else if (p == p.parent.right) p.parent.right = null;
				p.parent = null;
			}
		}
	}

	/**
	 * 获取节点颜色
	 * @param x 节点
	 * @return 颜色 红或黑
	 */
	boolean colorOf(Node x) {
		return x == null ? BLACK : x.color;
	}

	/** 数量减少 **/
	void decrementSize() {
		modCount++;
		size--;
	}

	/**
	 * 删除后校准树平衡
	 * @param x 节点对象
	 */
	void fixAfterDeletion(Node x) {
		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				Node sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // 对称处理
				Node sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}

		setColor(x, BLACK);
	}

	/**
	 * 新增后校准树平衡
	 * @param x 节点对象
	 */
	void fixAfterInsertion(Node x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Node y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Node y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	/** 数量增长 **/
	void incrementSize() {
		modCount++;
		size++;
	}

	/**
	 * 左移
	 * @param p 节点
	 */
	void rotateLeft(Node p) {
		Node r = p.right;
		p.right = r.left;
		if (r.left != null) r.left.parent = p;
		r.parent = p.parent;
		if (p.parent == null) root = r;
		else if (p.parent.left == p) p.parent.left = r;
		else p.parent.right = r;
		r.left = p;
		p.parent = r;
	}

	/**
	 * 右移
	 * @param p 节点
	 */
	void rotateRight(Node p) {
		Node l = p.left;
		p.left = l.right;
		if (l.right != null) l.right.parent = p;
		l.parent = p.parent;
		if (p.parent == null) root = l;
		else if (p.parent.right == p) p.parent.right = l;
		else p.parent.left = l;
		l.right = p;
		p.parent = l;
	}

	/**
	 * 获取后一节点
	 * @param n 当前节点
	 * @return 后一节点
	 */
	Node successor(Node n) {
		if (n == null) return null;
		else if (n.right != null) {
			Node p = n.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Node p = n.parent;
			Node ch = n;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
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

		int n = s.readInt();

		for (int i = 0; i < n; i++) {
			Node node = (Node) s.readObject();
			node.color = BLACK;
			Node t = root;

			if (t == null) {
				root = node;
				first = root;
			} else {
				while (true) {
					int cmp = compare(node.sample, t.sample);
					if (cmp == 0) {
						break;
					} else if (cmp < 0) {
						if (t.left != null) {
							t = t.left;
						} else {
							node.parent = t;
							t.left = node;
							if (t == first) first = node;
							fixAfterInsertion(t.left);
							break;
						}
					} else if (cmp > 0) {
						if (t.right != null) {
							t = t.right;
						} else {
							node.parent = t;
							t.right = node;
							fixAfterInsertion(t.right);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 写入节点
	 * @param node 节点
	 * @param list 目标列表
	 * @return 写入节点数量
	 * @throws IOException
	 */
	private int writeNode(Node node, Collection<Node> list) throws IOException {
		if (node == null) return 0;
		int n = 0;
		n += writeNode(node.left, list);
		list.add(node);
		n++;
		n += writeNode(node.right, list);
		return n;
	}

	/**
	 * 写入对象
	 * @param s 写入域
	 * @throws IOException
	 */
	private void writeObject(final ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		Collection<Node> list = new ArrayList(size);
		writeNode(root, list);
		s.writeInt(list.size());

		for (Node node : list) {
			s.writeObject(node);
		}
	}

	/**
	 * 元素节点
	 * @author Demon 2011-10-19
	 */
	protected class Node implements Serializable {

		private static final long serialVersionUID = 6577070831528001688L;

		protected transient Node parent;
		protected transient Node left = null;
		protected transient Node right = null;
		protected transient boolean color = BLACK;
		protected transient E sample;

		private transient E[] elementDatas;
		private int size = 0;

		/**
		 * 构造函数
		 * @param sample 样本元素
		 * @param parent 父元素
		 */
		public Node(E sample, Node parent) {
			ensureCapacity(10);
			this.sample = sample;
			this.parent = parent;
			addValue(sample);
		}

		/**
		 * 加入值
		 * @param e 元素值
		 */
		public void addValue(E e) {
			incrementSize();
			ensureCapacity(size + 1);
			elementDatas[size++] = e;
		}

		/**
		 * 存在元素
		 * @param e 元素值
		 * @return 是否存在
		 */
		public boolean contains(E e) {
			for (int index = 0; index < size; index++) {
				if (e.equals(elementDatas[index])) return true;
			}
			return false;
		}

		/**
		 * 移除值
		 * @param e 元素值
		 * @return 是否清空
		 */
		public boolean removeValue(E e) {
			boolean remove = false;
			for (int index = 0; index < size; index++) {
				if (e.equals(elementDatas[index])) {
					fastRemove(index);
					remove = true;
					break;
				}
			}
			if (size != 0) {
				sample = elementDatas[0];
			} else {
				removeNode(this);
			}
			return remove;
		}

		/**
		 * 移除值
		 * @param index 位置索引
		 * @return 元素值
		 */
		public E removeValue(int index) {
			if (index < 0 || index >= size) return null;
			E e = elementDatas[index];
			fastRemove(index);
			if (size != 0) {
				sample = elementDatas[0];
			} else {
				removeNode(this);
			}
			return e;
		}

		/**
		 * 数组扩容
		 * @param minCapacity 最小容器
		 */
		protected void ensureCapacity(int minCapacity) {
			synchronized (this) {
				int oldCapacity = elementDatas == null ? 0 : elementDatas.length;
				if (minCapacity > oldCapacity) {
					Object oldData[] = elementDatas;
					int newCapacity = (oldCapacity * 3) / 2 + 1;
					if (newCapacity < minCapacity) newCapacity = minCapacity;
					elementDatas = (E[]) new Object[newCapacity];
					if (oldData != null) System.arraycopy(oldData, 0, elementDatas, 0, size);
				}
			}
		}

		/**
		 * 快速移除
		 * @param index 位置索引
		 */
		private void fastRemove(int index) {
			decrementSize();
			int numMoved = size - index - 1;
			if (numMoved > 0) System.arraycopy(elementDatas, index + 1, elementDatas, index, numMoved);
			elementDatas[--size] = null; // 置空末位
		}

		/**
		 * 读取序列化对象
		 * @param s 对象读取器
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
			s.defaultReadObject();

			int length = s.readInt();
			Object[] a = elementDatas = (E[]) new Object[length];

			for (int i = 0; i < size; i++) {
				a[i] = s.readObject();
			}
			sample = elementDatas[0];
		}

		/**
		 * 写入序列化对象
		 * @param s 对象写入器
		 * @throws IOException
		 */
		private void writeObject(ObjectOutputStream s) throws IOException {
			s.defaultWriteObject();

			s.writeInt(elementDatas.length);

			for (int i = 0; i < size; i++) {
				s.writeObject(elementDatas[i]);
			}
		}
	}

	/**
	 * 树节点迭代器
	 * @author Demon 2010-10-8
	 */
	protected class TreeIterator implements Iterator<E> {

		private int expectedModCount = 0;
		private Node last;
		private Node next;

		private int index = 0;
		private int lastIndex = -1;

		/**
		 * 构造函数
		 */
		public TreeIterator() {
			expectedModCount = modCount;
			last = null;
			next = first;
		}

		public boolean hasNext() {
			return next != null;
		}

		public E next() {
			if (next == null) throw new NoSuchElementException();
			if (modCount != expectedModCount) throw new ConcurrentModificationException();
			lastIndex = index++;
			E e = next.elementDatas[lastIndex];
			if (index == next.size) {
				last = next;
				next = successor(next);
				index = 0;
			}
			return e;
		}

		public void remove() {
			if (lastIndex == -1) throw new IllegalStateException();
			if (modCount != expectedModCount) throw new ConcurrentModificationException();
			if (index != 0) {
				next.removeValue(--index);
				expectedModCount++;
			} else if (last != null) {
				if (last.size == 1) {
					if (last.left != null && last.right != null) {
						next = last;
						index = 0;
					}
					last.removeValue(lastIndex);
					last = null;
				} else {
					last.removeValue(lastIndex);
				}
				expectedModCount++;
			}
			lastIndex = -1;
		}
	}
}

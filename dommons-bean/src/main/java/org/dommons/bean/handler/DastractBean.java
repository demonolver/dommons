/*
 * @(#)DastractBean.java     2012-7-13
 */
package org.dommons.bean.handler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.dommons.bean.DommonBean;
import org.dommons.bean.DommonProperty;
import org.dommons.bean.transition.DommonTransition;
import org.dommons.core.Assertor;

/**
 * 抽象通用数据对象
 * @author Demon 2012-7-13
 */
public abstract class DastractBean<E> implements DommonBean<E> {

	static final Pattern numeric = Pattern.compile("[0-9]+|\\[[0-9]*\\]");

	/**
	 * 查找属性项集
	 * @param b 通用数据对象
	 * @param token 属性分词
	 * @param buf 字符缓存区
	 * @return 属性项集
	 */
	protected static List<DastractProperty> findProperties(DastractBean b, PropertyToken token, StringBuilder buf) {
		return b == null ? null : b.findProperties(token, buf);
	}

	/**
	 * 转换为属性名
	 * @param head 属性头
	 * @param token 属性分词
	 * @return 属性名
	 */
	protected static String name(String head, PropertyToken token) {
		String name = null;
		if (head != null && head.length() > 0) {
			name = new StringBuilder(head).append(token.part()).toString();
		} else {
			name = token.part();
		}
		return name;
	}

	protected E tar;

	protected final DommonTransition transition;
	protected final DommonBeanBuilder builder;
	protected final DastractBean parent;

	/**
	 * 构造函数
	 * @param tar 目标数据对象实体
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 */
	protected DastractBean(E tar, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent) {
		this.tar = tar;
		this.transition = transition;
		this.builder = builder;
		this.parent = parent;
	}

	public E entity() {
		return tar;
	}

	public Object get(String property) {
		return getProperty(property).get();
	}

	public <P> P get(String property, Class<P> type) throws ClassCastException {
		return getProperty(property).get(type);
	}

	public DommonProperty getProperty(String name) {
		Assertor.P.notEmpty(name, "The name of property must not be empty");
		StringBuilder buf = new StringBuilder(name.length());
		List<DastractProperty> properties = findProperties(PropertyToken.parse(name), buf);
		if (properties == null || properties.isEmpty() || !buf.toString().equals(name)) {
			throw new NoSuchElementException("Property '" + name + "' not exist");
		} else if (properties.size() == 1) {
			return properties.get(0);
		} else {
			DastractProperty p = properties.get(properties.size() - 1);
			if (p.parent.exists()) {
				return new NestificProperty(name, p);
			} else {
				int l = properties.size();
				String[] names = new String[l];
				for (int i = 0; i < l; i++) {
					names[i] = properties.get(i).getName();
				}
				return new RelaiveProperty(name, names);
			}
		}
	}

	public void set(String property, Object value) throws ClassCastException {
		getProperty(property).set(value);
	}

	/**
	 * 创建子数据对象
	 * @param property 属性项
	 * @param child 子属性名
	 * @return 子数据对象
	 */
	protected DastractBean child(DastractProperty property, String child) {
		Object v = property.get();
		Type type = property.readableType();
		if (v == null && Object.class.equals(type)) type = numeric.matcher(child).matches() ? Collection.class : Map.class;
		return builder.build(v, type, this);
	}

	/**
	 * 是否存在
	 * @return 是、否
	 */
	protected boolean exists() {
		return tar != null;
	}

	/**
	 * 查找属性项集
	 * @param t 属性分词
	 * @param buf 字符缓存区
	 * @return 属性项集
	 */
	protected List<DastractProperty> findProperties(PropertyToken t, StringBuilder buf) {
		int ob = buf.length();
		List<DastractProperty> list = new ArrayList();

		while (true) {
			list.clear();
			while (t.next()) {
				String h = t.appendSpacing(buf).substring(ob);
				buf.append(t.part());
				DastractProperty p = lookup(h, t);
				if (p != null) {
					list.add(p);
					break;
				}
			}

			if (t.next()) {
				DastractBean b = child(list.get(0), t.part());
				int l = buf.length();
				List<DastractProperty> ps = findProperties(b, t.child(t.index()), t.appendSpacing(buf));

				if (ps == null || ps.isEmpty() || !t.full().equals(buf.toString())) {
					buf.setLength(l);
					t.previous();
				} else {
					list.addAll(ps);
					break;
				}
			} else {
				break;
			}
		}

		return list;
	}

	/**
	 * 查找子属性项
	 * @param name 属性名
	 * @return 子属性项集
	 */
	protected abstract DastractProperty lookup(String name);

	/**
	 * 查找子属性项
	 * @param names 属性名集
	 * @return 子属性项集
	 */
	protected List<DastractProperty> lookup(String... names) {
		int len = names == null ? 0 : names.length;
		List<DastractProperty> list = new ArrayList(len);
		DastractBean b = this;
		DastractProperty p = null;
		for (int i = 0; i < len; i++) {
			if (p != null) b = builder.build(p.get(), p.readableType(), b);
			if (b == null) return null;

			p = b.lookup(names[i]);
			if (p == null) return null;
			list.add(p);
		}
		return list;
	}

	/**
	 * 查找属性项
	 * @param head 属性头
	 * @param token 属性分词
	 * @return 属性项
	 */
	protected DastractProperty lookup(String head, PropertyToken token) {
		return lookup(name(head, token));
	}

	/**
	 * 父数据对象
	 * @return 父数据对象
	 */
	protected DastractBean parent() {
		return parent;
	}

	/**
	 * 嵌套属性项
	 * @author Demon 2012-7-13
	 */
	protected final class NestificProperty extends DastractProperty<E> {

		protected DastractProperty tar;

		/**
		 * 构造函数
		 * @param name 属性名
		 * @param property 目标属性项
		 */
		protected NestificProperty(String name, DastractProperty property) {
			super(DastractBean.this.transition, DastractBean.this, name);
			this.tar = NestificProperty.class.isInstance(property) ? ((NestificProperty) property).tar : property;
		}

		public <T> T get(Class<T> type) throws ClassCastException {
			try {
				return super.get(type);
			} finally {
				relaive();
			}
		}

		public boolean readable() {
			return tar.readable();
		}

		public boolean writable() {
			return tar.writable();
		}

		protected Object getter() {
			try {
				return tar.get();
			} finally {
				relaive();
			}
		}

		protected Type readableType() {
			return tar.readableType();
		}

		/**
		 * 检查可承接属性项是否生成实体
		 */
		protected void relaive() {
			if (RelaiveProperty.class.isInstance(tar)) {
				DastractProperty r = ((RelaiveProperty) tar).getReally();
				if (r != null) tar = r;
			}
		}

		protected Object setter(Object value) {
			try {
				tar.set(value);
			} finally {
				relaive();
			}
			return value;
		}

		protected Type writableType() {
			return tar.writableType();
		}
	}

	/**
	 * 可承接属性项
	 * @author Demon 2012-7-13
	 */
	protected final class RelaiveProperty extends DastractProperty<E> {

		private final String[] relaives;

		private DastractProperty real;

		/**
		 * 构造函数
		 * @param name 属性名
		 * @param relaives 属性名集
		 */
		public RelaiveProperty(String name, String... relaives) {
			super(DastractBean.this.transition, DastractBean.this, name);
			this.relaives = relaives;
		}

		/**
		 * 获取实体属性项
		 * @return 属性项
		 */
		public DastractProperty getReally() {
			return real;
		}

		public boolean readable() {
			DommonProperty property = property();
			return property == null ? false : property.readable();
		}

		public void set(Object value) {
			$setter(value);
		}

		public boolean writable() {
			DommonProperty property = property();
			return property == null ? false : property.writable();
		}

		protected Object getter() {
			DommonProperty property = property();
			return property == null ? null : property.get();
		}

		protected Type readableType() {
			DastractProperty property = property();
			return property == null ? null : property.readableType();
		}

		protected Object setter(Object value) {
			return $setter(value) ? value : null;
		}

		protected Type writableType() {
			DastractProperty property = property();
			return property == null ? null : property.writableType();
		}

		/**
		 * 执行属性值设置
		 * @param value 属性值
		 * @return 是否设置成功
		 */
		private boolean $setter(Object value) {
			if (real == null) {
				List<DastractProperty> list = parent.lookup(relaives);
				int len = list == null ? 0 : list.size();
				int p = len;
				DastractProperty property = null;
				for (; p > 0; p--) {
					property = list.get(p - 1);
					if (property.parent.exists()) break;
				}

				if (p == 0 || property == null) {
					return false;
				} else if (p < len) {
					// 逐级构建属性值
					for (int i = p; i < len - 1; i++) {
						Type t = property.writableType();
						if (Object.class.equals(DommonTransition.type(t)))
							t = numeric.matcher(relaives[i]).matches() ? Collection.class : Map.class;
						Object v = property.setter(transition.create(t));
						DommonBean b = builder.bean(v);

						if (b == null || !(b instanceof DastractBean)) throw new UnsupportedOperationException();
						property = ((DastractBean) b).lookup(relaives[i]);
						if (property == null) return false;
					}
				}
				real = property;
			}
			real.set(value);
			return true;
		}

		/**
		 * 获取实体属性项
		 * @return 实体属性项
		 */
		private DastractProperty property() {
			if (real != null) return real;
			List<DastractProperty> list = parent.lookup(relaives);
			if (list == null || list.isEmpty()) {
				return null;
			} else {
				DastractProperty property = list.get(list.size() - 1);
				if (property.parent.exists()) real = property;
				return property;
			}
		}
	}
}

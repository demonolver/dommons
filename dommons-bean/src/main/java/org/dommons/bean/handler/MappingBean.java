/*
 * @(#)MappingBean.java     2012-7-16
 */
package org.dommons.bean.handler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.dommons.bean.transition.DommonTransition;

/**
 * 映射表数据对象
 * @author Demon 2012-7-16
 */
class MappingBean extends DastractBean<Map> {

	protected final Type ktype;
	protected final Type vtype;

	/**
	 * 构造函数
	 * @param map 目标映射表
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 */
	public MappingBean(Map map, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent) {
		this(map, transition, builder, parent, Object.class, Object.class);
	}

	/**
	 * 构造函数
	 * @param map 目标映射表
	 * @param transition 转换器
	 * @param builder 构建器
	 * @param parent 父数据对象
	 * @param kt 键类型
	 * @param vt 值类型
	 */
	public MappingBean(Map map, DommonTransition transition, DommonBeanBuilder builder, DastractBean parent, Type kt, Type vt) {
		super(map, transition, builder, parent);
		this.ktype = kt;
		this.vtype = vt;
	}

	protected List<DastractProperty> findProperties(PropertyToken t, StringBuilder buf) {
		int ob = buf.length();
		List<DastractProperty> list = new ArrayList();

		find: while (true) {
			list.clear();
			if (t.next()) {
				while (true) {
					String n = name(t.appendSpacing(buf).substring(ob), t);
					buf.append(t.part());
					if (tar != null && tar.containsKey(n)) {
						list.add(lookup(n));
						break;
					} else if (!t.next()) {
						list.add(lookup(t.full()));
						break find;
					}
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

	protected MappingProperty lookup(String name) {
		return new MappingProperty(name);
	}

	/**
	 * 映射表属性项
	 * @author Demon 2012-7-16
	 */
	class MappingProperty extends DastractProperty<Map> {

		/**
		 * 构造函数
		 * @param name 属性名
		 */
		protected MappingProperty(String name) {
			super(MappingBean.this.transition, MappingBean.this, name);
		}

		public boolean readable() {
			return true;
		}

		public boolean writable() {
			return true;
		}

		protected Object getter() {
			return tar == null ? null : tar.get(name);
		}

		protected Type readableType() {
			Object value = getter();
			return value == null ? vtype : value.getClass();
		}

		protected Object setter(Object value) {
			tar.put(name, value = transition.transform(value, vtype));
			return value;
		}

		protected Type writableType() {
			return vtype;
		}
	}
}

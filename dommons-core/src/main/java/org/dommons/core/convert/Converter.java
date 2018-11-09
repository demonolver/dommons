/*
 * @(#)Converter.java     2011-10-19
 */
package org.dommons.core.convert;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.dommons.core.Silewarner;
import org.dommons.core.convert.ConverterMap.ConverterProvider;
import org.dommons.core.convert.handlers.StringConverter;
import org.dommons.core.util.Arrayard;

/**
 * 数据转换器
 * @author Demon 2011-10-19
 */
public final class Converter {

	/** 强制转换 在类型不匹配或无法转换时，转换为目标类型默认值 */
	public static final Converter F = new Converter(true);

	/** 正常转换 在类型不匹配或无法转换时抛出转换异常 */
	public static final Converter P = new Converter(false);

	/**
	 * 异常抛出
	 * @param obj 待转换对象
	 * @param source 源类型
	 * @param target 目标类型
	 * @return 异常
	 */
	protected static ClassCastException castException(Object obj, Class source, Class target) {
		StringBuilder buffer = new StringBuilder("Can not cast ");
		if (source != null) {
			buffer.append(source).append(" [").append(StringConverter.toString(obj)).append(']');
		} else {
			buffer.append(obj);
		}
		buffer.append(" to ").append(target);
		ClassCastException e = new ClassCastException(buffer.toString());
		StackTraceElement[] elements = e.getStackTrace();
		int base = 2;
		StackTraceElement[] newElements = new StackTraceElement[elements.length - base];
		System.arraycopy(elements, base, newElements, 0, elements.length - base);
		e.setStackTrace(newElements);
		return e;
	}

	private final boolean force;

	/**
	 * 构造函数
	 * @param force 是否强制
	 */
	protected Converter(boolean force) {
		this.force = force;
	}

	/**
	 * 转换数据
	 * @param obj 数据对象
	 * @param cls 目标类型
	 * @return 结果对象
	 * @throws ClassCastException 无法转换
	 */
	public <T> T convert(Object obj, Class<T> cls) throws ClassCastException {
		return doConvert(obj, obj == null ? null : obj.getClass(), cls);
	}

	/**
	 * 转换数据
	 * @param obj 数据对象
	 * @param source 源数据类型
	 * @param target 目标类型
	 * @return 结果对象
	 * @throws ClassCastException 无法转换
	 */
	public <T, S> T convert(S obj, Class<? super S> source, Class<T> target) throws ClassCastException {
		return source == null || !source.isInstance(obj) ? convert(obj, target)
				: obj == null ? doConvert(obj, null, target) : doConvert(obj, source, target);
	}

	/**
	 * 执行数组转换
	 * @param obj 数组
	 * @param source 源类型
	 * @param target 目标类型
	 * @param levels 转换级别
	 * @return 结果
	 * @throws ClassCastException
	 */
	protected Object doArrayConvert(Object obj, Class source, Class target, int levels) throws ClassCastException {
		Iterator it = null;
		int size = 0;
		if (Collection.class.isAssignableFrom(source)) {
			Collection c = (Collection) obj;
			it = c.iterator();
			size = c.size();
			source = null;
		} else {
			size = Array.getLength(obj);
			source = source.getComponentType();
		}

		Class type = target.getComponentType();
		Object array = Array.newInstance(type, size);
		int o = 0;
		for (int i = 0; i < size; i++) {
			Object ele = it == null ? Array.get(obj, i) : it.next();
			Object r = doConvert(ele, source == null && ele != null ? ele.getClass() : source, type, levels + 1);
			if (r != null) {
				o++;
			} else if (!force && levels == 0) {
				throw castException(obj, source, target);
			} else {
				return null;
			}
			Arrayard.set(array, i, r);
		}

		return o > 0 ? array : null;
	}

	/**
	 * 执行数据转换
	 * @param obj 数据对象
	 * @param source 源数据类型
	 * @param target 目标类型
	 * @return 结果对象
	 * @throws ClassCastException
	 */
	protected <T> T doConvert(Object obj, Class source, Class<T> target) throws ClassCastException {
		return doConvert(obj, source, target, 0);
	}

	/**
	 * 执行数据转换
	 * @param obj 数据对象
	 * @param source 源数据类型
	 * @param target 目标类型
	 * @param levels 层次
	 * @return 结果对象
	 */
	<T> T doConvert(Object obj, Class source, Class<T> target, int levels) throws ClassCastException {
		boolean primitive = false;
		Class<T> t = target;

		if (target == null || (obj == null && !target.isPrimitive())) {
			return target.cast(obj);
		} else if (primitive = target.isPrimitive()) {
			target = Primitives.toClass(target);
		}
		if (target.isInstance(obj)) return target.cast(obj);

		if (source != null) {
			if ((source.isArray() || Collection.class.isAssignableFrom(source)) && target.isArray()) {
				try {
					return target.cast(doArrayConvert(obj, source, target, levels));
				} catch (ClassCastException e) {
				}
			} else {
				Class tar = target;
				if (target.isEnum()) tar = Enum.class;
				ConverterProvider<Object, T> provider = ConverterMap.getConverter(source, tar);
				if (provider != null) {
					ConvertHandler<Object, T> handler = null;
					while ((handler = provider.next()) != null) {
						T v = null;
						try {
							v = (T) handler.convert(obj, source, target);
						} catch (RuntimeException e) {
							Silewarner.warn(Converter.class, "Convert error", e);
						}
						if (v != null) return v;
					}
				}
			}
		}

		if (force) {
			return primitive ? Primitives.getNullValue(t) : null;
		} else if (levels > 0) {
			return null;
		} else {
			throw castException(obj, source, t);
		}
	}
}

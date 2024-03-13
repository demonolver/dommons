/*
 * @(#)Arrayard.java     2011-10-17
 */
package org.dommons.core.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dommons.core.convert.Converter;
import org.dommons.core.convert.handlers.StringConverter;

/**
 * 数组工具集
 * @author Demon 2011-10-17
 */
public final class Arrayard {

	/**
	 * 添加所有元素
	 * @param list 集合体
	 * @param array 数组
	 */
	public static void addAll(Collection list, Object array) {
		if (list == null) return;
		int len = length(array);
		for (int i = 0; i < len; i++)
			list.add(get(array, i));
	}

	/**
	 * 添加所有元素
	 * @param <E> 元素类型
	 * @param list 集合体
	 * @param array 数组
	 */
	public static <E, A extends E> void addAll(Collection<E> list, A... array) {
		if (list == null || array == null) return;
		for (E ele : array)
			list.add(ele);
	}

	/**
	 * 注入元素集
	 * @param list 集合体
	 * @param array 数组
	 * @return 集合体
	 */
	public static <E, A extends E, C extends Collection<E>> C afflux(C list, A... array) {
		addAll(list, array);
		return list;
	}

	/**
	 * 将数组转换为对象数组
	 * @param array 数组
	 * @return 对象数组
	 */
	public static Object[] asArray(Object array) {
		return asArray(array, Object.class);
	}

	/**
	 * 将数组转换为目标类型数组
	 * @param array 数组
	 * @param componentType 目标类型
	 * @return 新数组
	 */
	public static <T> T[] asArray(Object array, Class<T> componentType) {
		return (T[]) asArray(componentType, array);
	}

	/**
	 * 将布尔型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Boolean> asList(boolean... array) {
		return toList(array);
	}

	/**
	 * 将字节型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Byte> asList(byte... array) {
		return toList(array);
	}

	/**
	 * 将字节型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Character> asList(char... array) {
		return toList(array);
	}

	/**
	 * 将双精度型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Double> asList(double... array) {
		return toList(array);
	}

	/**
	 * 将浮点型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Float> asList(float... array) {
		return toList(array);
	}

	/**
	 * 将整型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Integer> asList(int... array) {
		return toList(array);
	}

	/**
	 * 将长整型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Long> asList(long... array) {
		return toList(array);
	}

	/**
	 * 将数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List asList(Object array) {
		if (array != null && !array.getClass().isArray()) array = new Object[] { array };
		return toList(array);
	}

	/**
	 * 将短整型数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static List<Short> asList(short... array) {
		return toList(array);
	}

	/**
	 * 将数组转换为列表
	 * @param array 数组
	 * @return 列表
	 */
	public static <T> List<T> asList(T... array) {
		return toList(array);
	}

	/**
	 * 将对象转换为数组
	 * @param obj 对象
	 * @param componentType 元素目标类型
	 * @return 数组
	 */
	public static <A> A castArray(Object obj, Class componentType) {
		if (componentType == null) componentType = Object.class;
		if (obj == null) {
			return null;
		} else if (Collection.class.isInstance(obj)) {
			return (A) toArray(componentType, Collection.class.cast(obj));
		} else if (obj.getClass().isArray()) {
			if (componentType.isAssignableFrom(obj.getClass().getComponentType())) return (A) obj;
			else return (A) asArray(componentType, obj);
		} else if (componentType.isPrimitive()) {
			return castArray(Collections.singleton(obj), componentType);
		} else {
			return (A) Arrayard.asArray(obj, componentType);
		}
	}

	/**
	 * 是否匹配
	 * @param obj 数值
	 * @param array 数组
	 * @return 是、否
	 */
	public static <O> boolean contains(Object obj, O... array) {
		return contains(obj, (Object) array);
	}

	/**
	 * 是否匹配
	 * @param obj 数值
	 * @param array 数组
	 * @return 是、否
	 */
	public static boolean contains(Object obj, Object array) {
		if (obj == null && array == null) return false;
		if (obj != null && obj.getClass().isArray() && (array == null || !array.getClass().isArray())) {
			Object o = obj;
			obj = array;
			array = o;
		}
		return indexOf(array, obj) >= 0;
	}

	/**
	 * 移除重复双精度数
	 * @param array 数组
	 * @return 新数组
	 */
	public static double[] deduplicat(double... array) {
		return toDouble(noduplicate(array));
	}

	/**
	 * 移除重复浮点数
	 * @param array 数组
	 * @return 新数组
	 */
	public static float[] deduplicat(float... array) {
		return toFloat(noduplicate(array));
	}

	/**
	 * 移除重复项
	 * @param array 数组
	 * @return 新数组
	 */
	public static <A> A[] deduplicate(A... array) {
		return deduplicate((Object) array);
	}

	/**
	 * 移除重复字节
	 * @param array 字节
	 * @return 新数组
	 */
	public static byte[] deduplicate(byte... array) {
		return toBytes(noduplicate(array));
	}

	/**
	 * 移除重复字符
	 * @param array 数组
	 * @return 新数组
	 */
	public static char[] deduplicate(char... array) {
		return toChars(noduplicate(array));
	}

	/**
	 * 移除重复整数
	 * @param array 数组
	 * @return 新数组
	 */
	public static int[] deduplicate(int... array) {
		return toInts(noduplicate(array));
	}

	/**
	 * 移除重复长整数
	 * @param array 数组
	 * @return 新数组
	 */
	public static long[] deduplicate(long... array) {
		return toLongs(noduplicate(array));
	}

	/**
	 * 移除重复项
	 * @param array 数组
	 * @return 新数组
	 */
	public static <A> A deduplicate(Object array) {
		if (array == null) return null;
		Collection set = noduplicate(array);
		return (A) set.toArray();
	}

	/**
	 * 移除重复短整数
	 * @param array 数组
	 * @return 新数组
	 */
	public static short[] deduplicate(short... array) {
		return toShorts(noduplicate(array));
	}

	/**
	 * 是否相等
	 * @param obj1 目标对象1
	 * @param obj2 目标对象2
	 * @return 是、否
	 */
	public static boolean equals(Object obj1, Object obj2) {
		if (obj1 == null) return obj2 == null;
		if (obj1.getClass().isArray()) {
			int len1 = Array.getLength(obj1);
			int len2 = length(obj2);
			if (len1 != len2) return false;
			else if (len1 > 0) return regionMatches(obj1, 0, obj2, 0, len1);
			else return obj1.getClass().equals(obj2.getClass());
		}
		return obj1.equals(obj2);
	}

	/**
	 * 获取元素项值 序号不存在返回 <code>null</code>
	 * @param list 数据集
	 * @param index 序号
	 * @return 元素项值
	 */
	public static <T> T get(Collection<T> list, int index) {
		return (T) get((Object) list, index);
	}

	/**
	 * 获取数组值 序号不存在返回 <code>null</code>
	 * @param array 数组
	 * @param index 序号
	 * @return 数组值
	 */
	public static <T> T get(Object array, int index) {
		int len = length(array);
		T v = null;
		if (len > index) {
			if (array instanceof List) {
				v = (T) ((List) array).get(index);
			} else if (array.getClass().isArray()) {
				v = (T) Array.get(array, index);
			} else {
				if (array instanceof Map) array = ((Map) array).entrySet();
				Iterator it = ((Collection) array).iterator();
				for (int i = 0; i < index && it.hasNext(); i++)
					it.next();
				v = (T) it.next();
			}
		}
		return v;
	}

	/**
	 * 获取数组值 序号不存在返回 <code>null</code>
	 * @param array 数组
	 * @param index 序号
	 * @param cls 值类型
	 * @return 数组值
	 */
	public static <T> T get(Object array, int index, Class<? extends T> cls) {
		return Converter.P.convert(get(array, index), cls);
	}

	/**
	 * 获取数组值 序号不存在返回 <code>null</code>
	 * @param array 数组
	 * @param index 序号
	 * @return 数组值
	 */
	public static <T> T get(T[] array, int index) {
		return (T) get((Object) array, index);
	}

	/**
	 * 查找目标对象在数组中的索引序号
	 * @param obj 值
	 * @param array 数组
	 * @return 索引号
	 */
	public static <O> int indexOf(Object obj, O... array) {
		return indexOf(array, obj, 0);
	}

	/**
	 * 查找目标对象在数组中的索引序号
	 * @param array 数组
	 * @param obj 值
	 * @return 索引号
	 */
	public static int indexOf(Object array, Object obj) {
		return indexOf(array, obj, 0);
	}

	/**
	 * 查找目标对象在数组中的索引序号
	 * @param array 数组
	 * @param obj 值
	 * @param start 起始位置
	 * @return 索引号
	 */
	public static int indexOf(Object array, Object obj, int start) {
		if (array == null && obj == null) return -1;
		if (array != null && !array.getClass().isArray() && obj != null && obj.getClass().isArray()) {
			Object a = array;
			array = obj;
			obj = a;
		}
		if (array == null || !array.getClass().isArray()) return -1;
		int len = Array.getLength(array);
		for (int i = start < 0 ? 0 : start; i < len; i++) {
			if (equals(get(array, i), obj)) return i;
		}
		return -1;
	}

	/**
	 * 是否数组
	 * @param obj 目标对象
	 * @return 是、否
	 */
	public static boolean isArray(Object obj) {
		return obj != null && obj.getClass().isArray();
	}

	/**
	 * 是否为空
	 * @param obj 目标对象
	 * @return 是、否
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) return true;
		if (obj instanceof Collection) return ((Collection) obj).isEmpty();
		else if (obj instanceof Map) return ((Map) obj).isEmpty();
		else if (obj.getClass().isArray()) return Array.getLength(obj) <= 0;
		else return true;
	}

	/**
	 * 反向查找目标对象在数组中的索引
	 * @param obj 目标对象
	 * @param array 数组
	 * @return 索引
	 */
	public static <O> int lastIndexOf(Object obj, O... array) {
		return lastIndexOf(array, obj);
	}

	/**
	 * 反向查找目标对象在数组中的索引
	 * @param array 数组
	 * @param obj 目标对象
	 * @return 索引
	 */
	public static int lastIndexOf(Object array, Object obj) {
		return lastIndexOf(array, obj, -1);
	}

	/**
	 * 从指定索引位置开始反向查找目标对象在数组中的索引
	 * @param array 数组
	 * @param obj 目标对象
	 * @param start 起始位置
	 * @return 索引
	 */
	public static int lastIndexOf(Object array, Object obj, int start) {
		if (array == null || !array.getClass().isArray()) return -1;
		int len = Array.getLength(array);
		int s = start >= 0 ? Math.min(start, len - 1) : len - 1;
		for (int i = s; i >= 0; i--) {
			if (equals(get(array, i), obj)) return i;
		}
		return -1;
	}

	/**
	 * 获取数组长度
	 * @param array 数组
	 * @return 长度
	 */
	public static int length(Object array) {
		if (array != null) {
			if (array instanceof Collection) return ((Collection) array).size();
			else if (array.getClass().isArray()) return Array.getLength(array);
			else if (array instanceof Map) return ((Map) array).size();
		}
		return 0;
	}

	/**
	 * 获取数组中首个非空值
	 * @param array 数组
	 * @return 值
	 */
	public static Object one(Object... array) {
		return one((Object) array);
	}

	/**
	 * 获取数组集合中首个非空值
	 * @param array 数组
	 * @return 值
	 */
	public static Object one(Object array) {
		int len = length(array);
		for (int i = 0; i < len; i++) {
			Object o = get(array, i);
			if (o != null) return o;
		}
		return null;
	}

	/**
	 * 是否范围内匹配
	 * @param array1 数组1
	 * @param start1 起始位1
	 * @param array2 数组2
	 * @param start2 起始位2
	 * @param len 范围长度
	 * @return 是否匹配
	 */
	public static boolean regionMatches(Object array1, int start1, Object array2, int start2, int len) {
		if (array1 == null || array2 == null) return false;
		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);
		if (start1 < 0) {
			start1 = 0;
		} else if (start1 >= len1) {
			return false;
		}
		if (start2 < 0) {
			start2 = 0;
		} else if (start2 >= len2) {
			return false;
		}

		int l = start1 + Math.min(Math.min(len, len2 - start2), len1 - start1);
		int d = start2 - start1;
		for (int i = start1; i < l; i++) {
			if (!equals(get(array1, i), get(array2, i + d))) return false;
		}
		return true;
	}

	/**
	 * 移除所有元素
	 * @param <E> 元素类型
	 * @param list 集合体
	 * @param array 数组
	 */
	public static <E, A extends E> void removeAll(Collection list, A... array) {
		if (list == null || array == null) return;
		for (E ele : array)
			list.remove(ele);
	}

	/**
	 * 移除所有元素
	 * @param list 集合体
	 * @param array 数组
	 */
	public static void removeAll(Collection list, Object array) {
		if (list == null) return;
		int len = length(array);
		for (int i = 0; i < len; i++)
			list.remove(get(array, i));
	}

	/**
	 * 设置数组值
	 * @param array 数组
	 * @param index 索引
	 * @param value 值
	 * @see #get(Object, int)
	 * @see Array#get(Object, int)
	 */
	public static void set(Object array, int index, Object value) {
		if (array == null || !array.getClass().isArray()) return;
		Class type = array.getClass().getComponentType();
		set(array, index, value, type);
	}

	/**
	 * 获取数组中首个非空值
	 * @param array 数组
	 * @return 值
	 */
	public static <T> T theOne(T... array) {
		return (T) one(array);
	}

	/**
	 * 集合转数组
	 * @param list 数据集合
	 * @param componentType 数组元素类型
	 * @return 数组
	 */
	public static <T> T[] toArray(Collection list, Class<T> componentType) {
		return (T[]) toArray(componentType, list);
	}

	/**
	 * 集合转布尔型数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static boolean[] toBoolean(Collection<Boolean> list) {
		return (boolean[]) toArray(boolean.class, list);
	}

	/**
	 * 集合转字节数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static byte[] toBytes(Collection<? extends Number> list) {
		return (byte[]) toArray(byte.class, list);
	}

	/**
	 * 集合转字符数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static char[] toChars(Collection<Character> list) {
		return (char[]) toArray(char.class, list);
	}

	/**
	 * 集合转双精度数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static double[] toDouble(Collection<? extends Number> list) {
		return (double[]) toArray(double.class, list);
	}

	/**
	 * 集合转浮点型数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static float[] toFloat(Collection<? extends Number> list) {
		return (float[]) toArray(float.class, list);
	}

	/**
	 * 集合转整型数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static int[] toInts(Collection<? extends Number> list) {
		return (int[]) toArray(int.class, list);
	}

	/**
	 * 集合转长整型数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static long[] toLongs(Collection<? extends Number> list) {
		return (long[]) toArray(long.class, list);
	}

	/**
	 * 集合转短整型数组
	 * @param list 数据集合
	 * @return 数组
	 */
	public static short[] toShorts(Collection<? extends Number> list) {
		return (short[]) toArray(short.class, list);
	}

	/**
	 * 转换为字符串
	 * @param obj 对象值
	 * @return 字符串
	 */
	public static String toString(Object obj) {
		if (obj == null) return "null";
		return importBuffer(obj, new StringBuilder()).toString();
	}

	/**
	 * 转换数组
	 * @param componentType 目标子元素类型
	 * @param array 数组
	 * @return 结果数组
	 */
	protected static Object asArray(Class componentType, Object array) {
		if (array == null) return null;
		if (!array.getClass().isArray()) array = new Object[] { array };
		if (componentType == null) componentType = Object.class;
		int len = length(array);
		Object ta = Array.newInstance(componentType, len);
		for (int i = 0; i < len; i++) {
			Object t = Converter.P.convert(get(array, i), componentType);
			set(ta, i, t, componentType);
		}
		return ta;
	}

	/**
	 * 导入内容
	 * @param obj 内容对象
	 * @param buffer 字符缓冲区
	 * @return 字符缓冲区
	 */
	protected static StringBuilder importBuffer(Object obj, StringBuilder buffer) {
		if (obj == null) {
			buffer.append("null");
		} else if (obj.getClass().isArray()) {
			buffer.append('[');
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				if (i > 0) buffer.append(", ");
				Object v = get(obj, i);
				importBuffer(v, buffer);
			}
			buffer.append(']');
		} else if (obj instanceof Collection) {
			importBuffer((Collection) obj, buffer);
		} else if (obj instanceof Map) {
			importBuffer((Map) obj, buffer);
		} else {
			buffer.append(StringConverter.toString(obj));
		}

		return buffer;
	}

	/**
	 * 生成无重复集合
	 * @param array 数组
	 * @return 集合
	 */
	protected static Collection noduplicate(Object array) {
		if (array == null) return null;
		Collection set = new LinkedHashSet();
		addAll(set, array);
		set.remove(null);
		return set;
	}

	/**
	 * 设置数组值
	 * @param array 数组
	 * @param index 序号
	 * @param value 值
	 * @param type 元素类型
	 */
	protected static void set(Object array, int index, Object value, Class type) {
		if (int.class.equals(type)) {
			Array.setInt(array, index, Converter.P.convert(value, int.class).intValue());
		} else if (short.class.equals(type)) {
			Array.setShort(array, index, Converter.P.convert(value, short.class).shortValue());
		} else if (long.class.equals(type)) {
			Array.setLong(array, index, Converter.P.convert(value, long.class).longValue());
		} else if (byte.class.equals(type)) {
			Array.setByte(array, index, Converter.P.convert(value, byte.class).byteValue());
		} else if (boolean.class.equals(type)) {
			Array.setBoolean(array, index, Converter.P.convert(value, boolean.class).booleanValue());
		} else if (double.class.equals(type)) {
			Array.setDouble(array, index, Converter.P.convert(value, double.class).doubleValue());
		} else if (float.class.equals(type)) {
			Array.setFloat(array, index, Converter.P.convert(value, float.class).floatValue());
		} else if (char.class.equals(type)) {
			Array.setChar(array, index, Converter.P.convert(value, char.class).charValue());
		} else {
			Array.set(array, index, value);
		}
	}

	/**
	 * 集合转数组
	 * @param componentType 数组元素类型
	 * @param list 数据集合
	 * @return 数组
	 */
	protected static Object toArray(Class componentType, Collection list) {
		if (list == null) return null;
		if (componentType == null) return list.toArray();
		Object array = Array.newInstance(componentType, list.size());
		int index = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			Object item = Converter.P.convert(it.next(), componentType);
			set(array, index++, item, componentType);
		}
		return array;
	}

	/**
	 * 转换数组为列表
	 * @param array 数组
	 * @return 列表
	 */
	protected static List toList(Object array) {
		return array == null || !array.getClass().isArray() ? null : new ArrayList(array);
	}

	/**
	 * 导入数据集
	 * @param list 数据集
	 * @param buf 字符缓冲区
	 */
	static void importBuffer(Collection list, StringBuilder buf) {
		int i = 0;
		buf.append('[');
		for (Iterator it = list.iterator(); it.hasNext(); i++) {
			if (i > 0) buf.append(", ");
			importBuffer(it.next(), buf);
		}
		buf.append(']');
	}

	/**
	 * 导入映射表
	 * @param map 映射表
	 * @param buf 字符缓冲区
	 */
	static void importBuffer(Map map, StringBuilder buf) {
		int i = 0;
		buf.append('{');
		for (Iterator<Entry> it = map.entrySet().iterator(); it.hasNext(); i++) {
			if (i > 0) buf.append(", ");
			Entry en = it.next();
			importBuffer(en.getKey(), buf);
			buf.append('=');
			importBuffer(en.getValue(), buf);
		}
		buf.append('}');
	}

	protected Arrayard() {}

	/**
	 * 数组列表
	 * @author Demon 2012-4-16
	 */
	static class ArrayList extends AbstractList implements Serializable {

		private static final long serialVersionUID = 951481999952039339L;

		private final Object array;

		public ArrayList(Object array) {
			this.array = array;
		}

		public Object get(int index) {
			return Array.get(array, index);
		}

		public Object set(int index, Object element) {
			Object old = Array.get(array, index);
			Arrayard.set(array, index, element);
			return old;
		}

		public int size() {
			return Array.getLength(array);
		}

		public Object[] toArray() {
			return array instanceof Object[] ? ((Object[]) array).clone() : super.toArray();
		}
	}
}

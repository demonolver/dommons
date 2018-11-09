/*
 * @(#)Assertor.java     2011-10-17
 */
package org.dommons.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.dommons.core.number.Numeric;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;

/**
 * 数据校验类 源于org.springframework.util.Assert类的思想，实现junit.framework.Assert类的功能
 * @author Demon 2011-10-17
 */
public final class Assertor {

	/** 强制数据校验类实例 校验不通过直接报错 */
	public static final Assertor F = new Assertor(true);

	/** 平和的数据校验类实例 只返回校验结果，不报错 */
	public static final Assertor P = new Assertor(false);

	/** 是否强制报错 */
	private final boolean force;

	/**
	 * 构造函数
	 * @param force 是否强制
	 */
	private Assertor(boolean force) {
		this.force = force;
	}

	/**
	 * 检验数值是否在范围中
	 * @param d 数值
	 * @param s 开始范围
	 * @param e 截止范围
	 * @return 校验是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean between(double d, double s, double e) throws IllegalArgumentException {
		return between(d, s, e,
			"The number '" + Numeric.toString(d) + "' is not between '" + Numeric.toString(s) + "' and '" + Numeric.toString(e) + "'!");
	}

	/**
	 * 检验数值是否在范围中
	 * @param d 数值
	 * @param s 开始范围
	 * @param e 截止范围
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean between(double d, double s, double e, String msg) throws IllegalArgumentException {
		return assertFalse(Numeric.between(d, s, e), msg);
	}

	/**
	 * 检验数值是否在范围中
	 * @param n 数值
	 * @param s 开始范围
	 * @param e 截止范围
	 * @return 校验是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean between(Number n, Number s, Number e) throws IllegalArgumentException {
		return between(n, s, e,
			"The number '" + Numeric.toString(n) + "' is not between '" + Numeric.toString(s) + "' and '" + Numeric.toString(e) + "'!");
	}

	/**
	 * 检验数值是否在范围中
	 * @param n 数值
	 * @param s 开始范围
	 * @param e 截止范围
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean between(Number n, Number s, Number e, String msg) throws IllegalArgumentException {
		return assertFalse(Numeric.between(n, s, e), msg);
	}

	/**
	 * 检查对象为空
	 * @param obj 目标对象
	 * @return 是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean empty(Object obj) throws IllegalArgumentException {
		return empty(obj, "This object must be empty");
	}

	/**
	 * 检查对象为空
	 * @param obj 目标对象
	 * @param msg 失败消息
	 * @return 是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean empty(Object obj, String msg) throws IllegalArgumentException {
		if (obj == null) {
			return isNull(obj, msg);
		} else if (obj instanceof CharSequence) {
			return assertFalse(Stringure.isEmpty((CharSequence) obj), msg);
		} else if (obj instanceof Collection) {
			return assertFalse(((Collection) obj).isEmpty(), msg);
		} else if (obj instanceof Map) {
			return assertFalse(((Map) obj).isEmpty(), msg);
		} else if (obj.getClass().isArray()) {
			return assertFalse(Arrayard.length(obj) == 0, msg);
		}
		return true;
	}

	/**
	 * 检验两对象是否相等
	 * @param o1 对象1
	 * @param o2 对象2
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean equals(Object o1, Object o2) throws IllegalArgumentException {
		return equals(o1, o2, "The object '" + o1 + "' must be equals to '" + o2 + "'!");
	}

	/**
	 * 检验两对象是否相等
	 * @param o1 对象1
	 * @param o2 对象2
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean equals(Object o1, Object o2, String msg) throws IllegalArgumentException {
		return assertFalse(Arrayard.equals(o1, o2), msg);
	}

	/**
	 * 检验两字符串是否相等
	 * @param s1 字符串1
	 * @param s2 字符串2
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean equalsIgnoreCase(String s1, String s2) throws IllegalArgumentException {
		return equalsIgnoreCase(s1, s2, "The argument of string must be equals to the other one!");
	}

	/**
	 * 检验两字符串
	 * @param s1 字符串1
	 * @param s2 字符串2
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean equalsIgnoreCase(String s1, String s2, String msg) throws IllegalArgumentException {
		return assertFalse((s1 == null && s2 == null) || (s1 != null && s1.equalsIgnoreCase(s2)), msg);
	}

	/**
	 * 检验class为另一个class的超类或超接口
	 * @param superType 超类
	 * @param subType 子类
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isAssignable(Class superType, Class subType) throws IllegalArgumentException {
		return isAssignable(superType, subType, "");
	}

	/**
	 * 检验class为另一个class的超类或超接口
	 * @param superType 超类
	 * @param subType 子类
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isAssignable(Class superType, Class subType, String msg) throws IllegalArgumentException {
		// 检查超类是否为空
		return notNull(superType, "Type to check against must not be null")
				? (assertFalse(subType != null && superType.isAssignableFrom(subType),
					(msg == null || msg.length() == 0 ? "" : (msg + " ")) + subType + " is not assignable to " + superType))
				: false;

	}

	/**
	 * 检验对象为指定class的实例
	 * @param clazz 指定类
	 * @param obj 待检验对象
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isInstanceOf(Class clazz, Object obj) throws IllegalArgumentException {
		return isInstanceOf(clazz, obj, "");
	}

	/**
	 * 检验对象为指定class的实例
	 * @param clazz 指定类
	 * @param obj 待检验对象
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isInstanceOf(Class clazz, Object obj, String msg) throws IllegalArgumentException {
		return notNull(clazz, "Type to check against must not be null")
				? assertFalse(clazz.isInstance(obj), (msg == null || msg.length() == 0 ? "" : (msg + " ")) + "Object of class ["
						+ (obj != null ? obj.getClass().getName() : null) + "] must be an instance of " + clazz)
				: false;
	}

	/**
	 * 检验对象为空
	 * @param obj 待检验对象
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isNull(Object obj) throws IllegalArgumentException {
		return isNull(obj, "The object argument must be null");
	}

	/**
	 * 检验对象为空
	 * @param obj 待检验对象
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isNull(Object obj, String msg) throws IllegalArgumentException {
		return assertFalse(obj == null, msg);
	}

	/**
	 * 检验条件为真
	 * @param expression 条件
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean isTrue(boolean expression, String msg) throws IllegalArgumentException {
		return assertFalse(expression, msg);
	}

	/**
	 * 检验字符串符合表达式
	 * @param str 待检验字符串
	 * @param expr 表达式
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean matches(String str, String expr) throws IllegalArgumentException {
		return matches(str, expr, "This string " + (str == null ? null : "'" + str + "'") + " is not matches the expression "
				+ (expr == null ? null : "'" + expr + "'"));
	}

	/**
	 * 检验字符串符合表达式
	 * @param str 待检验字符串
	 * @param expr 表达式
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean matches(String str, String expr, String msg) throws IllegalArgumentException {
		if (str != null && expr != null) {
			return assertFalse(str.matches(expr), msg);
		} else {
			return assertFalse(str == null && expr == null, msg);
		}
	}

	/**
	 * 检查对象不为空
	 * @param obj 目标对象
	 * @return 是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean notEmpty(Object obj) throws IllegalArgumentException {
		return notEmpty(obj, "This object must not be empty");
	}

	/**
	 * 检查对象不为空
	 * @param obj 目标对象
	 * @param msg 失败消息
	 * @return 是否通过
	 * @throws IllegalArgumentException
	 */
	public boolean notEmpty(Object obj, String msg) throws IllegalArgumentException {
		if (obj == null) {
			return notNull(obj, msg);
		} else if (obj instanceof CharSequence) {
			return assertFalse(!Stringure.isEmpty((CharSequence) obj), msg);
		} else if (obj instanceof Collection) {
			return assertFalse(!((Collection) obj).isEmpty(), msg);
		} else if (obj instanceof Map) {
			return assertFalse(!((Map) obj).isEmpty(), msg);
		} else if (obj.getClass().isArray()) {
			return assertFalse(Arrayard.length(obj) > 0, msg);
		}
		return true;
	}

	/**
	 * 检验两对象是否不相等
	 * @param o1 对象1
	 * @param o2 对象2
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notEquals(Object o1, Object o2) throws IllegalArgumentException {
		return notEquals(o1, o2, "The object '" + o1 + "' must not be equals to '" + o2 + "'!");
	}

	/**
	 * 检验两对象是否不相等
	 * @param o1 对象1
	 * @param o2 对象2
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notEquals(Object o1, Object o2, String msg) throws IllegalArgumentException {
		return assertFalse((o1 == null && o2 != null) || (o1 != null && (o2 == null || !o1.equals(o2))), msg);
	}

	/**
	 * 检验两字符串是否不相等
	 * @param s1 字符串1
	 * @param s2 字符串2
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notEqualsIgnoreCase(String s1, String s2) throws IllegalArgumentException {
		return notEqualsIgnoreCase(s1, s2, "The string '" + s1 + "' must not be equals to '" + s2 + "'!");
	}

	/**
	 * 检验两字符串是否不相等
	 * @param s1 字符串1
	 * @param s2 字符串2
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notEqualsIgnoreCase(String s1, String s2, String msg) throws IllegalArgumentException {
		return assertFalse((s1 == null && s2 != null) || (s1 != null && (s2 == null || !s1.equalsIgnoreCase(s2))), msg);
	}

	/**
	 * 检验对象不为空
	 * @param obj 待检验对象
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notNull(Object obj) throws IllegalArgumentException {
		return notNull(obj, "This argument is required, it must not be null");
	}

	/**
	 * 检验对象不为空
	 * @param obj 待检验对象
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	public boolean notNull(Object obj, String msg) throws IllegalArgumentException {
		return assertFalse(obj != null, msg);
	}

	/**
	 * 检验条件是否满足
	 * @param expression 条件
	 * @param msg 失败消息
	 * @return 校验是否通过
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	private boolean assertFalse(boolean expression, String msg) throws IllegalArgumentException {
		return expression ? true : fail(msg);
	}

	/**
	 * 检验失败处理方法
	 * @param msg 发出的失败消息
	 * @return <code>false</code>
	 * @throws IllegalArgumentException 强制校验未通过报错
	 */
	private boolean fail(String msg) throws IllegalArgumentException {
		if (!force) return false;
		IllegalArgumentException e = new IllegalArgumentException(msg);
		List<StackTraceElement> list = new ArrayList<StackTraceElement>();
		for (StackTraceElement element : e.getStackTrace()) {
			if (!element.getClassName().equals(Assertor.class.getName())) {
				list.add(element);
			}
		}
		e.setStackTrace(list.toArray(new StackTraceElement[list.size()]));
		throw e;
	}
}

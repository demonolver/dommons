/*
 * @(#)Numeric.java     2011-10-19
 */
package org.dommons.core.number;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.dommons.core.convert.Converter;

/**
 * 数学记录值 针对普通数字计算时精度会丢失，而使用 {@link BigDecimal} 在处理 10.50 和 10.5 是做为不同的两个数值来处理，特别封装 {@link Numeric} 类处理数字的运算
 * @author Demon 2011-10-19
 */
public class Numeric extends Number implements Serializable, Comparable<Numeric> {

	private static final long serialVersionUID = -6147568038624301791L;

	/** 零 */
	public final static Numeric zero = numeric(BigDecimal.ZERO);
	/** 壹 */
	public final static Numeric one = numeric(BigDecimal.ONE);
	/** 拾 */
	public final static Numeric ten = numeric(BigDecimal.TEN);

	/** 缓存 */
	final static Numeric[] cache = { zero, one };
	final static int d_scale = 12;

	/**
	 * 取正数
	 * @param num 数值
	 * @return 正数
	 */
	public static <N extends Number> N abs(N num) {
		if (num == null) return null;
		else if (ge(num, zero)) return num;
		else if (num instanceof Numeric) return (N) ((Numeric) num).negate();
		else if (num instanceof BigDecimal) return (N) ((BigDecimal) num).negate();
		else if (num instanceof BigInteger) return (N) ((BigInteger) num).negate();
		else return (N) Converter.P.convert(Math.abs(num.doubleValue()), num.getClass());
	}

	/**
	 * 均分数量
	 * @param total 总数量
	 * @param limit 批限制
	 * @return 数量
	 */
	public static int average(int total, int limit) {
		return (int) average((long) total, (long) limit);
	}

	/**
	 * 均分数量
	 * @param total 总数量
	 * @param limit 批限制
	 * @return 数量
	 */
	public static long average(long total, long limit) {
		long x = limit;
		if (total > 0) {
			long t = (total + x - 1) / x;
			x = (total + t - 1) / t;
		}
		return x;
	}

	/**
	 * 数值是否在范围中
	 * @param d 数值
	 * @param s 开始范围
	 * @param e 截止范围
	 * @return 是、否
	 */
	public static boolean between(double d, double s, double e) {
		return d >= Math.min(s, e) && d <= Math.max(s, e);
	}

	/**
	 * 数值是否在范围中
	 * @param num 数值
	 * @param start 开始范围
	 * @param end 截止范围
	 * @return 是、否
	 */
	public static boolean between(Number num, Number start, Number end) {
		return (start != null || end != null) && (start == null || ge(num, min(start, end))) && (end == null || le(num, max(start, end)));
	}

	/**
	 * 获取范围内值
	 * @param <N> 数值类型
	 * @param num 数值
	 * @param min 最小值
	 * @param max 最大值
	 * @return 结果数值
	 */
	public static <N extends Number> N betweenValue(N num, N min, N max) {
		if (num == null) return null;
		else if (Numeric.less(num, Numeric.minimum(min, max))) return min;
		else if (Numeric.greater(num, Numeric.maximum(min, max))) return max;
		else return num;
	}

	/**
	 * 数值是否相等 (仅针对数值大小，不区分数值类型)
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200'/>
	 * <th id='e' width='12'/>
	 * <th id='r' width='120'/>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'>Numeric.equals(Double.valueOf(1.0), Integer.valueOf(1))</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>true</td>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'>Numeric.equals(Double.valueOf(1.0), Float.valueOf(1.1))</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false</td>
	 * </tr>
	 * </table>
	 * @param num1 数值1
	 * @param num2 数值2
	 * @return 是、否
	 */
	public static boolean equals(Number num1, Number num2) {
		return compare(num1, num2, 1);
	}

	/**
	 * 数值是否大于等于
	 * @param num1 数值1
	 * @param num2 数值2
	 * @return 是、否
	 */
	public static boolean ge(Number num1, Number num2) {
		return compare(num1, num2, 1 | 2);
	}

	/**
	 * 数值是否大于
	 * @param num1 数值1
	 * @param num2 数值2
	 * @return 是、否
	 */
	public static boolean greater(Number num1, Number num2) {
		return compare(num1, num2, 2);
	}

	/**
	 * 数值是否小于等于
	 * @param num1 数值1
	 * @param num2 数值2
	 * @return 是、否
	 */
	public static boolean le(Number num1, Number num2) {
		return compare(num1, num2, 1 | 4);
	}

	/**
	 * 数值是否小于
	 * @param num1 数值1
	 * @param num2 数值2
	 * @return 是、否
	 */
	public static boolean less(Number num1, Number num2) {
		return compare(num1, num2, 4);
	}

	/**
	 * 计算乘积
	 * @param ds 数值集
	 * @return 数值
	 */
	public static double m(double... ds) {
		if (ds == null || ds.length == 0) return 0;
		Numeric n = one;
		for (double d : ds) {
			if (d == 0) return 0;
			n = n.multiply(d);
		}
		return n.round(d_scale);
	}

	/**
	 * 取最大值
	 * @param nums 数值集
	 * @return 最大值
	 */
	public static Number max(Number... nums) {
		if (nums == null) return null;
		Number max = null;
		double m = 0;
		for (Number n : nums) {
			if (n == null) continue;
			double d = n.doubleValue();
			if (Double.isNaN(d)) return n;
			if (max == null || d > m) {
				max = n;
				m = d;
			}
		}
		return max;
	}

	/**
	 * 取最大值
	 * @param nums 数值集
	 * @return 最大值
	 */
	public static <N extends Number> N maximum(N... nums) {
		return (N) max(nums);
	}

	/**
	 * 取最小值
	 * @param nums 数值集
	 * @return 最小值
	 */
	public static Number min(Number... nums) {
		if (nums == null) return null;
		Number min = null;
		double m = 0;
		for (Number n : nums) {
			if (n == null) continue;
			double d = n.doubleValue();
			if (Double.isNaN(d)) continue;
			if (min == null || d < m) {
				min = n;
				m = d;
			}
		}
		return min;
	}

	/**
	 * 取最小值
	 * @param nums 数值集
	 * @return 最小值
	 */
	public static <N extends Number> N minimum(N... nums) {
		return (N) min(nums);
	}

	/**
	 * 取负数
	 * @param d 数值
	 * @return 负值
	 */
	public static double negate(double d) {
		return Numeric.valueOf(d).negate().round(d_scale);
	}

	/**
	 * 计算乘积
	 * @param ns 数值集
	 * @return 数值
	 */
	public static Number product(Number... ns) {
		if (ns == null || ns.length == 0) return null;
		Numeric x = one;
		for (Number n : ns) {
			if (equals(zero, n)) return zero;
			x = x.multiply(n);
		}
		return valueOf(x.dec);
	}

	/**
	 * 计算总和
	 * @param ds 数值集
	 * @return 数值
	 */
	public static double s(double... ds) {
		if (ds == null || ds.length == 0) return 0;
		Numeric n = zero;
		for (double d : ds)
			n = n.add(d);
		return n.round(d_scale);
	}

	/**
	 * 计算总和
	 * @param ns 数值集
	 * @return 数值
	 */
	public static Number sum(Number... ns) {
		if (ns == null || ns.length == 0) return null;
		Numeric x = zero;
		for (Number n : ns)
			x = x.add(n);
		return valueOf(x.dec);
	}

	/**
	 * 转换为字符串
	 * @param val 双精度值
	 * @return 字符串
	 */
	public static String toString(double val) {
		String s = String.valueOf(val);
		if (s.toUpperCase().indexOf('E') < 0) return s;
		return valueOf(val).toString();
	}

	/**
	 * 转换为字符串
	 * @param n 数字值
	 * @return 字符串
	 */
	public static String toString(Number n) {
		if (n == null) return null;
		String s = String.valueOf(n.doubleValue());
		if (s.toUpperCase().indexOf('E') < 0) return s;
		BigDecimal dec = (n instanceof BigDecimal) ? (BigDecimal) n : valueOf(n).dec;
		return dec.equals(BigDecimal.ZERO) ? "0" : dec.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
	}

	/**
	 * 双精度值对应数学值
	 * @param val 双精度值
	 * @return 数学值
	 */
	public static Numeric valueOf(double val) {
		if (val == 0) return zero;
		Numeric n = numeric(val);
		return n != null ? n : numeric(decimal(val));
	}

	/**
	 * 数字类型对应数学值
	 * @param num 数字类型
	 * @return 数学值
	 */
	public static Numeric valueOf(Number num) {
		if (num == null) throw new NullPointerException();
		if (num instanceof Numeric) {
			return (Numeric) num;
		} else if (num instanceof BigDecimal) {
			return numeric((BigDecimal) num);
		} else {
			Numeric n = numeric(num.doubleValue());
			if (n != null) return n;
			else if (!(num instanceof BigDecimal)) num = decimal(num.doubleValue());
			return numeric((BigDecimal) num);
		}
	}

	/**
	 * 数字字符串对应数学值
	 * @param val 数字字符串
	 * @return 数学值
	 */
	public static Numeric valueOf(String val) {
		if (val == null) throw new NullPointerException();
		return valueOf(Converter.P.convert(val, BigDecimal.class));
	}

	/**
	 * 数值比较
	 * @param num1 数值1
	 * @param num2 数值2
	 * @param type 比较类型
	 * @return 是否满足
	 */
	protected static boolean compare(Number num1, Number num2, int type) {
		if (num1 == null) return type == 1 && num2 == null;
		if (num2 == null) return type != 1;
		double d1 = d(num1), d2 = d(num2);
		switch (type) {
		case 1:
			return d1 == d2;
		case 2:
			return Double.isNaN(d1) || d1 > d2;
		case 3:
			return Double.isNaN(d1) || d1 >= d2;
		case 4:
			return Double.isNaN(d2) || d1 < d2;
		case 5:
			return Double.isNaN(d2) || d1 <= d2;
		default:
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * 转换为双精度型
	 * @param num 数字
	 * @return 双精度型
	 */
	protected static Double d(Number num) {
		if (num == null) return null;
		if (num instanceof Numeric) {
			num = ((Numeric) num).dec;
		} else if (num instanceof Double || num instanceof Float) {
			double d = num.doubleValue();
			if (!Double.isNaN(d) && !Double.isInfinite(d)) num = Numeric.valueOf(num).dec;
		}
		if (num instanceof BigDecimal) return r((BigDecimal) num, d_scale).doubleValue();
		else return num.doubleValue();
	}

	/**
	 * 创建数字对象
	 * @param d 数字
	 * @return 数字对象
	 */
	static Numeric numeric(BigDecimal d) {
		return new Numeric(r(d, d_scale));
	}

	/**
	 * 检索数字对象
	 * @param v 数字值
	 * @return 数字对象
	 */
	static Numeric numeric(double v) {
		for (Numeric n : cache)
			if (d(n) == v) return n;
		return null;
	}

	/**
	 * 四舍五入
	 * @param d 数值
	 * @param s 保留小数后位数
	 * @return 数字值
	 */
	static BigDecimal r(BigDecimal d, int s) {
		return r(d, s, null);
	}

	/**
	 * 保留小数位
	 * @param d 数值
	 * @param s 保留后小数位
	 * @param up 是否进位 <code>true</code>:进位，<code>false</code>:降位，<code>null</code>:四舍五入
	 * @return 数字值
	 */
	static BigDecimal r(BigDecimal d, int s, Boolean up) {
		if (d.scale() <= s) return d;
		int rm = BigDecimal.ROUND_HALF_UP;
		if (up != null) rm = up ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
		return d.setScale(s, rm).stripTrailingZeros();
	}

	/**
	 * 转换数学对象
	 * @param val 值
	 * @return 数学对象
	 */
	private static BigDecimal decimal(double val) {
		BigDecimal d1 = new BigDecimal(val);
		if (d1.scale() > 6) {
			BigDecimal d2 = BigDecimal.valueOf(val);
			if (d2.scale() < d1.scale()) return r(d2, d_scale);
		}
		return d1;
	}

	private transient BigDecimal dec;

	/**
	 * 构造函数
	 * @param dec 不可变数字值
	 */
	protected Numeric(BigDecimal dec) {
		this.dec = dec.stripTrailingZeros();
	}

	/**
	 * 取正数
	 * @return 数值
	 */
	public Numeric abs() {
		return valueOf(dec.abs());
	}

	/**
	 * 数字相加
	 * @param val 加数
	 * @return 结果数值
	 */
	public Numeric add(double val) {
		return add(decimal(val));
	}

	/**
	 * 数字相加
	 * @param num 加数
	 * @return 结果数值
	 */
	public Numeric add(Number num) {
		if (num == null) {
			return this;
		} else if (num instanceof BigDecimal) {
			return add((BigDecimal) num);
		} else if (num instanceof Numeric) {
			return add(((Numeric) num).dec);
		} else {
			return add(num.doubleValue());
		}
	}

	/**
	 * 数字累加
	 * @param vals 加数集
	 * @return 结果数值
	 */
	public Numeric addTo(double... vals) {
		if (vals == null || vals.length == 0) {
			return this;
		} else {
			BigDecimal r = dec;
			for (double val : vals) {
				r = r.add(decimal(val));
			}
			return valueOf(r);
		}
	}

	public int compareTo(Numeric o) {
		if (o == null) return -1;
		BigDecimal d = o.dec;
		if (d == null) return dec == null ? 0 : -1;
		else if (dec == null) return 1;
		double d1 = d(dec), d2 = d(d);
		double x = d1 - d2;
		if (x == 0) return 0;
		else if (x < 0) return -1;
		else return 1;
	}

	/**
	 * 数字除法
	 * @param val 除数
	 * @return 结果数值
	 */
	public Numeric divide(double val) {
		return divide(decimal(val));
	}

	/**
	 * 数字除法
	 * @param num 除数
	 * @return 结果数值
	 */
	public Numeric divide(Number num) {
		if (num == null) {
			return this;
		} else if (num instanceof BigDecimal) {
			return divide((BigDecimal) num);
		} else if (num instanceof Numeric) {
			return divide(((Numeric) num).dec);
		} else {
			return divide(num.doubleValue());
		}
	}

	public double doubleValue() {
		return d(dec).doubleValue();
	}

	/**
	 * 是否相同
	 * @param num 目标数值
	 * @return 是、否
	 */
	public boolean equals(Numeric num) {
		if (num == null) return false;
		BigDecimal d = num.dec;
		if (d == null) return dec == null;
		else if (dec == null) return false;
		double d1 = d(dec), d2 = d(d);
		return d1 == d2;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Numeric) {
			return equals((Numeric) obj);
		} else if (obj instanceof Number) {
			return d(dec).doubleValue() == d((Number) obj).doubleValue();
		} else {
			return false;
		}
	}

	public float floatValue() {
		return dec.floatValue();
	}

	public int hashCode() {
		return dec.hashCode();
	}

	public int intValue() {
		return dec.intValue();
	}

	public long longValue() {
		return dec.longValue();
	}

	/**
	 * 数字相乘
	 * @param val 乘数
	 * @return 结果数值
	 */
	public Numeric multiply(double val) {
		return valueOf(dec.multiply(valueOf(val).dec));
	}

	/**
	 * 数字相乘
	 * @param num 乘数
	 * @return 结果数值
	 */
	public Numeric multiply(Number num) {
		if (num == null) {
			throw new NullPointerException();
		} else if (num instanceof BigDecimal) {
			return multiply((BigDecimal) num);
		} else if (num instanceof Numeric) {
			return multiply(((Numeric) num).dec);
		} else {
			return multiply(num.doubleValue());
		}
	}

	/**
	 * 数字累乘
	 * @param vals 乘数集
	 * @return 结果数值
	 */
	public Numeric multiplyBy(double... vals) {
		if (vals == null || vals.length == 0) {
			return this;
		} else {
			BigDecimal r = dec;
			for (double val : vals) {
				r = r.multiply(decimal(val));
			}
			return valueOf(r);
		}
	}

	/**
	 * 取负数
	 * @return 负数值
	 */
	public Numeric negate() {
		return valueOf(dec.negate());
	}

	/**
	 * 四舍五入
	 * @param scale 保留小数后位数
	 * @return 数字值
	 */
	public double round(int scale) {
		return round(scale, null);
	}

	/**
	 * 保留小数位
	 * @param scale 保留后小数位
	 * @param up 是否进位 <code>true</code>:进位，<code>false</code>:降位，<code>null</code>:四舍五入
	 * @return 数字值
	 */
	public double round(int scale, Boolean up) {
		return r(scale, up).doubleValue();
	}

	/**
	 * 数字相减
	 * @param val 减数
	 * @return 结果数值
	 */
	public Numeric subtract(double val) {
		return valueOf(dec.subtract(decimal(val)));
	}

	/**
	 * 数字相减
	 * @param num 减数
	 * @return 结果数值
	 */
	public Numeric subtract(Number num) {
		if (num == null) throw new NullPointerException();
		else if (num instanceof BigDecimal) return subtract((BigDecimal) num);
		else if (num instanceof Numeric) return subtract(((Numeric) num).dec);
		else return subtract(num.doubleValue());
	}

	public String toString() {
		return toString(this);
	}

	/**
	 * 数字相加
	 * @param other 加数
	 * @return 结果数值
	 */
	protected Numeric add(BigDecimal other) {
		return valueOf(dec.add(other));
	}

	/**
	 * 数字相除
	 * @param other 除数
	 * @return 结果数值
	 */
	protected Numeric divide(BigDecimal other) {
		return valueOf(dec.divide(other, new MathContext(Math.max(0, dec.precision() - dec.scale() + d_scale), RoundingMode.HALF_UP)));
	}

	/**
	 * 数字相乘
	 * @param other 乘数
	 * @return 结果数值
	 */
	protected Numeric multiply(BigDecimal other) {
		return valueOf(dec.multiply(other));
	}

	/**
	 * 四舍五入
	 * @param scale 保留小数后位数
	 * @return 数字值
	 */
	protected BigDecimal r(int scale) {
		return r(dec, scale);
	}

	/**
	 * 保留小数位
	 * @param scale 保留后小数位
	 * @param up 是否进位
	 * @return 数字值
	 */
	protected BigDecimal r(int scale, Boolean up) {
		return r(dec, scale, up);
	}

	/**
	 * 数字相减
	 * @param other 减数
	 * @return 结果数值
	 */
	protected Numeric subtract(BigDecimal other) {
		return valueOf(dec.subtract(other));
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		double d = s.readDouble();
		dec = decimal(d);
	}

	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeDouble(d(dec).doubleValue());
	}
}

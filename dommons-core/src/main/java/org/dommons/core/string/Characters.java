/*
 * @(#)Characters.java     2012-7-11
 */
package org.dommons.core.string;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.dommons.core.util.Arrayard;

/**
 * 可追加字符序列
 * @author Demon 2012-7-11
 */
public final class Characters implements Serializable, CharSequence, Appendable {

	private static final long serialVersionUID = 5162892146799515305L;

	/**
	 * 创建可追加字符序列
	 * @param buffer 目标可追加字符序列
	 * @return 可追加字符序列
	 */
	public static Characters create(StringBuffer buffer) {
		if (buffer == null) throw new NullPointerException();
		return new Characters(buffer);
	}

	/**
	 * 创建可追加字符序列
	 * @param builder 目标可追加字符序列
	 * @return 可追加字符序列
	 */
	public static Characters create(StringBuilder builder) {
		if (builder == null) throw new NullPointerException();
		return new Characters(builder);
	}

	/**
	 * 创建线程安全可追加字符序列
	 * @return 可追加字符序列
	 */
	public static Characters createBuffer() {
		return new Characters(new StringBuffer());
	}

	/**
	 * 创建线程安全可追加字符序列
	 * @param cs 初始字符序列
	 * @return 可追加字符序列
	 */
	public static Characters createBuffer(CharSequence cs) {
		return new Characters(cs == null ? new StringBuffer() : new StringBuffer(cs));
	}

	/**
	 * 创建线程安全可追加字符序列
	 * @param capacity 初始容量
	 * @return 可追加字符序列
	 */
	public static Characters createBuffer(int capacity) {
		return new Characters(new StringBuffer(capacity));
	}

	/**
	 * 创建线程安全可追加字符序列
	 * @param string 初始字符串
	 * @return 可追加字符序列
	 */
	public static Characters createBuffer(String string) {
		return new Characters(string == null ? new StringBuffer() : new StringBuffer(string));
	}

	/**
	 * 创建可追加字符序列
	 * @return 可追加字符序列
	 */
	public static Characters createBuilder() {
		return new Characters(new StringBuilder());
	}

	/**
	 * 创建可追加字符序列
	 * @param cs 初始字符序列
	 * @return 可追加字符序列
	 */
	public static Characters createBuilder(CharSequence cs) {
		return new Characters(cs == null ? new StringBuilder() : new StringBuilder(cs));
	}

	/**
	 * 创建可追加字符序列
	 * @param capacity 初始容量
	 * @return 可追加字符序列
	 */
	public static Characters createBuilder(int capacity) {
		return new Characters(new StringBuilder(capacity));
	}

	/**
	 * 创建可追加字符序列
	 * @param string 初始字符串
	 * @return 可追加字符序列
	 */
	public static Characters createBuilder(String string) {
		return new Characters(string == null ? new StringBuilder() : new StringBuilder(string));
	}

	/**
	 * 是否中文字符
	 * @param c 字符
	 * @return 是、否
	 * @deprecated {@link Charician#isChinese(char)}
	 */
	public static boolean isChinese(char c) {
		return Charician.isChinese(c);
	}

	private transient StringBuilder builder;
	private transient StringBuffer buffer;

	protected Characters(StringBuffer buffer) {
		this.buffer = buffer;
	}

	protected Characters(StringBuilder builder) {
		this.builder = builder;
	}

	/**
	 * 追加布尔型值
	 * @param value 布尔型
	 * @return 可追加字符序列
	 */
	public Characters append(boolean value) {
		if (this.buffer != null) {
			this.buffer.append(value);
		} else {
			this.builder.append(value);
		}
		return this;
	}

	public Characters append(char c) {
		if (buffer != null) {
			buffer.append(c);
		} else {
			builder.append(c);
		}
		return this;
	}

	/**
	 * 追加字符数组
	 * @param chars 字符数组
	 * @return 可追加字符序列
	 */
	public Characters append(char... chars) {
		if (this.buffer != null) {
			this.buffer.append(chars);
		} else {
			this.builder.append(chars);
		}
		return this;
	}

	/**
	 * 追加字符数组
	 * @param chars 字符数组
	 * @param start 开始索引
	 * @param length 长度
	 * @return 可追加字符序列
	 */
	public Characters append(char chars[], int start, int length) {
		if (this.buffer != null) {
			this.buffer.append(chars, start, length);
		} else {
			this.builder.append(chars, start, length);
		}
		return this;
	}

	/**
	 * 追加新的可追加字符序列
	 * @param buffer 可追加字符序列
	 * @return 可追加字符序列
	 */
	public Characters append(Characters buffer) {
		return buffer == null ? append((String) null) : buffer.buffer != null ? append(buffer.buffer) : append(buffer.builder);
	}

	public Characters append(CharSequence csq) {
		if (buffer != null) {
			buffer.append(csq);
		} else {
			builder.append(csq);
		}
		return this;
	}

	public Characters append(CharSequence csq, int start, int end) {
		if (buffer != null) {
			buffer.append(csq, start, end);
		} else {
			builder.append(csq, start, end);
		}
		return this;
	}

	/**
	 * 追加双精度值
	 * @param value 双精度值
	 * @return 可追加字符序列
	 */
	public Characters append(double value) {
		if (this.buffer != null) {
			this.buffer.append(value);
		} else {
			this.builder.append(value);
		}
		return this;
	}

	/**
	 * 追加浮点数字值
	 * @param value 浮点数字
	 * @return 可追加字符序列
	 */
	public Characters append(float value) {
		if (this.buffer != null) {
			this.buffer.append(value);
		} else {
			this.builder.append(value);
		}
		return this;
	}

	/**
	 * 追加整型数值
	 * @param value 整数
	 * @return 可追加字符序列
	 */
	public Characters append(int value) {
		if (this.buffer != null) {
			this.buffer.append(value);
		} else {
			this.builder.append(value);
		}
		return this;
	}

	/**
	 * 追加长整型数值
	 * @param value 长整数
	 * @return 可追加字符序列
	 */
	public Characters append(long value) {
		if (this.buffer != null) {
			this.buffer.append(value);
		} else {
			this.builder.append(value);
		}
		return this;
	}

	/**
	 * 追加对象型字符串
	 * @param obj 对象值
	 * @return 可追加字符序列
	 */
	public Characters append(Object obj) {
		if (buffer != null) {
			buffer.append(obj);
		} else {
			builder.append(obj);
		}
		return this;
	}

	/**
	 * 追加字符串
	 * @param string 字符串
	 * @return 可追加字符序列
	 */
	public Characters append(String string) {
		if (this.buffer != null) {
			this.buffer.append(string);
		} else {
			this.builder.append(string);
		}
		return this;
	}

	/**
	 * 追加新字符序列
	 * @param buffer 字符序列
	 * @return 可追加字符序列
	 */
	public Characters append(StringBuffer buffer) {
		if (this.buffer != null) {
			this.buffer.append(buffer);
		} else {
			this.builder.append(buffer);
		}
		return this;
	}

	/**
	 * 追加新字符序列
	 * @param builder 字符序列
	 * @return 可追加字符序列
	 */
	public Characters append(StringBuilder builder) {
		if (this.buffer != null) {
			this.buffer.append(builder);
		} else {
			this.builder.append(builder);
		}
		return this;
	}

	/**
	 * 追加字符代码
	 * @param codePoint 字符代码
	 * @return 可追加字符序列
	 */
	public Characters appendCodePoint(int codePoint) {
		if (this.buffer != null) {
			this.buffer.appendCodePoint(codePoint);
		} else {
			this.builder.appendCodePoint(codePoint);
		}
		return this;
	}

	/**
	 * 返回当前容量
	 * @return 容量
	 */
	public int capacity() {
		return buffer != null ? buffer.capacity() : builder.capacity();
	}

	public char charAt(int index) {
		return buffer != null ? buffer.charAt(index) : builder.charAt(index);
	}

	/**
	 * 返回指定索引处的字符代码
	 * @param index 索引
	 * @return 字符代码
	 */
	public int codePointAt(int index) {
		return buffer != null ? buffer.codePointAt(index) : builder.codePointAt(index);
	}

	/**
	 * 返回指定索引前的字符代码
	 * @param index 索引
	 * @return 字符代码
	 */
	public int codePointBefore(int index) {
		return buffer != null ? buffer.codePointBefore(index) : builder.codePointBefore(index);
	}

	/**
	 * 返回此序列指定文本范围内的字符代码数量
	 * @param start 开始索引
	 * @param end 截止索引
	 * @return 数量
	 */
	public int codePointCount(int start, int end) {
		return buffer != null ? buffer.codePointCount(start, end) : builder.codePointCount(start, end);
	}

	/**
	 * 移除序列中的字符，从指定的索引处开始，一直到截止索引 - 1 处的字符
	 * @param start 开始索引
	 * @param end 截止索引
	 * @return 可追加字符序列
	 */
	public Characters delete(int start, int end) {
		if (this.buffer != null) {
			this.buffer.delete(start, end);
		} else {
			this.builder.delete(start, end);
		}
		return this;
	}

	/**
	 * 移除序列中的指定位置的字符
	 * @param location 位置
	 * @return 可追加字符序列
	 */
	public Characters deleteCharAt(int location) {
		if (this.buffer != null) {
			this.buffer.deleteCharAt(location);
		} else {
			this.builder.deleteCharAt(location);
		}
		return this;
	}

	/**
	 * 确保容量至少等于指定的最小值
	 * @param min 最小值
	 * @return 可追加字符序列
	 */
	public Characters ensureCapacity(int min) {
		if (this.buffer != null) {
			this.buffer.ensureCapacity(min);
		} else {
			this.builder.ensureCapacity(min);
		}
		return this;
	}

	public boolean equals(Object o) {
		return o != null && (o instanceof Characters) && Arrayard.equals(buffer, ((Characters) o).buffer)
				&& Arrayard.equals(builder, ((Characters) o).builder);
	}

	/**
	 * 将字符从此序列复制到目标字符数组
	 * @param start 开始索引
	 * @param end 截止索引
	 * @param buffer 目标字符数组
	 * @param index 数组中的偏移
	 * @return 目标字符数组
	 */
	public char[] getChars(int start, int end, char[] buffer, int index) {
		if (this.buffer != null) {
			this.buffer.getChars(start, end, buffer, index);
		} else {
			this.builder.getChars(start, end, buffer, index);
		}
		return buffer;
	}

	public int hashCode() {
		return buffer != null ? buffer.hashCode() : builder.hashCode();
	}

	/**
	 * 返回第一次出现的指定子字符串在该字符串中的索引
	 * @param string 子字符串
	 * @return 索引 未出现返回 －1
	 */
	public int indexOf(String string) {
		return string == null ? -1 : (this.buffer != null ? this.buffer.indexOf(string) : this.builder.indexOf(string));
	}

	/**
	 * 返回指定索引开始第一次出现的指定子字符串在该字符串中的索引
	 * @param subString 子字符串
	 * @param start 开始索引
	 * @return 索引 未出现返回 －1
	 */
	public int indexOf(String subString, int start) {
		return subString == null ? -1
				: (this.buffer != null ? this.buffer.indexOf(subString, start) : this.builder.indexOf(subString, start));
	}

	/**
	 * 将布尔型值插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 布尔值
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, boolean value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将字符数组插入到此序列的指定位置中
	 * @param index 索引
	 * @param chars 字符数组
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, char... chars) {
		if (this.buffer != null) {
			this.buffer.insert(index, chars);
		} else {
			this.builder.insert(index, chars);
		}
		return this;
	}

	/**
	 * 将字符插入到此序列的指定位置中
	 * @param index 索引
	 * @param ch 字符
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, char ch) {
		if (this.buffer != null) {
			this.buffer.insert(index, ch);
		} else {
			this.builder.insert(index, ch);
		}
		return this;
	}

	/**
	 * 将字符数组的子数组插入到此序列的指定位置中
	 * @param index 索引
	 * @param chars 字符数组
	 * @param start 数组开始索引
	 * @param length 子数组长度
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, char chars[], int start, int length) {
		if (this.buffer != null) {
			this.buffer.insert(index, chars, start, length);
		} else {
			this.builder.insert(index, chars, start, length);
		}
		return this;
	}

	/**
	 * 将指定字符序列插入到此序列的指定位置中
	 * @param index 索引
	 * @param sequence 字符序列
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, CharSequence sequence) {
		if (this.buffer != null) {
			this.buffer.insert(index, sequence);
		} else {
			this.builder.insert(index, sequence);
		}
		return this;
	}

	/**
	 * 将指定字符序列的子序列插入到此序列的指定位置中
	 * @param index 索引
	 * @param sequence 字符序列
	 * @param start 子序列开始索引
	 * @param end 子序列截止索引
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, CharSequence sequence, int start, int end) {
		if (this.buffer != null) {
			this.buffer.insert(index, sequence, start, end);
		} else {
			this.builder.insert(index, sequence, start, end);
		}
		return this;
	}

	/**
	 * 将双精度数值插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 双精度数值
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, double value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将浮点数值插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 浮点数值
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, float value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将整型数值插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 整数
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, int value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将长整型数值插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 长整数
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, long value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将对象插入到此序列的指定位置中
	 * @param index 索引
	 * @param value 对象值
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, Object value) {
		if (this.buffer != null) {
			this.buffer.insert(index, value);
		} else {
			this.builder.insert(index, value);
		}
		return this;
	}

	/**
	 * 将字符串插入到此序列的指定位置中
	 * @param index 索引
	 * @param string 字符串
	 * @return 可追加字符序列
	 */
	public Characters insert(int index, String string) {
		if (this.buffer != null) {
			this.buffer.insert(index, string);
		} else {
			this.builder.insert(index, string);
		}
		return this;
	}

	/**
	 * 返回最右边出现的指定子字符串在此字符串中的索引
	 * @param string 子字符串
	 * @return 索引 未出现返回 －1
	 */
	public int lastIndexOf(String string) {
		return string == null ? -1 : this.buffer != null ? this.buffer.lastIndexOf(string) : this.builder.lastIndexOf(string);
	}

	/**
	 * 返回指定索引之前最后一次出现的指定子字符串在此字符串中的索引
	 * @param subString 子字符串
	 * @param start 开始索引
	 * @return 索引 未出现返回 －1
	 */
	public int lastIndexOf(String subString, int start) {
		return subString == null ? -1
				: this.buffer != null ? this.buffer.lastIndexOf(subString, start) : this.builder.lastIndexOf(subString, start);
	}

	public int length() {
		return buffer != null ? buffer.length() : builder.length();
	}

	/**
	 * 返回此序列中的一个索引，该索引是从给定索引偏移指定数量个代码点后得到的
	 * @param start 开始索引
	 * @param count 偏移数量
	 * @return 索引
	 */
	public int offsetByCodePoints(int start, int count) {
		return buffer != null ? buffer.offsetByCodePoints(start, count) : builder.offsetByCodePoints(start, count);
	}

	/**
	 * 使用指定的字符串替换此序列中指定的子序列
	 * @param start 开始索引
	 * @param end 截止索引
	 * @param string 字符串
	 * @return 可追加字符序列
	 */
	public Characters replace(int start, int end, String string) {
		if (this.buffer != null) {
			this.buffer.replace(start, end, string);
		} else {
			this.builder.replace(start, end, string);
		}
		return this;
	}

	/**
	 * 反转序列
	 * @return 可追加字符序列
	 */
	public Characters reverse() {
		if (this.buffer != null) {
			this.buffer.reverse();
		} else {
			this.builder.reverse();
		}
		return this;
	}

	/**
	 * 将给定索引处的字符设置为新指定字符
	 * @param index 索引
	 * @param ch 字符
	 * @return 可追加字符序列
	 */
	public Characters setCharAt(int index, char ch) {
		if (buffer != null) {
			buffer.setCharAt(index, ch);
		} else {
			builder.setCharAt(index, ch);
		}
		return this;
	}

	/**
	 * 设置字符序列的长度。序列将被更改为一个新的字符序列，新序列的长度由参数指定
	 * @param len 新长度
	 * @return 可追加字符序列
	 */
	public Characters setLength(int len) {
		if (buffer != null) {
			buffer.setLength(len);
		} else {
			builder.setLength(len);
		}
		return this;
	}

	public CharSequence subSequence(int start, int end) {
		return buffer != null ? buffer.subSequence(start, end) : builder.subSequence(start, end);
	}

	/**
	 * 返回一个新的字符串，它包含此字符序列当前所包含的字符子序列。该子字符串始于指定索引处的字符，一直到此字符串末尾。
	 * @param start 开始索引
	 * @return 字符串
	 */
	public String substring(int start) {
		return buffer != null ? buffer.substring(start) : builder.substring(start);
	}

	/**
	 * 返回一个新的字符串，它包含此序列当前所包含的字符子序列。该子字符串从指定的索引处开始，一直到截止索引 - 1 处的字符。
	 * @param start 开始索引
	 * @param end 截止索引
	 * @return 字符串
	 */
	public String substring(int start, int end) {
		return buffer != null ? buffer.substring(start, end) : builder.substring(start, end);
	}

	public String toString() {
		return buffer != null ? buffer.toString() : builder.toString();
	}

	/**
	 * 尝试减少用于字符序列的存储空间
	 * @return 可追加字符序列
	 */
	public Characters trimToSize() {
		if (this.buffer != null) {
			this.buffer.trimToSize();
		} else {
			this.builder.trimToSize();
		}
		return this;
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		buffer = (StringBuffer) s.readObject();
		builder = (StringBuilder) s.readObject();
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();

		s.writeObject(buffer);
		s.writeObject(builder);
	}
}

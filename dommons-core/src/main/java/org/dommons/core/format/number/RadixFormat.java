/*
 * @(#)RadixFormat.java     2011-10-18
 */
package org.dommons.core.format.number;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.regex.Pattern;

import org.dommons.core.format.text.TextFormat;
import org.dommons.core.number.Radix64;
import org.dommons.core.string.Characters;
import org.dommons.core.string.Stringure;

/**
 * 多进制数字格式器
 * <table>
 * <tr>
 * <td><i>pattern : </i><code>'content'</code><i>$</i><code>{radix}</code><code>'content'</code>
 * </tr>
 * </table>
 * <p>
 * <h4>示例</h4>
 * <table border='1px' cellspacing='0'>
 * <tr align="left">
 * <th id="f" width="200">Format</th>
 * <th id="a" width="80">Argument</th>
 * <th id="r" width="60">Result</th>
 * </tr>
 * <tr>
 * <td headers="f"><code>${16}</code>
 * <td headers="a"><code>15</code>
 * <td headers="r"><code>0xF</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>{16}</code>
 * <td headers="a"><code>-15</code>
 * <td headers="r"><code>-F</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>${8}</code>
 * <td headers="a"><code>9</code>
 * <td headers="r"><code>011</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>'AD'{2}''X</code>
 * <td headers="a"><code>9</code>
 * <td headers="r"><code>AD1001'X</code>
 * </tr>
 * </table>
 * </p>
 * @author Demon 2011-10-18
 */
public class RadixFormat extends NumberFormat {
	private static final long serialVersionUID = 5936820121444513698L;

	static final String[] radixFlags = { null, null, "0", "0x" };
	static final Pattern radixPattern = Pattern.compile("(\\$)?\\{[1-9][0-9]*\\}");

	/**
	 * 复制内容并填入对应的引号
	 * @param source 文本
	 * @param start 起始位置
	 * @param end 结束位置
	 * @param target 目标缓冲区
	 */
	protected static final void copyAndFixQuotes(String source, int start, int end, Characters target) {
		for (int i = start; i < end; ++i) {
			char ch = source.charAt(i);
			switch (ch) {
			case '{':
				TextFormat.mixQuotes(target, "'{'");
				break;
			case '}':
				TextFormat.mixQuotes(target, "'}'");
				break;
			case '$':
				TextFormat.mixQuotes(target, "'$'");
				break;
			case '\n':
				TextFormat.mixQuotes(target, "'\n'");
				break;
			case '\'':
				target.append("''");
				break;
			default:
				target.append(ch);
				break;
			}
		}
	}

	/**
	 * 取二的次方数
	 * @param x 值
	 * @return 次方数
	 */
	static final int upow(int x) {
		int n = 1;
		while (x % 2 == 0 && (x >>>= 1) > 1) {
			n++;
		}
		return x != 1 ? -1 : n;
	}

	private int radix;

	private int offset;

	private boolean hasFlag;

	private String pattern;

	/**
	 * 构造函数
	 * @param pattern 格式串
	 */
	public RadixFormat(String pattern) {
		super();
		super.setParseIntegerOnly(true);
		applyPattern(pattern);
	}

	/**
	 * 应用格式定义
	 * @param pattern 格式定义串
	 */
	public void applyPattern(String pattern) {
		pattern = Stringure.trim(pattern);
		int len = pattern.length();
		if (len == 0) return;

		StringBuilder[] buffers = new StringBuilder[2];
		for (int i = 0; i < 2; i++) {
			buffers[i] = new StringBuilder();
		}

		int offset = 0;
		boolean inQuote = false;
		int p = 0;
		for (int i = 0; i < len; i++) {
			char ch = pattern.charAt(i);
			if (inQuote) {
				if (ch == '\'') {
					if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
						buffers[p].append(ch); // 处理连续引号
						++i;
					} else {
						inQuote = !inQuote;
					}
				} else {
					buffers[p].append(ch);
				}
			} else {
				switch (ch) {
				case '\'':
					if (p == 0) {
						if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
							buffers[p].append(ch); // 处理连续引号
							++i;
						} else {
							inQuote = !inQuote;
						}
						break;
					} else {
						throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
					}
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					buffers[p].append(ch);
					break;
				case '{':
					if (p == 0) {
						offset = buffers[0].length();
						p++;
					}
					buffers[p].append(ch);
					break;
				case '}':
					buffers[p].append(ch);
					if (p == 1) p--;
					break;
				case '$':
					if (p == 0) {
						offset = buffers[0].length();
						p++;
					}
					buffers[p].append(ch);
					break;
				default:
					if (p == 1) throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
					buffers[p].append(ch);
					break;
				}
			}
		}

		String value = buffers[1].toString();
		if (!radixPattern.matcher(value).matches()) throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");

		boolean hasFlag = value.startsWith("$");
		int radix = Integer.parseInt(Stringure.subString(value, hasFlag ? 2 : 1, -1));
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) throw new IllegalArgumentException("Invalid radix [" + radix + "]");

		this.radix = radix;
		int x = upow(radix);
		this.hasFlag = hasFlag && x != -1 && radixFlags[x - 1] != null;
		this.offset = offset;
		this.pattern = buffers[0].toString();
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj.getClass() != this.getClass()) return false;
		RadixFormat other = (RadixFormat) obj;
		return radix == other.radix && hasFlag == other.hasFlag && offset == other.offset && pattern.equals(other.pattern);
	}

	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return format((long) number, toAppendTo, pos);
	}

	public StringBuffer format(long number, StringBuffer result, FieldPosition pos) {
		pos.setBeginIndex(result.length());
		if (number < 0) {
			number = -number;
			result.append('-');
		}
		if (offset > 0) result.append(pattern, 0, offset);
		if (hasFlag) result.append(radixFlags[upow(radix) - 1]);
		result.append(Radix64.toString(number, radix));
		result.append(pattern.substring(offset));
		pos.setEndIndex(result.length());
		return result;
	}

	public Number parse(String source, ParsePosition pos) {
		if (source == null) return null;
		StringBuilder num = new StringBuilder();
		int sourceOffset = pos.getIndex();
		// 解析负数符号
		if (source.charAt(sourceOffset) == '-') {
			num.append('-');
			sourceOffset++;
		}

		// 比较是否与模板相符
		if (offset != 0 && !pattern.regionMatches(0, source, sourceOffset, offset)) {
			pos.setErrorIndex(sourceOffset);
			return null;
		} else {
			sourceOffset += offset;
		}

		// 解析数字符号
		if (hasFlag) {
			String flag = radixFlags[upow(radix) - 1];
			if (source.regionMatches(sourceOffset, flag, 0, flag.length())) {
				sourceOffset += flag.length();
			} else {
				pos.setErrorIndex(sourceOffset);
				return null;
			}
		}

		// 解析数字区间和匹配模板
		int next = 0;
		boolean ensure = false;
		if (offset < pattern.length()) next = source.indexOf(pattern.substring(offset), sourceOffset);

		if (next == -1) {
			pos.setErrorIndex(sourceOffset);
			return null;
		} else {
			ensure = next > 0;
			if (!ensure) next = source.length();

		}

		// 解析数字区域
		for (; sourceOffset < next; sourceOffset++) {
			char ch = source.charAt(sourceOffset);
			int n = Character.digit(ch, radix);
			if (n != -1) {
				num.append(ch);
			} else if (ensure) {
				pos.setErrorIndex(sourceOffset);
				return null;
			} else {
				break;
			}
		}

		Number number = Long.valueOf(num.toString(), radix);

		pos.setIndex(!ensure ? sourceOffset : next);

		return number;
	}

	/**
	 * 转换为格式表达式
	 * @return 格式表达式
	 */
	public String toPattern() {
		Characters buffer = Characters.createBuilder();
		copyAndFixQuotes(pattern, 0, offset, buffer);
		if (hasFlag) buffer.append('$');
		buffer.append('{').append(radix).append('}');
		copyAndFixQuotes(pattern, offset, pattern.length(), buffer);
		return buffer.toString();
	}
}

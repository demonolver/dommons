/*
 * @(#)TextFormat.java     2011-10-18
 */
package org.dommons.core.format.text;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.string.Characters;
import org.dommons.core.string.Charician;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;

/**
 * 文本格式 模仿<code>Formatter</code>对文本内容进行格式化，可控制定长或将子文本内容分割输出
 * <table>
 * <tr>
 * <td><i>pattern : </i><code>{content}</code> <code>[part]</code> <code>{content}</code> <code>[part]</code> <code>{content}</code>...</td>
 * </tr>
 * <tr>
 * <td><i>part : </i><i>&lt;flag&gt;</i><code>[width]|(sub)'place holder'</code></td>
 * </tr>
 * </table>
 * <p>
 * <h4>示例:</h4>
 * <table border="1px" cellspacing="0">
 * <tr align="left">
 * <th id="f" width="200">Format</th>
 * <th id="a" width="80">Argument</th>
 * <th id="r" width="60">Result</th>
 * </tr>
 * <tr>
 * <td headers="f"><code>[-5'\u0023']</code>
 * <td headers="a"><code>123a</code>
 * <td headers="r"><code>#123a</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>'['[-5'\u0023']']'</code>
 * <td headers="a"><code>123a</code>
 * <td headers="r"><code>[#123a]</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>'['[(0,3)]''[^(3,@3)'']']'</code>
 * <td headers="a"><code>123a</code>
 * <td headers="r"><code>[123'A]</code>
 * </tr>
 * <tr>
 * <td headers="f"><code>([(0,3)]''[^(3,5)''])</code>
 * <td headers="a"><code>123a</code>
 * <td headers="r"><code>(123'A )</code>
 * </tr>
 * </table>
 * </p>
 * @author Demon 2011-10-18
 */
public class TextFormat extends Format {

	private static final long serialVersionUID = 3105973944956700697L;

	static final int UPPERCASE = 1 << 1; // '^'

	static final int LEFT_JUSTIFY = 1 << 0; // '-'

	/** 默认占位符 */
	static final char DEFAULT_PLACEHOLDER = ' ';

	static final int INITIAL_PARTS = 10;

	/**
	 * 整合连续的引号
	 * @param target 目标缓冲区
	 * @param value 新加内容串
	 */
	public static final void mixQuotes(Characters target, String value) {
		int oldLen = target.length();
		if ((oldLen > 0 && target.charAt(oldLen - 1) == '\''
				&& ((oldLen > 1 && target.charAt(oldLen - 2) != '\'') || (oldLen > 2 && target.charAt(oldLen - 3) == '\'')))) {
			target.setLength(oldLen - 1);
			target.append(value, 1, value.length());
		} else {
			target.append(value);
		}
	}

	protected final Locale locale;

	private String pattern;

	private PartFormat[] parts = new PartFormat[INITIAL_PARTS];
	private int[] offsets = new int[INITIAL_PARTS];
	private int maxOffsets = -1;

	/**
	 * 构造函数
	 * @param pattern 格式串
	 */
	public TextFormat(String pattern) {
		this(pattern, Environments.defaultLocale());
	}

	/**
	 * 构造函数
	 * @param pattern 格式串
	 * @param locale 语言区域
	 */
	public TextFormat(String pattern, Locale locale) {
		this.locale = locale;
		applyFormat(pattern);
	}

	/**
	 * 应用格式定义
	 * @param pattern 格式定义串
	 */
	public void applyFormat(String pattern) {
		if (pattern == null || pattern.length() == 0) return;

		pattern = pattern.trim();
		int len = pattern.length();
		boolean inPart = false;
		boolean inQuote = false;
		int braceStack = 0;
		int number = 0;
		StringBuilder patternBuffer = new StringBuilder();
		StringBuilder part = new StringBuilder();
		for (int i = 0; i < len; i++) {
			char ch = pattern.charAt(i);

			if (!inPart) { // 常规内容
				if (ch == '\'') {
					if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
						patternBuffer.append(ch); // 处理连续引号
						++i;
					} else {
						inQuote = !inQuote;
					}
				} else if (!inQuote && ch == '[') {
					inPart = true;
				} else {
					patternBuffer.append(ch);
				}
			} else { // 子格式
				if (ch == '\'') {
					inQuote = !inQuote;
					part.append(ch);
				} else if (!inQuote && ch == '[') {
					++braceStack;
					part.append(ch);
				} else if (!inQuote && ch == ']') {
					if (braceStack == 0) {
						inPart = false;
						makePart(patternBuffer.length(), number++, part);
					} else {
						--braceStack;
						part.append(ch);
					}
				} else {
					part.append(ch);
				}
			}
		}

		this.pattern = patternBuffer.toString();
	}

	public Object clone() {
		TextFormat other = (TextFormat) super.clone();
		other.parts = parts.clone(); // shallow clone
		for (int i = 0; i < parts.length; ++i) {
			if (parts[i] != null) other.parts[i] = (PartFormat) parts[i].clone();
		}
		other.offsets = other.offsets.clone();
		return other;
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (o.getClass() != this.getClass()) return false;
		TextFormat other = (TextFormat) o;

		return maxOffsets == other.maxOffsets && pattern.equals(other.pattern) && Arrayard.equals(offsets, other.offsets)
				&& Arrayard.equals(parts, other.parts);
	}

	public StringBuffer format(Object obj, StringBuffer result, FieldPosition pos) {
		String value = String.valueOf(obj);

		int lastOffset = 0;
		int last = 0;
		for (int i = 0; i <= maxOffsets; i++) {
			result.append(pattern, lastOffset, lastOffset = offsets[i]);
			parts[i].format(value, result);
			if (pos != null) {
				pos.setBeginIndex(last);
				pos.setEndIndex(last = result.length());
			}
		}

		result.append(pattern, lastOffset, pattern.length());
		return result;
	}

	public int hashCode() {
		return pattern.hashCode();
	}

	public Object parseObject(String source, ParsePosition pos) {
		if (source == null) return null;

		int patternOffset = 0;
		int sourceOffset = pos.getIndex();
		TextPart[] parts = new TextPart[maxOffsets + 1];
		for (int i = 0; i <= maxOffsets; i++) {
			int len = offsets[i] - patternOffset;
			if (len == 0 || pattern.regionMatches(patternOffset, source, sourceOffset, len)) {
				sourceOffset += len;
				patternOffset += len;
			} else {
				pos.setErrorIndex(sourceOffset);
				return null;
			}

			int tempLength = (i != maxOffsets) ? offsets[i + 1] : pattern.length();

			int next;

			if (this.parts[i].length() != -1 && source.length() >= sourceOffset + this.parts[i].length()) {
				next = sourceOffset + this.parts[i].length();
			} else if (patternOffset >= tempLength) {
				next = source.length();
			} else {
				next = source.indexOf(pattern.substring(patternOffset, tempLength), sourceOffset);
			}

			if (next > 0) {
				parts[i] = this.parts[i].parse(source, sourceOffset, next - sourceOffset);
				if (parts[i] != null) {
					sourceOffset = next;
					continue;
				}
			}
			pos.setErrorIndex(sourceOffset);
			return null;
		}

		int len = pattern.length() - patternOffset;
		if (len == 0 || pattern.regionMatches(patternOffset, source, sourceOffset, len)) {
			pos.setIndex(sourceOffset + len);
		} else {
			pos.setErrorIndex(sourceOffset);
			return null; // leave index as is to signal error
		}
		return merge(parts);
	}

	/**
	 * 转换为格式串
	 * @return 格式串
	 */
	public String toPattern() {
		int lastOffset = 0;
		Characters buffer = Characters.createBuilder();
		for (int i = 0; i <= maxOffsets; i++) {
			copyAndFixQuotes(pattern, lastOffset, lastOffset = offsets[i], buffer);
			buffer.append('[');
			parts[i].toPattern(buffer);
			buffer.append(']');
		}
		copyAndFixQuotes(pattern, lastOffset, pattern.length(), buffer);
		return buffer.toString();
	}

	/**
	 * 复制内容并填入对应的引号
	 * @param source 文本
	 * @param start 起始位置
	 * @param end 结束位置
	 * @param target 目标缓冲区
	 */
	protected final void copyAndFixQuotes(String source, int start, int end, Characters target) {
		for (int i = start; i < end; ++i) {
			char ch = source.charAt(i);
			switch (ch) {
			case '[':
				mixQuotes(target, "'['");
				break;
			case ']':
				mixQuotes(target, "']'");
				break;
			case '\n':
				mixQuotes(target, "'\n'");
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
	 * 创建子格式
	 * @param offset 模板起始位
	 * @param number 格式编号
	 * @param buffer 格式内容
	 */
	protected void makePart(int offset, int number, StringBuilder buffer) {
		if (number >= parts.length) {
			int n = Math.max(parts.length * 2, number + 1);
			int[] newOffsets = new int[n];
			PartFormat[] newParts = new PartFormat[n];
			System.arraycopy(parts, 0, newParts, 0, maxOffsets + 1);
			System.arraycopy(offsets, 0, newOffsets, 0, maxOffsets + 1);
			parts = newParts;
			offsets = newOffsets;
		}

		PartFormat part = new PartFormat(buffer.toString());
		maxOffsets = number;
		parts[number] = part;
		offsets[number] = offset;

		buffer.setLength(0);
	}

	/**
	 * 合并子文本
	 * @param parts 子文本集
	 * @return 合并后字符串
	 */
	protected String merge(TextPart[] parts) {
		final char defaultChar = ' ';
		int len = 0;
		for (TextPart part : parts) {
			len = Math.max(part.endIndex, len);
		}
		char[] chars = new char[len];
		for (int i = chars.length - 1; i >= 0; i--) {
			chars[i] = defaultChar;
		}

		for (TextPart part : parts) {
			for (int i = part.beginIndex; i < part.endIndex; i++) {
				char ch = part.part.charAt(i - part.beginIndex);
				if (chars[i] == defaultChar && ch != defaultChar) {
					chars[i] = ch;
				} else if (!Character.isUpperCase(ch) && Character.isUpperCase(chars[i])) {
					chars[i] = ch;
				}
			}
		}
		return new String(chars);
	}

	/**
	 * 文本区格式
	 * @author Demon 2011-10-18
	 */
	protected class PartFormat implements Serializable, Cloneable {

		private static final long serialVersionUID = -1469462457383963453L;

		private int flag = 0;
		private int[] parts = null;
		private int width = -1;
		private char placeholder = DEFAULT_PLACEHOLDER;

		/**
		 * 构造函数
		 * @param pattern
		 */
		protected PartFormat(String pattern) {
			init(pattern);
		}

		public boolean equals(Object o) {
			if (o == null) return false;
			if (o == this) return true;
			if (o.getClass() != this.getClass()) return false;
			PartFormat other = (PartFormat) o;
			return flag == other.flag && width == other.width && placeholder == other.placeholder && Arrayard.equals(parts, other.parts);
		}

		/**
		 * 格式化
		 * @param argument 文本参数
		 * @param buffer 结果缓冲区
		 */
		public void format(String argument, StringBuffer buffer) {
			String value = null;
			if (parts == null) {
				value = argument;
			} else {
				value = Stringure.subString(argument, parts[0], parts[1]);
			}

			if (needUppercase()) value = value.toUpperCase();

			int len = value.length();

			int work = width != -1 && width > len ? 2 : 1;
			for (int i = 0; i < work; i++) {
				if (work == 2 && ((i == 0 && leftJustifiable()) || (i == 1 && !leftJustifiable()))) {
					for (int j = width - len; j > 0; j--) {
						buffer.append(placeholder);
					}
				} else {
					buffer.append(value);
				}
			}
		}

		public int hashCode() {
			int h = 0;
			h ^= flag;
			h ^= width;
			h ^= placeholder;
			if (parts != null) {
				for (int p : parts)
					h ^= p;
			}
			return h;
		}

		/**
		 * 获取格式结果长度
		 * @return 长度
		 */
		public int length() {
			return parts == null || parts[1] == 0 ? -1 : parts[1] - parts[0];
		}

		/**
		 * 解析
		 * @param source 原文本
		 * @param offset 解析起始位置
		 * @param len 长度
		 * @return 子文本区
		 */
		public TextPart parse(String source, int offset, int len) {
			if (source.length() < offset + len) return null;

			String value = source.substring(offset, len + offset);
			if (width > len) return null;
			if (needUppercase() && !value.equals(value.toUpperCase())) return null;

			boolean cut = false;
			boolean match = false;
			if (width != -1 && width == len) {
				if (leftJustifiable()) {
					for (int i = 0; i < len; i++) {
						char ch = value.charAt(i);
						if (ch != placeholder) {
							if (i > 0) value = value.substring(i);
							cut = true;
							break;
						} else {
							match = true;
						}
					}
				} else {
					for (int i = len; i > 0; i--) {
						char ch = value.charAt(i - 1);
						if (ch != placeholder) {
							if (i < len) value = value.substring(0, i);
							cut = true;
							break;
						} else {
							match = true;
						}
					}
				}
				if (match && !cut) value = "";
			}

			return new TextPart(parts == null ? 0 : parts[0], value);
		}

		/**
		 * 转换为格式串
		 * @return 格式串
		 */
		public String toPattern() {
			return toPattern(Characters.createBuilder()).toString();
		}

		protected Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		/**
		 * 是否右对齐
		 * @return 是、否
		 */
		protected boolean leftJustifiable() {
			return (flag & LEFT_JUSTIFY) == LEFT_JUSTIFY;
		}

		/**
		 * 是否转大写
		 * @return 是、否
		 */
		protected boolean needUppercase() {
			return (flag & UPPERCASE) == UPPERCASE;
		}

		/**
		 * 转换格式串
		 * @param buffer 缓冲区
		 */
		protected Characters toPattern(Characters buffer) {
			if (needUppercase()) buffer.append('^');
			if (leftJustifiable()) buffer.append('-');
			if (parts == null && width > 0) {
				buffer.append(width);
			} else if (parts != null) {
				buffer.append('(').append(parts[0]).append(',');
				if (parts[1] == 0 && width > parts[1]) {
					buffer.append('@').append(width + parts[0]);
				} else {
					buffer.append(parts[1]);
				}
				buffer.append(')');
			}
			buffer.append('\'');
			if (placeholder != DEFAULT_PLACEHOLDER) {
				if ((placeholder < 0x0020) || (placeholder > 0x007e)) {
					Charician.ascII(placeholder, buffer);
				} else {
					buffer.append(placeholder);
				}
			}
			buffer.append('\'');

			return buffer;
		}

		/**
		 * 初始化
		 * @param pattern 格式串
		 */
		private void init(String pattern) {
			if (pattern == null || pattern.length() == 0) return;
			int len = pattern.length();

			StringBuilder[] nums = new StringBuilder[2];
			for (int i = 0; i < 2; i++) {
				nums[i] = new StringBuilder();
			}

			int n = 1;
			boolean inPlace = false;
			int flag = 0;
			char placeholder = DEFAULT_PLACEHOLDER;
			boolean isMinEnd = false;
			int group = 0;
			for (int i = 0; i < len; i++) {
				char ch = pattern.charAt(i);
				if (!inPlace) {
					switch (ch) {
					case '^': // 大写标识符
						flag |= UPPERCASE;
						break;
					case '-': // 左对齐标识符
						if (group == 0) {
							flag |= LEFT_JUSTIFY;
						} else {
							throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
						}
						break;

					case '(': // 定义子文本区间开始
						if (group == 0 && nums[1].length() == 0) {
							n = 0;
							group++;
						} else {
							throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
						}
						break;
					case ')': // 定义子文本区间结束
						if (group != 1) throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
						break;

					case ',': // 区间分隔
						if (n == 0) {
							n++;
						} else {
							throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
						}
						break;

					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9': // 定义长度
						nums[n].append(ch);
						break;

					case '\'': // 定义占位符
						inPlace = true;
						break;

					case '@': // 最小区间标识
						if (n == 1) {
							isMinEnd = true;
							break;
						}
					default:
						throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
					}
				} else { // 解析占位符值
					if (len == i + 1 && ch == '\'') {
						break;
					} else if (len == i + 2 && pattern.charAt(i + 1) == '\'') {
						placeholder = ch;
						break;
					} else if (len == i + 6 && pattern.charAt(i + 5) == '\'' && ch == '\\' && pattern.charAt(i + 1) == 'u') {
						String encode = pattern.substring(i, i + 5);
						Character value = Charician.ascII(encode);
						if (value == null) {
							throw new IllegalArgumentException("Malformed \\uxxxx encoding [" + encode + "]");
						} else {
							placeholder = value.charValue();
						}
						break;
					} else {
						throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
					}
				}
			}

			int begin = 0;
			int end = -1;
			if (nums[0].length() != 0) {
				try {
					begin = Integer.parseInt(nums[0].toString());
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
				}
			}

			if (nums[1].length() != 0) {
				try {
					end = Integer.parseInt(nums[1].toString());
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");
				}
			}

			if ((end > 0 && end < begin) || (!isMinEnd && end == begin))
				throw new IllegalArgumentException("Unmatched braces in the pattern [" + pattern + "]");

			if (begin > 0 || (!isMinEnd && end > 0)) parts = new int[] { begin, isMinEnd || end < 0 ? 0 : end };

			this.width = end > begin ? end - (begin < 0 ? 0 : begin) : width;
			this.placeholder = placeholder;
			this.flag = flag;
		}
	}

	/**
	 * 子文本区
	 * @author Demon 2011-10-18
	 */
	protected static class TextPart {

		public final int beginIndex;
		public final int endIndex;
		public final String part;

		/**
		 * 构造函数
		 * @param begin 起始位序
		 * @param part 子文本内容
		 */
		protected TextPart(int begin, String part) {
			Assertor.F.notNull(part);
			this.beginIndex = begin < 0 ? 0 : begin;
			this.part = part;
			this.endIndex = begin + part.length();
		}

		public int hashCode() {
			return part.hashCode();
		}

		public String toString() {
			return part;
		}
	}
}

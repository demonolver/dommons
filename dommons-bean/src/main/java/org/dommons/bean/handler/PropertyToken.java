/*
 * @(#)PropertyToken.java     2012-7-18
 */
package org.dommons.bean.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.collections.stack.LinkedStack;
import org.dommons.core.collections.stack.Stack;

/**
 * 属性分词
 * @author Demon 2012-7-18
 */
class PropertyToken {

	static final Pattern pattern = Pattern.compile("^\\[[0-9]*\\](?=$|\\[|\\.)");

	/**
	 * 解析内容
	 * @param content 内容
	 * @return 属性分词
	 */
	public static PropertyToken parse(String content) {
		return new TokenParser(content).parse();
	}

	private final String content;

	private final List<int[]> list;
	private int index;

	/**
	 * 构造函数
	 * @param content 全文
	 * @param list 分词索引集
	 */
	protected PropertyToken(String content, List<int[]> list) {
		this.content = content;
		this.list = list;
		index = -1;
	}

	/**
	 * 追加间隔
	 * @param builder 字符缓存区
	 * @return 字符缓存区
	 */
	public StringBuilder appendSpacing(StringBuilder builder) {
		if (index > 0) {
			int[] last = list.get(index - 1);
			int[] p = list.get(index);
			builder.append(content, last[1], p[0]);
		}
		return builder;
	}

	/**
	 * 获取子分词集
	 * @param from 开始序号
	 * @return 子分词集
	 */
	public PropertyToken child(int from) {
		return new PropertyToken(content, list.subList(from, list.size()));
	}

	/**
	 * 获取分词数量
	 * @return 数量
	 */
	public int count() {
		return list.size();
	}

	/**
	 * 分词截止索引
	 * @return 截止索引
	 */
	public int end() {
		return list.get(index)[1];
	}

	/**
	 * 获取全文
	 * @return 全文
	 */
	public String full() {
		int s = list.get(0)[0];
		return s == 0 ? content : content.substring(s);
	}

	/**
	 * 获取当前分词序号
	 * @return 序号
	 */
	public int index() {
		return index;
	}

	/**
	 * 下一个
	 * @return 是、否
	 */
	public boolean next() {
		return index < list.size() - 1 && ++index >= 0;
	}

	/**
	 * 获取分词
	 * @return 分词
	 */
	public String part() {
		int[] p = list.get(index);
		return content.substring(p[0], p[1]);
	}

	/**
	 * 上一个
	 * @return 是、否
	 */
	public boolean previous() {
		return index > 0 && --index < list.size();
	}

	/**
	 * 分词开始索引
	 * @return 开始索引
	 */
	public int start() {
		return list.get(index)[0];
	}

	/**
	 * 分词解析器
	 * @author Demon 2012-7-18
	 */
	static class TokenParser {

		private final String content;
		private final int len;

		private StringBuilder buf;
		private List<int[]> list;
		private int sp;
		private Stack<Integer> stack;

		public TokenParser(String content) {
			this.content = content;
			this.len = content == null ? 0 : content.length();
		}

		/**
		 * 解析
		 * @return 分词结果
		 */
		public PropertyToken parse() {
			reset();
			execute();
			return new PropertyToken(buf.toString(), list);
		}

		/**
		 * 执行解析
		 */
		protected void execute() {
			for (int i = 0; i < len; i++) {
				char c = content.charAt(i);

				switch (c) {
				case '\\':
					stack.add(i);
					if (i < len - 1) buf.append(content.charAt(++i));
					break;
				case '.':
					buf.append(c);
					add(sp, i);
					sp = i + 1;
					break;
				case '[':
					if (i < len - 1) {
						Matcher m = pattern.matcher(content.substring(i));
						if (m.find()) {
							buf.append(m.group());
							if (sp < i + m.start()) add(sp, i);
							add(i + m.start(), i + m.end());
							if (i + m.end() < len && content.charAt(i + m.end()) == '.') {
								buf.append('.');
								i += m.end();
							} else if (i + m.end() >= len) {
								return;
							} else {
								i += m.end() - 1;
							}
							sp = i + 1;
							break;
						}
					}
				default:
					buf.append(c);
					break;
				}
			}

			add(sp, len);
		}

		/**
		 * 添加分词
		 * @param s 开始位
		 * @param e 截止位
		 */
		void add(int s, int e) {
			if (!stack.isEmpty()) {
				int op = stack.peek();
				if (s > op) s -= stack.size();
			}
			list.add(new int[] { s, e - stack.size() });
		}

		/**
		 * 重置
		 */
		private void reset() {
			buf = new StringBuilder(len);
			list = new ArrayList();
			stack = new LinkedStack();
			sp = 0;
		}
	}
}

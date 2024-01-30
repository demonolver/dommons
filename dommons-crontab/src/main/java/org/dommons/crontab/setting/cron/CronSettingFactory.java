/*
 * @(#)CronSettingFactory.java     2013-10-17
 */
package org.dommons.crontab.setting.cron;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.string.Stringure;
import org.dommons.crontab.setting.Cronset;
import org.dommons.crontab.setting.CronsetFactory;

/**
 * cron 设置解析工厂
 * @author Demon 2013-10-17
 */
class CronSettingFactory extends CronsetFactory {

	private static final Map<String, Pattern> patterns = new ConcurrentSoftMap();

	/**
	 * 编译正则
	 * @param regex 表达式
	 * @return 正则
	 */
	protected static Pattern pattern(String regex) {
		return pattern(regex, 0);
	}

	/**
	 * 编译正则
	 * @param regex 表达式
	 * @param flags 匹配模式
	 * @return 正则
	 */
	protected static Pattern pattern(String regex, int flags) {
		String key = Stringure.join(':', flags, regex);
		Pattern pattern = patterns.get(key);
		if (pattern == null) patterns.put(key, pattern = Pattern.compile(regex, flags));
		return pattern;
	}

	/**
	 * 解析数值设定
	 * @param set 值集
	 * @param s 设定内容
	 * @param min 最小值
	 * @param max 最大值
	 * @return 是否合法
	 */
	static boolean numericSet(TreeSet<Integer> set, String s, int min, int max) {
		return numericSet(set, s, min, max, max);
	}

	/**
	 * 解析数值设定
	 * @param set 值集
	 * @param s 设定内容
	 * @param min 最小值
	 * @param max 最大值
	 * @param ed 截止值
	 * @return 是否合法
	 */
	static boolean numericSet(TreeSet<Integer> set, String s, int min, int max, int ed) {
		if ("*".equals(s)) return inner(set, min, ed, 1, max);

		if (s.contains(",")) { // 多段值集
			for (String p : Stringure.split(s, ','))
				numericSet(set, p, min, max, ed);
			return true;
		} else if (pattern("[0-9]+").matcher(s).matches()) { // 单项值
			int n = Integer.parseInt(s);
			if (n >= max) n = (n + min) % max + min;
			if (n >= min) set.add(n);
			return true;
		}

		Matcher m = pattern("((([0-9]+)(\\-([0-9]+))?)|\\*)(\\/([1-9][0-9]*))?").matcher(s); // 区间值
		if (!m.matches()) return false;
		int start = min, end = ed - 1, step = 1;
		String tx = m.group(7);
		if (tx != null) {
			int n = Integer.parseInt(tx);
			if (n > 0) step = n;
		}

		String sx = m.group(3);
		if (sx != null) {
			int n = numeric(sx, min, max);
			if (n > min) start = n;
		}

		String ex = m.group(5);
		if (ex != null) {
			int n = numeric(ex, min, max);
			if (n >= min && n < max) end = n;
		}

		if (end < start) return inner(set, start, end + max + 1, step, min, max);
		else return inner(set, start, end + 1, step, min, max);
	}

	/**
	 * 导入值集
	 * @param list 值集
	 * @param s 开始
	 * @param e 截止
	 * @param l 步长
	 * @param z 区间
	 * @return 是否成功
	 */
	private static boolean inner(Collection<Integer> list, int s, int e, int l, int z) {
		return inner(list, s, e, l, 0, z);
	}

	/**
	 * 导入值集
	 * @param list 值集
	 * @param s 开始
	 * @param e 截止
	 * @param l 步长
	 * @param a 区间起始
	 * @param z 区间
	 * @return 是否成功
	 */
	private static boolean inner(Collection<Integer> list, int s, int e, int l, int a, int z) {
		if (list == null) return false;
		for (int i = s; i < e; i += l) {
			int x = i < z ? i : i % z;
			if (x < a) continue;
			if (!list.add(Integer.valueOf(x))) return false;
		}
		return true;
	}

	/**
	 * 解析数值
	 * @param x 数值
	 * @param min 最小值
	 * @param max 最大值
	 * @return 数值
	 */
	private static int numeric(String x, int min, int max) {
		int n = Integer.parseInt(x);
		if (n >= max) n = (n + min) % max + min;
		return n;
	}

	/**
	 * 类 linux crontab 表达式解析器
	 * @author Demon 2013-10-15
	 */
	static class CrontabParser extends SettingParser {

		protected static final int[] fields = { -1, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH,
				Calendar.MONTH, Calendar.DAY_OF_WEEK };

		/**
		 * 解析表达式
		 * @param cs 时间设定
		 * @param m 匹配内容
		 */
		protected static boolean build(CrontabSetting cs, Matcher m) {
			cs.reset();

			for (int i = 0, tc = fields.length; i < tc; i++) {
				String expr = Stringure.trim(m.group(i + 1)).toUpperCase();
				switch (fields[i]) {
				case Calendar.SECOND:
					if (!expr.isEmpty() && !numericSet(cs.seconds, expr, 0, 60)) return false;
					break;
				case Calendar.MINUTE:
					if (!numericSet(cs.minutes, expr, 0, 60)) return false;
					break;
				case Calendar.HOUR_OF_DAY:
					if (!numericSet(cs.hours, expr, 0, 24)) return false;
					break;
				case Calendar.DAY_OF_MONTH:
					if (!numericSet(cs.daysOfMonth, expr, 1, 32)) return false;
					break;
				case Calendar.MONTH:
					if (!numericSet(cs.months, expr, 0, 12)) return false;
					break;
				case Calendar.DAY_OF_WEEK:
					if (!numericSet(cs.daysOfWeek, expr, 0, 7, 8)) return false;
					break;
				case -1:
					break;
				default:
					return false;
				}
			}
			return true;
		}

		/**
		 * 编译表达式
		 * @param cs 时间设定
		 * @param content 表达式内容
		 */
		protected static void build(CrontabSetting cs, String content) {
			Matcher m = pattern().matcher(content);
			if (!m.find()) build(cs, m);
		}

		/**
		 * 获取格式正则
		 * @return 格式正则
		 */
		protected static Pattern pattern() {
			// http://blog.csdn.net/sipsir/article/details/3973713
			return CronSettingFactory.pattern("(([0-9\\*\\-\\/\\,]+)\\s+)?"
					+ "([0-9\\*\\-\\/\\,]+)\\s+([0-9\\*\\-\\/\\,]+)\\s+([0-9\\*\\-\\/\\,]+)\\s+([0-9\\*\\-\\/\\,]+)\\s+([0-9\\*\\-\\/\\,]+)");
		}

		public CrontabParser() {
			super();
		}

		public Cronset parse(String content) {
			Matcher m = pattern().matcher(content);
			if (!m.matches() || content.contains("?")) return null;
			CrontabSetting cs = new CrontabSetting(content);
			return build(cs, m) ? cs : null;
		}
	}

	/**
	 * 类 spring quartz cronExpression 表达式解析器
	 * @author Demon 2013-10-15
	 */
	static class ExpressionParser extends SettingParser implements CalenderAlias {

		protected static final int[] fields = { Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH,
				Calendar.MONTH, Calendar.DAY_OF_WEEK, -1, Calendar.YEAR };

		/**
		 * 解析表达式
		 * @param es 时间设定
		 * @param m 匹配内容
		 */
		protected static boolean build(ExpressionSetting es, Matcher m) {
			es.reset();

			for (int i = 0; i < fields.length; i++) {
				String expr = Stringure.trim(m.group(i + 1)).toUpperCase();
				switch (fields[i]) {
				case Calendar.SECOND:
					if (!numericSet(es.seconds, expr, 0, 60)) return false;
					break;
				case Calendar.MINUTE:
					if (!numericSet(es.minutes, expr, 0, 60)) return false;
					break;
				case Calendar.HOUR_OF_DAY:
					if (!numericSet(es.hours, expr, 0, 24)) return false;
					break;
				case Calendar.DAY_OF_MONTH:
					if (!dayMonthSet(es, expr)) return false;
					break;
				case Calendar.MONTH:
					if (!monthSet(es.months, expr)) return false;
					break;
				case Calendar.DAY_OF_WEEK:
					if (!dayWeekSet(es, expr)) return false;
					break;
				case Calendar.YEAR:
					if (expr.length() < 1) expr = "*";
					if (!numericSet(es.years, expr, 1970, CronSetting.MAX_YEAR)) return false;
					break;
				case -1:
					break;
				default:
					return false;
				}
			}

			TreeSet<Integer> dow = es.daysOfWeek, dom = es.daysOfMonth;

			// 日期和星期不可同时指定
			return !(dom.contains(Integer.valueOf(ExpressionSetting.NO_SPEC)) && dow.contains(Integer.valueOf(ExpressionSetting.NO_SPEC)));
		}

		/**
		 * 编译表达式
		 * @param es 时间设定
		 * @param content 表达式内容
		 */
		protected static void build(ExpressionSetting es, String content) {
			Matcher m = pattern().matcher(content);
			if (!m.find()) build(es, m);
		}

		/**
		 * 解析月-日定义
		 * @param es 时间设定
		 * @param cx 内容
		 * @return 是否合法
		 */
		static boolean dayMonthSet(ExpressionSetting es, String cx) {
			if ("?".equals(cx)) return es.daysOfMonth.add(Integer.valueOf(ExpressionSetting.NO_SPEC));
			Matcher m = CronSettingFactory.pattern("([0-9]+)?[LW]{1,2}").matcher(cx);
			if (m.matches()) {
				if (cx.contains("W")) es.nearestWeekday = true;
				int l = 0;
				String lx = m.group(1);
				if (lx != null) l = Integer.parseInt(lx);
				if (cx.contains("L")) {
					if (l < 31) es.lastdayOffset = l;
					else return false;
					es.lastdayOfMonth = true;
				} else {
					if (l > 0 && l <= 31) es.daysOfMonth.add(Integer.valueOf(l));
					else return false;
				}
				return true;
			}

			return numericSet(es.daysOfMonth, cx, 1, 32);
		}

		/**
		 * 解析周-日定义
		 * @param es 时间设定
		 * @param cx 内容
		 * @return 是否合法
		 */
		static boolean dayWeekSet(ExpressionSetting es, String cx) {
			cx = replaceNumeric(cx, days, -1);
			if (cx == null) return false;
			if ("?".equals(cx)) return es.daysOfWeek.add(Integer.valueOf(ExpressionSetting.NO_SPEC));
			if (cx.endsWith("L")) {
				cx = Stringure.subString(cx, 0, -1);
				es.lastdayOfWeek = true;
			} else {
				Matcher m = CronSettingFactory.pattern("([0-9\\*\\,\\-\\/]+)\\#([1-5])").matcher(cx);
				if (m.matches()) {
					cx = m.group(1);
					es.nthdayOfWeek = Integer.parseInt(m.group(2));
				}
			}
			return numericSet(es.daysOfWeek, cx, 0, 7, 8);
		}

		/**
		 * 解析月份定义
		 * @param set 值集
		 * @param cx 内容
		 * @return 是否合法
		 */
		static boolean monthSet(TreeSet<Integer> set, String cx) {
			cx = replaceNumeric(cx, months, 1);
			if (cx == null) return false;
			return numericSet(set, cx, 1, 13);
		}

		/**
		 * 获取表达式正则
		 * @return 正则
		 */
		static Pattern pattern() {
			// https://blog.csdn.net/Mr_EvanChen/article/details/100107599
			return CronSettingFactory.pattern("([0-9\\*\\,\\-\\/]+)\\s+([0-9\\*\\,\\-\\/]+)\\s+([0-9\\*\\,\\-\\/]+)"
					+ "\\s+([0-9\\*\\,\\-\\/\\?LW]+)\\s+([0-9a-z\\*\\,\\-\\/]+)\\s+([0-7a-z\\*\\,\\-\\/\\?#]+)(\\s+([0-9\\*\\,\\-\\/]+))?",
				Pattern.CASE_INSENSITIVE);
		}

		/**
		 * 获取对应数字
		 * @param s 内容
		 * @param map 映射表
		 * @return 数字
		 */
		private static int numeric(String s, Map<String, Integer> map) {
			Integer integer = map == null ? null : map.get(s);
			return (integer == null) ? -1 : integer;
		}

		/**
		 * 替换枚举定义数值
		 * @param cx 内容
		 * @param map 定义映射表
		 * @param add 附加数
		 * @return 新内容
		 */
		private static String replaceNumeric(String cx, Map<String, Integer> map, int add) {
			Matcher m = Pattern.compile("[A-Z]{3}").matcher(cx);
			if (m.find()) {
				int p = 0, len = cx.length();
				StringBuffer buf = new StringBuffer(len);
				do {
					String v = m.group();
					int n = numeric(v, map);

					int s = m.start(), e = m.end();
					if (n < 0) {
						return null;
					} else {
						if (p < s) buf.append(cx, p, s);
						buf.append(n + add);
					}
					p = e;
				} while (m.find());
				if (p < cx.length()) buf.append(cx, p, cx.length());
				cx = buf.toString();
			}
			return cx;
		}

		public ExpressionParser() {
			super();
		}

		public Cronset parse(String content) {
			Matcher m = pattern().matcher(content);
			if (!m.matches() || !content.contains("?")) return null;
			ExpressionSetting es = new ExpressionSetting(content);
			return build(es, m) ? es : null;
		}
	}
}

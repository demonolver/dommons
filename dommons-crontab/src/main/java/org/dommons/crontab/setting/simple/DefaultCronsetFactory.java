/*
 * @(#)DefaultCronsetFactory.java     2013-10-17
 */
package org.dommons.crontab.setting.simple;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.crontab.setting.Cronset;
import org.dommons.crontab.setting.CronsetFactory;

/**
 * 默认定时设定工厂
 * @author Demon 2013-10-17
 */
class DefaultCronsetFactory extends CronsetFactory {

	/**
	 * 默认定时时间设定
	 * @author Demon 2013-10-15
	 */
	static class DefaultCronset implements Cronset {

		private static final long serialVersionUID = -1525359171599264717L;

		protected CronsetPart start;
		protected CronsetPart over;
		protected CronsetPart step;
		protected CronsetPart zone;

		private long first;

		public long time(long last, TimeZone tz) {
			long time = 0;
			if (last < 10000) first = time = start != null ? start.time(last = System.currentTimeMillis(), tz) : System.currentTimeMillis();
			else if (step != null) time = step.time(last, tz);

			if (over != null) {
				long e = over.time(first > 0 ? first : last, tz);
				if (e > 0 && e < time) return 0;
			}

			if (zone != null) {
				for (long l = time, t = 0, i = 0; i < 50; l = t, i++) {
					t = zone.time(l, tz);
					if (t == 0 || t >= time) return t;
					else if (l == t) break;
				}
			}

			return time;
		}
	}

	/**
	 * 固定时间设定
	 * @author Demon 2013-10-17
	 */
	static class FixedPart implements CronsetPart, CalenderAlias {

		private static final long serialVersionUID = -7191365937938653259L;

		protected static final int[][] fields = {
				{ Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND },
				{ Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_MONTH, Calendar.DAY_OF_WEEK, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
						Calendar.SECOND } };

		private static Ref<Pattern> ref;

		/**
		 * 编译表达式
		 * @param content 表达式内容
		 * @return 固定时间设定
		 */
		protected static FixedPart complie(String content) {
			return complie(content, false);
		}

		/**
		 * 编译表达式
		 * @param content 表达式内容
		 * @param hour 单数值指定为小时
		 * @return 固定时间设定
		 */
		protected static FixedPart complie(String content, boolean hour) {
			Matcher m = pattern().matcher(content);
			if (!m.matches()) return null;
			FixedPart fp = new FixedPart();
			if (m.group(2) != null) fp.val = vals(fields[0].length);
			else fp.val = vals(fields[1].length);
			if (!year(fp, m, 4, 20)) return null;
			if (!month(fp, m, 6, 22)) return null;
			if (!date(fp, m, 7, 23)) return null;

			if (!weekday(fp, m, 8, 24)) return null;
			if (!monthAlias(fp, m, 10, 26)) return null;
			if (!weekth(fp, m, 11, 27)) return null;
			if (!year(fp, m, 13, 29)) return null;

			if (!hour(fp, m, hour)) return null;
			if (!minute(fp, m)) return null;
			if (!second(fp, m)) return null;
			return fp;
		}

		/**
		 * 解析日期值设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 值序号集
		 * @return 是否合法
		 */
		static boolean date(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String dx = m.group(i);
				if (dx != null) {
					int n = Integer.parseInt(dx);
					if (m.group(i - 3) != null && m.group(i - 1) == null) {
						if (n >= 1 && n < 13) fp.val[1] = n - 1;
						else return false;
					} else {
						if (n >= 1 && n < 32) fp.val[2] = n;
						else return false;
					}
				}
			}
			return true;
		}

		/**
		 * 解析小时值设定
		 * @param fp 设定
		 * @param m 内容
		 * @param hour 单数值指定为小时
		 * @return 是否合法
		 */
		static boolean hour(FixedPart fp, Matcher m, boolean hour) {
			String hx = m.group(14);
			int n = 0, l = fp.val.length;
			if (hx != null) n = Integer.parseInt(hx);
			if (hour || m.group(1) != null || m.group(16) != null) {
				if (n >= 0 && n < 24) fp.val[l - 3] = n;
				else return false;
			} else { // 独单指定设置分钟
				if (n >= 0 && n < 60) fp.val[l - 2] = n;
				else return false;
			}
			return true;
		}

		/**
		 * 解析分钟值设定
		 * @param fp 设定
		 * @param m 内容
		 * @return 是否合法
		 */
		static boolean minute(FixedPart fp, Matcher m) {
			String mx = m.group(16);
			int i = fp.val.length - 2;
			if (mx != null) {
				int n = Integer.parseInt(mx);
				if (n >= 0 && n < 60) fp.val[i] = n;
				else return false;
			} else if (fp.val[i] == -1) {
				fp.val[i] = 0;
			}
			return true;
		}

		/**
		 * 解析月份值设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 值序号集
		 * @return 是否合法
		 */
		static boolean month(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String mx = m.group(i);
				if (mx != null) {
					int n = Integer.parseInt(mx);
					if (n >= 1 && n < 13) fp.val[1] = n - 1;
					else return false;
				}
			}
			return true;
		}

		/**
		 * 解析月份别名设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 值序号集
		 * @return 是否合法
		 */
		static boolean monthAlias(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String mx = m.group(i);
				if (mx == null) continue;
				Integer d = months.get(mx.toUpperCase());
				if (d == null) return false;
				fp.val[1] = d.intValue();
				return true;
			}
			return true;
		}

		/**
		 * 解析秒值设定
		 * @param fp 设定
		 * @param m 内容
		 * @return 是否合法
		 */
		static boolean second(FixedPart fp, Matcher m) {
			String sx = m.group(18);
			int i = fp.val.length - 1;
			if (sx != null) {
				int n = Integer.parseInt(sx);
				if (n >= 0 && n < 60) fp.val[i] = n;
				else return false;
			} else {
				fp.val[i] = 0;
			}
			return true;
		}

		/**
		 * 解析周-日设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 值序号集
		 * @return 是否合法
		 */
		static boolean weekday(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String wx = m.group(i);
				if (wx == null) continue;
				String x = wx.toUpperCase();
				Integer d = days.get(x);
				if (d != null) {
					fp.val[3] = d.intValue();
					return true;
				}
				d = months.get(x);
				if (d != null && m.group(i + 1) == null) {
					fp.val[1] = d.intValue();
					return true;
				}
				return false;
			}
			return true;
		}

		/**
		 * 解析周数设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 值序号集
		 * @return 是否合法
		 */
		static boolean weekth(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String wx = m.group(i);
				if (wx == null) continue;
				String x = wx.toUpperCase();
				if (x.equals("LAST")) {
					fp.val[2] = 6;
				} else {
					int n = Integer.parseInt(x);
					if (n >= 1 && n < 6) fp.val[2] = n;
					else return false;
				}
			}
			return true;
		}

		/**
		 * 获取正则
		 * @return 正则
		 */
		private static Pattern pattern() {
			Pattern pattern = ref == null ? null : ref.get();
			if (pattern == null) ref = new Softref(pattern = Pattern.compile(regex(true), Pattern.CASE_INSENSITIVE));
			return pattern;
		}

		private static String regex(boolean ele) {
			StringBuilder buf = new StringBuilder(64);
			buf.append("(((");
			regex(buf, "[0-9]{4}", ele);
			buf.append("\\-)?(");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append("\\-)?");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append('|');
			regex(buf, "[A-Z]{3}", ele);
			buf.append("(\\,");
			regex(buf, "[A-Z]{3}", ele);
			buf.append("?\\s*([1-5]|LAST)?)?");
			buf.append("(\\,");
			regex(buf, "[0-9]{4}", ele);
			buf.append(")?)\\s+)?");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append("(\\:");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append(")?");
			buf.append("(\\:");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append(")?|");
			buf.append('(');
			regex(buf, "[0-9]{4}", ele);
			buf.append("\\-)?(");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append("\\-)?");
			regex(buf, "[0-9]{1,2}", ele);
			buf.append('|');
			regex(buf, "[A-Z]{3}", ele);
			buf.append("(\\,([A-Z]{3})?\\s+?([1-5]|LAST)?)?");
			buf.append("(\\,");
			regex(buf, "[0-9]{4}", ele);
			buf.append(")?");
			return buf.toString();
		}

		private static StringBuilder regex(StringBuilder buf, String part, boolean ele) {
			if (ele) buf.append('(');
			buf.append(part);
			if (ele) buf.append(')');
			return buf;
		}

		/**
		 * 创建值集
		 * @param length 值集数量
		 * @return 值集
		 */
		private static int[] vals(int length) {
			int[] vs = new int[length];
			for (int i = 0; i < length; i++)
				vs[i] = -1;
			return vs;
		}

		/**
		 * 解析年份值设定
		 * @param fp 设定
		 * @param m 内容
		 * @param index 内容序号集
		 * @return 是否合法
		 */
		private static boolean year(FixedPart fp, Matcher m, int... index) {
			for (int i : index) {
				String yx = m.group(i);
				if (yx != null) {
					int n = Integer.parseInt(yx);
					if (n >= 1970 && n < 2199) fp.val[0] = n;
					else return false;
					return true;
				}
			}
			return true;
		}

		private int[] val;

		protected FixedPart() {
		}

		public long time(long base, TimeZone tz) {
			Calendar cal = Calendar.getInstance(tz);
			cal.setTimeInMillis(base + 1000);
			cal.clear(Calendar.MILLISECOND);
			for (int x = 0; x < fields.length; x++) {
				if (val.length != fields[x].length) continue;
				int[] fs = fields[x];
				for (int i = fs.length - 1; i >= 0; i--) {
					if (val[i] < 0) continue;
					int v = cal.get(fs[i]), p = -1;
					if (i > 0) p = cal.get(fs[i - 1]);
					if (fs[i] != Calendar.WEEK_OF_MONTH || val[i] <= 5) {
						cal.set(fs[i], val[i]);
					} else {
						cal.set(fs[i], cal.getActualMaximum(fs[i]));
						if (p != cal.get(fs[i - 1])) cal.add(Calendar.WEEK_OF_MONTH, -1);
					}
					if (p > -1 && p == cal.get(fs[i - 1]) && v > val[i]) {
						cal.add(fs[i - 1], 1);
						if (fs[i] == Calendar.WEEK_OF_MONTH) i += 2;
					}
				}
			}
			return cal.getTimeInMillis();
		}
	}

	/**
	 * 简单表达式解析器
	 * @author Demon 2013-10-15
	 */
	static class PatternParser extends SettingParser {

		private static Ref<Pattern> ref;

		protected static Pattern pattern() {
			Pattern pattern = ref == null ? null : ref.get();
			if (pattern == null) {
				String p = FixedPart.regex(false);
				pattern = Pattern.compile("(START\\s+(AT\\s+(" + p + ")|DELAY\\s+([1-9][0-9]*)\\s+([A-Z]+))\\s+FOR\\s+)?"
						+ "EVERY\\s+(([1-9][0-9]*)\\s+([A-Z]+)|(" + p + "))" + "(\\s+BETWEEN\\s+(" + p + ")\\s+AND\\s+(" + p + "))?"
						+ "(\\s+END\\s+(AT\\s+(" + p + ")|DELAY\\s+([1-9][0-9]*)\\s+([A-Z]+)))?",
					Pattern.CASE_INSENSITIVE);
				ref = new Softref(pattern);
			}
			return pattern;
		}

		public PatternParser() {
			super();
		}

		public Cronset parse(String content) {
			// (START (AT 05:00)|(DELAY 5 MINUTES) FOR )?EVERY (5 HOURS)|(05:00)|(WED,JUL 4,2012 07:00) BETWEEN
			Matcher m = pattern().matcher(content);
			if (!m.matches()) return null;
			try {
				DefaultCronset dc = new DefaultCronset();
				if (m.group(1) != null) { // start
					if (m.group(3) != null) dc.start = FixedPart.complie(m.group(3), false);
					else dc.start = new LengthPart(Long.parseLong(m.group(19)), TimeUnit.valueOf(m.group(20).toUpperCase()));;
				}
				{ // every
					if (m.group(22) != null)
						dc.step = new LengthPart(Long.parseLong(m.group(22)), TimeUnit.valueOf(m.group(23).toUpperCase()));
					else dc.step = FixedPart.complie(m.group(24), false);

					if (dc.start == null) dc.start = dc.step;
				}
				if (m.group(40) != null) { // zone
					dc.zone = new ZonePart(m.group(41), m.group(57));
				}

				if (m.group(73) != null) { // end
					if (m.group(75) != null) dc.over = FixedPart.complie(m.group(75), false);
					else dc.over = new LengthPart(Long.parseLong(m.group(91)), TimeUnit.valueOf(m.group(92).toUpperCase()));;
				}
				return dc;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}

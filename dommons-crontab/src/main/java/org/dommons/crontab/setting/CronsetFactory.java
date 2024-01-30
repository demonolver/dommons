/*
 * @(#)CronsetFactory.java     2013-10-17
 */
package org.dommons.crontab.setting;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dommons.core.Silewarner;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.dom.XDomor;
import org.dommons.dom.bean.XElement;
import org.dommons.io.Pathfinder;

/**
 * 定时设置工厂
 * @author Demon 2013-10-17
 */
public abstract class CronsetFactory {

	static {
		CalenderAlias.months.put("JAN", Calendar.JANUARY);
		CalenderAlias.months.put("FEB", Calendar.FEBRUARY);
		CalenderAlias.months.put("MAR", Calendar.MARCH);
		CalenderAlias.months.put("APR", Calendar.APRIL);
		CalenderAlias.months.put("MAY", Calendar.MAY);
		CalenderAlias.months.put("JUN", Calendar.JUNE);
		CalenderAlias.months.put("JUL", Calendar.JULY);
		CalenderAlias.months.put("AUG", Calendar.AUGUST);
		CalenderAlias.months.put("SEP", Calendar.SEPTEMBER);
		CalenderAlias.months.put("OCT", Calendar.OCTOBER);
		CalenderAlias.months.put("NOV", Calendar.NOVEMBER);
		CalenderAlias.months.put("DEC", Calendar.DECEMBER);

		CalenderAlias.days.put("SUN", Calendar.SUNDAY);
		CalenderAlias.days.put("MON", Calendar.MONDAY);
		CalenderAlias.days.put("TUE", Calendar.TUESDAY);
		CalenderAlias.days.put("WED", Calendar.WEDNESDAY);
		CalenderAlias.days.put("THU", Calendar.THURSDAY);
		CalenderAlias.days.put("FRI", Calendar.FRIDAY);
		CalenderAlias.days.put("SAT", Calendar.SATURDAY);
	}

	/**
	 * 解析表达式
	 * <br>&emsp;支持 UNIX Cron 表达式
	 * <br>&emsp;支持 Spring Scheduled Cron 表达式 
	 * @param expression 表达式
	 * @return 时间设置
	 */
	public static Cronset parse(String expression) {
		if (expression == null) return null;
		Collection<SettingParser> parsers = SettingParser.parsers();
		if (parsers != null) {
			for (SettingParser parser : parsers) {
				Cronset cs = parser.parse(expression);
				if (cs != null) return cs;
			}
		}
		return null;
	}

	/**
	 * 时间别名
	 * @author Demon 2013-10-18
	 */
	protected static interface CalenderAlias {
		public final Map<String, Integer> months = new HashMap(15);
		public final Map<String, Integer> days = new HashMap(10);
	}

	/**
	 * 时间设置解析器
	 * @author Demon 2013-10-14
	 */
	protected static abstract class SettingParser {

		static Ref<Collection<SettingParser>> ref;

		/**
		 * 获取解析器集
		 * @return 解析器集
		 */
		public static Collection<SettingParser> parsers() {
			Collection<SettingParser> ps = ref == null ? null : ref.get();
			if (ps == null) ref = new Softref(ps = load());
			return ps;
		}

		/**
		 * 加载解析器集
		 * @return 解析器集
		 */
		protected static Collection<SettingParser> load() {
			Collection<SettingParser> ps = new ArrayList();
			URL url = file();
			if (url != null) {
				try {
					XElement root = XDomor.parse(url).rootElement();
					inner(root.elements("setting"), ps);
				} catch (Throwable t) {
					Silewarner.warn(SettingParser.class, "load parsers fail", t);
				}
			}
			return ps;
		}

		/**
		 * 获取配置文件
		 * @return 配置文件路径
		 */
		static URL file() {
			URL url = Pathfinder.getResource("cron.setting.xml");
			if (url == null) url = Pathfinder.getResource(Cronset.class, "cron.setting.xml");
			return url;
		}

		/**
		 * 导入解析器集
		 * @param elements 配置节点集
		 * @param ps 解析器集
		 */
		static void inner(Collection<XElement> elements, Collection<SettingParser> ps) {
			if (elements == null) return;
			for (Iterator<XElement> it = elements.iterator(); it.hasNext(); it.remove()) {
				SettingParser parser = load(it.next());
				if (parser != null) ps.add(parser);
			}
		}

		/**
		 * 加载解析器类
		 * @param cn 类名
		 * @return 解析器类
		 */
		static Class load(String cn) {
			try {
				return Class.forName(cn, false, Cronset.class.getClassLoader());
			} catch (ClassNotFoundException e) {
			}
			try {
				return Class.forName(cn, false, Thread.currentThread().getContextClassLoader());
			} catch (ClassNotFoundException e) {
			}
			return null;
		}

		/**
		 * 加载解析器配置
		 * @param ele 配置节点
		 * @return 解析器
		 */
		static SettingParser load(XElement ele) {
			String cn = Stringure.trim(ele.attribute("parser"));
			if (cn.length() < 1) return null;
			Class cls = load(cn);
			if (cls == null || !SettingParser.class.isAssignableFrom(cls)) return null;
			try {
				Constructor<SettingParser> c = cls.getConstructor();
				c.setAccessible(true);
				return c.newInstance();
			} catch (Throwable t) {
				Silewarner.warn(SettingParser.class, "load parser '" + cls.getName() + "' fail", t);
			}
			return null;
		}

		/**
		 * 解析表达式
		 * @param content 表达式内容
		 * @return 时间设置 返回 <code>null</code> 表示表达式不符
		 */
		public abstract Cronset parse(String content);
	}
}

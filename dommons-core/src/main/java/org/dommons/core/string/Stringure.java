/*
 * @(#)Stringure.java     2011-10-18
 */
package org.dommons.core.string;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.dommons.core.Assertor;
import org.dommons.core.Environments;
import org.dommons.core.collections.enumeration.ArrayEnumeration;
import org.dommons.core.collections.stack.LinkedStack;
import org.dommons.core.collections.stack.Stack;
import org.dommons.core.convert.Converter;
import org.dommons.core.number.Numeric;
import org.dommons.core.util.Arrayard;

/**
 * 字符串工具类 学习自<code>apache</code>的<code>common-lang</code>包中<code>StringUtils</code>类，提供对<code>String</code>类中常用方法的封装，方便使用
 * @author Demon 2011-10-18
 */
public final class Stringure {

	/** 空字符串 */
	public static final String empty = "";

	/** 空数组 */
	public static final String[] emarr = new String[0];
	/** 空字节数组 */
	public static final byte[] embytes = new byte[0];

	/** UTF-8 字符集 */
	public static Charset utf_8 = charset("utf-8");

	/**
	 * 获取字符集
	 * @param defaultable 允许最后使用默认字符集
	 * @param names 字符集名
	 * @return 字符集
	 * @throws UnsupportedCharsetException
	 */
	public static Charset charset(boolean defaultable, String... names) throws UnsupportedCharsetException {
		Queue<RuntimeException> res = new LinkedList();
		if (names != null) {
			for (String n : names) {
				try {
					if (!Stringure.isEmpty(n)) return Charset.forName(n);
				} catch (RuntimeException e) {
					res.add(e);
				}
			}
		}
		if (defaultable) return Environments.defaultCharset();
		else if (res.size() == 1) throw res.poll();
		else throw new UnsupportedCharsetException(Stringure.join(',', names));
	}

	/**
	 * 获取字符集
	 * @param names 字符集名
	 * @return 字符集
	 * @throws UnsupportedCharsetException
	 */
	public static Charset charset(String... names) throws UnsupportedCharsetException {
		return charset(false, names);
	}

	/**
	 * 串联字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.concat(&quot;a&quot;, null, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td
	 * headers='r'>&quot;ab&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.concat(&quot;a&quot;, &quot;b&quot;,
	 * &quot;c&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;abc&quot;;</td>
	 * </tr>
	 * </table>
	 * @param parts 子字符串集
	 * @return 串联后字符串
	 */
	public static String concat(CharSequence... parts) {
		return concat(parts, 0, 0);
	}

	/**
	 * 串联字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.concat(&quot;a&quot;, null, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td
	 * headers='r'>&quot;ab&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.concat(&quot;a&quot;, &quot;b&quot;,
	 * &quot;c&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;abc&quot;;</td>
	 * </tr>
	 * </table>
	 * @param strs 字符串集
	 * @return 串联后字符串
	 */
	public static String concat(Object... strs) {
		return concat(strs, 0, 0);
	}

	/**
	 * 串联字符串
	 * @param strs 字符串集
	 * @param start 起始位 <code>0</code>表示为集合起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为集合结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @return 串联后字符串
	 */
	public static String concat(Object[] strs, int start, int end) {
		return join(null, strs, start, end);
	}

	/**
	 * 判断子字符串是否存在
	 * <table border='0px'>
	 * <tr>
	 * <th id='d' width='20' />
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.contains("abc", "ab")</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>true;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.contains(null,null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.contains("abc", "de")
	 * </td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.contains("", "de")</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false;</td>
	 * </tr>
	 * </table>
	 * @param parent 父字符串
	 * @param child 子字符串
	 * @return 是否存在
	 */
	public static boolean contains(CharSequence parent, CharSequence child) {
		if (parent == null || parent.length() < 1) return false;
		else if (child == null || child.length() < 1) return false;
		int l1 = parent.length(), l2 = child.length();
		char head = child.charAt(0);
		loop: for (int i = 0; i <= l1 - l2; i++) {
			if (parent.charAt(i) != head) continue;
			for (int j = 1; j < l2; j++) {
				if (parent.charAt(i + j) != child.charAt(j)) continue loop;
			}
			return true;
		}
		return false;
	}

	/**
	 * 转换字符串中变量值，如 :
	 * <p>
	 * ${java.home}/bin/javaw.exe -> D:/java/bin/javaw.exe
	 * </p>
	 * @param str 字符串
	 * @return 转换后字符串
	 */
	public static String convertSystemVariables(CharSequence str) {
		return convertVariables(str, Environments.getProperties());
	}

	/**
	 * 转换字符串中变量值，如 :
	 * <p>
	 * <table border=1 summary="variables">
	 * <tr>
	 * <th id="k">key
	 * <th id="v">value
	 * <tr>
	 * <td headers="k"><i>a</i>
	 * <td headers="v"><i>apple</i>
	 * <tr>
	 * <td headers="k"><i>b</i>
	 * <td headers="v"><i>bean</i>
	 * <tr>
	 * <td headers="k"><i>c</i>
	 * <td headers="v"><i>class</i>
	 * </table>
	 * </p>
	 * <p>
	 * ${a} + ${b} = $[c} -> apple + bean = class
	 * </p>
	 * @param str 字符串
	 * @param vars 变量集
	 * @return 转换后字符串
	 */
	public static String convertVariables(CharSequence str, Map vars) {
		if (str == null) return null;
		else if (vars == null) vars = Collections.EMPTY_MAP;

		StringBuilder result = new StringBuilder(32);
		try {
			convertVariables(str, vars, result);
		} catch (IOException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}
		return result.toString();
	}

	/**
	 * 去除重复串
	 * @param vs 字符串集
	 * @return 新字符串集
	 */
	public static String[] deduplicate(String... vs) {
		return deduplicate(vs, 0);
	}

	/**
	 * 去除重复串
	 * @param vs 字符串集
	 * @param trim 是否去除空格
	 * @param ignoreCase 是否忽略大小写
	 * @return 新字符串集
	 */
	public static String[] deduplicate(String[] vs, boolean trim, boolean ignoreCase) {
		int t = 0;
		if (trim) t |= 1;
		if (ignoreCase) t |= 2;
		return deduplicate(vs, t);
	}

	/**
	 * 去除重复串
	 * @param vs 字符串集
	 * @param type 去重类型 1:去除空格，2:忽略大小写，3:去除空格且忽略大小写
	 * @return 新字符串集
	 */
	public static String[] deduplicate(String[] vs, int type) {
		if (vs == null) return null;
		Map<String, String> xs = new LinkedHashMap();
		for (String s : vs) {
			if ((type & 1) == 1) s = Stringure.trim(s);
			String k = s;
			if ((type & 2) == 2) k = s.toLowerCase();
			if (xs.containsKey(k)) continue;
			xs.put(k, s);
		}
		if ((type & 1) == 1) xs.remove(empty);
		return xs.isEmpty() ? emarr : Arrayard.toArray(xs.values(), String.class);
	}

	/**
	 * 检查字符串的尾部是不是目标后缀
	 * @param str 字符串
	 * @param suffix 后缀
	 * @return 是、否
	 */
	public static boolean endsWith(CharSequence str, CharSequence suffix) {
		return endsWith(str, suffix, false);
	}

	/**
	 * 检查字符串的尾部是不是目标后缀
	 * @param str 字符串
	 * @param suffix 后缀
	 * @param ignoreCase 是否忽略大小写
	 * @return 是、否
	 */
	public static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
		if (str == null || suffix == null) return false;
		return startsWith(str, suffix, str.length() - suffix.length(), ignoreCase);
	}

	/**
	 * 忽略大小写匹配两个字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='d' width='20' />
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.equalsIgnoreCase("abc", "aBc")</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>true;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.equalsIgnoreCase(null,null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>true;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.equalsIgnoreCase("abc", "de")</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false;</td>
	 * </tr>
	 * <tr>
	 * <td headers='d'></td>
	 * <td headers='f'>Stringure.equalsIgnoreCase("","de")</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>false;</td>
	 * </tr>
	 * </table>
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 是否匹配
	 */
	public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
		if (str1 == null) return str2 == null;
		else if (str2 == null) return false;
		int len = str1.length();
		if (len != str2.length()) return false;
		for (int i = 0; i < len; i++) {
			char c1 = str1.charAt(i), c2 = str2.charAt(i);
			if (Character.toUpperCase(c1) == Character.toUpperCase(c2)) continue;
			if (Character.toLowerCase(c1) == Character.toLowerCase(c2)) continue; // string 代码中比较是指出部分字符大写不相同但小写相同
			return false;
		}
		return true;
	}

	/**
	 * 内容提取
	 * @param str 原字符串
	 * @param pattern 正则表达式
	 * @return 内容数组 未找到为空数组
	 */
	public static String[] extract(String str, Pattern pattern) {
		return extract(str, pattern, -1);
	}

	/**
	 * 内容提取
	 * @param str 原字符串
	 * @param pattern 正则表达式
	 * @param max 最大提取数量 小于等于<code>0</code>表示不限制
	 * @return 内容数组 未找到为空数组
	 */
	public static String[] extract(String str, Pattern pattern, int max) {
		Assertor.F.notNull(pattern, "The pattern is must not be null.");
		if (str == null) return emarr;
		Matcher matcher = pattern.matcher(str);
		Collection<String> list = null;
		if (max > 0) {
			list = new ArrayList(max);
		} else {
			list = new ArrayList();
		}
		for (int i = 0; matcher.find() && (i < max || max <= 0); i++) {
			list.add(matcher.group());
		}
		return list.toArray(emarr);
	}

	/**
	 * 内空提取
	 * @param str 原字符串
	 * @param regex 正则表达式
	 * @return 内空数组 未找到为空数组
	 */
	public static String[] extract(String str, String regex) {
		return extract(str, Pattern.compile(regex));
	}

	/**
	 * 内容提取
	 * @param str 原字符串
	 * @param regex 正则表达式
	 * @param max 最大提取数量 小于<code>0</code>表示不限制
	 * @return 内容数组 未找到为空数组
	 */
	public static String[] extract(String str, String regex, int max) {
		return extract(str, Pattern.compile(regex), max);
	}

	/**
	 * 子元素数量
	 * @param str 字符串
	 * @param pattern 正则表达式
	 * @return 数量
	 */
	public static int indexCount(CharSequence str, Pattern pattern) {
		if (str == null) return 0;
		Assertor.F.notNull(pattern, "The pattern is must not be null.");

		int size = 0;
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			size++;
		}

		return size;
	}

	/**
	 * 子元素数量
	 * @param str 字符串
	 * @param regex 正则表达式
	 * @return 数量
	 */
	public static int indexCount(CharSequence str, String regex) {
		Assertor.F.notEmpty(regex, "The expression is must not be empty.");
		return indexCount(str, Pattern.compile(regex));
	}

	/**
	 * 返回第一次出现的指定正则表达式符合的内容在此字符串中的索引
	 * @param str 字符串
	 * @param pattern 正则表达式
	 * @return 索引值 {开始索引, 截止索引} 不存在返回<code>null</code>
	 */
	public static int[] indexOf(CharSequence str, Pattern pattern) {
		return indexOf(str, pattern, 0);
	}

	/**
	 * 从指定的索引处开始，返回第一次出现的指定正则表达式符合的内容在此字符串中的索引
	 * @param str 字符串
	 * @param pattern 正则表达式
	 * @param start 开始索引
	 * @return 索引值 {开始索引, 截止索引} 不存在返回<code>null</code>
	 */
	public static int[] indexOf(CharSequence str, Pattern pattern, int start) {
		if (str == null || str.length() <= start) return null;
		Assertor.F.notNull(pattern, "The pattern is must not be null.");

		int[] index = null;
		Matcher matcher = pattern.matcher(str);
		if (matcher.find(start)) index = new int[] { matcher.start(), matcher.end() };
		return index;
	}

	/**
	 * 返回第一次出现的指定正则表达式符合的内容在此字符串中的索引
	 * @param str 字符串
	 * @param regex 正则表达式
	 * @return 索引值 {开始索引, 截止索引} 不存在返回<code>null</code>
	 */
	public static int[] indexOf(CharSequence str, String regex) {
		Assertor.F.notEmpty(regex, "The expression is must not be empty.");
		return indexOf(str, Pattern.compile(regex));
	}

	/**
	 * 从指定的索引处开始，返回第一次出现的指定正则表达式符合的内容在此字符串中的索引
	 * @param str 字符串
	 * @param regex 正则表达式
	 * @param start 开始索引
	 * @return 索引值 {开始索引, 截止索引} 不存在返回<code>null</code>
	 */
	public static int[] indexOf(CharSequence str, String regex, int start) {
		Assertor.F.notEmpty(regex, "The expression is must not be empty.");
		return indexOf(str, Pattern.compile(regex), start);
	}

	/**
	 * 是否为空
	 * <table border='0px' cellspacing='0px'>
	 * <tr align='left'>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200'/>
	 * <th id='e' width='16'/>
	 * <th id='r' width='120'/>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.isEmpty(null)</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>true</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.isEmpty("   ")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>true</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.isEmpty("\t \n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>true</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.isEmpty("\t text\n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>false</code>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @return 是、否
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null) return true;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (!isEmpty(str.charAt(i))) return false;
		}
		return true;
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join('.', [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, 0)</td>
	 * <td
	 * headers='e'>=</td>
	 * <td headers='r'>&quot;a.b.c&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join('.',
	 * [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='i' />
	 * <td headers='f'>Stringure.join('.', [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], -1, -2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;b&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @return 完整字符串
	 */
	public static String join(char separator, CharSequence... parts) {
		return join(separator, parts, 0, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @return 完整字符串
	 */
	public static String join(char separator, Collection collection) {
		return join(separator, collection, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @param size 连接数量 <code>0</code>表示不限数量，大于<code>0</code>表示指定数量，小于<code>0</code>表示少于总数的量
	 * @return 完整字符串
	 */
	public static String join(char separator, Collection collection, int size) {
		return join(String.valueOf(separator), collection, size);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @param start 起始位 <code>0</code>表示为集合起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为集合结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @return 完整字符串
	 */
	public static String join(char separator, Collection collection, int start, int end) {
		return join(String.valueOf(separator), collection, start, end);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param it 数据迭代器
	 * @return 完整字符串
	 */
	public static String join(char separator, Iterator it) {
		return join(separator, it, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param it 数据迭代器
	 * @param size 连接数量 大于<code>0</code>表示为指定数量，否则不限数量
	 * @return 完整字符串
	 */
	public static String join(char separator, Iterator it, int size) {
		return join(String.valueOf(separator), it, size);
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <th headers='i' />
	 * <td headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <th headers='i' />
	 * <td headers='f'>Stringure.join(*)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <th headers='i' />
	 * <td headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <th headers='i' />
	 * <td headers='f'>Stringure.join('.', &quot;a&quot;, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td
	 * headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @return 完整字符串
	 */
	public static String join(char separator, Object... parts) {
		return join(separator, parts, 0, 0);
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join('.', [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, 0)</td>
	 * <td
	 * headers='e'>=</td>
	 * <td headers='r'>&quot;a.b.c&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join('.',
	 * [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='i' />
	 * <td headers='f'>Stringure.join('.', [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], -1, -2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;b&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @param start 起始位 <code>0</code>表示为数组起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示数组结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为数组结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示数组结束位向后回退的位数
	 * @return 完整字符串
	 */
	public static String join(char separator, Object[] parts, int start, int end) {
		return join(String.valueOf(separator), parts, start, end);
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td
	 * headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='f'>Stringure.join(*)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='f'>Stringure.join(&quot;.&quot;, &quot;a&quot;, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td
	 * headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='f'>Stringure.join(null, &quot;a&quot;, &quot;b&quot;)</td>
	 * <td
	 * headers='e'>=</td>
	 * <td headers='r'>&quot;ab&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, CharSequence... parts) {
		return join(separator, parts, 0, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Collection collection) {
		return join(separator, collection, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @param size 连接数量 <code>0</code>表示不限数量，大于<code>0</code>表示指定数量，小于<code>0</code>表示少于总数的量
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Collection collection, int size) {
		return join(separator, collection, 0, size);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param collection 数据集合
	 * @param start 起始位 <code>0</code>表示为集合起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为集合结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示集合结束位向后回退的位数
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Collection collection, int start, int end) {
		if (!Assertor.P.notEmpty(collection)) return empty;
		int[] tmp = parsePosition(collection.size(), start, end);
		if (tmp == null) return empty;
		int size = tmp[1] - tmp[0];
		if (size == 0) return empty;
		Iterator it = collection.iterator();
		for (int i = 0; i < tmp[0] && it.hasNext(); i++) {
			it.next();
		}
		if (!it.hasNext()) return empty;
		return join(separator, it, size);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param it 数据迭代器
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Iterator it) {
		return join(separator, it, 0);
	}

	/**
	 * 连接字符串
	 * @param separator 连接符号
	 * @param it 数据迭代器
	 * @param size 连接数量 大于<code>0</code>表示为指定数量，否则不限数量
	 * @return 完整字符器
	 */
	public static String join(CharSequence separator, Iterator it, int size) {
		if (it == null) return empty;
		boolean hasSep = separator != null && separator.length() > 0;
		StringBuilder builder = new StringBuilder(32);
		for (int i = 0, a = 0; (size < 1 || i < size) && it.hasNext(); i++) {
			String part = Converter.P.convert(it.next(), String.class);
			if (part == null || part.length() == 0) continue;
			if (hasSep && a > 0) builder.append(separator);
			builder.append(part);
			a++;
		}
		return builder.toString();
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='f'>Stringure.join(*)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='f'>Stringure.join(&quot;.&quot;, &quot;a&quot;, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='f'>Stringure.join(null, &quot;a&quot;, &quot;b&quot;)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;ab&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Object... parts) {
		return join(separator, parts, 0, 0);
	}

	/**
	 * 连接字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, null)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(*, [null])</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(&quot;.&quot;, [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, 0)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;a.b.c&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td
	 * headers='f'>Stringure.join(&quot;.&quot;, [&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], 0, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;a.b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.join(&quot;.&quot;,[&quot;a&quot;,&quot;b&quot;,&quot;c&quot;], -1, 1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;b&quot;;</td>
	 * </tr>
	 * </table>
	 * @param separator 连接符号
	 * @param parts 字符串集
	 * @param start 起始位 <code>0</code>表示为数组起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示数组结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为数组结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示数组结束位向后回退的位数
	 * @return 完整字符串
	 */
	public static String join(CharSequence separator, Object[] parts, int start, int end) {
		if (!Assertor.P.notEmpty(parts)) return empty;
		int[] res = parsePosition(parts.length, start, end);
		if (res == null) return empty;
		int size = res[1] - res[0];
		return join(separator, ArrayEnumeration.create(parts, res[0], size), 0);
	}

	/**
	 * 获取首个非空值
	 * @param cs 备选值集
	 * @return 单个值
	 */
	public static String one(CharSequence... cs) {
		if (cs != null) {
			for (CharSequence c : cs) {
				String s = Stringure.trim(c);
				if (!s.isEmpty()) return s;
			}
		}
		return Stringure.empty;
	}

	/**
	 * 内容覆盖
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' />
	 * <th id='e' width='12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.overlay(&quot;&quot;, *, *, *)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.overlay(&quot;abc&quot;, &quot;d&quot;, 1, 2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;adc&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.overlay(&quot;abcd&quot;, &quot;e&quot;, 1, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;aed&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.overlay(&quot;abcd&quot;, &quot;e&quot;, -1, 0)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;abce&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.overlay(&quot;abcd&quot;, &quot;e&quot;, -1, -2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;abed&quot;;</td>
	 * </tr>
	 * </table>
	 * @param str 原字符串
	 * @param overlay 加入串
	 * @param start 覆盖起始位 <code>0</code>表示为字符串起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @param end 覆盖结束位 <code>0</code>表示为字符串结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @return 新字符串
	 */
	public static String overlay(String str, String overlay, int start, int end) {
		// 原串为NULL，返回空串
		if (str == null) return empty;
		if (overlay == null) overlay = empty;

		int len = str.length();
		int[] tmp = parsePosition(len, start, end);
		if (tmp == null) return overlay;
		// 起始位超过长度，返回原串
		if (tmp[0] >= len) return str;

		return new StringBuilder(str).replace(tmp[0], tmp[1], overlay).toString();
	}

	/**
	 * 生成重复字符串
	 * @param ch 字符
	 * @param count 重复数量
	 * @return 字符串
	 */
	public static String repeat(char ch, int count) {
		return repeat(Character.toString(ch), count);
	}

	/**
	 * 生成重复字符串
	 * @param part 字段
	 * @param count 重复数量
	 * @return 字符串
	 */
	public static String repeat(CharSequence part, int count) {
		if (count < 1) return empty;
		else if (count == 1) return Converter.F.convert(part, String.class);
		int len = part == null ? 0 : part.length();
		if (len < 1) return empty;
		StringBuilder buf = new StringBuilder(len * count);
		for (int i = 0; i < count; i++)
			buf.append(part);
		return buf.toString();
	}

	/**
	 * 计算字符串相似度
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 相似度
	 */
	public static double sim(CharSequence str1, CharSequence str2) {
		str1 = trim(str1);
		str2 = trim(str2);
		int ld = ld(str1, str2);
		return 1 - (double) ld / Math.max(str1.length(), str2.length());
	}

	/**
	 * 分割字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,2,3&quot;, ',')</td>
	 * <td headers='e'>=</td>
	 * <td
	 * headers='r'>[&quot;1&quot;, &quot;2&quot;, &quot;3&quot;]</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,,2,3&quot;, ',')</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;2&quot;, &quot;3&quot;]</td>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @param ch 分割符
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence str, char ch) {
		return split(str, ch, 0);
	}

	/**
	 * 分割字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,2,3&quot;, ',', 2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;2,3&quot;]</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,,2,3&quot;, ',', 2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;,2,3&quot;]</td>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @param ch 分割符
	 * @param max 最大
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence str, char ch, int max) {
		return split(str, ch, max, true);
	}

	/**
	 * 分割字符串
	 * @param cs 字符串
	 * @param ch 分割字符
	 * @param max 最大分割数
	 * @param skipEmpty 是否忽略空串
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence cs, char ch, int max, boolean skipEmpty) {
		if (isEmpty(cs)) return emarr;
		if (max == 1) return new String[] { cs.toString() };
		int len = cs.length();
		Collection<String> list = new ArrayList(max > 0 ? max : 10);
		int start = 0;
		for (int i = 0; i <= len; i++) {
			if (i < len && cs.charAt(i) != ch) continue;
			if (i >= len) {
				list.add((start == 0 ? cs : cs.subSequence(start, len)).toString());
			} else if (i == start && !skipEmpty) {
				list.add(Stringure.empty);
			} else if (i > start) {
				list.add(cs.subSequence(start, i).toString());
				if (max > 0 && list.size() == max - 1) {
					list.add(cs.subSequence(i + 1, len).toString());
					break;
				}
			}
			start = i + 1;
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 分割字符串
	 * @param cs 字符集
	 * @param pattern 分割表达式
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence cs, CharSequence pattern) {
		return split(cs, pattern, 0);
	}

	/**
	 * 分割字符串
	 * @param cs 字符集
	 * @param pattern 分割表达式
	 * @param max 最大分割数
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence cs, CharSequence pattern, int max) {
		return split(cs, pattern, max, true);
	}

	/**
	 * 分割字符串
	 * @param cs 字符集
	 * @param pattern 分割表达式
	 * @param max 最大分割数
	 * @param skipEmpty 是否忽略空字符串
	 * @return 子字符串集
	 */
	public static String[] split(CharSequence cs, CharSequence pattern, int max, boolean skipEmpty) {
		if (isEmpty(cs)) return emarr;
		try {
			if (!isEmpty(pattern) && max != 1) {
				String[] ss = Pattern.compile(pattern.toString()).split(cs, max);
				if (skipEmpty && ss != null) {
					Collection<String> rs = new ArrayList(ss.length);
					for (String s : ss)
						if (!Stringure.empty.equals(s)) rs.add(s);
					ss = rs.toArray(new String[rs.size()]);
				}
				return ss;
			}
		} catch (PatternSyntaxException e) {
		}
		return new String[] { cs.toString() };
	}

	/**
	 * 分割字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,2,3&quot;, ',')</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;2&quot;, &quot;3&quot;]</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,,2,3&quot;, ',')</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;2&quot;, &quot;3&quot;]</td>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @param ch 分割符
	 * @return 子字符串集
	 */
	public static String[] split(String str, char ch) {
		return split((CharSequence) str, ch);
	}

	/**
	 * 分割字符串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,2,3&quot;, ',', 2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;2,3&quot;]</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.split(&quot;1,,2,3&quot;, ',', 2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>[&quot;1&quot;, &quot;,2,3&quot;]</td>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @param ch 分割符
	 * @param max 最大
	 * @return 子字符串集
	 */
	public static String[] split(String str, char ch, int max) {
		return split((CharSequence) str, ch, max);
	}

	/**
	 * 分割字符串
	 * @param str 字符串
	 * @param ch 分割字符
	 * @param max 最大分割数
	 * @param skipEmpty 是否忽略空串
	 * @return 子字符串集
	 */
	public static String[] split(String str, char ch, int max, boolean skipEmpty) {
		return split((CharSequence) str, ch, max, skipEmpty);
	}

	/**
	 * 检查字符串的首部是不是目标前缀
	 * @param str 字符串
	 * @param prefix 目标前缀
	 * @return 是、否
	 */
	public static boolean startsWith(CharSequence str, CharSequence prefix) {
		return startsWith(str, prefix, 0);
	}

	/**
	 * 检查字符串的首部是不是目标前缀
	 * @param str 字符串
	 * @param prefix 目标前缀
	 * @param offset 检查起始字符
	 * @return 是、否
	 */
	public static boolean startsWith(CharSequence str, CharSequence prefix, int offset) {
		return startsWith(str, prefix, offset, false);
	}

	/**
	 * 检查字符串的首部是不是目标前缀
	 * @param str 字符串
	 * @param prefix 目标前缀
	 * @param offset 检查起始字符
	 * @param ignoreCase 是否忽略大小写
	 * @return 是、否
	 */
	public static boolean startsWith(CharSequence str, CharSequence prefix, int offset, boolean ignoreCase) {
		if (str == null || prefix == null) return false;
		int po = 0, pc = prefix.length();
		if ((offset < 0) || (offset > str.length() - pc)) return false;
		while (--pc >= 0) {
			char c1 = str.charAt(offset++), c2 = prefix.charAt(po++);
			if (ignoreCase) {
				c1 = Character.toLowerCase(c1);
				c2 = Character.toLowerCase(c2);
			}
			if (c1 != c2) return false;
		}
		return true;
	}

	/**
	 * 获取子串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(null, *)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;&quot;, *)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;a&quot;, 1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, 1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;bc&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td
	 * headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;c&quot;;</td>
	 * </tr>
	 * </table>
	 * @param str 原字符串
	 * @param start 起始位 <code>0</code>表示为字符串起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @return 子串
	 */
	public static String subString(String str, int start) {
		return subString(str, start, 0);
	}

	/**
	 * 获取子串
	 * <table border='0px'>
	 * <tr>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200' /><th id='e' width=12' />
	 * <th id='r' width='120' />
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(null, *, *)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;&quot;, *, *)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;a&quot;, 1, 0)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, 1, 0)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;bc&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, -1, 0)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;c&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, 1, -1)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;b&quot;;</td>
	 * </tr>
	 * <tr>
	 * <td headers='i' />
	 * <td headers='f'>Stringure.subString(&quot;abc&quot;, -1, -2)</td>
	 * <td headers='e'>=</td>
	 * <td headers='r'>&quot;b&quot;;</td>
	 * </tr>
	 * </table>
	 * @param str 原字符串
	 * @param start 起始位 <code>0</code>表示为字符串起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为字符串结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @return 子串
	 */
	public static String subString(String str, int start, int end) {
		// 原串为NULL，返回空串
		if (str == null) return empty;
		int len = str.length();
		int[] tmp = parsePosition(len, start, end);
		if (tmp == null) return empty;
		// 起始位超过长度，返回空串
		if (tmp[0] >= len) return empty;

		return str.substring(tmp[0], tmp[1]);
	}

	/**
	 * 将字符串转换为字节流
	 * @param content 字符串
	 * @param cs 字符集
	 * @return 字节流
	 */
	public static byte[] toBytes(CharSequence content, Charset cs) {
		return toBytes(content, 0, content == null ? 0 : content.length(), cs);
	}

	/**
	 * 将字符串转换为字节流
	 * @param content 字符串
	 * @param offset 起始位
	 * @param length 长度
	 * @param cs 字符集
	 * @return 字节流
	 */
	public static byte[] toBytes(CharSequence content, int offset, int length, Charset cs) {
		int len = content == null ? 0 : content.length();
		if (length == 0 || offset > len) return embytes;
		if (offset < 0) offset = 0;

		if (cs == null) cs = Environments.defaultCharset();
		ByteBuffer buf;
		try {
			buf = cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
					.encode(CharBuffer.wrap(content, offset, offset + Math.min(length, len - offset)));
		} catch (CharacterCodingException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}

		byte[] bytes = new byte[buf.limit()];
		System.arraycopy(buf.array(), buf.arrayOffset(), bytes, 0, buf.limit());
		return bytes;
	}

	/**
	 * 将字符串转换为字节流
	 * @param content 字符串
	 * @param offset 起始位
	 * @param length 长度
	 * @param enc 字符集
	 * @return 字节流
	 */
	public static byte[] toBytes(CharSequence content, int offset, int length, String enc) {
		return toBytes(content, offset, length, charset(true, enc));
	}

	/**
	 * 将字符串转换为字节流
	 * @param content 字符串
	 * @param enc 字符集
	 * @return 字节流
	 */
	public static byte[] toBytes(CharSequence content, String enc) {
		return toBytes(content, 0, content == null ? 0 : content.length(), enc);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param cs 字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, Charset cs) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, cs);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param cs 字符集
	 * @param buffer 目标缓存区
	 * @return 字符缓存区
	 */
	public static <A extends Appendable> A toString(byte[] bytes, Charset cs, A buffer) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, cs, buffer);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param dc 字符反序列
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, CharsetDecoder dc) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, dc);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param dc 字符反序列
	 * @param buffer 目标缓存区
	 * @return 字符缓存区
	 */
	public static <A extends Appendable> A toString(byte[] bytes, CharsetDecoder dc, A buffer) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, dc, buffer);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param offset 起始位
	 * @param length 长度
	 * @param cs 字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, int offset, int length, Charset cs) {
		return toString(bytes, offset, length, cs, new StringBuilder(length)).toString();
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param offset 起始位
	 * @param length 长度
	 * @param cs 字符集
	 * @param buffer 目标缓存区
	 * @return 字符缓存区
	 */
	public static <A extends Appendable> A toString(byte[] bytes, int offset, int length, Charset cs, A buffer) {
		CharsetDecoder dc = null;
		if (cs != null) dc = cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
		return toString(bytes, offset, length, dc, buffer);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param offset 起始位
	 * @param length 长度
	 * @param dc 字符反序列
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, int offset, int length, CharsetDecoder dc) {
		return toString(bytes, offset, length, dc, new StringBuilder(length)).toString();
	}

	/**
	 * @param bytes
	 * @param offset
	 * @param length
	 * @param dc
	 * @param buffer
	 * @return
	 */
	public static <A extends Appendable> A toString(byte[] bytes, int offset, int length, CharsetDecoder dc, A buffer) {
		int len = bytes == null ? 0 : bytes.length;
		if (length == 0 || offset >= len) return buffer;
		if (offset < 0) offset = 0;

		if (buffer == null) buffer = (A) new StringBuilder(length);
		if (dc == null) {
			dc = Environments.defaultCharset().newDecoder();
			dc = dc.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
		}
		decode(bytes, offset, Math.min(len - offset, length), dc, buffer);
		return buffer;
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param offset 起始位
	 * @param length 长度
	 * @param enc 字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, int offset, int length, String enc) {
		return toString(bytes, offset, length, charset(enc));
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param offset 起始位
	 * @param length 长度
	 * @param enc 字符集
	 * @param buffer 目标缓存区
	 * @return 字符缓存区
	 */
	public static <A extends Appendable> A toString(byte[] bytes, int offset, int length, String enc, A buffer) {
		return toString(bytes, offset, length, charset(enc), buffer);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param enc 字符集
	 * @return 字符串
	 */
	public static String toString(byte[] bytes, String enc) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, enc);
	}

	/**
	 * 将字节流转为字符串
	 * @param bytes 字节流
	 * @param enc 字符集
	 * @param buffer 目标缓存区
	 * @return 字符缓存区
	 */
	public static <A extends Appendable> A toString(byte[] bytes, String enc, A buffer) {
		return toString(bytes, 0, bytes == null ? 0 : bytes.length, enc, buffer);
	}

	/**
	 * 修剪字符串
	 * <table border='0px' cellspacing='0px'>
	 * <tr align='left'>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200'/>
	 * <th id='e' width='16'/>
	 * <th id='r' width='120'/>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim(null)</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>""</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("   ")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>""</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t \n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>""</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t text\n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>"text"</code>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @return 修剪后字符串
	 */
	public static String trim(CharSequence str) {
		if (str == null) return empty;
		int start = 0;
		int end = str.length() - 1;
		while (start <= end && isEmpty(str.charAt(start)))
			start++;
		while (end >= start && isEmpty(str.charAt(end)))
			end--;
		return str.subSequence(start, end + 1).toString();
	}

	/**
	 * 修剪字符串，字符串为空返回空
	 * <table border='0px' cellspacing='0px'>
	 * <tr align='left'>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200'/>
	 * <th id='e' width='16'/>
	 * <th id='r' width='120'/>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim(null)</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>null</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("   ")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>""</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t \n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>""</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t text\n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>"text"</code>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @return 修剪后字符串
	 */
	public static String trimOrNull(CharSequence str) {
		return str == null ? null : trim(str);
	}

	/**
	 * 修剪字符串，空串返回空
	 * <table border='0px' cellspacing='0px'>
	 * <tr align='left'>
	 * <th id='i' width='20'/>
	 * <th id='f' width='200'/>
	 * <th id='e' width='16'/>
	 * <th id='r' width='120'/>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim(null)</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>null</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("   ")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>null</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t \n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>null</code>
	 * </tr>
	 * <tr>
	 * <td headers='i'/>
	 * <td headers='f'><code>Stringure.trim("\t text\n")</code>
	 * <td headers='e'><code>=</code>
	 * <td headers='r'><code>"text"</code>
	 * </tr>
	 * </table>
	 * @param str 字符串
	 * @return 修剪后字符串
	 */
	public static String trimToNull(CharSequence str) {
		String s = trim(str);
		return s.length() < 1 ? null : s;
	}

	/**
	 * 对象值对应字符串值
	 * @param obj 对象
	 * @return 字符串值
	 */
	public static String valueOf(Object obj) {
		return Converter.F.convert(obj, String.class);
	}

	/**
	 * 转换字符串中的变量值
	 * @param str 字符串
	 * @param vars 变量集
	 * @param buffer 字符串缓存区
	 * @throws IOException
	 */
	protected static void convertVariables(CharSequence str, Map vars, Appendable buffer) throws IOException {
		// 转换变量值
		Stack<Appendable> stack = new LinkedStack();
		Appendable var = null;
		int len = str.length();
		for (int p = 0; p <= len; p++) {
			Appendable ab = Arrayard.theOne(var, buffer);
			if (p < len) {
				char ch = str.charAt(p);

				switch (ch) {
				case '$':
					if (++p < len) {
						ch = str.charAt(p);
						if (ch == '{') {
							stack.push(var = new StringBuilder());
						} else {
							ab.append('$');
						}
						continue;
					} else {
						ab.append(ch);
						break;
					}
				case '}':
					if (stack.peek() != null) break;
				default:
					ab.append(ch);
					continue;
				}
			}

			do {
				var = stack.pop();
				if (var != null) {
					String key = var.toString(), def = null;
					int s = key.indexOf(':');
					if (s > -1) {
						def = Stringure.subString(key, s + 1);
						key = Stringure.subString(key, 0, s);
					}

					var = stack.peek();

					ab = Arrayard.theOne(var, buffer);
					// 获取变量值
					Object value = get(vars, key);
					if (value != null) {
						convertVariables(Converter.P.convert(value, String.class), vars, ab);
					} else if (def != null) {
						convertVariables(def, vars, ab);
					} else if (!vars.containsKey(key)) { // 无效变量不改变原值
						ab.append("${").append(key).append('}');
					}
					break;
				}
			} while (p >= len && var != null);
		}
	}

	/**
	 * 计算转换字符串
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 步骤数
	 */
	protected static int ld(CharSequence str1, CharSequence str2) {
		int n = str1.length(), m = str2.length();
		int i, j;
		char ch1, ch2;
		if (n == 0 || m == 0) return Math.max(m, n);
		int d[][] = new int[n + 1][m + 1]; // 矩阵
		for (i = 0; i <= n; i++)
			d[i][0] = i;
		for (j = 0; j <= m; j++)
			d[0][j] = j;
		int t; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		for (i = 1; i <= n; i++) { // 遍历str1
			ch1 = str1.charAt(i - 1);
			// 去匹配str2
			for (j = 1; j <= m; j++) {
				ch2 = str2.charAt(j - 1);
				if (ch1 == ch2) t = 0;
				else t = 1;
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = Numeric.minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + t);
			}
		}
		return d[n][m];
	}

	/**
	 * 检查字符串的首部是不是目标前缀
	 * @param str 字符串
	 * @param prefix 目标前缀
	 * @param ignoreCase 是否忽略大小写
	 * @return 是、否
	 */
	protected static boolean startsWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
		return startsWith(str, prefix, 0, ignoreCase);
	}

	/**
	 * 获取变量值
	 * @param vars 变量集
	 * @param key 键名称
	 * @return 变量值
	 */
	static Object get(Map vars, String key) {
		Object value = null;
		if (vars instanceof Properties) value = ((Properties) vars).getProperty(key);
		if (value == null) value = vars.get(key);
		return value;
	}

	/**
	 * 位置计算
	 * @param len 总长度
	 * @param start 起始位 <code>0</code>表示为字符串起始位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @param end 结束位 <code>0</code>表示为字符串结束位，大于<code>0</code>表示为指定位数，小于<code>0</code>表示字符串结束位向后回退的位数
	 * @return 实际的起始位和结束位
	 */
	static int[] parsePosition(int len, int start, int end) {
		if (start < 0) start += len;
		if (start < 0) start = 0;
		// 转换负结束位
		if (end <= 0) end += len;
		if (end <= 0) return null;
		// 值交换
		if (end < start) {
			int tmp = start;
			start = end;
			end = tmp;
		}
		if (start > len) return null;
		if (end > len) end = len;
		return new int[] { start, end };
	}

	/**
	 * 执行反解
	 * @param ba 字节流
	 * @param off 起始位
	 * @param len 长度
	 * @param dc 字符反解
	 * @param buf 缓存区
	 */
	private static <A extends Appendable> void decode(byte[] ba, int off, int len, CharsetDecoder dc, A buf) {
		try {
			try {
				if (decodeArray(ba, off, len, dc, buf)) return;
			} catch (IOException e) {
				throw e;
			} catch (Throwable t) { // ignored;
			}

			ByteBuffer bb = ByteBuffer.wrap(ba, off, len);
			buf.append(dc.decode(bb));
		} catch (Throwable t) {
			throw Converter.F.convert(t, RuntimeException.class);
		}
	}

	/**
	 * Array 式反序列化
	 * @param ba 字节流
	 * @param o 起始位
	 * @param len 长度
	 * @param dc 字符反解
	 * @param buf 缓存区
	 * @return 是否成功
	 * @throws IOException
	 */
	@SuppressWarnings("restriction")
	private static <A extends Appendable> boolean decodeArray(byte[] ba, int o, int len, CharsetDecoder dc, A buf) throws IOException {
		if (!(dc instanceof sun.nio.cs.ArrayDecoder)) return false;
		char[] ca = new char[(int) (len * (double) dc.maxCharsPerByte())];
		int clen = ((sun.nio.cs.ArrayDecoder) dc).decode(ba, o, len, ca);
		if (clen > 0) buf.append(new String(ca, 0, clen));
		return true;
	}

	/**
	 * 是否空字符
	 * @param ch 字符
	 * @return 是、否
	 */
	private static boolean isEmpty(char ch) {
		if (Character.isWhitespace(ch)) return true;
		else if (Character.isISOControl(ch)) return true;
		else if (Character.isIdentifierIgnorable(ch) && Emchar.isEmpty(ch)) return true;
		return false;
	}

	/**
	 * 构造函数
	 */
	protected Stringure() {}
}

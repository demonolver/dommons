/*
 * @(#)HtmlCoder.java     2012-4-25
 */
package org.dommons.io.coder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.convert.Converter;
import org.dommons.core.number.Radix64;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.io.prop.Bundles;
import org.dommons.security.coder.Coder;

/**
 * HTML 字符转换器
 * @author Demon 2012-4-25
 */
public class HtmlCoder implements Coder {

	private static Ref<HtmlCoder> ref;

	/**
	 * 获取转换器实例
	 * @return 转换器实例
	 */
	public static HtmlCoder instance() {
		HtmlCoder coder = ref == null ? null : ref.get();
		if (coder == null) ref = new Softref(coder = new HtmlCoder());
		return coder;
	}

	private final Pattern pattern;
	private final Map<String, Integer> map;
	private final Map<Integer, String> chars;

	/**
	 * 构造函数
	 */
	protected HtmlCoder() {
		init(map = new HashMap(), chars = new HashMap());
		pattern = Pattern.compile("&(#(x|X)?([0-9a-fA-F]+)|[a-zA-Z]+\\d*);?");
	}

	public String decode(String code) {
		if (code == null || !code.contains("&")) return code;

		int len = code.length();

		StringBuilder buffer = new StringBuilder(code.length());

		boolean change = false;
		int last = 0;
		for (Matcher m = pattern.matcher(code); m.find(); last = m.end()) {
			int charval = -1;
			String num = m.group(3);
			if (num != null) {
				try {
					charval = Radix64.toInteger(num, m.group(2) != null ? 16 : 10);
				} catch (NumberFormatException e) { // 忽略无效内容
				}
			} else {
				String name = m.group(1);
				if (map.containsKey(name)) charval = map.get(name);
			}

			if (m.start() > last) buffer.append(code, last, m.start());
			if (charval != -1) {
				buffer.append((char) charval);
				change = true;
			} else {
				buffer.append(m.group(0));
			}
		}
		return !change ? code : (last < len ? buffer.append(code, last, len) : buffer).toString();
	}

	public String encode(String code) {
		int len = code == null ? 0 : code.length();
		StringBuilder buffer = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = code.charAt(i);
			String str = chars.get(Integer.valueOf(c));
			if (str != null) { // 转换转义字符
				buffer.append('&').append(str).append(';');
			} else if (c > 0x7e || c < 0x20) { // 转换非英文字符
				buffer.append("&#").append((int) c).append(';');
			} else {
				buffer.append(c);
			}
		}
		return len != buffer.length() ? buffer.toString() : code;
	}

	/**
	 * 初始化字符对照关系
	 * @param map 文本对应关系
	 * @param chars 字符对应关系
	 */
	protected void init(Map<String, Integer> map, Map<Integer, String> chars) {
		Properties prop = null;
		try {
			prop = Bundles.load("classpath:org/dommons/io/coder/html.codes");
		} catch (IOException e) {
			throw Converter.P.convert(e, RuntimeException.class);
		}

		for (String key : Bundles.keys(prop)) {
			if (Stringure.isEmpty(key)) continue;
			Integer integer = Converter.F.convert(prop.getProperty(key), Integer.class);
			if (integer == null) continue;
			map.put(key, integer);

			String old = chars.get(integer);
			if (old != null) {
				chars.put(integer, compare(old, key));
			} else {
				chars.put(integer, key);
			}
		}
	}

	/**
	 * 比较两字符串返回优先级高的字符串 优先短串或小写
	 * @param str1 字符串 1
	 * @param str2 字符串 2
	 * @return 优先串
	 */
	private String compare(String str1, String str2) {
		int len = str1.length();
		int r = len - str2.length();
		if (r < 0) {
			return str1;
		} else if (r > 0) {
			return str2;
		}

		for (int i = 0; i < len; i++) {
			switch ((Character.isLowerCase(str1.charAt(i)) ? 1 : 0) | (Character.isLowerCase(str2.charAt(i)) ? 2 : 0)) {
			case 1:
				return str1;
			case 2:
				return str2;
			}
		}

		return str1;
	}
}

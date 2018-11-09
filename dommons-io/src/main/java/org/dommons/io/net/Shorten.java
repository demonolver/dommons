/*
 * @(#)Shorten.java     2014-12-10
 */
package org.dommons.io.net;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.collections.queue.TreeQueue;
import org.dommons.core.number.Radix64.Radix64Digits;
import org.dommons.core.string.Stringure;
import org.dommons.security.cipher.MD5Cipher;

/**
 * 短链接算法
 * @author Demon 2014-12-10
 */
public class Shorten extends Radix64Digits {

	private static final Map<CharSequence, CharSequence> shorts = new ConcurrentSoftMap();
	private static final Charset cs = Stringure.charset(true, "utf8", "gbk", "iso-8859-1");

	/**
	 * 生成短链键值
	 * @param content 内容串
	 * @return 短链键值
	 */
	public static CharSequence shorten(CharSequence content) {
		CharSequence s = shorts.get(content);
		if (s == null) {
			String hex = md5(content);
			int len = hex.length() / 8, l = 7;

			char[] c = new char[l];
			for (int i = 0; i < len; i++) {
				long idx = Long.parseLong(hex.substring(i * 8, (i + 1) * 8), 16);

				for (int k = 0; k < l; k++) {
					c[k] = (char) ((Math.abs(idx & 0x23) + c[k]) % 36);
					idx = k < 2 ? idx >> 6 : idx >> 4;
				}
			}
			for (int i = 0; i < l; i++)
				c[i] = digits[c[i]];

			shorts.put(content, s = new String(c));
		}
		return s;
	}

	/**
	 * 生成短链键值
	 * @param url 链接地址
	 * @return 短链键值
	 * @throws IOException
	 */
	public static String shorten(URL url) throws IOException {
		return String.valueOf(shorten(url(url)));
	}

	/**
	 * 生成短链键值
	 * @param url 原地址
	 * @return 短链键值
	 * @throws IOException
	 */
	public static String shortenUrl(String url) throws IOException {
		return String.valueOf(shorten(url(url)));
	}

	/**
	 * 规整链接
	 * @param url 链接地址
	 * @return 链接地址
	 * @throws IOException
	 */
	public static String url(String url) throws IOException {
		return url(new URL(url));
	}

	/**
	 * 规整链接地址
	 * @param url 链接地址
	 * @return 新链接地址
	 */
	public static String url(URL url) {
		StringBuilder buf = new StringBuilder(64);
		buf.append(url.getProtocol()).append("://");

		{
			String usr = url.getUserInfo();
			if (!Stringure.isEmpty(usr)) buf.append(usr).append('@');
		}

		buf.append(url.getHost());
		port: {
			if (url.getPort() <= 0) break port;
			else if ("http".equalsIgnoreCase(url.getProtocol()) && url.getPort() == 80) break port;
			else if ("https".equalsIgnoreCase(url.getProtocol()) && url.getPort() == 443) break port;
			buf.append(':').append(url.getPort());
		}

		{
			String path = url.getPath();
			Matcher m = Pattern.compile("([^\\/]+)\\/+").matcher(path);
			Collection<String> list = new ArrayList();
			int tail = -1;
			while (m.find()) {
				list.add(Stringure.trim(m.group(1)));
				tail = m.end();
			}
			if (tail < path.length() - 1) list.add(path.substring(tail));
			String[] ps = list.toArray(new String[list.size()]);
			list.clear();
			for (int i = 0; i < ps.length; i++) {
				if (!"..".equals(ps[i])) continue;
				if (i > 0) {
					String[] t = new String[ps.length - 2];
					if (i > 1) System.arraycopy(ps, 0, t, 0, i);
					System.arraycopy(ps, i + 1, t, i - 1, ps.length - i - 1);
					ps = t;
					i -= 2;
				}
			}

			for (int i = 0; i < ps.length; i++) {
				buf.append('/');
				buf.append(ps[i]);
			}
		}

		if (url.getQuery() != null && url.getQuery().length() > 0) {
			Collection<String> parts = new TreeQueue();
			for (String p : Stringure.split(url.getQuery(), '&'))
				parts.add(p);
			int x = 0;
			for (Iterator<String> it = parts.iterator(); it.hasNext(); it.remove()) {
				buf.append(x++ > 0 ? '&' : '?');
				buf.append(it.next());
			}
		}

		{
			String ref = url.getRef();
			if (!Stringure.isEmpty(ref)) buf.append('#').append(ref);
		}
		return buf.toString();
	}

	/**
	 * MD5 转换
	 * @param str 字符串
	 * @return 转换后十六进制串
	 */
	private static String md5(CharSequence str) {
		return MD5Cipher.encodeHex(Stringure.toBytes(str, cs));
	}
}

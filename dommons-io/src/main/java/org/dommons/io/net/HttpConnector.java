/*
 * @(#)HttpConnector.java     2016-12-27
 */
package org.dommons.io.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.io.coder.URLCoder;
import org.dommons.io.file.Zipper;
import org.dommons.security.coder.B64Coder;

/**
 * HTTP 请求连接器
 * @author demon 2016-12-27
 */
public class HttpConnector {

	private static Ref<SSLContext> sref;

	/**
	 * 用户授权
	 * @param conn 连接
	 * @param username 用户
	 * @param passwd 密码
	 */
	public static void authorization(HttpURLConnection conn, String username, String passwd) {
		String a = authorization(username, passwd);
		if (Stringure.isEmpty(a)) return;
		conn.setRequestProperty("Authorization", a);
	}

	/**
	 * 用户授权
	 * @param headers 请求头
	 * @param username 用户
	 * @param passwd 密码
	 */
	public static void authorization(Map<String, String> headers, String username, String passwd) {
		if (headers == null) return;
		String a = authorization(username, passwd);
		if (Stringure.isEmpty(a)) return;
		headers.put("Authorization", a);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(String url, int timeout) throws IOException {
		return connect(url, timeout, null);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @param headers 报文头
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(String url, int timeout, Map<String, String> headers) throws IOException {
		return connect(url, timeout, headers, null);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @param headers 报文头
	 * @param proxy 请求代理服务
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(String url, int timeout, Map<String, String> headers, Proxy proxy) throws IOException {
		URL u = url(url);
		return connect(u, timeout, headers, proxy);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(URL url, int timeout) throws IOException {
		return connect(url, timeout, null);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @param headers 报文头
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(URL url, int timeout, Map<String, String> headers) throws IOException {
		return connect(url, timeout, headers, null);
	}

	/**
	 * 连接地址
	 * @param url 请求地址
	 * @param timeout 连接超时时长
	 * @param headers 报文头
	 * @param proxy 请求代理服务
	 * @return HTTP 连接
	 * @throws IOException
	 */
	public static HttpURLConnection connect(URL url, int timeout, Map<String, String> headers, Proxy proxy) throws IOException {
		if (url == null) return null;
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = ssl();
			HttpsURLConnection connHttps = (HttpsURLConnection) openConnection(url, proxy);
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;// 默认都认证通过
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) openConnection(url, proxy);
		}
		authorization(conn, url.getUserInfo()); // 用户信息授权

		if (timeout > 0) conn.setConnectTimeout(timeout);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("User-Agent", "Jave-IO");
		headers(conn, headers);

		return conn;
	}

	/**
	 * 获取地址内容
	 * @param url 请求地址
	 * @param os 内容输出流
	 * @throws IOException
	 */
	public static void get(String url, OutputStream os) throws IOException {
		get(url, os, null);
	}

	/**
	 * 获取地址内容
	 * @param url 请求地址
	 * @param os 内容输出流
	 * @param headers 报文头
	 * @throws IOException
	 */
	public static void get(String url, OutputStream os, Map<String, String> headers) throws IOException {
		get(url(url), os, headers);
	}

	/**
	 * 获取地址内容
	 * @param url 请求地址
	 * @return 响应内容
	 * @throws IOException
	 */
	public static byte[] get(URL url) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
		try {
			get(url, bos);
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 获取地址内容
	 * @param url 请求地址
	 * @param os 内容输出流
	 * @throws IOException
	 */
	public static void get(URL url, OutputStream os) throws IOException {
		get(url, os, null);
	}

	/**
	 * 获取地址内容
	 * @param url 请求地址
	 * @param os 内容输出流
	 * @param headers 报文头
	 * @throws IOException
	 */
	public static void get(URL url, OutputStream os, Map<String, String> headers) throws IOException {
		HttpURLConnection conn = connect(url, 0, headers);
		if (conn == null) return;
		try {
			int code = conn.getResponseCode();
			if (code == 301 || code == 302) {
				String location = conn.getHeaderField("Location");
				if (!Stringure.isEmpty(location)) {
					get(location, os);
					return;
				}
			}
			read(conn, os);
		} finally {
			conn.disconnect();
		}
	}

	/**
	 * 执行 POST 请求发送
	 * @param conn 连接
	 * @param type 内容类型
	 * @param content 内容
	 * @param gzip 是否压缩
	 * @throws IOException
	 */
	public static void post(HttpURLConnection conn, String type, byte[] content, boolean gzip) throws IOException {
		if (conn == null || content == null || content.length < 1) return;
		conn.setDoOutput(true);
		if (gzip) {
			conn.setRequestProperty("Accept-Encoding", "compress,gzip");
			if ((gzip = content.length > Zipper.gzip_min_size)) conn.setRequestProperty("Content-Encoding", "gzip");
		} else {
			conn.setRequestProperty("Accept-Encoding", "compress");
		}
		conn.setRequestProperty("Content-Type", type);

		byte[] bs = content;
		if (gzip) bs = Zipper.gzip(content);

		OutputStream os = null;
		try {
			conn.setRequestProperty("Content-Length", String.valueOf(bs.length));
			os = conn.getOutputStream();
			int len = bs.length, p = 512;
			for (int i = 0; i < len; i += p) {
				os.write(bs, i, Math.min(p, len - i));
				os.flush();
			}
		} finally {
			if (os != null) os.close();
		}
	}

	/**
	 * 执行 POST 请求发送
	 * @param conn 连接
	 * @param type 内容类型
	 * @param content 内容
	 * @param gzip 是否压缩
	 * @throws IOException
	 */
	public static void post(HttpURLConnection conn, String type, String content, boolean gzip) throws IOException {
		post(conn, type, Stringure.toBytes(content, "utf-8"), gzip);
	}

	/**
	 * 读取响应
	 * @param conn 连接
	 * @return 响应内容
	 * @throws IOException
	 */
	public static byte[] read(HttpURLConnection conn) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
		try {
			read(conn, bos);
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 读取响应
	 * @param conn 连接
	 * @param os 响应输出流
	 * @throws IOException
	 */
	public static void read(HttpURLConnection conn, OutputStream os) throws IOException {
		InputStream is = conn.getInputStream();
		try {
			if (isGzip(conn.getHeaderField("Content-Encoding"))) is = new GZIPInputStream(is);
			transform(is, os);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 获取内容
	 * @param is 输入流
	 * @return 内容报文
	 * @throws IOException
	 */
	public static byte[] read(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
		try {
			transform(is, bos);
		} finally {
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 是否压缩
	 * @param ec 类型
	 * @return 是、否
	 */
	protected static boolean isGzip(String ec) {
		return ec != null && Pattern.compile("(?<=^|[\\s\\p{Punct}])gzip(?=$|[\\s\\p{Punct}])").matcher(ec.toLowerCase()).find();
	}

	/**
	 * 获取 SSL 引擎
	 * @return SSL 引擎
	 * @throws IOException
	 */
	protected static SSLContext ssl() throws IOException {
		SSLContext ctx = sref == null ? null : sref.get();
		if (ctx == null) {
			try {
				try {
					ctx = SSLContext.getInstance("TLSv1.2");
				} catch (NoSuchAlgorithmException e) {
					ctx = SSLContext.getInstance("TLS");
				}
				ctx.init(new KeyManager[0], new TrustManager[] { new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} }, new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			sref = new Softref(ctx);
		}
		return ctx;
	}

	/**
	 * 设置报文头
	 * @param conn 连接
	 * @param headers 报文头
	 */
	static void headers(HttpURLConnection conn, Map<String, String> headers) {
		if (conn == null || headers == null) return;
		for (Entry<String, String> en : headers.entrySet()) {
			String k = Stringure.trimToNull(en.getKey()), v = Stringure.trimToNull(en.getValue());
			if (k == null || v == null) continue;
			conn.setRequestProperty(k, v);
		}
	}

	/**
	 * 开启连接
	 * @param url 请求地址
	 * @param proxy 连接代理
	 * @return 连接
	 * @throws IOException
	 */
	static URLConnection openConnection(URL url, Proxy proxy) throws IOException {
		if (proxy == null) return url.openConnection();
		else return url.openConnection(proxy);
	}

	/**
	 * 迁移流
	 * @param is 输入流
	 * @param os 输出流
	 * @throws IOException
	 */
	static void transform(InputStream is, OutputStream os) throws IOException {
		byte[] bs = new byte[128];
		for (int r = 0; (r = is.read(bs)) > 0;) {
			os.write(bs, 0, r);
			os.flush();
		}
	}

	/**
	 * 处理用户授权
	 * @param conn 连接
	 * @param userInfo 用户授权内容
	 */
	private static void authorization(HttpURLConnection conn, String userInfo) {
		if (Stringure.isEmpty(userInfo)) return;
		String[] u = Stringure.split(userInfo, ':', 2);
		String un = u.length > 0 ? URLCoder.decode(u[0], "utf8") : null;
		String pw = u.length > 1 ? URLCoder.decode(u[1], "utf8") : null;
		authorization(conn, un, pw);
	}

	/**
	 * 生成用户授权串
	 * @param username 用户
	 * @param passwd 密码
	 * @return 授权串
	 */
	private static String authorization(String username, String passwd) {
		if (Stringure.isEmpty(username)) return Stringure.empty;
		String u = Stringure.concat(username, ":", passwd);
		return "Basic " + B64Coder.encodeBuffer(Stringure.toBytes(u, "utf8"));
	}

	/**
	 * 生成 URL
	 * @param url URL 串
	 * @return URL
	 * @throws IOException
	 */
	private static URL url(String url) throws IOException {
		if (Stringure.isEmpty(url)) return null;
		return new URL(Stringure.trim(url));
	}
}

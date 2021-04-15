/*
 * @(#)UniQueness.java     2012-7-5
 */
package org.dommons.io.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;

import org.dommons.core.convert.Converter;
import org.dommons.core.number.Radix64;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;
import org.dommons.security.cipher.MD5Cipher;

/**
 * 唯一编号生成器
 * @author Demon 2012-7-5
 */
public class UniQueness {

	private static Ref<UniQueness> ref;

	/**
	 * 生成十六进制唯一编号
	 * @return 唯一编号
	 */
	public static String generateHexUUID() {
		return instance().generate();
	}

	/**
	 * 生成短型唯一编号 (64进制转换，同 base64 字符项)
	 * @return 唯一编号
	 */
	public static String generateShortUUID() {
		return toString(instance().createUUID(), false);
	}

	/**
	 * 获取默认生成器
	 * @return 生成器
	 */
	public static UniQueness instance() {
		UniQueness instance = ref == null ? null : ref.get();
		if (instance == null) ref = new Softref(instance = new UniQueness());
		return instance;
	}

	/**
	 * 生成唯一编号串
	 * @param uuid 唯一编号
	 * @return 字符串
	 */
	public static String string(UUID uuid) {
		return toString(uuid, true);
	}

	/**
	 * 将 UUID 转换为字符串
	 * @param uuid UUID
	 * @param hex 十六进制转换
	 * @return UUID 串
	 */
	protected static String toString(UUID uuid, boolean hex) {
		StringBuilder builder = new StringBuilder(32);
		long mostSigBits = uuid.getMostSignificantBits();
		long leastSigBits = uuid.getLeastSignificantBits();

		digits(mostSigBits >> 32, 8, builder, hex);
		digits(mostSigBits >> 16, 4, builder, hex);
		digits(mostSigBits, 4, builder, hex);
		digits(leastSigBits >> 48, 4, builder, hex);
		digits(leastSigBits, 12, builder, hex);

		return builder.toString();
	}

	/**
	 * 取数值
	 * @param val 值
	 * @param digits 长度
	 * @param builder 字符缓存区
	 * @param hex 十六进制转换
	 */
	private static void digits(long val, int digits, StringBuilder builder, boolean hex) {
		long hi = 1L << (digits * 4);
		val = hi | (val & (hi - 1));
		if (hex) {
			String v = null;
			v = Radix64.toHex(val).toLowerCase();
			builder.append(v, 1, v.length());
		} else {
			builder.append(Radix64.toString(val, 62));
		}
	}

	/**
	 * 加载文件种子
	 * @return 种子数据
	 */
	private static byte[] load() {
		String p = UniQueness.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		return MD5Cipher.encode(Stringure.toBytes(p, Stringure.utf_8));
	}

	/** 基础数据 */
	private final byte[] base;
	/** 随机数 */
	private final Random rd;

	/** 时间信息 */
	private long[] time;

	private Ref<Method> mref;

	/**
	 * 构造函数
	 */
	public UniQueness() {
		this(load());
	}

	/**
	 * 构造函数
	 * @param seed 6 位种子数据
	 */
	public UniQueness(byte[] seed) {
		Random s = seed == null ? new SecureRandom() : new SecureRandom(seed);
		rd = new Random(s.nextLong());
		base = new byte[12];
		time = new long[2];

		int l = 0;
		if (seed != null && seed.length >= 2) {
			l = Math.min(4, seed.length);
			System.arraycopy(seed, 0, base, 0, l);
		}
		if (l < 6) {
			for (int i = l; i < 6; i++)
				base[i] = random();
		}
		System.arraycopy(address(), 0, base, 6, 6);
	}

	/**
	 * 生成串唯一编号
	 * @return 唯一编号
	 */
	public UUID createUUID() {
		return UUID.nameUUIDFromBytes(generateData());
	}

	/**
	 * 生成唯一编号串
	 * @return 唯一编号
	 */
	public String generate() {
		return MD5Cipher.encodeHex(generateData()).toLowerCase();
	}

	/**
	 * 生成唯一数据
	 * @return 数据内容
	 */
	protected byte[] generateData() {
		byte[] b = Radix64.toHex(time()).getBytes();
		byte[] data = new byte[b.length + 14];

		int r = rd.nextInt();
		data[0] = (byte) (r & 0xff);
		data[1] = (byte) ((r >> 8) & 0xff);

		System.arraycopy(base, 0, data, 2, 12);
		System.arraycopy(b, 0, data, 14, b.length);
		return data;
	}

	/**
	 * 获取 IP 地址信息
	 * @return 地址信息
	 */
	byte[] address() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				byte[] b = address(en.nextElement());
				if (b != null) return b;
			}
			return new byte[] { random(), 127, 0, 0, 1, random() };
		} catch (Throwable t) {
			throw Converter.P.convert(t, RuntimeException.class);
		}
	}

	/**
	 * 获取当前时间信息
	 * @return 时间信息
	 */
	long time() {
		long t = 0;
		synchronized (base) {
			long now = System.currentTimeMillis();
			if (now > time[0]) {
				time[0] = now;
				t = time[1] = now << 20;
			} else {
				t = time[1] = time[1] + 1;
			}
		}
		return t;
	}

	/**
	 * 获取网卡地址信息
	 * @param network 网卡信息
	 * @return 地址信息
	 * @throws Throwable
	 */
	private byte[] address(NetworkInterface network) throws Throwable {
		if (network == null) return null;
		Enumeration<InetAddress> en = network.getInetAddresses();
		while (en.hasMoreElements()) {
			InetAddress address = en.nextElement();
			if (address.isLoopbackAddress()) continue;
			byte[] b = hardware(network);
			if (b != null && b.length == 6) return b;

			if (!(address instanceof Inet4Address)) continue;
			byte[] v = { random(), 0, 0, 0, 0, random() };
			System.arraycopy(address.getAddress(), 0, v, 1, 4);
			return v;
		}
		return null;
	}

	/**
	 * 获取 MAC 地址
	 * @param network 网卡信息
	 * @return MAC 地址
	 * @throws Throwable
	 */
	private byte[] hardware(NetworkInterface network) throws Throwable {
		Method m = mref == null ? null : mref.get();
		if (m == null) {
			try {
				m = NetworkInterface.class.getMethod("getHardwareAddress");
				mref = new Softref(m);
			} catch (SecurityException e) {
				return null;
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
		try {
			return byte[].class.cast(m.invoke(network));
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	/**
	 * 生成随机字节
	 * @return 字节
	 */
	private byte random() {
		return (byte) rd.nextInt(Byte.MAX_VALUE);
	}
}

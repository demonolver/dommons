/*
 * @(#)Procontent.java     2017-12-19
 */
package org.dommons.core;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.dommons.core.convert.Converter;
import org.dommons.core.string.Stringure;

/**
 * 进程信息
 * @author demon 2017-12-19
 */
public class Procontent {

	/**
	 * 获取启动参数集
	 * @return 参数集
	 */
	public static List<String> getArguments() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments();
	}

	/**
	 * 获取资源路径
	 * @return 资源路径
	 */
	public static String getClasspath() {
		return ManagementFactory.getRuntimeMXBean().getClassPath();
	}

	/**
	 * 获取当前进程号
	 * @return 进程号
	 */
	public static long pid() {
		String n = ManagementFactory.getRuntimeMXBean().getName();
		String[] s = Stringure.extract(n, "[0-9]+(?=\\@)", 1);
		return Converter.P.convert(s[0], long.class);
	}
}

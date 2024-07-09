/*
 * @(#)AbstractNLS.java     2024-07-09
 */
package org.dommons.io.nls;

import java.io.PrintStream;

/**
 * 抽象多语言信息
 * @author demon 2024-07-09
 */
public abstract class AbstractNLS implements NLS {

	/**
	 * 启动自检
	 * @param nls 多语言信息体
	 * @param out 结果输出
	 */
	public static void startEvaluate(NLS nls, PrintStream out) {
		if (out != null && nls instanceof AbstractNLS) ((AbstractNLS) nls).startEvaluate(out);
	}

	/**
	 * 启动自检
	 * @param out 结果输出
	 */
	protected abstract void startEvaluate(PrintStream out);
}

/*
 * @(#)Emchar.java     2020-07-31
 */
package org.dommons.core.string;

import java.awt.Font;
import java.awt.FontMetrics;

import org.dommons.core.util.Arrayard;

/**
 * 空字符处理
 * @author demon 2020-07-31
 */
class Emchar {

	static final char[] es = { 0, 1, 8, 127, 1807, 8203, 8204, 8205, 8206, 8207, 8234, 8235, 8236, 8237, 8238, 8288, 8289, 8290, 8291, 8292,
			8298, 8299, 8300, 8301, 8302, 8303 };

	static FontMetrics fm;

	/**
	 * 是否空字符
	 * @param ch 字符
	 * @return 是、否
	 */
	static boolean isEmpty(char ch) {
		if (Arrayard.contains(ch, es)) return true;
		else if (ch < 60000) return false;
		return fm().charWidth(ch) <= 0;
	}

	/**
	 * 获取字体属性
	 * @return 字体属性
	 */
	@SuppressWarnings("restriction")
	static FontMetrics fm() {
		if (fm == null) {
			synchronized (Emchar.class) {
				if (fm == null) {
					fm = sun.font.FontDesignMetrics.getMetrics(new Font(null, Font.PLAIN, 6));
				}
			}
		}
		return fm;
	}
}

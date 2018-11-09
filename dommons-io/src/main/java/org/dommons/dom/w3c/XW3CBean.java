/*
 * @(#)XW3CBean.java     2011-11-1
 */
package org.dommons.dom.w3c;

import org.dommons.dom.bean.XBean;
import org.w3c.dom.Node;

/**
 * w3c XML 内容基本接口
 * @author Demon 2011-11-1
 */
public abstract class XW3CBean implements XBean {

	/**
	 * 获取目标内容
	 * @return 目标内容
	 */
	protected abstract Node target();
}

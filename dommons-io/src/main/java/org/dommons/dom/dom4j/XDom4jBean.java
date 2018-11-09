/*
 * @(#)XDom4jBean.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import java.io.IOException;
import java.io.StringWriter;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dommons.core.Silewarner;
import org.dommons.dom.bean.XBean;

/**
 * dom4j XML 抽象结构体
 * @author Demon 2011-11-1
 */
public abstract class XDom4jBean implements XBean {

	/**
	 * 生成 XML 内容串
	 * @return XML 内容串
	 */
	public String asXML() {
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, OutputFormat.createCompactFormat());
		try {
			writer.write(target());
		} catch (IOException e) {
			Silewarner.warn(XDom4jBean.class, "XML string generate fail", e);
		}
		return out.toString();
	}

	/**
	 * 获取目标节点
	 * @return 目标节点
	 */
	protected abstract Node target();
}

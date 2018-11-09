/*
 * @(#)XW3CText.java     2012-3-14
 */
package org.dommons.dom.w3c;

import org.dommons.core.string.Stringure;
import org.dommons.dom.bean.XText;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * w3c XML 文本节点
 * @author Demon 2012-3-14
 */
public class XW3CText extends XW3CBean implements XText {

	private Text text;

	protected XW3CText(Text text) {
		this.text = text;
	}

	public String content() {
		return text != null ? text.getData() : Stringure.empty;
	}

	public int type() {
		return Type_Text;
	}

	protected Node target() {
		return text;
	}
}

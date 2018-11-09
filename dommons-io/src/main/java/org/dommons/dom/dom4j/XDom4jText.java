/*
 * @(#)XDomText.java     2012-3-14
 */
package org.dommons.dom.dom4j;

import org.dom4j.CharacterData;
import org.dom4j.Node;
import org.dommons.dom.bean.XText;

/**
 * dom4j XML 文本节点
 * @author Demon 2012-3-14
 */
public class XDom4jText extends XDom4jBean implements XText {

	private final CharacterData text;

	protected XDom4jText(CharacterData text) {
		this.text = text;
	}

	public String content() {
		return text.getText();
	}

	public String toString() {
		return text.asXML();
	}

	public int type() {
		return Type_Text;
	}

	protected Node target() {
		return text;
	}
}

/*
 * @(#)XDom4jComment.java     2011-11-1
 */
package org.dommons.dom.dom4j;

import org.dom4j.Comment;
import org.dom4j.Node;
import org.dommons.dom.bean.XComment;

/**
 * dom4j XML 注释
 * @author Demon 2011-11-1
 */
public class XDom4jComment extends XDom4jBean implements XComment {

	/** 目标注释 */
	private final Comment comment;

	/**
	 * 构造函数
	 * @param comment 目标注释
	 */
	protected XDom4jComment(Comment comment) {
		if (comment == null) throw new NullPointerException();
		this.comment = comment;
	}

	public String getContext() {
		return comment.getText();
	}

	public int type() {
		return Type_Comment;
	}

	/**
	 * 获取目标注释
	 * @return 注释
	 */
	protected Comment comment() {
		return comment;
	}

	protected Node target() {
		return comment();
	}
}
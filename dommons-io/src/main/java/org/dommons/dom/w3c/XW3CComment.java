/*
 * @(#)XW3CComment.java     2011-11-1
 */
package org.dommons.dom.w3c;

import org.dommons.dom.bean.XComment;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

/**
 * w3c XML 注释
 * @author Demon 2011-11-1
 */
public class XW3CComment extends XW3CBean implements XComment {

	/** 目标注释 */
	private final Comment comment;

	protected XW3CComment(Comment comment) {
		if (comment == null) throw new NullPointerException();
		this.comment = comment;
	}

	public String getContext() {
		return comment.getData();
	}

	public String toString() {
		return new StringBuilder().append("<!--").append(getContext()).append("-->").toString();
	}

	public int type() {
		return Type_Comment;
	}

	/**
	 * 获取目标注释
	 * @return 目标注释
	 */
	protected Comment comment() {
		return comment;
	}

	protected Node target() {
		return comment();
	}
}
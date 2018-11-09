/*
 * @(#)CaseInsensitiveHashSet.java     2012-3-19
 */
package org.dommons.core.collections.set;

import java.util.LinkedHashMap;

import org.dommons.core.collections.CaseInsensitiveWrapper;
import org.dommons.core.collections.map.ci.CaseInsensitiveHashMap;

/**
 * 无视大小写的哈希无重复数据集
 * @author Demon 2012-3-19
 */
public class CaseInsensitiveHashSet extends AbsSet<String> {

	private static final long serialVersionUID = 3701009790073441750L;

	/**
	 * 构造函数
	 */
	public CaseInsensitiveHashSet() {
		this(false);
	}

	/**
	 * 构造函数
	 * @param linked 是否保持顺序
	 */
	public CaseInsensitiveHashSet(boolean linked) {
		super(linked ? CaseInsensitiveWrapper.wrap(new LinkedHashMap(), true) : new CaseInsensitiveHashMap(true));
	}
}

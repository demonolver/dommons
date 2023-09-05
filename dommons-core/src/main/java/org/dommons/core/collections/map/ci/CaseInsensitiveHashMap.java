/*
 * @(#)CaseInsensitiveHashMap.java     2011-10-19
 */
package org.dommons.core.collections.map.ci;

import java.util.HashMap;

/**
 * 无视键值大小写的映射表
 * @author Demon 2011-10-19
 */
public class CaseInsensitiveHashMap<V> extends AbsCaseInsensitiveMap<V> {

	private static final long serialVersionUID = 6108945280312259651L;

	/**
	 * 构造函数
	 */
	public CaseInsensitiveHashMap() {
		this(false);
	}

	/**
	 * 构造函数
	 * @param caseInsensitive 是否默认写入无视大小写
	 */
	public CaseInsensitiveHashMap(boolean caseInsensitive) {
		super(new HashMap(), new HashMap(), caseInsensitive);
	}
}

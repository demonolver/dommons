/*
 * @(#)XValidation.java     2011-11-1
 */
package org.dommons.dom;

/**
 * XML 内容校验
 * @author Demon 2011-11-1
 */
public interface XValidation {

	/** 无校验 */
	int NONE = 0;
	/** DTD 校验 */
	int DTD = 1;
	/** Schema 校验 */
	int XSD = 2;
}

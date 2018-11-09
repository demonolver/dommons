/*
 * @(#)ContentWriter.java     2011-10-17
 */
package org.dommons.io.file;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件内容写入器
 * @author Demon 2011-10-17
 */
public interface ContentWriter {

	/**
	 * 写入内容
	 * @param out 输出流
	 * @throws IOException
	 */
	void write(OutputStream out) throws IOException;
}

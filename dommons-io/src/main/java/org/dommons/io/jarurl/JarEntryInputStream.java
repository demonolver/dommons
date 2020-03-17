/*
 * @(#)JarEntryInputStream.java     2020-03-17
 */
package org.dommons.io.jarurl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jar 子项输入流
 * @author demon 2020-03-17
 */
class JarEntryInputStream extends FilterInputStream {

	protected final boolean closeable;

	protected JarEntryInputStream(InputStream in, boolean closeable) {
		super(in);
		this.closeable = closeable;
	}

	@Override
	public void close() throws IOException {
		if (closeable) super.close();
	}
}

/*
 * @(#)MultiWriter.java     2013-5-7
 */
package org.dommons.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 多流输出器
 * @author Demon 2013-5-7
 */
public class MultiWriter extends Writer {

	private final Collection<Appendable> appends;

	/**
	 * 构造函数
	 * @param appends 输出集
	 */
	public MultiWriter(Appendable... appends) {
		if (appends == null) throw new NullPointerException();
		this.appends = new LinkedHashSet(appends.length);
		for (Appendable a : appends) {
			if (a != null) this.appends.add(a);
		}
	}

	public Writer append(char c) throws IOException {
		for (Appendable a : appends) {
			a.append(c);
		}
		return this;
	}

	public Writer append(CharSequence csq) throws IOException {
		for (Appendable a : appends) {
			a.append(csq);
		}
		return this;
	}

	public Writer append(CharSequence csq, int start, int end) throws IOException {
		for (Appendable a : appends) {
			a.append(csq, start, end);
		}
		return this;
	}

	public void close() throws IOException {
		for (Appendable a : appends) {
			try {
				if (a instanceof Writer) ((Writer) a).close();
			} catch (IOException e) {
			}
		}
	}

	public void flush() throws IOException {
		for (Appendable a : appends) {
			if (a instanceof Writer) ((Writer) a).flush();
		}
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		for (Appendable a : appends) {
			if (a instanceof Writer) ((Writer) a).write(cbuf, off, len);
			else a.append(new String(cbuf, off, len));
		}
	}

	public void write(int c) throws IOException {
		append((char) c);
	}
}

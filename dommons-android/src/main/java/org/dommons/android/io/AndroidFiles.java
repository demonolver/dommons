/*
 * @(#)AndroidFiles.java     2015-7-7
 */
package org.dommons.android.io;

import java.io.File;

import org.dommons.android.ContextSet;
import org.dommons.io.file.Filenvironment;

/**
 * 安卓文件系统环境
 * @author Demon 2015-7-7
 */
public class AndroidFiles extends Filenvironment {

	public AndroidFiles() {
		super();
	}

	public File cacheFile(String name) {
		return ContextSet.cacheFile(name);
	}
}

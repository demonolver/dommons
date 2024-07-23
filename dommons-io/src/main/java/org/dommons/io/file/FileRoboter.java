/*
 * @(#)FileRoboter.java     2011-10-17
 */
package org.dommons.io.file;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dommons.core.Assertor;
import org.dommons.core.string.Stringure;
import org.dommons.io.Pathfinder;
import org.dommons.security.coder.HexCoder;

/**
 * 文件工具类
 * @author Demon 2011-10-17
 */
public final class FileRoboter {
	private static final int PACKAGE_SIZE = 256 * 104;

	static final int BUFFER_SIZE = 1024;

	/**
	 * 清空文件夹
	 * @param directory 文件夹对象
	 * @return 是否成功
	 */
	public static boolean clearDirectory(File directory) {
		Assertor.F.notNull(directory);
		if (!directory.exists()) return true;
		if (!directory.isDirectory()) return false;

		boolean ret = true;
		for (File f : directory.listFiles()) {
			boolean del = false;
			if (f.isFile()) {
				del = f.delete();
			} else if (f.isDirectory()) {
				del = clearDirectory(f) && f.delete();
			}
			if (!del && ret) ret = del;
		}

		return ret;
	}

	/**
	 * 复制文件
	 * @param file 待复制文件 包括：文件、文件夹
	 * @param target 目标文件夹
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File file, File target) throws IOException {
		return copy(file, target, false);
	}

	/**
	 * 复制文件
	 * @param file 待复制文件 包括：文件、文件夹
	 * @param target 目标文件夹
	 * @param overlap 是否覆盖
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File file, File target, boolean overlap) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!file.exists()) return null;

		if (file.isFile()) {
			if ((!target.exists() || !target.isDirectory()) && !target.mkdirs()) {
				throw new IllegalArgumentException("Illegal target directory path : " + target.getAbsolutePath());
			}
			return duplicate(file, target, false, overlap);
		} else {
			if (Assertor.P.equals(file.getParentFile(), target)) return null;
			return duplicate(file, target, FileFilters.allTrue(), true, false, overlap);
		}
	}

	/**
	 * 复制文件夹到指定目录
	 * @param directory 待复制文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param loop 是否复制子目录
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File directory, File target, FileFilter fileFilter, boolean loop) throws IOException {
		return copy(directory, target, fileFilter, loop, false);
	}

	/**
	 * 复制文件夹到指定目录
	 * @param directory 待复制文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param loop 是否复制子目录
	 * @param overlap 是否覆盖
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File directory, File target, FileFilter fileFilter, boolean loop, boolean overlap) throws IOException {
		return copy(directory, target, fileFilter, loop ? FileFilters.allTrue() : FileFilters.allFalse(), overlap);
	}

	/**
	 * 复制文件夹到指定目录
	 * @param directory 待复制文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File directory, File target, FileFilter fileFilter, FileFilter dirFilter) throws IOException {
		return copy(directory, target, fileFilter, dirFilter, false);
	}

	/**
	 * 复制文件夹到指定目录
	 * @param directory 待复制文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @param overlap 是否覆盖
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copy(File directory, File target, FileFilter fileFilter, FileFilter dirFilter, boolean overlap) throws IOException {
		Assertor.F.notNull(fileFilter, "The file filter is must not be null!");
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists()) return null;
		if (directory.isFile()) throw new IllegalArgumentException("The source file is must be a directory!");

		if (Assertor.P.equals(directory.getParentFile(), target)) return null;

		FileFilter filter = FileFilters.or(FileFilters.and(FileFilters.isFile(), fileFilter),
			FileFilters.and(FileFilters.isDirectory(), dirFilter == null ? FileFilters.allFalse() : dirFilter));
		return duplicate(directory, target, filter, true, false, overlap);
	}

	/**
	 * 复制所有子文件到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copyAllSub(File directory, File target) throws IOException {
		return copyAllSub(directory, target, false);
	}

	/**
	 * 复制所有子文件到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @param overlap 是否覆盖
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copyAllSub(File directory, File target, boolean overlap) throws IOException {
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists() || !directory.isDirectory()) return null;
		if (Assertor.P.equals(directory, target)) return null;

		return duplicate(directory, target, FileFilters.allTrue(), false, false, overlap);
	}

	/**
	 * 复制子文件到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param loop 是否复制子文件夹
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copySubFiles(File directory, File target, FileFilter fileFilter, boolean loop) throws IOException {
		return copySubFiles(directory, target, fileFilter, loop ? FileFilters.allTrue() : FileFilters.allFalse());
	}

	/**
	 * 复制子文件到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copySubFiles(File directory, File target, FileFilter fileFilter, FileFilter dirFilter) throws IOException {
		return copySubFiles(directory, target, fileFilter, dirFilter, false);
	}

	/**
	 * 复制子文件到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @param overlap 是否覆盖
	 * @return 副本文件
	 * @throws IOException 复制出错
	 */
	public static File copySubFiles(File directory, File target, FileFilter fileFilter, FileFilter dirFilter, boolean overlap)
			throws IOException {
		Assertor.F.notNull(fileFilter, "The file filter is must not be null!");
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists() || !directory.isDirectory()) return null;
		if (Assertor.P.equals(directory, target)) return null;

		FileFilter filter = FileFilters.or(FileFilters.and(FileFilters.isFile(), fileFilter),
			FileFilters.and(FileFilters.isDirectory(), dirFilter == null ? FileFilters.allFalse() : dirFilter));
		return duplicate(directory, target, filter, false, false, overlap);
	}

	/**
	 * 删除文件
	 * @param file 文件对象
	 * @return 是否成功
	 */
	public static boolean delete(File file) {
		if (file == null) return false;
		if (file.isDirectory()) clearDirectory(file);
		return file.delete();
	}

	/**
	 * 列出文件夹中文件路径 包括子目录
	 * @param file 文件夹对象
	 * @param filter 过滤条件
	 * @return 文件路径数组
	 */
	public static String[] list(File file, FileFilter filter) {
		return list(file, filter, true);
	}

	/**
	 * 列出文件夹中文件路径
	 * @param file 文件夹对象
	 * @param filter 过滤条件
	 * @param loop 是否包含子目录
	 * @return 文件路径数组
	 */
	public static String[] list(File file, FileFilter filter, boolean loop) {
		return list(file, filter, loop ? FileFilters.allTrue() : FileFilters.allFalse());
	}

	/**
	 * 列出文件夹中文件路径
	 * @param file 文件夹对象
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @return 文件路径数组
	 */
	public static String[] list(File file, FileFilter fileFilter, FileFilter dirFilter) {
		return toPaths(getSubFileList(file, fileFilter, dirFilter));
	}

	/**
	 * 列出文件路径
	 * @param base 根文件夹
	 * @param pattern 文件路径模板 如:<code>temp/&#42;&#42;/&#42;-cfg.xml</code>
	 * @return 文件路径数组
	 */
	public static String[] list(File base, String pattern) {
		return toPaths(listFiles(base, pattern));
	}

	/**
	 * 列出文件路径
	 * @param pattern 文件路径模板 如:<code>D:/&#42;&#42;/&#42;-cfg.xml</code>
	 * @return 文件路径数组
	 */
	public static String[] list(String pattern) {
		return list(null, pattern);
	}

	/**
	 * 列出文件夹子文件 包含子目录
	 * @param file 文件夹对象
	 * @param filter 过滤条件
	 * @return 文件数组
	 */
	public static File[] listFiles(File file, FileFilter filter) {
		return listFiles(file, filter, true);
	}

	/**
	 * 列出文件夹子文件
	 * @param file 文件夹对象
	 * @param filter 过滤条件
	 * @param loop 是否包含子目录
	 * @return 文件数组
	 */
	public static File[] listFiles(File file, FileFilter filter, boolean loop) {
		return listFiles(file, filter, loop ? FileFilters.allTrue() : FileFilters.allFalse());
	}

	/**
	 * 列出文件夹子文件
	 * @param file 文件夹对象
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 子文件夹过滤条件
	 * @return 文件数组
	 */
	public static File[] listFiles(File file, FileFilter fileFilter, FileFilter dirFilter) {
		return getSubFileList(file, fileFilter, dirFilter).toArray(new File[0]);
	}

	/**
	 * 列出文件路径
	 * @param base 根文件夹
	 * @param pattern 文件路径模板 如:<code>temp/&#42;&#42;/&#42;-cfg.xml</code>
	 * @return 文件数组
	 */
	public static File[] listFiles(File base, String pattern) {
		Assertor.F.notEmpty(pattern);
		if (base != null && (!base.exists() || !base.isDirectory())) throw new IllegalArgumentException("Invalid base file!");
		PatternParser parser = PatternParser.compile(base, pattern);
		return listFiles(parser.getDirectory(), parser.getFilter(), parser.isLoop());
	}

	/**
	 * 列出文件路径
	 * @param pattern 文件路径模板 如:<code>D:/&#42;&#42;/&#42;-cfg.xml</code>
	 * @return 文件数组
	 */
	public static File[] listFiles(String pattern) {
		return listFiles(null, pattern);
	}

	/**
	 * 生成文件 MD5 特征码
	 * @param file 文件
	 * @return MD5 特征码
	 * @throws IOException
	 */
	public static String md5(File file) throws IOException {
		if (file == null) return null;
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			return md5(is);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 生成文件 MD5 特征码
	 * @param is 输入流
	 * @return MD5 特征码
	 */
	public static String md5(InputStream is) {
		if (is == null) return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = new byte[128];
			for (int r = 0; (r = is.read(bs)) != -1;)
				digest.update(bs, 0, r);
			String md5 = HexCoder.encodeBuffer(digest.digest()).toLowerCase();
			return md5;
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 生成文件 MD5 特征码
	 * @param url 文件路径
	 * @return MD5 特征码
	 * @throws IOException
	 */
	public static String md5(URL url) throws IOException {
		if (url == null) return null;
		InputStream is = null;
		try {
			is = url.openStream();
			return md5(is);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 移动文件到指定目录
	 * @param file 待移动文件 包括：文件、文件夹
	 * @param target 目标文件夹
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File move(File file, File target) throws IOException {
		return move(file, target, false);
	}

	/**
	 * 移动文件到指定目录
	 * @param file 待移动文件 包括：文件、文件夹
	 * @param target 目标文件夹
	 * @param overlap 是否覆盖
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File move(File file, File target, boolean overlap) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!file.exists()) return null;

		if (file.isFile()) {
			if ((!target.exists() || !target.isDirectory()) && !target.mkdirs()) {
				throw new IllegalArgumentException("Illegal target directory path : " + target.getAbsolutePath());
			}
			return duplicate(file, target, true, overlap);
		} else {
			if (Assertor.P.equals(file.getParentFile(), target)) return file;
			return duplicate(file, target, FileFilters.allTrue(), true, true, overlap);
		}
	}

	/**
	 * 移动文件夹到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File move(File directory, File target, FileFilter fileFilter, FileFilter dirFilter) throws IOException {
		return move(directory, target, fileFilter, dirFilter, false);
	}

	/**
	 * 移动文件夹到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @param overlap 是否覆盖
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File move(File directory, File target, FileFilter fileFilter, FileFilter dirFilter, boolean overlap) throws IOException {
		Assertor.F.notNull(fileFilter, "The file filter is must not be null!");
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists() || !directory.isDirectory()) return null;
		if (Assertor.P.equals(directory.getParentFile(), target)) return directory;

		FileFilter filter = FileFilters.or(FileFilters.and(FileFilters.isFile(), fileFilter),
			FileFilters.and(FileFilters.isDirectory(), dirFilter == null ? FileFilters.allFalse() : dirFilter));

		return duplicate(directory, target, filter, true, true, overlap);
	}

	/**
	 * 移动所有子文件到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File moveAllSub(File directory, File target) throws IOException {
		return moveAllSub(directory, target, false);
	}

	/**
	 * 移动所有子文件到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @param overlap 是否覆盖
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File moveAllSub(File directory, File target, boolean overlap) throws IOException {
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists() || !directory.isDirectory()) return null;
		if (Assertor.P.equals(directory, target)) return directory;

		return duplicate(directory, target, FileFilters.allTrue(), false, true, overlap);
	}

	/**
	 * 移动子文件到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File moveSubFiles(File directory, File target, FileFilter fileFilter, FileFilter dirFilter) throws IOException {
		return moveSubFiles(directory, target, fileFilter, dirFilter, false);
	}

	/**
	 * 移动子文件到指定目录
	 * @param directory 待移动文件夹
	 * @param target 目标文件夹
	 * @param fileFilter 文件过滤条件
	 * @param dirFilter 文件夹过滤条件
	 * @param overlap 是否覆盖
	 * @return 移动后文件
	 * @throws IOException 移动出错
	 */
	public static File moveSubFiles(File directory, File target, FileFilter fileFilter, FileFilter dirFilter, boolean overlap)
			throws IOException {
		Assertor.F.notNull(fileFilter, "The file filter is must not be null!");
		Assertor.F.notNull(directory, "The directory is must not be null!");
		Assertor.F.notNull(target, "The target directory is must not be null!");

		if (!directory.exists() || !directory.isDirectory()) return null;
		if (Assertor.P.equals(directory, target)) return directory;

		FileFilter filter = FileFilters.or(FileFilters.and(FileFilters.isFile(), fileFilter),
			FileFilters.and(FileFilters.isDirectory(), dirFilter == null ? FileFilters.allFalse() : dirFilter));
		return duplicate(directory, target, filter, false, true, overlap);
	}

	/**
	 * 读取文件内容
	 * @param url 文件路径
	 * @param os
	 * @throws IOException
	 */
	public static void read(URL url, OutputStream os) throws IOException {
		assert url != null && os != null;
		InputStream is = null;
		try {
			is = url.openStream();
			duplicate(is, os);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 读取字符串内容
	 * @param url 文件路径
	 * @return 字符串内容
	 * @throws IOException
	 */
	public static String string(URL url) throws IOException {
		return string(url, null);
	}

	/**
	 * 读取字符串内容
	 * @param url 文件路径
	 * @param charset 字符集
	 * @return 字符串内容
	 * @throws IOException
	 */
	public static String string(URL url, String charset) throws IOException {
		assert url != null;
		InputStream is = null;
		try {
			return string(is = url.openStream(), charset);
		} finally {
			if (is != null) is.close();
		}
	}

	/**
	 * 写入文件内容
	 * @param file 文件对象
	 * @param content 内容
	 * @throws IOException
	 */
	public static void write(File file, byte[] content) throws IOException {
		Assertor.F.notNull(content, "The content is must not be null!");
		write(file, content, null);
	}

	/**
	 * 写入文件内容
	 * @param file 文件对象
	 * @param writer 内容写入器
	 * @throws IOException
	 */
	public static void write(File file, ContentWriter writer) throws IOException {
		Assertor.F.notNull(writer, "The writer is must not be null!");
		write(file, null, writer);
	}

	/**
	 * 读取字符串内容
	 * @param is 输入流
	 * @param charset 字符集
	 * @return 内容
	 * @throws IOException
	 */
	protected static String string(InputStream is, String charset) throws IOException {
		Reader r = null;
		try {
			CharsetDecoder decoder = Stringure.charset(true, charset).newDecoder().onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT);
			r = new InputStreamReader(is, decoder);
			StringBuilder buf = new StringBuilder();
			char[] cs = new char[64];
			for (int x = 0; (x = r.read(cs)) != -1;) {
				buf.append(cs, 0, x);
			}
			return buf.toString();
		} finally {
			if (r != null) r.close();
		}
	}

	/**
	 * 复制文件
	 * @param file 源文件
	 * @param target 目标文件
	 * @throws IOException
	 */
	static void duplicate(File file, File target) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new FileOutputStream(target);
			duplicate(in, out);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * 复制文件
	 * @param file 文件对象
	 * @param target 目标文件夹
	 * @param remove 是否移除
	 * @param overlap 是否覆盖
	 * @return 复制后文件对象
	 * @throws IOException 复制出错
	 */
	static File duplicate(File file, File target, boolean remove, boolean overlap) throws IOException {
		String name = file.getName();
		if (Assertor.P.equals(file.getParentFile(), target)) {
			if (remove || overlap) return file;
			name = "CopyOf" + name;
		}
		File tf = new File(target, name);
		cp: {
			for (int i = 1; i <= 5; i++) {
				if (!tf.exists()) break;
				if (overlap) { // 覆盖已有文件，则尝试比较两个文件是否完全一致
					if (file.length() == tf.length()) {
						String fmd5 = md5(file), tmd5 = md5(tf);
						if (Stringure.equalsIgnoreCase(fmd5, tmd5)) break cp;
					}
					delete(tf); // 不一致删除原重复文件
				} else {
					tf = new File(target, name + "(" + i + ")");
				}
			}
			// 如复制后移除直接调用重命名功能
			if (remove && file.renameTo(tf)) return tf;
			if (!tf.exists()) tf.createNewFile();
			duplicate(file, tf);
		}
		if (remove) file.delete();
		return tf;
	}

	/**
	 * 复制文件夹到指定目录
	 * @param directory 文件夹对象
	 * @param target 目标文件夹
	 * @param filter 过滤条件
	 * @param overTop 是否包含顶层文件夹
	 * @param remove 是否移除
	 * @param overlap 是否覆盖
	 * @return 复制后文件对象
	 * @throws IOException 复制出错
	 */
	static File duplicate(File directory, File target, FileFilter filter, boolean overTop, boolean remove, boolean overlap)
			throws IOException {
		File tarDir = null;
		if (overTop) tarDir = new File(target, directory.getName()); // 包含顶层构建新顶层文件夹
		else tarDir = target;
		if ((!tarDir.exists() || !tarDir.isDirectory()) && !tarDir.mkdirs())
			throw new IllegalArgumentException("Illegal target directory path : " + target.getAbsolutePath());

		target = tarDir;

		for (File file : directory.listFiles(filter)) {
			if (file.isFile()) duplicate(file, target, remove, overlap);
			else if (file.isDirectory()) duplicate(file, target, filter, true, remove, overlap);
		}

		if (remove) directory.delete();
		return target;
	}

	/**
	 * 复制内容
	 * @param in 输入流
	 * @param out 输出流
	 * @throws IOException
	 */
	static void duplicate(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[PACKAGE_SIZE];
		int l = 0;
		while ((l = in.read(b)) > 0) {
			out.write(b, 0, l);
		}
		out.flush();
	}

	/**
	 * 复制压缩文件
	 * @param zip 压缩文件
	 * @param entry 文件项
	 * @param target 目标文件
	 * @throws IOException
	 */
	static void duplicate(ZipFile zip, ZipEntry entry, File target) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = zip.getInputStream(entry);
			out = new FileOutputStream(target);
			duplicate(in, out);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * 获取文件夹子文件列表
	 * @param file 文件夹对象
	 * @param filter 过滤条件
	 * @param loop 是否包含子目录
	 * @return 文件列表
	 */
	static Collection<File> getSubFileList(File file, FileFilter fileFilter, FileFilter dirFilter) {
		Assertor.F.notNull(file, "The file is must not be null!");
		if (!file.exists()) return Collections.EMPTY_LIST;
		Assertor.F.notNull(fileFilter, "The file filter is must not be null!");

		FileFilter filter = FileFilters.or(FileFilters.and(FileFilters.isFile(), fileFilter),
			FileFilters.and(FileFilters.isDirectory(), dirFilter == null ? FileFilters.allFalse() : dirFilter));

		Collection<File> list = new LinkedList();
		innerFileList(list, file, filter);
		return list;
	}

	/**
	 * 文件列表迭代加入
	 * @param list 文件列表
	 * @param dir 文件夹
	 * @param filter 过滤器
	 */
	static void innerFileList(Collection<File> list, File dir, FileFilter filter) {
		Assertor.F.notNull(dir, "The directory is must not be null!");
		Assertor.F.notNull(filter, "The filter is must not be null!");
		if (!dir.isDirectory()) return;

		for (File file : dir.listFiles(filter)) {
			if (file.isDirectory()) {
				innerFileList(list, file, filter);
			} else {
				list.add(file);
			}
		}
	}

	static String[] toPaths(Collection<File> files) {
		Collection<String> list = new ArrayList(files.size());
		for (File file : files) {
			list.add(Pathfinder.getCanonicalPath(file));
		}
		return list.toArray(new String[list.size()]);
	}

	static String[] toPaths(File... files) {
		Collection<String> list = new ArrayList(files.length);
		for (File file : files) {
			list.add(Pathfinder.getCanonicalPath(file));
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * 写入文件
	 * @param file 文件对象
	 * @param content 内容
	 * @param writer 内容写入器
	 * @throws IOException 写入出错
	 */
	static void write(File file, byte[] content, ContentWriter writer) throws IOException {
		Assertor.F.notNull(file, "The output file is must not be null!");
		file = file.getAbsoluteFile();
		File parent = file.getParentFile();
		if (!parent.exists() || !parent.isDirectory()) parent.mkdirs();
		File tFile = null;
		try {
			// 文件存在以临时文件写入
			if (!file.exists()) {
				tFile = file;
			} else {
				tFile = new File(file.getAbsolutePath() + '.' + System.currentTimeMillis());
			}
			int i = 0;
			// 创建临时文件 重复测试5次，不成功抛出异常
			while (true) {
				if (tFile.createNewFile()) {
					break;
				} else if (i == 5) {
					throw new IOException("Creating the file fail![" + file.getAbsolutePath() + "]");
				}
				if (tFile.delete()) {
					tFile.createNewFile();
					break;
				} else {
					tFile = new File(file.getAbsolutePath() + '.' + System.currentTimeMillis() + '.' + i);
				}
			}
			OutputStream stream = new FileOutputStream(tFile);
			try {
				// 写入文件内容
				if (content == null) {
					writer.write(stream);
				} else {
					stream = new BufferedOutputStream(stream, 8 * BUFFER_SIZE);
					stream.write(content);
				}
				stream.flush();
				// 删除原文件
				if (!file.equals(tFile) && file.exists() && !file.delete())
					throw new IOException("Delete old file fail![" + file.getAbsolutePath() + "]");
			} finally {
				if (stream != null) stream.close();
			}
			// 重命名临时文件
			tFile.renameTo(file);
		} catch (IOException e) {
			tFile.delete();
			throw e;
		}
	}

	/**
	 * 关闭IO对象
	 * @param closer 待关闭对象
	 */
	private static void close(Closeable closer) {
		if (closer == null) return;
		try {
			closer.close();
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * 构造函数
	 */
	protected FileRoboter() {}

	/**
	 * 模板解析器
	 * @author Demon 2010-9-29
	 */
	static class PatternParser {

		static final Pattern templatePattern = Pattern.compile(".*([\\?\\*]+.*)+");
		static final Pattern loopPattern = Pattern.compile("([\\?\\*][^\\\\\\/]*[\\\\\\/])|[\\*]{2,}");

		/**
		 * 编译
		 * @param base 根目录
		 * @param pattern 文件路径模板
		 * @return 模板解析器
		 */
		public static PatternParser compile(File base, String pattern) {
			PatternParser parser = new PatternParser(base, pattern);
			parser.parse();
			return parser;
		}

		private final File base;
		private final String pattern;

		private boolean loop = false;
		private File dir;
		private FileFilter filter;

		/**
		 * 构造函数
		 * @param base 根目录
		 * @param pattern 文件路径模板
		 */
		protected PatternParser(File base, String pattern) {
			this.base = base;
			this.pattern = Stringure.convertSystemVariables(pattern);
		}

		/**
		 * 获取文件目录
		 * @return 文件夹对象
		 */
		public File getDirectory() {
			return dir;
		}

		/**
		 * 获取过滤器
		 * @return 文件过滤器
		 */
		public FileFilter getFilter() {
			return filter;
		}

		/**
		 * 是否迭代子目录
		 * @return 是、否
		 */
		public boolean isLoop() {
			return loop;
		}

		/**
		 * 解析
		 */
		protected void parse() {
			StringBuilder patternBuffer = new StringBuilder();
			String tail = pattern;
			int split = 0;
			if (templatePattern.matcher(pattern).matches()) {
				int[] s = Stringure.indexOf(pattern, "^[^?*]*[\\\\\\/](?=[^\\\\\\/]*[?*])");
				if (s == null) {
					split = 0;
					this.dir = new File(base, ".");
				} else if (base != null) {
					this.dir = new File(base, pattern.substring(0, split = s[1]));
				} else {
					this.dir = Pathfinder.getFile(Pathfinder.findPath(pattern.substring(0, split = s[1])));
				}
				loop = Stringure.indexOf(pattern, loopPattern) != null;
			} else {
				File file = new File(base, pattern);
				this.dir = file.getAbsoluteFile().getParentFile();
				tail = file.getName();
				loop = false;
			}

			String path = Pathfinder.getCanonicalPath(this.dir);
			convert(path, 0, path.length(), patternBuffer);
			if (patternBuffer.length() > 0) {
				char ch = tail.charAt(split);
				if (ch != '\\' && ch != '/') patternBuffer.append("[\\\\\\/]");
			}
			convert(tail, split, tail.length(), patternBuffer);
			filter = FileFilters.matches(patternBuffer.toString(), true);
		}

		/**
		 * 转换正则表达式
		 * @param path 原路径串
		 * @param start 开始位
		 * @param end 结束位
		 * @param pattern 正则缓冲区
		 */
		void convert(String path, int start, int end, StringBuilder pattern) {
			for (int i = start; i < end; i++) {
				char ch = path.charAt(i);
				switch (ch) {
				case '\\':
				case '/':
					pattern.append("[\\\\\\/]");
					break;

				case '?': {
					int j = i;
					for (; j + 1 < end; j++) {
						if (path.charAt(j + 1) != '?') break;
					}
					pattern.append("[^\\\\\\/]");
					if (j > i) {
						pattern.append("{0,").append(j - i + 1).append('}');
						i = j;
					} else {
						pattern.append('?');
					}
					break;
				}

				case '*': {
					int s = 0;
					for (int j = i; j + 1 < end; j++) {
						ch = path.charAt(j + 1);
						if (ch == '*') {
							s |= 1;
							continue;
						} else if (ch == '\\' || ch == '/') {
							s |= 2;
							j++;
						}
						i = j;
						break;
					}

					if (s == 3) { // 连续 * 号加斜杠允许包含斜杠即路径分隔符
						pattern.append("([^\\\\\\/]*[\\\\\\/])*");
					} else if (s == 2) {
						pattern.append("[^\\\\\\/]*[\\\\\\/]");
					} else {
						pattern.append("[^\\\\\\/]*");
					}
					break;
				}

				case '.':
				case '[':
				case ']':
				case '|':
				case '{':
				case '}':
				case '(':
				case ')':
					pattern.append('\\').append(ch);
					break;

				default:
					if (ch > 0x7e) {
						String hex = Integer.toHexString(ch);
						pattern.append('\\');
						if (hex.length() <= 2) fill(hex, pattern.append('x'), '0', 2);
						else fill(hex, pattern.append('u'), '0', 4);
					} else {
						pattern.append(ch);
					}
					break;
				}
			}
		}

		/**
		 * 填充
		 * @param s 字符串
		 * @param buffer 缓存区
		 * @param c 字符串
		 * @param len 预期长度
		 */
		private void fill(String s, StringBuilder buffer, char c, int len) {
			int l = s == null ? 0 : s.length();
			for (int i = l; i < len; i++)
				buffer.append(c);

			if (l > 0) buffer.append(s);
		}
	}
}

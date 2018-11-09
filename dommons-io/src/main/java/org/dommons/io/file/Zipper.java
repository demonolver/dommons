/*
 * @(#)Zipper.java     2011-10-18
 */
package org.dommons.io.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.dommons.core.Assertor;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;
import org.dommons.io.Pathfinder;

/**
 * ZIP 压缩工具集
 * @author Demon 2011-10-18
 */
public final class Zipper {

	/** GZIP 压缩最小有效字节数, 少于有可能压缩后比原内容更大 */
	public static int gzip_min_size = 180;
	
	/**
	 * GZIP 解压
	 * @param bytes 内容
	 * @return 解压后内容
	 * @throws IOException
	 */
	public static byte[] gunzip(byte[] bytes) throws IOException {
		if (Arrayard.isEmpty(bytes)) return bytes;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length * 2);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		try {
			gunzip(bis, bos);
		} finally {
			bos.close();
			bis.close();
		}
		return bos.toByteArray();
	}

	/**
	 * 解压缩 GZIP 文件
	 * @param file gz 文件
	 * @return 解压后文件
	 * @throws IOException
	 */
	public static File gunzip(File file) throws IOException {
		return gunzip(file, false);
	}

	/**
	 * 解压缩 GZIP 文件
	 * @param file gz 文件
	 * @param cut 是否删除源文件
	 * @return 解压后文件
	 * @throws IOException
	 */
	public static File gunzip(File file, boolean cut) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		if (!file.getName().endsWith(".gz")) throw new IllegalArgumentException();
		File t = new File(file.getParentFile(), Stringure.subString(file.getName(), 0, -3));
		gunzip(file, t, cut);
		return t;
	}

	/**
	 * 解压缩 GZIP 文件
	 * @param file gz 文件
	 * @param target 目标文件
	 * @param cut 是否删除源文件
	 * @throws IOException
	 */
	public static void gunzip(final File file, File target, boolean cut) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		Assertor.F.notNull(file, "The target file is must not be null!");

		if (!file.exists() || !file.isFile())
			throw new IllegalArgumentException("File '" + Pathfinder.getCanonicalPath(file) + "' is not exist.");
		if (target.exists() && target.isFile())
			throw new IllegalArgumentException("File '" + Pathfinder.getCanonicalPath(target) + "' has been existed.");

		FileRoboter.write(target, new ContentWriter() {
			public void write(OutputStream out) throws IOException {
				InputStream in = new FileInputStream(file);
				try {
					gunzip(in, out, FileRoboter.BUFFER_SIZE);
				} finally {
					if (in != null) in.close();
				}
			}
		});

		if (cut) file.delete();
	}

	/**
	 * GZIP 解压
	 * @param is 输入
	 * @param os 输出
	 * @throws IOException
	 */
	public static void gunzip(InputStream is, OutputStream os) throws IOException {
		gunzip(is, os, 0);
	}

	/**
	 * GZIP 压缩
	 * @param bytes 内容
	 * @return 压缩后内容
	 * @throws IOException
	 */
	public static byte[] gzip(byte[] bytes) throws IOException {
		if (Arrayard.isEmpty(bytes)) return bytes;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		try {
			gzip(bis, bos);
		} finally {
			bis.close();
			bos.close();
		}
		return bos.toByteArray();
	}

	/**
	 * GZIP 压缩文件
	 * @param file 文件
	 * @return 压缩后文件
	 * @throws IOException
	 */
	public static File gzip(File file) throws IOException {
		return gzip(file, false);
	}

	/**
	 * GZIP 压缩文件
	 * @param file 文件
	 * @param cut 是否删除源文件
	 * @return 压缩后文件
	 * @throws IOException
	 */
	public static File gzip(File file, boolean cut) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		File g = new File(file.getParentFile(), file.getName() + ".gz");
		gzip(file, g, cut);
		return g;
	}

	/**
	 * GZIP 压缩文件
	 * @param file 源文件
	 * @param target 目标文件
	 * @param cut 是否删除源文件
	 * @throws IOException
	 */
	public static void gzip(final File file, File target, boolean cut) throws IOException {
		Assertor.F.notNull(file, "The file is must not be null!");
		Assertor.F.notNull(file, "The target file is must not be null!");

		if (!file.exists() || !file.isFile())
			throw new IllegalArgumentException("File '" + Pathfinder.getCanonicalPath(file) + "' is not exist.");
		if (target.exists() && target.isFile())
			throw new IllegalArgumentException("File '" + Pathfinder.getCanonicalPath(target) + "' has been existed.");

		FileRoboter.write(target, new ContentWriter() {
			public void write(OutputStream out) throws IOException {
				InputStream in = new FileInputStream(file);
				try {
					gzip(in, out, FileRoboter.BUFFER_SIZE);
				} finally {
					if (in != null) in.close();
				}
			}
		});

		if (cut) file.delete();
	}

	/**
	 * GZIP 压缩
	 * @param is 输入
	 * @param os 输出
	 * @throws IOException
	 */
	public static void gzip(InputStream is, OutputStream os) throws IOException {
		gzip(is, os, 0);
	}

	/**
	 * 解压缩
	 * @param zip 压缩文件
	 * @param target 目标目录
	 * @throws IOException
	 */
	public static void unzip(ZipFile zip, File target) throws IOException {
		if (!target.exists() || !target.isDirectory()) target.mkdirs();
		Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
		while (en.hasMoreElements()) {
			ZipEntry entry = en.nextElement();
			unzip(zip, entry, target);
		}
	}

	/**
	 * 压缩文件
	 * @param file 目标文件
	 * @return 目标压缩文件
	 * @throws IOException
	 */
	public static File zip(File file) throws IOException {
		if (file == null || !file.exists()) return null;

		return zip(file.getParentFile(), file.getName(), file);
	}

	/**
	 * 压缩文件
	 * @param tarDir 压缩存放文件夹
	 * @param name 压缩文件名
	 * @param file 目标文件
	 * @return 目标压缩文件
	 * @throws IOException
	 */
	public static File zip(File tarDir, String name, final File file) throws IOException {
		if (file == null || !file.exists()) return null;

		return zip(tarDir == null ? file.getParentFile() : tarDir, Assertor.P.empty(name) ? file.getName() : name, new File[] { file });
	}

	/**
	 * 压缩文件
	 * @param tarDir 压缩存放文件夹
	 * @param name 压缩文件名
	 * @param files 目标文件集
	 * @return 目标压缩压编文件
	 * @throws IOException
	 */
	public static File zip(File tarDir, String name, final File... files) throws IOException {
		if (files == null || files.length == 0) return null;

		if (tarDir == null) tarDir = new File(".");

		File tar = new File(tarDir, Assertor.P.empty(name) ? tarDir.getName() : name + ".zip");
		FileRoboter.write(tar, new ContentWriter() {
			public void write(OutputStream out) throws IOException {
				zip(out, files);
			}
		});
		return tar;
	}

	/**
	 * 压缩文件
	 * @param out 输出流
	 * @param files 目标文件集
	 * @throws IOException
	 */
	public static void zip(OutputStream out, File... files) throws IOException {
		if (files == null || files.length == 0 || out == null) return;

		ZipOutputStream zos = new ZipOutputStream(out);
		try {
			for (File file : files) {
				if (file == null || !file.exists()) continue;
				zip(zos, file, null);
			}
		} finally {
			zos.close();
		}
	}

	/**
	 * 执行解压
	 * @param is 输入
	 * @param os 输出
	 * @param size 缓冲区大小
	 * @throws IOException
	 */
	protected static void gunzip(InputStream is, OutputStream os, int size) throws IOException {
		if (is == null || os == null) return;
		GZIPInputStream gis = null;
		size = Math.max(128, size);
		try {
			byte[] bs = new byte[size];
			gis = new GZIPInputStream(is);
			for (int r = 0; (r = gis.read(bs)) != -1;) {
				os.write(bs, 0, r);
				os.flush();
			}
		} finally {
			if (gis != null) gis.close();
		}
	}

	/**
	 * 执行压缩
	 * @param is 输入
	 * @param os 输出
	 * @param size 缓冲区大小
	 * @throws IOException
	 */
	protected static void gzip(InputStream is, OutputStream os, int size) throws IOException {
		if (is == null || os == null) return;
		size = Math.max(128, size);
		GZIPOutputStream gos = null;
		try {
			byte[] bs = new byte[size];
			gos = new GZIPOutputStream(os);
			for (int r = 0; (r = is.read(bs)) != -1;) {
				gos.write(bs, 0, r);
				gos.flush();
			}
		} finally {
			if (gos != null) gos.close();
		}
	}

	/**
	 * 解压缩
	 * @param zip 压缩文件
	 * @param entry 文件项
	 * @param base 根目录
	 * @throws IOException
	 */
	protected static void unzip(ZipFile zip, ZipEntry entry, File base) throws IOException {
		String name = entry.getName();
		File file = new File(base, name);
		if (entry.isDirectory()) {
			file.mkdirs();
		} else {
			FileRoboter.duplicate(zip, entry, file);
		}
	}

	/**
	 * 压缩
	 * @param zos 压缩输出流
	 * @param f 文件
	 * @param base 基路径
	 * @throws IOException
	 */
	static void zip(ZipOutputStream zos, File f, CharSequence base) throws IOException {
		StringBuilder buffer = new StringBuilder(base != null ? base : "").append(f.getName());
		if (f.isDirectory()) {
			zos.putNextEntry(new ZipEntry(buffer.append('/').toString()));
			File[] fc = f.listFiles();
			if (fc != null) {
				for (int i = 0; i < fc.length; i++) {
					zip(zos, fc[i], buffer);
				}
			}
		} else {
			ZipEntry ze = new ZipEntry(buffer.toString());
			ze.setTime(f.lastModified());
			ze.setSize(f.length());
			zos.putNextEntry(ze);

			InputStream in = new BufferedInputStream(new FileInputStream(f), 1024);
			try {
				byte[] b = new byte[1024];
				int r;
				while ((r = in.read(b)) > 0) {
					zos.write(b, 0, r);
				}
				zos.flush();
			} finally {
				if (in != null) in.close();
				zos.closeEntry();
			}
		}
	}

	/**
	 * 构造函数
	 */
	protected Zipper() {
	}
}

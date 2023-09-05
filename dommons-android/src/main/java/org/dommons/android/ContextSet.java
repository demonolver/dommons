/*
 * @(#)ContextSet.java     2015-6-24
 */
package org.dommons.android;

import java.io.File;

import org.dommons.core.collections.stack.LinkedStack;
import org.dommons.core.collections.stack.Stack;
import org.dommons.core.string.Stringure;
import org.dommons.core.util.Arrayard;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * 应用上下文集
 * @author Demon 2015-6-24
 */
public class ContextSet {

	private static Stack<Context> contexts;

	/**
	 * 应用文件夹
	 * @return 文件夹
	 */
	public static File applicationDirectory() {
		Context context = get();
		if (context == null) return null;
		File dir = applicationDirectory(context);
		if (dir.exists()) return dir;
		return context.getFilesDir();
	}

	/**
	 * 应用文件夹
	 * @param context 应用上下文
	 * @return 文件夹
	 */
	public static File applicationDirectory(Context context) {
		if (context == null) return null;
		File directory = context.getExternalFilesDir(".").getParentFile();
		if (enableStorage(directory) && !directory.exists()) directory.mkdirs();
		return directory;
	}

	/**
	 * 获取应用文件
	 * @param parts 文件路径
	 * @return 应用文件
	 */
	public static File applicationFile(String... parts) {
		return applicationFile(Stringure.join('/', parts));
	}

	/**
	 * 获取应用文件
	 * @param name 文件名
	 * @return 应用文件
	 */
	public static File applicationFile(String name) {
		return file(applicationDirectory(), name);
	}

	/**
	 * 获取应用缓存文件夹
	 * @return 缓存文件夹
	 */
	public static File cacheDirectory() {
		Context context = get();
		if (context == null) return null;
		File dir = applicationDirectory(context);
		if (dir.exists()) {
			dir = new File(dir, "cache");
			dir.mkdirs();
			return dir;
		}
		return context.getCacheDir();
	}

	/**
	 * 获取缓存文件
	 * @param parts 文件路径
	 * @return 缓存文件
	 */
	public static File cacheFile(String... parts) {
		return cacheFile(Stringure.join('/', parts));
	}

	/**
	 * 获取缓存文件
	 * @param name 文件名
	 * @return 缓存文件
	 */
	public static File cacheFile(String name) {
		return file(cacheDirectory(), name);
	}

	/**
	 * 存储卡是否可用
	 * @param file 文件
	 * @return 是否可用
	 */
	@SuppressWarnings("deprecation")
	public static boolean enableStorage(File file) {
		Object[] enables = { Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY, Environment.MEDIA_SHARED,
				Environment.MEDIA_NOFS };
		try {
			if (Arrayard.contains(Environment.getExternalStorageState(), enables)) return true;
		} catch (Throwable t) {
		}
		try {
			if (Build.VERSION.SDK_INT >= 21 && !Arrayard.contains(Environment.getExternalStorageState(file), enables)) return true;
			else if (Build.VERSION.SDK_INT >= 19 && !Arrayard.contains(Environment.getStorageState(file), enables)) return true;
		} catch (Throwable t) {
		}
		return false;
	}

	/**
	 * 获取当前应用上下文
	 * @return 应用上下文
	 */
	public static Context get() {
		return contexts == null ? null : contexts.peek();
	}

	/**
	 * 注册上下文
	 * @param context 应用上下文
	 */
	public static void register(Context context) {
		if (contexts == null) contexts = new LinkedStack();
		contexts.push(context);
	}

	/**
	 * 注销上下文
	 * @param context 应用上下文
	 */
	public static void unregister(Context context) {
		if (contexts == null) return;
		contexts.remove(context);
	}

	/**
	 * 获取文件
	 * @param dir 目录
	 * @param name 文件名
	 * @return 文件
	 */
	protected static File file(File dir, String name) {
		File f = new File("/", name.replace('\\', '/'));
		name = f.getAbsolutePath().substring(1);

		File storage = get().getExternalFilesDir(".").getParentFile().getParentFile();
		if (dir.getAbsolutePath().startsWith(storage.getAbsolutePath())) {
			File file = new File(dir, name);
			file.getParentFile().mkdirs();
			return file;
		} else {
			return new File(dir, name.replace('/', '$'));
		}
	}
}

/*
 * @(#)FileFilters.java     2011-10-17
 */
package org.dommons.io.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import org.dommons.core.Assertor;
import org.dommons.io.Pathfinder;

/**
 * 文件过滤器
 * @author Demon 2011-10-17
 */
public final class FileFilters {

	private static Map<Integer, FileFilter> finalMap = new HashMap(); // 缓存体

	/**
	 * 创建永假过滤器
	 * @return 过滤器
	 */
	public static FileFilter allFalse() {
		Integer key = Integer.valueOf(1);
		FileFilter filter = finalMap.get(key);
		if (filter == null) {
			synchronized (finalMap) {
				filter = finalMap.get(key);
				if (filter == null) {
					filter = new FileFilter() {
						public boolean accept(File pathname) {
							return false;
						}
					};
					finalMap.put(key, filter);
				}
			}
		}
		return filter;
	}

	/**
	 * 创建永真过滤器
	 * @return 过滤器
	 */
	public static FileFilter allTrue() {
		Integer key = Integer.valueOf(2);
		FileFilter filter = finalMap.get(key);
		if (filter == null) {
			synchronized (finalMap) {
				filter = finalMap.get(key);
				if (filter == null) {
					filter = new FileFilter() {
						public boolean accept(File pathname) {
							return true;
						}
					};
					finalMap.put(key, filter);
				}
			}
		}
		return filter;
	}

	/**
	 * 创建与组合过滤器
	 * @param filters 子过滤器集合
	 * @return 组合过滤器
	 */
	public static FileFilter and(FileFilter... filters) {
		Assertor.F.notNull(filters);
		Collection<FileFilter> list = new HashSet();
		for (FileFilter filter : filters) {
			if (filter == null) continue;
			list.add(filter);
		}

		if (list.size() < 2) return list.iterator().next();
		GroupFilter gf = new GroupFilter(true);
		for (FileFilter filter : list) {
			gf.addFilter(filter);
		}
		return gf;
	}

	/**
	 * 创建文件夹过滤器
	 * @return 过滤器
	 */
	public static FileFilter isDirectory() {
		Integer key = Integer.valueOf(4);
		FileFilter filter = finalMap.get(key);
		if (filter == null) {
			synchronized (finalMap) {
				filter = finalMap.get(key);
				if (filter == null) {
					filter = new FileFilter() {
						public boolean accept(File file) {
							return file != null && file.isDirectory();
						}
					};
					finalMap.put(key, filter);
				}
			}
		}
		return filter;
	}

	/**
	 * 创建文件过滤器
	 * @return 过滤器
	 */
	public static FileFilter isFile() {
		Integer key = Integer.valueOf(3);
		FileFilter filter = finalMap.get(key);
		if (filter == null) {
			synchronized (finalMap) {
				filter = finalMap.get(key);
				if (filter == null) {
					filter = new FileFilter() {
						public boolean accept(File file) {
							return file != null && file.isFile();
						}
					};
					finalMap.put(key, filter);
				}
			}
		}
		return filter;
	}

	/**
	 * 后缀名过滤器
	 * @param suffix 后缀名集
	 * @return 过滤器
	 */
	public static FileFilter isSuffix(String... suffix) {
		Assertor.F.notEmpty(suffix);
		return new SuffixFilter(suffix);
	}

	/**
	 * 文件名匹配
	 * @param pattern 正则表达式
	 * @return 过滤器
	 */
	public static FileFilter matches(Pattern pattern) {
		Assertor.F.notNull(pattern);
		return new PatternFilter(pattern, false);
	}

	/**
	 * 文件匹配
	 * @param pattern 正则表达式
	 * @param matchPath 是否匹配全路径
	 * @return 过滤器
	 */
	public static FileFilter matches(Pattern pattern, boolean matchPath) {
		Assertor.F.notNull(pattern);
		return new PatternFilter(pattern, matchPath);
	}

	/**
	 * 文件名匹配
	 * @param pattern 正则表达式
	 * @return 过滤器
	 */
	public static FileFilter matches(String pattern) {
		Assertor.F.notEmpty(pattern);
		return new PatternFilter(pattern, false);
	}

	/**
	 * 文件匹配
	 * @param pattern 正则表达式
	 * @param matchPath 是否匹配全路径
	 * @return 过滤器
	 */
	public static FileFilter matches(String pattern, boolean matchPath) {
		Assertor.F.notEmpty(pattern);
		return new PatternFilter(pattern, matchPath);
	}

	/**
	 * 创建取非过滤器
	 * @param filter 原过滤器
	 * @return 新过滤器
	 */
	public static FileFilter not(FileFilter filter) {
		if (filter instanceof NotFilter) return ((NotFilter) filter).filter;
		return new NotFilter(filter);
	}

	/**
	 * 创建或组合过滤器
	 * @param filters 子过滤器集合
	 * @return 组合过滤器
	 */
	public static FileFilter or(FileFilter... filters) {
		Assertor.F.notNull(filters);
		Collection<FileFilter> list = new HashSet();
		for (FileFilter filter : filters) {
			if (filter == null) continue;
			list.add(filter);
		}

		if (list.size() < 2) return list.iterator().next();
		GroupFilter gf = new GroupFilter(false);
		for (FileFilter filter : list) {
			gf.addFilter(filter);
		}
		return gf;
	}

	/**
	 * 执行过滤条件
	 * @param filter 过滤器
	 * @param file 待过滤文件
	 * @return 是、否
	 */
	protected static boolean doAccept(FileFilter filter, File file) {
		try {
			return filter.accept(file);
		} catch (RuntimeException e) {
			return false;
		}
	}

	protected FileFilters() {
	}

	/**
	 * 组合过滤器
	 * @author Demon 2009-11-19
	 */
	public static class GroupFilter implements FileFilter {

		private final Collection<FileFilter> list; // 过滤器集合

		private final boolean flag;

		/**
		 * 构造函数
		 */
		protected GroupFilter(boolean ret) {
			list = new LinkedList();
			this.flag = ret;
		}

		public boolean accept(File file) {
			for (FileFilter filter : list) {
				if (doAccept(filter, file) != flag) {
					return !flag;
				}
			}

			return flag;
		}

		/**
		 * 添加过滤器
		 * @param filter 过滤器
		 * @return 是否添加成功
		 */
		public boolean addFilter(FileFilter filter) {
			if (filter == null) return false;
			if (filter == this) throw new IllegalArgumentException("Can not add the group filter itself!");

			return list.add(filter);
		}
	}

	/**
	 * 取非过滤器
	 * @author Demon 2009-11-19
	 */
	protected static class NotFilter implements FileFilter {

		private final FileFilter filter; // 目标过滤器

		/**
		 * 构造函数
		 * @param filter 过滤器
		 */
		protected NotFilter(FileFilter filter) {
			this.filter = filter;
		}

		public boolean accept(File file) {
			return !doAccept(filter, file);
		}
	}

	/**
	 * 正则过滤器
	 * @author Demon 2010-9-28
	 */
	protected static class PatternFilter implements FileFilter {

		private final Pattern pattern; // 正则表达式
		private final boolean matchPath; // 匹配全路径

		/**
		 * 构造函数
		 * @param pattern 表达式
		 * @param matchPath 匹配全路径
		 */
		protected PatternFilter(Pattern pattern, boolean matchPath) {
			this.pattern = pattern;
			this.matchPath = matchPath;
		}

		/**
		 * 构造函数
		 * @param pattern 表达式
		 * @param matchPath 匹配全路径
		 */
		protected PatternFilter(String pattern, boolean matchPath) {
			this(Pattern.compile(pattern), matchPath);
		}

		public boolean accept(File file) {
			return pattern.matcher(matchPath ? Pathfinder.getCanonicalPath(file) : file.getName()).matches();
		}
	}

	/**
	 * 后缀名过滤器
	 * @author Demon 2010-1-15
	 */
	protected static class SuffixFilter implements FileFilter {

		private final Collection<String> list; // 后缀名列表

		protected SuffixFilter(String... suffix) {
			list = new HashSet();

			for (String suf : suffix) {
				if (suf == null) continue;
				list.add(suf.toUpperCase());
			}
		}

		public boolean accept(File file) {
			String name = file.getName().toUpperCase();
			for (String suffix : list) {
				if (name.endsWith(suffix)) return true;
			}
			return false;
		}
	}
}

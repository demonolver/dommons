/*
 * @(#)ClasspathDiscoverer.java     2011-10-20
 */
package org.dommons.classloader.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.dommons.classloader.bean.ClasspathContainer;
import org.dommons.classloader.bean.ClasspathItem;
import org.dommons.classloader.bean.file.FileClasspathContainer;
import org.dommons.classloader.bean.zip.ZipClasspathContainer;

/**
 * 类路径查找器
 * @author Demon 2011-10-20
 */
public class ClasspathDiscoverer {

	private final Collection<ClasspathContainer> containers;
	private final ClasspathIndex index;

	/**
	 * 构造函数
	 */
	public ClasspathDiscoverer() {
		containers = new LinkedHashSet();
		index = new ClasspathIndex();
	}

	/**
	 * 添加文件
	 * @param file 文件
	 * @return 是否添加成功
	 */
	public boolean addFile(File file) {
		ClasspathContainer container = convert(file);
		synchronized (containers) {
			return container != null ? containers.add(container) : false;
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		ClasspathContainer[] containers = getContainers(true);
		for (ClasspathContainer container : containers) {
			container.close();
		}
	}

	/**
	 * 查找资源项
	 * @param resName 资源名
	 * @param loop 是否全部查找
	 * @return 资源项结果集
	 */
	public List<ClasspathItem> find(String resName, boolean loop) {
		List<ClasspathItem> items = new ArrayList();
		// 转换资源名
		resName = convert(resName);
		if (resName != null) {
			boolean found = false;
			if (!loop) {
				// 从索引中查找
				ClasspathItem item = index.getItem(resName);
				if (item != null) {
					items.add(item);
					found = true;
				}
			}
			if (!found) {
				ClasspathContainer[] containers = getContainers();
				for (ClasspathContainer container : containers) {
					ClasspathItem item = container.findItem(resName);
					// 查找资源项并添加到结果集中，如不须要查出全结果直接跳出
					if (item != null && items.add(item) && !loop) break;
				}
				// 添加到索引中以备再次使用
				if (!items.isEmpty()) {
					ClasspathItem item = items.get(0);
					// 只记录非类文件，类文件一般不会查找多次
					if (!item.isClass()) index.addItem(resName, item);
				}
			}
		}
		return items;
	}

	/**
	 * 获取文件集
	 * @return 文件集
	 */
	public File[] getFiles() {
		ClasspathContainer[] containers = getContainers();
		Collection<File> list = new ArrayList(containers.length);
		for (ClasspathContainer container : containers) {
			list.add(container.getFile());
		}
		return list.toArray(new File[list.size()]);
	}

	/**
	 * 转换资源容器
	 * @param file 文件
	 * @return 容器 无效文件返回<code>null</code>
	 */
	protected ClasspathContainer convert(File file) {
		if (file.isDirectory()) {
			return new FileClasspathContainer(file);
		} else if (file.exists() && file.isFile()) {
			ZipFile zip = null;
			try {
				zip = new ZipFile(file);
			} catch (ZipException e) {
			} catch (IOException e) {
			}
			if (zip != null) return new ZipClasspathContainer(file, zip);
		}
		return null;
	}

	/**
	 * 转换资源名
	 * @param resName 原资源名
	 * @return 新资源名
	 */
	protected String convert(String resName) {
		if (resName == null) return null;
		if (File.separatorChar != '/') resName = resName.replace(File.separatorChar, '/');
		if (resName.endsWith(".")) resName = resName.substring(0, resName.length() - 1);
		if (resName.startsWith("/")) return null;
		// 解析真实路径
		String[] parts = resName.split("\\/");
		List<String> list = new ArrayList(parts.length);
		int index = 0;
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (part.equals(".") || part.trim().length() == 0) {
				continue;
			} else if (part.equals("..")) {
				index--;
				if (index < 0) return null;
			} else {
				if (index == list.size()) {
					if (!list.add(part)) continue;
				} else {
					list.set(index, part);
				}
				index++;
			}
		}
		// 路径重组
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) buffer.append('/');
			buffer.append(list.get(i));
		}
		if (resName.endsWith("/")) buffer.append('/');
		resName = buffer.toString();
		return resName;
	}

	/**
	 * 获取资源容器集
	 * @return 容器集
	 */
	protected ClasspathContainer[] getContainers() {
		return getContainers(false);
	}

	/**
	 * 获取资源容器集
	 * @param clear 是否获取后清除
	 * @return 容器集
	 */
	protected ClasspathContainer[] getContainers(boolean clear) {
		ClasspathContainer[] containers = null;
		synchronized (this.containers) {
			containers = this.containers.toArray(new ClasspathContainer[this.containers.size()]);
			if (clear) this.containers.clear();
		}
		return containers;
	}
}
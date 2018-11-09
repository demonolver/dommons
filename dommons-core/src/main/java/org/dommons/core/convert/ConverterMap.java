/*
 * @(#)ConverterMap.java     2011-10-28
 */
package org.dommons.core.convert;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dommons.core.Assertor;
import org.dommons.core.Silewarner;
import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.util.Arrayard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 数据转换器映射关系
 * @author Demon 2011-10-28
 */
final class ConverterMap {

	/** 排除类型集 */
	static final Class[] outers = { Object.class, Serializable.class, Cloneable.class, Comparable.class };

	private static Map<Class, Map<Class, MapNode>> cache = new ConcurrentSoftMap();

	/**
	 * 获取转换器
	 * @param source 源类型
	 * @param target 目标类型
	 * @return 转换器
	 */
	public static <S, T> ConverterProvider<S, T> getConverter(Class<S> source, Class<T> target) {
		MapNode node = map().get(target);
		return node == null ? null : new ConverterProvider(source, node);
	}

	/**
	 * 获取转换类型对应关系
	 * @return 映射表
	 */
	protected static Map<Class, MapNode> map() {
		Map<Class, MapNode> map = cache.get(ConverterMap.class);
		if (map == null) {
			synchronized (ConverterMap.class) {
				map = cache.get(ConverterMap.class);
				if (map == null) cache.put(ConverterMap.class, map = load());
			}
		}
		return map;
	}

	/**
	 * 读取配置
	 * @return 映射关系
	 */
	static Map<Class, MapNode> load() {
		Map<Class, MapNode> map = new HashMap();
		try {
			load(map);
		} catch (Exception e) {
			Silewarner.warn(ConverterMap.class, "Load converters map fail", e);
		}
		return map;
	}

	/**
	 * 读取配置导入到映射关系中
	 * @param map 映射关系
	 * @throws Exception
	 */
	static void load(Map<Class, MapNode> map) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		// 加载扩展自定义配置
		for (Enumeration<URL> en = Converter.class.getClassLoader().getResources("converters.map.xml"); en.hasMoreElements();) {
			URL url = en.nextElement();
			try {
				load(map, url, builder);
			} catch (Exception e) {
				Silewarner.warn(ConverterMap.class, "Load extend xml [" + url + "] fail", e);
			}
		}
		for (Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("converters.map.xml"); en
				.hasMoreElements();) {
			URL url = en.nextElement();
			try {
				load(map, en.nextElement(), builder);
			} catch (Exception e) {
				Silewarner.warn(ConverterMap.class, "Load extend xml [" + url + "] fail", e);
			}
		}
		// 读取默认配置
		try {
			load(map, ConverterMap.class.getResource("converters.map"), builder);
		} catch (Exception e) {
			Silewarner.warn(ConverterMap.class, "Load default xml fail", e);
		}
	}

	/**
	 * 读取 XML 内容配置
	 * @param map 映射关系
	 * @param doc XML 文档对象
	 */
	static void load(Map<Class, MapNode> map, Document doc) {
		NodeList list = doc.getDocumentElement().getElementsByTagName("converter");
		int len = list.getLength();
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (node instanceof Element && "converter".equals(node.getNodeName())) {
				parse(map, (Element) node);
			}
		}
	}

	/**
	 * 读取配置文件
	 * @param map 映射关系
	 * @param url 文件路径
	 * @param builder XML 构建器
	 * @throws IOException
	 */
	static void load(Map<Class, MapNode> map, URL url, DocumentBuilder builder) throws IOException {
		InputStream in = null;
		try {
			in = url.openStream();
			load(map, builder.parse(in));
		} catch (Exception e) {
			Silewarner.warn(ConverterMap.class, "Load xml [" + url + "] fail", e);
		} finally {
			if (in != null) in.close();
		}
	}

	/**
	 * @param className
	 * @return
	 */
	static ConvertHandler newInstance(String className) {
		try {
			Class cls = findClass(className);
			return cls == null ? null : (ConvertHandler) cls.newInstance();
		} catch (Exception e) {
			Silewarner.warn(ConverterMap.class, "Create convertHandler fail", e);
			return null;
		}
	}

	/**
	 * 解析转换器节点配置
	 * @param map 映射关系
	 * @param ele 节点对象
	 */
	static void parse(Map<Class, MapNode> map, Element ele) {
		String className = ele.getAttribute("class");
		if (Assertor.P.empty(className)) return;

		ConvertHandler converter = newInstance(className);
		if (converter == null) return;

		NodeList list = ele.getElementsByTagName("target");
		int len = list.getLength();
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (node instanceof Element && "target".equals(node.getNodeName())) {
				addTarget(map, converter, (Element) node);
			}
		}
	}

	/**
	 * 导入目标类型转换器
	 * @param map 映射关系
	 * @param converter 转换器
	 * @param ele 目标定义节点对象
	 */
	private static void addTarget(Map<Class, MapNode> map, ConvertHandler converter, Element ele) {
		String className = ele.getAttribute("class");
		Class cls = findClass(className);
		if (cls == null) return;

		MapNode node = map.get(cls);
		if (node == null) {
			node = new MapNode(cls);
			if (importSources(node, converter, ele.getElementsByTagName("source"))) importParent(map, cls, node);
		} else {
			importSources(node, converter, ele.getElementsByTagName("source"));
		}
	}

	/**
	 * 查询目标类
	 * @param className 类名
	 * @return 类实例
	 */
	private static Class findClass(String className) {
		try {
			return Class.forName(className, false, ConverterMap.class.getClassLoader());
		} catch (ClassNotFoundException e) {
		}
		try {
			return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	/**
	 * 添加父节点
	 * @param map 映射关系
	 * @param cls 类
	 * @param node 映射节点
	 */
	private static void importParent(Map<Class, MapNode> map, Class cls, MapNode node) {
		map.put(cls, node);

		Class parent = cls.getSuperclass();
		if (parent != null && Arrayard.indexOf(outers, parent) < 0) {
			MapNode pNode = map.get(parent);
			if (pNode == null) importParent(map, parent, pNode = new MapNode(parent));
			pNode.addChild(node);
		}

		Class[] interfaces = cls.getInterfaces();
		if (interfaces != null) {
			for (Class c : interfaces) {
				if (Arrayard.indexOf(outers, c) > -1) continue;
				MapNode pNode = map.get(c);
				if (pNode == null) importParent(map, c, pNode = new MapNode(c));
				pNode.addChild(node);
			}
		}
	}

	/**
	 * 导入源类型集
	 * @param node 映射节点
	 * @param converter 转换器
	 * @param list 节点列表
	 * @return 是否导入源类型
	 */
	private static boolean importSources(MapNode node, ConvertHandler converter, NodeList list) {
		int len = list.getLength();
		for (int i = 0; i < len; i++) {
			Node n = list.item(i);
			if (n instanceof Element && "source".equals(n.getNodeName())) {
				String className = ((Element) n).getAttribute("class");
				Class cls = findClass(className);
				if (cls != null) node.addConverter(cls, converter);
			}
		}
		return !node.isEmpty();
	}

	/**
	 * 转换器提供器
	 * @author Demon 2011-10-31
	 */
	static class ConverterProvider<S, T> {

		private final HierarchyIterator hit;
		private MapNode node;

		private Iterator<MapNode> child;
		private Iterator<ConvertHandler> it;

		/**
		 * 构造函数
		 * @param source 源类型
		 * @param node 映射节点
		 */
		public ConverterProvider(Class source, MapNode node) {
			this.hit = new HierarchyIterator(source);
			this.node = node;
		}

		/**
		 * 获取下一个转换器
		 * @return 转换器
		 */
		public ConvertHandler<S, T> next() {
			find: while (true) {
				if (it != null && it.hasNext()) {
					try {
						return it.next();
					} catch (ClassCastException e) {
						it.remove();
						Silewarner.warn(ConverterProvider.class, "Converter cast error", e);
					}
				}

				while (hit.hasNext()) {
					it = node.getConverter(hit.next());
					if (it != null) continue find;
				}

				hit.reset();

				while (true) {
					if (child != null && child.hasNext()) {
						node = child.next();
						break;
					} else {
						child = node.getChildren();
						if (child == null) return null;
					}
				}
			}
		}
	}

	/**
	 * 类层级迭代器
	 * @author Demon 2011-10-31
	 */
	static class HierarchyIterator implements Iterator<Class> {

		private int i;

		private int index;
		private List<Class> cache;
		private Iterator<Class> it;

		public HierarchyIterator(Class cls) {
			cache = new ArrayList();
			cache.add(cls);
			index = 0;
			i = 0;
		}

		public boolean hasNext() {
			if (it != null) {
				return it.hasNext();
			} else if (index < cache.size()) {
				return true;
			}
			while (i < index) {
				if (importSuper(cache.get(i++))) return true;
			}
			return false;
		}

		public Class next() {
			if (it != null) {
				return it.next();
			} else {
				return cache.get(index++);
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * 重置迭代器
		 */
		public void reset() {
			it = cache.iterator();
		}

		/**
		 * 导入超类或接口
		 * @param cls 类
		 * @return 是否导入
		 */
		boolean importSuper(Class cls) {
			int s = 0;

			Class parent = cls.getSuperclass();
			if (parent != null && !cache.contains(parent)) {
				cache.add(parent);
				s++;
			}

			Class[] interfaces = cls.getInterfaces();
			if (interfaces != null) {
				for (Class c : interfaces) {
					if (c != null && Arrayard.indexOf(outers, c) < 0 && !cache.contains(c)) {
						cache.add(c);
						s++;
					}
				}
			}

			return s > 0;
		}
	}

	/**
	 * 映射节点
	 * @author Demon 2011-10-31
	 */
	static class MapNode {

		private Collection<MapNode> children;
		private Map<Class, Collection<ConvertHandler>> converters;

		private final Class cls;

		/**
		 * 构造函数
		 * @param cls 类
		 */
		public MapNode(Class cls) {
			this.cls = cls;
		}

		/**
		 * 添加子节点
		 * @param node 子节点
		 */
		public void addChild(MapNode node) {
			if (children == null) children = new LinkedHashSet();
			children.add(node);
		}

		/**
		 * 添加转换器
		 * @param cls 源类型
		 * @param converter 转换器
		 */
		public void addConverter(Class cls, ConvertHandler converter) {
			if (converters == null) converters = new HashMap();
			Collection cs = converters.get(cls);
			if (cs == null) converters.put(cls, cs = new LinkedHashSet());
			cs.add(converter);
		}

		public boolean equals(Object o) {
			return (o == null || !(o instanceof MapNode)) ? false : cls.equals(((MapNode) o).cls);
		}

		/**
		 * 获取子节点
		 * @return 子节点迭代器
		 */
		public Iterator<MapNode> getChildren() {
			return children == null ? null : children.iterator();
		}

		/**
		 * 获取转换器
		 * @param source 源类型
		 * @return 转换器迭代器
		 */
		public Iterator<ConvertHandler> getConverter(Class source) {
			Collection<ConvertHandler> cs = converters == null ? null : converters.get(source);
			return cs == null ? null : cs.iterator();
		}

		public int hashCode() {
			return cls.hashCode();
		}

		/**
		 * 是否内容为空
		 * @return 是、否
		 */
		public boolean isEmpty() {
			return converters == null || converters.isEmpty();
		}

		public String toString() {
			return cls.getName();
		}
	}
}

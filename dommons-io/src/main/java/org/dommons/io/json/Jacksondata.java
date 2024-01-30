/*
 * @(#)Jacksondata.java     2017-07-26
 */
package org.dommons.io.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.dommons.core.collections.map.concurrent.ConcurrentSoftMap;
import org.dommons.core.convert.Converter;
import org.dommons.core.proxy.ProxyFactory;
import org.dommons.core.ref.Ref;
import org.dommons.core.ref.Softref;
import org.dommons.core.string.Stringure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * Jackson 数据
 * @author demon 2017-07-26
 */
public class Jacksondata {
	private static Map<Boolean, ObjectMapper> oms = new ConcurrentSoftMap();
	private static Ref<PropertyNamingStrategyBase> pref;

	/**
	 * 配置转换器
	 * @param mapper 转换器实例
	 * @param underscore 大写属性名是否转成小写下划线
	 */
	public static void configure(ObjectMapper mapper, boolean underscore) {
		if (mapper == null) return;
		mapper.configure(com.fasterxml.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, false);
		mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
		configureModules(mapper);
		if (underscore) mapper.setPropertyNamingStrategy(strategy());
	}

	/**
	 * 转换小写&下划线属性名
	 * @param name 属性名
	 * @return 新属性名
	 */
	public static String lowerCaseProperty(String name) {
		String n = Stringure.trim(name);
		if (n.isEmpty()) return name;
		return strategy().translate(name);
	}

	/**
	 * 获取数据转换器
	 * @return 转换器
	 */
	public static ObjectMapper mapper() {
		return mapper(false);
	}

	/**
	 * 获取数据转换器
	 * @param underscore 大写属性名是否转成小写下划线
	 * @return 转换器
	 */
	public static ObjectMapper mapper(boolean underscore) {
		Boolean b = Boolean.valueOf(underscore);
		ObjectMapper mapper = oms.get(b);
		if (mapper == null) {
			mapper = new ObjectMapper();
			configure(mapper, underscore);
			oms.put(b, mapper);
		}
		return mapper;
	}

	/**
	 * 转换为修饰后的字符串
	 * @param obj 目标对象
	 * @return 字符串
	 */
	public static String pretty(Object obj) {
		return pretty(obj, false);
	}

	/**
	 * 转换为修饰后的字符串
	 * @param obj 目标对象
	 * @param underscore 大写属性名是转成小写下划线
	 * @return 字符串
	 */
	public static String pretty(Object obj, boolean underscore) {
		return string(obj, underscore, true);
	}

	/**
	 * 拆分对象 JSON 串
	 * @param json 原 JSON 串
	 * @return 结果集 [属性名, 属性值 JSON 串]
	 */
	public static Map<String, String> split(String json) {
		if (json == null || !json.startsWith("{") || !json.endsWith("}")) return null;
		ObjectMapper mapper = mapper();
		ObjectNode obj = null;
		read: {
			try {
				JsonNode node = mapper.readTree(json);
				if (node instanceof ObjectNode) {
					obj = (ObjectNode) node;
					break read;
				}
			} catch (IOException e) { // ignored
			}
			return null;
		}

		Map<String, String> map = new LinkedHashMap();
		for (Iterator<Entry<String, JsonNode>> it = obj.fields(); it.hasNext();) {
			Entry<String, JsonNode> en = it.next();
			try {
				map.put(en.getKey(), mapper.writeValueAsString(en.getValue()));
			} catch (JsonProcessingException e) { // ignored
			}
		}
		return map;
	}

	/**
	 * 对象转换为字符串
	 * @param obj 目标对象
	 * @return 字符串
	 */
	public static String string(Object obj) {
		return string(obj, false);
	}

	/**
	 * 对象转换为字符串
	 * @param obj 目标对象
	 * @param underscore 大写属性名是转成小写下划线
	 * @return 字符串
	 */
	public static String string(Object obj, boolean underscore) {
		return string(obj, underscore, false);
	}

	/**
	 * 对象转换为字符串
	 * @param obj 目标对象
	 * @param underscore 大写属性名是转成小写下划线
	 * @param pretty
	 * @return 字符串
	 */
	public static String string(Object obj, boolean underscore, boolean pretty) {
		try {
			if (obj == null) return null;
			else if (obj instanceof CharSequence) return String.valueOf(obj);
			else if (!pretty) return mapper(underscore).writeValueAsString(obj);
			else return mapper(underscore).writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (IOException e) {
			throw Converter.F.convert(e, RuntimeException.class);
		}
	}

	/**
	 * 转换类型
	 * @param type 类型
	 * @return JSON 类型
	 */
	public static JavaType type(Type type) {
		for (ObjectMapper om : oms.values()) {
			if (om != null) return om.constructType(type);
		}
		return mapper().constructType(type);
	}

	/**
	 * 转换集合类型
	 * @param rawType 集合类型
	 * @param elemType 元素类型
	 * @return 集合 JSON 类型
	 */
	public static JavaType typeCollection(Class<? extends Collection> rawType, Class<?> elemType) {
		JavaType elem = type(elemType);
		return typeCollection(rawType, elem);
	}

	/**
	 * 转换集合类型
	 * @param rawType 集合类型
	 * @param elemType 元素类型
	 * @return 集合 JSON 类型
	 */
	@SuppressWarnings("deprecation")
	public static JavaType typeCollection(Class<? extends Collection> rawType, JavaType elemType) {
		if (rawType == null) rawType = LinkedList.class;
		try {
			JavaType type = JsonCollectionType.collection(rawType, elemType);
			if (type != null) return type;
		} catch (Throwable t) { // ignored
		}
		{
			return CollectionType.construct(rawType, elemType);
		}
	}

	/**
	 * 转换集合类型
	 * @param rawType 集合类型
	 * @param elemType 元素类型
	 * @return 集合 JSON 类型
	 */
	public static JavaType typeCollection(Class<? extends Collection> rawType, Type elemType) {
		JavaType elem = type(elemType);
		return typeCollection(rawType, elem);
	}

	/**
	 * 转换映射表类型
	 * @param mapType 映射表类型
	 * @param keyT 键类型
	 * @param valueT 值类型
	 * @return 映射表 JSON 类型
	 */
	@SuppressWarnings("deprecation")
	public static JavaType typeMap(Class<? extends Map> mapType, JavaType keyT, JavaType valueT) {
		return MapType.construct(mapType, keyT, valueT);
	}

	/**
	 * 转换映射表类型
	 * @param mapType 映射表类型
	 * @param keyT 键类型
	 * @param valueT 值类型
	 * @return 映射表 JSON 类型
	 */
	public static JavaType typeMap(Class<? extends Map> mapType, Type keyT, Type valueT) {
		return typeMap(mapType, type(keyT), type(valueT));
	}

	/**
	 * 转换映射表类型
	 * @param keyT 键类型
	 * @param valueT 值类型
	 * @return 映射表 JSON 类型
	 */
	public static JavaType typeMap(Type keyT, Type valueT) {
		return typeMap(Map.class, keyT, valueT);
	}

	/**
	 * 下划线转为大写属性名
	 * @param name 属性名
	 * @return 新属性名
	 */
	public static String upperCaseProperty(String name) {
		String n = Stringure.trim(name);
		if (!n.contains("_")) return name;
		int len = n.length();
		StringBuffer buf = new StringBuffer(len);
		boolean up = false;
		for (int i = 0; i < len; i++) {
			char c = n.charAt(i);
			if (c == '_') {
				up = true;
				continue;
			} else if (up) {
				up = false;
				c = Character.toUpperCase(c);
			}
			buf.append(c);
		}
		return buf.toString();
	}

	/**
	 * 获取属性名转换器
	 * @return 属性名转换器
	 */
	protected static PropertyNamingStrategyBase strategy() {
		PropertyNamingStrategyBase pns = pref == null ? null : pref.get();
		if (pns == null) {
			pns = JacksonVersions.snake();
			pref = new Softref(pns);
		}
		return pns;
	}

	/**
	 * 设置数据模型
	 * @param mapper 转换器实例
	 */
	static void configureModules(ObjectMapper mapper) {
		try {
			Class clazz = ProxyFactory.findClass("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");
			if (clazz == null) return;
			Module module = (Module) clazz.newInstance();
			mapper.registerModule(module);
		} catch (Throwable t) { // ignored
		}
	}

}

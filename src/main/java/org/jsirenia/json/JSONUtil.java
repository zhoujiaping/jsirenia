package org.jsirenia.json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsirenia.collection.CollectionUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json反序列化的类型信息处理 Class参数指定：实际上调用 指定Type参数的方法 Type参数指定：动态指定但是对泛型支持不好
 * TypeReference指定：静态指定，每个类型写一遍，不能做到各类型通用
 * 
 * @type 指定：动态指定但是序列化时需要带类型的序列化。相对而言，这种方式功能最强大。
 *       考虑到泛型太复杂，泛型类、泛型方法、嵌套泛型、泛型通配符、未泛化的泛型， 以及考虑到接口类型等原因，在需要类型的时候，建议使用@type方式。
 */
public class JSONUtil {
	public static final ParserConfig parseconfig = new ParserConfig();
	static{
		parseconfig.setAutoTypeSupport(true);
	}
	/**
	 * 这个方法的意义，在于 在没有指定类型时反序列化，会出现数字默认为Integer类型，而方法签名里面需要的类型是Long， 诸如此类的情况。
	 */
	/*public static Object toJavaObject(Object obj, Class<?> type, Type genericType) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof JSON) {
			JSON json = (JSON) obj;
			String s = json.toJSONString();
			return JSONUtil.parseJSON(s, type, genericType);
		}
		String text = JSON.toJSONString(obj);
		return JSON.parseObject(text, type);// 这里不能直接返回obj，因为有可能obj是Integer类型，type是long类型。
	}*/

	/**
	 * JSON.toJSONString会自动处理对象和数组
	 * 
	 * @param obj:对象或数组或集合
	 */
	public static String toJSONStringWithType(Object obj) {
		// SerializerFeature.PrettyFormat
		return JSON.toJSONString(obj, SerializerFeature.WriteClassName);
	}

	public static <T> T parseObjectWithType(String text, Class<T> clazz) {
		//如果parseconfig没有设置支持类型，同时文本中有@type，那么会抛出异常
		T res = JSON.parseObject(text, clazz, parseconfig);
		return res;
	}

	
	/* 使用parseJSON方法代替
	 @Deprecated
	 public static Object parseJSON2(String json, Class<?> type, Type genericType) {
		if (JSONObject.class.isAssignableFrom(type)) {
			return JSON.parseObject(json);
		}
		if (JSONArray.class.isAssignableFrom(type)) {
			return JSON.parseArray(json);
		}
		if (type.isPrimitive()) {// 基本类型，以及void
			return JSON.parseObject(json, type);
		}
		if (type.isArray()) {// 数组类型
			Class<?> componentType = type.getComponentType();
			List<?> list = JSON.parseArray(json, componentType);
			Object array = Array.newInstance(componentType, list.size());
			for (int i = 0; i < list.size(); i++) {
				Array.set(array, i, list.get(i));
			}
			return array;
		}
		if (type.isAnnotation()) {
			throw new RuntimeException("不支持注解类型");// 无法实现
		}
		if (type.isEnum()) {
			return JSON.parseObject(json, type);
		}
		if (type.isInterface()) {// 如果是接口
			if (List.class.isAssignableFrom(type)) {// 使用ArrayList
				type = ArrayList.class;
			} else if (Set.class.isAssignableFrom(type)) {// 使用HashSet
				type = HashSet.class;
			} else if (Map.class.isAssignableFrom(type)) {
				type = HashMap.class;
			} else {
				throw new RuntimeException("不支持List, Set, Map之外的接口类型");
			}
		}
		if (Modifier.isAbstract(type.getModifiers())) {// 抽象类
			throw new RuntimeException("不支持抽象类型");
		}
		if (Collection.class.isAssignableFrom(type)) {// 集合类型
			if (genericType instanceof ParameterizedType) {// 有泛型参数
				ParameterizedType pt = (ParameterizedType) genericType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				if (actualTypeArguments[0] instanceof WildcardType) {
					throw new RuntimeException("不支持集合泛型通配符");
				}
				if (actualTypeArguments[0] instanceof ParameterizedType) {
					throw new RuntimeException("不支持集合嵌套泛型");// 比如List<Map<String,Object>>
				}
				Class<?> c = (Class<?>) actualTypeArguments[0];
				return JSONArray.parseArray(json, c);
			} else {// 无泛型参数
				throw new RuntimeException("不支持未泛化的集合类型");
			}
		}
		if (Map.class.isAssignableFrom(type)) {
			if (genericType instanceof ParameterizedType) {// 有泛型参数
				ParameterizedType pt = (ParameterizedType) genericType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				Type keyType = actualTypeArguments[0];
				Type valueType = actualTypeArguments[1];
				if (keyType instanceof WildcardType || valueType instanceof WildcardType) {
					throw new RuntimeException("不支持Map泛型通配符");
				}
				if (keyType instanceof ParameterizedType || valueType instanceof ParameterizedType) {
					throw new RuntimeException("不支持Map嵌套泛型");
				}
				TypeReference<?> tf = new MapTypeReference<>(actualTypeArguments);
				return JSON.parseObject(json, tf.getType());
				// return JSONObject.parseObject(retString,tf );
			} else {// 无泛型参数
				throw new RuntimeException("不支持未泛化的Map类型");
			}
		}
		if (genericType instanceof ParameterizedType) {// 有泛型参数
			ParameterizedType pt = (ParameterizedType) genericType;
			// Type[] actualTypeArguments = pt.getActualTypeArguments();
			Type type0 = JSONTypeUtil.createType(pt);
			return JSON.parseObject(json, type0);
		}
		if (genericType instanceof TypeVariable<?>) {
			throw new RuntimeException("不支持返回值为泛型变量");
		}
		return JSONObject.parseObject(json, type);
	}
*/
	/**
	 * 不支持泛型变量，不支持除List、Set、Map之外的接口类型，不支持抽象类型，不支持泛型通配符，不支持泛型变量，不支持注解类型？
	 * 支持枚举类型，支持泛型嵌套，支持数组，支持泛型数组，支持数组泛型嵌套
	 * 
	 * @param json
	 * @param type
	 * @param genericType
	 * @return
	 */
	/*public static Object parseJSON(String json, Class<?> type, Type genericType) {
		if (type.isArray()) {// 数组
			if (genericType instanceof GenericArrayType) {// 泛型数组
				GenericArrayType genericArrayType = (GenericArrayType) genericType;
				Type genericComponentType = genericArrayType.getGenericComponentType();
				ParameterizedType parameterizedType = (ParameterizedType) genericComponentType;
				Type actualType = JSONTypeUtil.createType(parameterizedType);
				List<Object> o2 = JSON.parseArray(json, new Type[] { actualType });
				Class<?> componentType = type.getComponentType();
				return CollectionUtil.toArray(o2, componentType);
			} else {// 1.1普通数组
				return JSON.parseObject(json, type);
			}
		} else {// 非数组
			if (genericType instanceof ParameterizedType) {// 有泛型
				ParameterizedType parameterizedType = (ParameterizedType) genericType;
				Type actualType = JSONTypeUtil.createType(parameterizedType);
				return JSON.parseObject(json, actualType);
			} else {// 无泛型
				return JSON.parseObject(json, type);
			}
		}
	}*/
}

package org.jsirenia.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentMap;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
/**
 * fastjson反序列化处理泛型时，有一种TypeReference的方式。
 * 这种方式，可以通过静态的指定泛型的类型，解决问题。
 * 但是缺陷有两个，
 * 一是定义的TypeReference的子类型的泛型，不能再嵌套泛型。
 * 即可以这样定义public class MapTypeReference<K,V> extends TypeReference<Map<K,V>>{}
 * 但是不能这样定义public class MapTypeReference<K,E> extends TypeReference<Map<K,List<E>>{}
 * 可以这样TypeReference<Map<String,List<User>> tf = new TypeReference<Map<String,List<User>>(){};
 * 第二个是不能动弹定义TypeReference的子类型。
 * 
 * 这个类，就是阅读了TypeReference源码之后，根据其工作原理，设计的一个支持动态处理嵌套泛型的工具。
 * 		String text = ...;
 *     	Method method = MethodUtil.getMethodByName("User", "testMap");
    	//获取方法第0个参数带泛型的类型，通用点可以先判断是否为泛型类型。
    	ParameterizedType parameterizedType = (ParameterizedType) method.getGenericParameterTypes()[0];
		Type type = JSONTypeUtil.createType(parameterizedType);
    	Object o = JSON.parseObject(text, type);
    	System.out.println(o);
    这样就可以反序列化嵌套泛型了。
    
    缺陷：
    不能处理未泛化的泛型类型、带通配符的泛型类型。即方法参数的泛型类型都必须是明确的类型。
    不能处理接口类型和抽象类型。
    不能处理元素类型不相同的集合/数组
 */
public class JSONTypeUtil {
	private static TypeReference<?> typeReference = new TypeReference<Object>(){};
	private static ConcurrentMap<Type, Type> classTypeCache;
	static{
		try {
			Field field = TypeReference.class.getDeclaredField("classTypeCache");
			field.setAccessible(true);
			classTypeCache = (ConcurrentMap<Type, Type>) field.get(null);
			field.setAccessible(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Type createType(ParameterizedType parameterizedType){
        Type rawType = parameterizedType.getRawType();
        Type[] argTypes = parameterizedType.getActualTypeArguments();

        Type key = new ParameterizedTypeImpl(argTypes, typeReference.getClass() , rawType);
        Type cachedType = classTypeCache.get(key);
        if (cachedType == null) {
            classTypeCache.putIfAbsent(key, key);
            cachedType = classTypeCache.get(key);
        }
        return cachedType;
    }
}

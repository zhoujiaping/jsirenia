

import java.lang.reflect.Array;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

/**
 * 经验：只有不关心类型信息的时候，才用不带类型信息的序列化。
 */
public class JSONUtil {
	/**
	 * @param obj:对象或数组或集合
	 * */
	public static String toJSONString(Object obj,boolean prettyFormat){
		//JSON.toJSONString会自动处理对象和数组
		return JSON.toJSONString(obj,prettyFormat);
	}
	public static String toJSONString(Object obj){
		return toJSONString(obj, false);
	}
	/**
	 * 返回JSONObject或者JSONArray
	* */
	public static Object parse(String text){
		return JSON.parse(text);
	}
	public static <T> T parseObject(String text,Class<T> clazz){
		return JSON.parseObject(text,clazz);
	}

	/*public static Object parseJSONForMethodReturnType(String json,Method method){
		Class<?> returnType = method.getReturnType();// 获取返回值类型
		Type genericReturnType = method.getGenericReturnType();// 获取泛型返回值类型
		return parseJSON(json,returnType,genericReturnType);
	}*/
	/*public static Object toJavaObject(Object obj,Class<?> returnType ,Type genericReturnType){
		if(obj==null){
			return null;
		}
		if(obj instanceof Integer){
			if(Long.class.equals(returnType)){
				return ((Integer)obj).longValue();
			}
		}
		if(obj instanceof JSON){
			JSON json = (JSON) obj;
			String s = json.toJSONString();
			return parseJSON(s,returnType,genericReturnType);
		}
		return obj;
	}*/
	/*public static Object parseJSON(String json,Class<?> returnType ,Type genericReturnType){
		if(JSONObject.class.isAssignableFrom(returnType)){
			return JSONObject.parseObject(json);
		}
		if(JSONArray.class.isAssignableFrom(returnType)){
			return JSONArray.parseArray(json);
		}
		if (returnType.isPrimitive()) {//基本类型，以及void
			return JSONObject.parseObject(json, returnType);
		}
		if (returnType.isArray()) {//数组类型
			Class<?> componentType = returnType.getComponentType();
			JSONArray jsonArray = JSONArray.parseArray(json);
			Object array = Array.newInstance(componentType,jsonArray.size());
			for(int i=0;i<jsonArray.size();i++){
				Array.set(array, i, jsonArray.get(i));
			}
			return array;
		}
		if(returnType.isAnnotation()){
			throw new RuntimeException("不支持注解类型");//无法实现
		}
		if(returnType.isEnum()){
			return JSONObject.parseObject(json, returnType);
		}
		if(returnType.isInterface()){//如果是接口
			if(List.class.isAssignableFrom(returnType)){//使用ArrayList
				returnType = ArrayList.class;
			}else if(Set.class.isAssignableFrom(returnType)){//使用HashSet
				returnType = HashSet.class;
			}else if(Map.class.isAssignableFrom(returnType)){
				returnType = HashMap.class;
			}else{
				throw new RuntimeException("不支持List, Set, Map之外的接口类型");
			}
		}
		if(Modifier.isAbstract(returnType.getModifiers())){//抽象类
			throw new RuntimeException("不支持抽象类型");
		}
		if(Collection.class.isAssignableFrom(returnType)){//集合类型
			if(genericReturnType instanceof ParameterizedType){//有泛型参数
				ParameterizedType pt = (ParameterizedType) genericReturnType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				if(actualTypeArguments[0] instanceof WildcardType){
					throw new RuntimeException("不支持集合泛型通配符");
				}
				if(actualTypeArguments[0] instanceof ParameterizedType){
					throw new RuntimeException("不支持集合嵌套泛型");//比如List<Map<String,Object>>
				}
				Class<?> c = (Class<?>) actualTypeArguments[0];
				return JSONArray.parseArray(json, c);
			}else{//无泛型参数
				throw new RuntimeException("不支持未泛化的集合类型");
			}
		}
		if(Map.class.isAssignableFrom(returnType)){
			if(genericReturnType instanceof ParameterizedType){//有泛型参数
				ParameterizedType pt = (ParameterizedType) genericReturnType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				Type keyType = actualTypeArguments[0];
				Type valueType =actualTypeArguments[1];
				if(keyType instanceof WildcardType || valueType instanceof WildcardType){
					throw new RuntimeException("不支持Map泛型通配符");
				}
				if(keyType instanceof ParameterizedType || valueType instanceof ParameterizedType){
					throw new RuntimeException("不支持Map嵌套泛型");
				}
				TypeReference<?> tf = new MapTypeReference<>(actualTypeArguments);
				return JSON.parseObject(json, tf.getType());
				//return JSONObject.parseObject(retString,tf );
			}else{//无泛型参数
				throw new RuntimeException("不支持未泛化的Map类型");
			}
		}
		if(genericReturnType instanceof ParameterizedType){//有泛型参数
			ParameterizedType pt = (ParameterizedType) genericReturnType;
			//Type[] actualTypeArguments = pt.getActualTypeArguments();
			Type type = JSONTypeUtil.createType(pt);
			return JSON.parseObject(json,type);
		}
		if(genericReturnType instanceof TypeVariable<?>){
			throw new RuntimeException("不支持返回值为泛型变量");
		}
		return JSONObject.parseObject(json, returnType);
	}*/
}

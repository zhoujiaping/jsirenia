package org.jsirenia.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.jsirenia.json.JSONUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

public class MethodUtil {
	public static final Class<?> objArrayClass = Object[].class;
	private static ParserConfig config = new ParserConfig();
	static{
		config.setAutoTypeSupport(true);
	}
	/**
	 * 带@type的json
	 * @param args
	 * @return
	 */
	public static Object[] parseJSONForArgsWithType(String args){
		return JSON.parseObject(args,objArrayClass , config);
	}
	public static Object[] parseJSONForArgs(Method method,String args){
		Type[] argTypes = method.getGenericParameterTypes();
    	List<Object> list = JSON.parseArray(args,argTypes);
    	return null;
	}
	/**
	 * 不带@type的json
	 * args：JSON.parseArray("[...]")的结果，或者JSON.parse("{..."list":[...]}")的list属性值
	 */
	public static Object[] parseJSONForArgs(Method method,List<Object> args){
		/*Class<?>[] parameterTypes = method.getParameterTypes();
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		Object[] methodArgs = new Object[parameterTypes.length];
		for(int i=0;i<parameterTypes.length;i++){
			methodArgs[i] = JSONUtil.toJavaObject(args.get(i), parameterTypes[i],genericParameterTypes[i]);
		}
		return methodArgs;*/
		return null;
	}
	public static Object parseJSONForReturnType(String json, Method method) {
		Class<?> returnType = method.getReturnType();// 获取返回值类型
		Type genericReturnType = method.getGenericReturnType();// 获取泛型返回值类型
		return JSON.parseObject(json, genericReturnType);
		//return JSONUtil.parseJSON(json, returnType, genericReturnType);
	}
	public static Method getMethodByName(String clazz, String method) {
		try {
			Class<?> klass = Class.forName(clazz);
			Method[] methods = klass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals(method)) {
					return methods[i];
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对于无参方法，需要传空数组
	 * 
	 * @param clazz
	 * @param method
	 * @param argTypes
	 * @return
	 */
	public static Method getMethod(String clazz, String method, String... argTypes) {
		try {
			Class<?> klass = Class.forName(clazz);
			if (argTypes == null) {
				return klass.getMethod(method);
			}
			Class<?>[] types = new Class<?>[argTypes.length];
			for (int i = 0; i < types.length; i++) {
				types[i] = Class.forName(argTypes[i]);
			}
			return klass.getMethod(method, types);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

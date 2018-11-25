package org.jsirenia.aop;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AspectUtil {
	public static boolean hasTypeArg(Class<?> clazz){
		TypeVariable<?>[] tv = clazz.getTypeParameters();
		return tv!=null && tv.length>0;
	}
	public static boolean isArray(Class<?> clazz){
		if(clazz.isArray()){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isCollection(Class<?> clazz){
		if(Collection.class.isAssignableFrom(clazz)){
			return true;
		}else{
			return false;
		}
	}
	public static Class<?> getReturnType(ProceedingJoinPoint joinPoint){
		Signature signature =	joinPoint.getSignature();
		MethodSignature ms = (MethodSignature)signature;
		Method method = ms.getMethod();
		return method.getReturnType();
	}
	public static Type[] getActualReturnTypeArguments(ProceedingJoinPoint joinPoint){
		Signature signature =	joinPoint.getSignature();
		MethodSignature ms = (MethodSignature)signature;
		Method method = ms.getMethod();
		Type t = method.getGenericReturnType();
		if(t instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType) t;
			return pt.getActualTypeArguments();
		}
		return null;
	}
	public static Type[] getActualReturnTypeArguments(Method method){
		Type t = method.getGenericReturnType();
		if(t instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType) t;
			return pt.getActualTypeArguments();
		}
		return null;
	}
	public List<AspectUtil> aaa(){
		return null;
	}
	//@Test
	public void test() throws NoSuchMethodException, SecurityException, ClassNotFoundException{
		Method method = AspectUtil.class.getMethod("aaa");
		Type[] res = getActualReturnTypeArguments(method);
		System.out.println(res[0] instanceof WildcardType);
		//Class<?> c = Class.forName(res[0].getTypeName());
		System.out.println((Class)res[0]);
	}
}

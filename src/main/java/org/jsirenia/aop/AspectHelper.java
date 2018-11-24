package org.jsirenia.aop;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

public class AspectHelper {
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
	public List<AspectHelper> aaa(){
		return null;
	}
	//@Test
	public void test() throws NoSuchMethodException, SecurityException, ClassNotFoundException{
		Method method = AspectHelper.class.getMethod("aaa");
		Type[] res = getActualReturnTypeArguments(method);
		System.out.println(res[0] instanceof WildcardType);
		//Class<?> c = Class.forName(res[0].getTypeName());
		System.out.println((Class)res[0]);
	}
}

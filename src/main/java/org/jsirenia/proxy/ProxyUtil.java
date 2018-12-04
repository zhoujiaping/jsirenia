package org.jsirenia.proxy;

import org.jsirenia.exception.demo.ApiProducerDemo;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class ProxyUtil {
	public static <T> T createProxy(Class<T> superclass,MethodInterceptor methodInterceptor){
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(superclass);
		enhancer.setCallback(methodInterceptor);
		Object proxyObj = enhancer.create();
		return (T) proxyObj;
	}
	public static void main(String[] args){
		MethodInterceptor methodInterceptor = new MyMethodInterceptor();
		ApiProducerDemo proxy = createProxy(ApiProducerDemo.class, methodInterceptor);
		String res = proxy.filter();
		System.out.println(res);
	}
}

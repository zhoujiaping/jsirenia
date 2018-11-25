package org.jsirenia.proxy;

import org.jsirenia.exception.demo.ApiProducerDemo;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class ProxyFactory {
	public <T> T createProxy(Class<T> clazz,MethodInterceptor methodInterceptor){
		Enhancer enhancer = new Enhancer();
		Class<?> superclass = ApiProducerDemo.class;
		enhancer.setSuperclass(superclass);
		enhancer.setCallback(methodInterceptor);
		Object proxyObj = enhancer.create();
		return (T) proxyObj;
	}
	public static void main(String[] args){
		ProxyFactory fac = new ProxyFactory();
		MethodInterceptor methodInterceptor = new MyMethodInterceptor();
		ApiProducerDemo proxy = fac.createProxy(ApiProducerDemo.class, methodInterceptor);
		String res = proxy.filter();
		System.out.println(res);
	}
}

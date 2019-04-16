package org.jsirenia.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import org.jsirenia.exception.demo.ApiProducerDemo;
import org.jsirenia.reflect.ReflectHelper;
import org.jsirenia.util.Callback.Callback31;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class ProxyUtil {
	/**
	 * 根据java类创建cglib代理
	 * @param superclass
	 * @param methodInterceptor
	 * @return
	 */
	public static <T> T createProxy(Class<T> superclass,MethodInterceptor methodInterceptor){
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(superclass);
		enhancer.setCallback(methodInterceptor);
		Object proxyObj = enhancer.create();
		return (T) proxyObj;
	}
	/**
	 * 根据java对象创建cblib代理，注意，intercept方法的第一个参数是被代理对象。
	 * @param target
	 * @param cb
	 * @return
	 */
	public static <T> T createProxy(Object delegate,Callback31<Object,Object,Method,Object[]> cb){
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(delegate.getClass());
		enhancer.setCallback(new MethodInterceptor(){
			@Override
			public Object intercept(Object obj, Method m, Object[] args, MethodProxy mp) throws Throwable {
				return cb.apply(delegate,m,args);
			}});
		Object proxyObj = enhancer.create();
		return (T) proxyObj;
	}
	/**
	 * jdk动态代理，返回值是被代理对象的接口类型
	 * @param delegate
	 * @param handler
	 * @return
	 */
	public static <T> T newProxyInstance(Object delegate,InvocationHandler handler){
		Class<?> clazz = delegate.getClass();
		Set<Class<?>> is = ReflectHelper.getAllInterfaces(clazz);
		InvocationHandler h = new InvocationHandler() {
			@Override
			public Object invoke(Object target, Method method, Object[] args) throws Throwable {
				return handler.invoke(delegate, method, args);
			}
		};
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), is.toArray(new Class<?>[is.size()]), h );
	}
	
	public static void main(String[] args){
		MethodInterceptor methodInterceptor = new MyMethodInterceptor();
		ApiProducerDemo proxy = createProxy(ApiProducerDemo.class, methodInterceptor);
		String res = proxy.filter();
		System.out.println(res);
	}
}

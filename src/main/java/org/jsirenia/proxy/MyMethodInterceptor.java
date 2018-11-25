package org.jsirenia.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class MyMethodInterceptor implements MethodInterceptor{
	 private void before() {
	        System.out.println("before method invoke");  
	    }  
	    private void after() {
	        System.out.println("after method invoke");  
	    }  
		public void afterReturning(Object returnVal) {
			System.out.println("后置通知...." + returnVal);
		}
	    protected Object around(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable{
	    	return methodProxy.invokeSuper(target, args);
	    }
		public void afterThrowing(Throwable e) {
			System.out.println("出现异常:msg=" + e.getMessage());
		}
		@Override
		public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			try{
				before();
				Object res = around(target, method, args, methodProxy);
				afterReturning(res);
				return res;
			}catch(Exception e){
				afterThrowing(e);
				throw e;
			}finally{
				after();
			}
		}  
}

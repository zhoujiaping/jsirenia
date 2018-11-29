package org.jsirenia.dubbo.proxy;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
@Component("stubService")
public class StubServiceImpl implements StubService{
	
	public Object invoke(String interfaceClazz,String method,String jsonArray){
		try {
			Class<?> delegateClazz = Class.forName(interfaceClazz);
			Object delegate = ProxyBootstrap.getRefFromActual(delegateClazz);
			Class<?> clazz ;
			Object target;
			try{
				clazz = Class.forName(interfaceClazz+"Mock");
				target = clazz.newInstance();
				Delegateable delegateable = (Delegateable) target;
				delegateable.setDelegate(delegate);
				Method m = getMethod(clazz,method);
				return m.invoke(target, JSONArray.parseArray(jsonArray,Object.class).toArray());
			}catch(ClassNotFoundException e0){
				Method m = getMethod(delegateClazz,method);
				return m.invoke(delegate, JSONArray.parseArray(jsonArray,Object.class));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private Method getMethod(Class<?> targetClazz,String method) throws NoSuchMethodException, SecurityException{
		Method[] methods = targetClazz.getMethods();
		for(Method m : methods){
			if(m.getName().equals(method)){
				return m;
			}
		}
		return null;
	}
}

package org.jsirenia.dubbo.filter;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 开发环境为了方便测试，将所有的dubbo请求拦截，去请求mock。
 * mock可能直接返回结果，也可能请求真正的producer，然后将真正的结果返回给consumer。
 *
 */
public class DubboProducerMockFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String name = invoker.getInterface().getName();
		Object[] args = invocation.getArguments();
		String method = invocation.getMethodName();
		String prefix = "生产者："+name+"."+method;
		logger.info(prefix+" 入参=>"+JSONArray.toJSONString(args));
		//如果找不到对应的Mock 类，打印消息，然后直接调用真实的dubbo服务。
		//如果找到了对应的Mock类，先调用setDelegate方法，然后调用method
		Result r = invokeMock(invoker,invocation);
		//Result r = invoker.invoke(invocation);
		if(r.hasException()){
			Throwable e = r.getException();
			if(e.getClass().getName().equals("java.lang.RuntimeException")){
				logger.error(prefix+" 运行时异常=>"+JSONObject.toJSONString(r));
			}else{
				logger.error(prefix+" 异常=>"+JSONObject.toJSONString(r));
			}
		}else{
			logger.info(prefix+" 结果=>"+JSONObject.toJSONString(r));
		}
		return r;
	}
	private Result invokeMock(Invoker<?> invoker, Invocation invocation) {
		try{
			Class<?> clazz = invoker.getInterface();
			Object[] args = invocation.getArguments();
			String method = invocation.getMethodName();
			String simpleName = clazz.getSimpleName();
			simpleName = simpleName.substring(0, 1).toLowerCase()+simpleName.substring(1)+"Mock";
			String clazzName = clazz.getName();
			clazzName = clazzName.substring(0, 1).toLowerCase()+clazzName.substring(1)+"Mock";
			Class<?> mockClazz ;
			try{
				mockClazz = Class.forName(clazzName);
			}catch(ClassNotFoundException e0){
				return invoker.invoke(invocation);
			}
			Object mock = mockClazz.newInstance();
			Method m = getMethod(mock.getClass(), method);
			Object result = m.invoke(mock, args);
			RpcResult rpcResult = new RpcResult(result);
			return rpcResult;
		}catch(Exception e){
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

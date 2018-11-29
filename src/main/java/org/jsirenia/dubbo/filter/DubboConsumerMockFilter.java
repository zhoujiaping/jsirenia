package org.jsirenia.dubbo.filter;


import org.jsirenia.dubbo.common.StubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 开发环境为了方便测试，将所有的dubbo请求拦截，去请求stub。
 * stub可能直接返回结果，也可能请求真正的producer，然后将真正的结果返回给consumer。
 */
public class DubboConsumerMockFilter implements Filter {
	/*private static ApplicationConfig application = new ApplicationConfig();
	static{
		application.setName("jyd-repayment");
	}*/
	static StubService stub;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String name = invoker.getInterface().getName();
		Object[] args = invocation.getArguments();
		String method = invocation.getMethodName();
		String prefix = "消费者："+name+"."+method;
		logger.info(prefix+" 入参=>"+JSONArray.toJSONString(args));
		Result r = invokeProxy(invoker,invocation);
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
	private Result invokeProxy(Invoker<?> invoker, Invocation invocation) {
		try{
			Class<?> interfaceClazz = invoker.getInterface();
			String name = interfaceClazz.getName();
			Object[] args = invocation.getArguments();
			String method = invocation.getMethodName();
			//StubService stub = createStub();
			if(stub==null){//不考虑并发问题
				stub = createStub();
			}
			Object result = stub.invoke(name, method, JSONArray.toJSONString(args));
			RpcResult rpcResult = new RpcResult(result);
			return rpcResult;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private static StubService createStub(){
		ApplicationConfig application = new ApplicationConfig();
		application.setName("dubbo-client");
		// Registry Info
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("127.0.0.1:2181");
		//registry.setUsername("aaa");
		//registry.setPassword("bbb");
		 
		// NOTES: ReferenceConfig holds the connections to registry and providers, please cache it for performance.
		 
		// Refer remote service
		ReferenceConfig<?> reference = new ReferenceConfig<>(); // In case of memory leak, please cache.
		//reference.setApplication(application);
		reference.setRegistry(registry); 
		reference.setInterface(StubService.class);
		//reference.setVersion("1.0.0");
		reference.setApplication(application);
		reference.setRetries(1);
		reference.setCheck(false);
		reference.setTimeout(100000);
		 
		// Use xxxService just like a local bean
		Object proxy = reference.get(); // NOTES: Please cache this proxy instance.
		return (StubService) proxy;
	}
}

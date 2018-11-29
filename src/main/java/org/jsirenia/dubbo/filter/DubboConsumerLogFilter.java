package org.jsirenia.dubbo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DubboConsumerLogFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String name = invoker.getInterface().getName();
		Object[] args = invocation.getArguments();
		String method = invocation.getMethodName();
		String prefix = "消费者："+name+"."+method;
		logger.info(prefix+" 入参=>"+JSONArray.toJSONString(args));
		Result r = invoker.invoke(invocation);
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

}

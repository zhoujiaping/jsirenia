package org.jsirenia.dubbo;

import org.jsirenia.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class DubboProducerLogFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger("DPLFilter");
	private SerializeConfig inConfig = FastJsonLogUtil.getInSerializeConfig();
	private SerializeConfig outConfig = FastJsonLogUtil.getOutSerializeConfig();
	private SerializerFeature features = SerializerFeature.WriteDateUseDateFormat;

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String name = invoker.getInterface().getName();
		Object[] args = invocation.getArguments();
		String method = invocation.getMethodName();
		String prefix = "提供者："+name+"#"+method;
		logger.info("{} 入参=>【{}】",prefix,JSONArray.toJSONString(args,inConfig,features));
		Result r = invoker.invoke(invocation);
		if(r.hasException()){
			Throwable e = r.getException();
			if(e instanceof ServiceException) {
				ServiceException serviceException = (ServiceException) e;
				String msg = serviceException.getMsg();
				if(StringUtils.isEmpty(msg)){
					msg = serviceException.getMessage();
				}
				String location = findLocation(e);
				logger.info(prefix+" 业务异常 "+location+"=>"+ msg);
			}else if(e instanceof RuntimeException) {
				logger.info(prefix+" 运行时异常=>"+ e.getMessage());
			}else{
				logger.info(prefix+" 异常=>"+ e.getMessage());
			}
		}else{
			Object v = r.getValue();
			if(v instanceof byte[]){
				byte[] b = (byte[])v;
				logger.info(prefix+" 结果=> 字节数组，长度"+b.length);
			}else{
				logger.info(prefix+" 结果=>"+ JSONObject.toJSONString(v,outConfig,features));
			}
		}
		return r;
	}
	private String findLocation(Throwable e){
		StackTraceElement[] traces = e.getStackTrace();
		StackTraceElement trace = traces[0];
		String className = trace.getClassName();
		String methodName = trace.getMethodName();
		String fileName = trace.getFileName();
		int lineNumber = trace.getLineNumber();
		return "at "+className+"."+methodName+"("+fileName+":"+lineNumber+")";
	}

}

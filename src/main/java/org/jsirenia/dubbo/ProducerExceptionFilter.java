package org.jsirenia.dubbo;

import org.jsirenia.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
/**
 * rpc统一异常处理
 * rpc接口必须声明抛出ServiceException
 * dubbo ExceptionFilter  参考https://www.jianshu.com/p/c5ebe3e08161
 * 对于dubbo重新包装的异常（包装为RuntimeException），dubbo会打印error日志。
 * 对于dubbo不重新包装的异常，dubbo不会打印error日志。
 */
public class ProducerExceptionFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Result r = invoker.invoke(invocation);
		if(r instanceof RpcResult){
			RpcResult rr = (RpcResult) r;
			if(rr.hasException()){
				Throwable e = rr.getException();
				/*
				 * 如果开发者遵循了 所有dubbo接口都声明throws ServiceException
				 * 有3种情况：
				 * 1、ServiceException不包含cause
				 * 不处理
				 * 2、ServiceException包含cause
				 * 替换异常对象
				 * 3、系统异常
				 *  替换异常对象
				 *  
				 * 如果开发者在接口声明上throws 其他异常
				 * 会被替换成ServiceException，这时候系统行为就会不符合开发者预期，
				 * 为了防止这种现象，可以配合代码检查，校验接口的throws
				 * */
				if(e instanceof ServiceException){
					ServiceException se = (ServiceException) e;
					Throwable c = se.getCause();
					if(c != null){
						logger.error("业务异常",se);
						rr.setException(new ServiceException(se.getCode(),se.getMsg()));
					}
				}else{
					logger.error("接口异常",e); //该异常dubbo不会打印
					rr.setException(new ServiceException("9999","系统异常"));
				}
			}
		}
		return r;
	}

}

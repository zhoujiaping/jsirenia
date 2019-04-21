package org.jsirenia.exception.demo;

import org.jsirenia.exception.ApiException;
import org.jsirenia.exception.Result;
import org.jsirenia.exception.ServiceException;

/**
 * api消费者(Demo2模块)统一异常处理
 * 比如dubbo的cosumer filter做统一异常处理
 */
public class ApiConsumerDemo {
	public Result<Object> filter(String method){
		Result<Object> result = new Result<>();
		try{
			if(method=="doService1"){
				result.setData(new ApiConsumerServiceDemo().doService1());
			}else{
				result.setData(new ApiConsumerServiceDemo().doService2());
			}
			result.success();
			return result;
		}catch(ApiException e){
			System.out.println("at consumer");
			System.err.print(e.getCode());
			e.printStackTrace();
			String msg = e.getMessage();
			if(msg==null||msg.trim()==""){
				msg = "系统内部错误";
			}
			result.systemError(msg);
			return result;
		}catch(ServiceException e){
			System.out.println("at consumer");
			e.printStackTrace();//模拟日志打印异常
			return result.serviceError(e.getCode(), e.getMsg());
		}catch(Exception e){
			System.out.println("at consumer");
			e.printStackTrace();//模拟日志打印异常
			return result.systemError("系统内部异常");
		}
	}
}

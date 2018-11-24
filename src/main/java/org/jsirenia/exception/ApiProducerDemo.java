package org.jsirenia.exception;
/**
 * api提供者的统一异常处理
 * 比如dubbo的producer filter做统一异常处理
 */
public class ApiProducerDemo {
	public String filter(){
		try{
			return new ApiProducerServiceDemo().doService();
		}catch(ApiException e){
			throw e;
		}catch(ServiceException e){
			System.out.println("at producer");
			e.printStackTrace();//模拟日志打印异常
			throw new ApiException(e);
		}catch(Exception e){
			e.printStackTrace();//模拟日志打印异常
			throw new ApiException(new ServiceException(DemoExceptionCode.SYSTEM_ERROR,"系统内部异常了"));
		}
	}
}

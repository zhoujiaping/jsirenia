package org.jsirenia.exception.demo;

import org.jsirenia.exception.ServiceException;

/**
 * api提供者(Demo模块)的业务异常处理
 */
public class ApiProducerServiceDemo {
	public String doService(){
		throw new ServiceException("001","id为1的用户未找到");
	}
}

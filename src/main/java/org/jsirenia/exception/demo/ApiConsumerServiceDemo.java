package org.jsirenia.exception.demo;

import java.io.FileNotFoundException;

/**
 * api消费者业务异常处理
 */
public class ApiConsumerServiceDemo {
	public String doService1(){
		return new ApiProducerDemo().filter();
	}
	public String doService2() throws FileNotFoundException{
		throw new FileNotFoundException();
	}
}

package org.jsirenia.exception;
/**
 * 测试api异常设计
 * 结果：src/main/resource/exception/异常处理结果.png
 * 其中有些消息的顺序错了，是因为控制台打印是异步的，并不是同步的，改成slf4j日志（或者log4j或者其他）就可以了。
 */
public class ApiExceptionTest {
	public static void main(String[] args) {
		try{
			Result r = test1();
			System.out.println(r);
		}catch(Exception e){
			System.out.println("at main");
			e.printStackTrace();
		}
		try{
			Result r = test2();
			System.out.println(r);
		}catch(Exception e){
			System.out.println("at main");
			e.printStackTrace();
		}
	}
	public static Result test1(){
		return new ApiConsumerDemo().filter("doService1");
	}
	public static Result test2(){
		return new ApiConsumerDemo().filter("doService2");
	}
}

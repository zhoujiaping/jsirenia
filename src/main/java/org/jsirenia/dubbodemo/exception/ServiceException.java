package org.jsirenia.dubbodemo.exception;


/**
 * 系统内部异常
 * 携带异常栈
 * 处理业务有异常时，抛此异常
 */
public class ServiceException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	/**
	 * 这个属性是提供给接口调用者的消息
	 */
	private String msg;
	
	public ServiceException(String code,String msg){
		super(code+"#"+msg);
		this.code = code;
		this.msg = msg;
	}
	public ServiceException(String code,String msg,Throwable cause){
		super(code+"#"+msg,cause);
		this.code = code;
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}

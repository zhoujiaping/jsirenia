package org.jsirenia.exception;

/**
 * 系统内部异常
 * 携带异常栈
 * 处理业务有异常时，抛此异常
 */
public class ServiceException extends RuntimeException{
	//默认使用properties的实现
	private static ExceptionCodeSource exceptionCodeSource = new PropertiesExceptionCodeSource();
	public static ExceptionCodeSource currentExceptionCodeSource(){
		return exceptionCodeSource;
	}
	public static void withExceptionCodeSource(ExceptionCodeSource exceptionCodeSource ){
		ServiceException.exceptionCodeSource = exceptionCodeSource;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	/**
	 * 这个属性是提供给接口调用者的消息
	 */
	private String msg;
	
	public ServiceException(String code,String message){
		super(message);
		this.code = code;
		this.msg = exceptionCodeSource.getMsg(code);
	}
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	@Override
	public String getMessage() {
		String message = super.getMessage();
		return code+"#"+message+"#"+msg;
	}
}

package org.jsirenia.exception;
/**
 * 接口异常
 * 不携带异常栈
 * 
 * 系统异常设计（包括异常日志）
 * 1、系统内部异常
 * 2、接口异常
 * 用统一异常处理的方式，将异常栈打印，并且重新抛出异常。
 * 重新抛出的异常，不要带原来的异常，避免将系统内部信息暴露给接口外部。
 * 重新跑出来的异常，其异常消息对客户是可理解的，比如“系统内部错误”、“用户名不合法”，
 * 而内部异常的消息需要记录详细的信息，比如“用户id【】关联角色列表为null”、“用户名不符合正则表达式【[a-zA-Z]{1,30}】”。
 * 有时候接口调用者要区分异常发生的各种原因，这时候可以设计接口异常还包含一个异常码。
 * 异常码的设计，需要考虑到接口调用者可能调用多个系统的接口。
 * 需要为异常码设计一个层次。第一层用于区分系统，第二层用于区分系统内各种异常。
 * 否则可能会出现a模块和b模块都有一个异常码为"500"的情况，调用者要区分他们，需要做更多的工作。
 * 返回给外部的异常消息，其内容有时候需要满足产品设计的需求，往往不只是个技术问题，
 * 还可能要考虑法律等问题，所以异常消息需要能够很容易进行修改，
 * 要求不重新编译、重启系统即可生效，或者不用重启系统也能够生效。
 * 如果再考虑到运维，修改文件需要经过各种流程才能生效，还可以将异常消息存储在数据库。
 * 还有一种情况，a模块调用b模块，可能出现的情况有：
 * a模块捕获了b模块抛出的ApiException，a模块捕获了a模块底层抛出的ServiceException，a模块捕获其他Exception。
 * 对于第一种情况，直接抛出即可。对于第二种情况，转换成ApiException。
 * 对于第三种情况，需要表达是a模块系统内部异常，
 * 做法是先将其他Exception封装为ServiceException,code为a模块系统内部异常的code，
 * 然后再转换成ApiException。
 */
public class ApiException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	public ApiException(ServiceException e){
		super(e.getMsg());
		this.code = e.getCode();
	}
	public String getCode() {
		return code;
	}
}

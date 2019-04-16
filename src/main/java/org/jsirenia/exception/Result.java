package org.jsirenia.exception;

import java.io.Serializable;

import org.jsirenia.exception.demo.ResultCode;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回给用户的结果
 */
public class Result<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String msg;
	private T data;
	public String getCode(){return this.code;}
	public void setCode(String code){
		this.code = code;
	}
	public Result<T> success(){
		this.code = ResultCode.SUCCESS;
		return this;
	}
	public Result<T> systemError(String msg){
		this.code = ResultCode.SYSTEM_ERROR;
		this.msg = msg;
		return this;
	}
	public Result<T> serviceError(String code,String msg){
		this.code = code;
		this.msg = msg;
		return this;
	}
	public Result<T> success(T data){
		this.code = ResultCode.SUCCESS;
		this.data = data;
		return this;
	}
	public boolean isSuccess() {
		return ResultCode.SUCCESS.equals(code);
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString(){
		return JSONObject.toJSONString(this);
	}
	
}

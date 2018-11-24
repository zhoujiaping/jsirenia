package org.jsirenia.exception;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回给用户的结果
 */
public class Result<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String msg;
	private T data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
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

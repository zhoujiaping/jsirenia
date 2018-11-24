package org.jsirenia.exception;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * 返回给用户的结果
 */
public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String msg;
	private Object data;
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
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	@Override
	public String toString(){
		return JSONObject.toJSONString(this);
	}
	
}

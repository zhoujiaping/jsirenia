package org.jsirenia.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Res<T> {
	private String code;
	private String msg;
	private T data;
	public Res(){
		
	}
	public Res(T data){
		this.data = data;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String toString(){
    	return JSON.toJSONString(this);
    }
    public String toStringWithType(){
		return JSON.toJSONString(this, SerializerFeature.WriteClassName);
    }
}

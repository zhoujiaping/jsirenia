package org.jsirenia.json;

import java.util.Collection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
	public static String toJSONString(Object obj){
		String jsonString = null;
		if(obj!=null){
			Class<?> clazz = obj.getClass();
			if(clazz.isArray()){
				jsonString = JSONArray.toJSONString(obj);
			}else if(Collection.class.isAssignableFrom(clazz)){
				jsonString = JSONArray.toJSONString(obj);
			}else{
				jsonString = JSONObject.toJSONString(obj);
			}
		}else{
			jsonString = JSONObject.toJSONString(obj);
		}
		return jsonString;
	}
}

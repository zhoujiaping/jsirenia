package org.jsirenia.reflect;

import java.lang.reflect.Modifier;
import java.util.Collection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TypeUtil {
    public static boolean isArray(Class<?> clazz){
        return clazz.isArray();
    }
    public static boolean isCollection(Class<?> clazz){
        return Collection.class.isAssignableFrom(clazz);
    }
    public static boolean isJSONArray(Class<?> clazz){
        return JSONArray.class.isAssignableFrom(clazz);
    }
    public static boolean isJSONObject(Class<?> clazz){
        return JSONObject.class.isAssignableFrom(clazz);
    }
    public static boolean isPrimitive(Class<?> clazz){
    	return clazz.isPrimitive();
    }
    public static boolean isAbstract(Class<?> clazz){
    	return Modifier.isAbstract(clazz.getModifiers());
    }
}

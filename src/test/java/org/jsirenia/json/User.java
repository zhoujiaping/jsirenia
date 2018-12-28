package org.jsirenia.json;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class User implements IUser{
    private String name;
    
    public User(){}
    public User(String name){
    	this.name = name;
    }
    
    @UserAnno(name="getName",value="GETNAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //原始类型
    public Object testPrimitive(String a,int b,char c){
    	return new Object[]{a,b,c};
    }
    //map类型
    public Object testMap(Map<String,User> map,String a){
    	return new Object[]{map,a};
    }
    //map嵌套
    public String testMapNest(Map<String,Res<List<User>>> map,String a){
    	return map.toString()+a;
    }
    //泛型嵌套
    public Object testGenericNest(Res<List<User>> res){
    	return res;
    }
    //泛型数组
    public Map<String,Res<List<User>>>[] testGenericArray(Map<String,Res<List<User>>>[] res){
    	return res;
    }
    //数组的数组
    public Map<String,Res<List<User>>>[][] testGenericArrayArray(Map<String,Res<List<User>>>[][] res){
    	return res;
    }
    //数组
    public Object testArray(User[] res){
    	return res;
    }
    //枚举类型
    public Object testEnum(UserStatus status){
    	return status;
    }
    //JSON类型
    public Object testJSON(JSON json,JSON json2){
    	return new Object[]{json,json2};
    }
    public String toString(){
    	return JSON.toJSONString(this);
    }
    public String toStringWithType(){
		return JSON.toJSONString(this, SerializerFeature.WriteClassName);
    }
}

package org.jsirenia.json;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsirenia.collection.CollectionUtil;
import org.jsirenia.reflect.MethodUtil;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;

public class JSONTest {
	private String userClass = User.class.getName();
    @Test
    public void testToJSONStringWithType() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Object[] users = new Object[2];
        users[0] = new User("john");
        users[1] = new User("lucy");
    	String text = JSONUtil.toJSONStringWithType(users);
    	System.out.println(text);
    	Class<?> clazz = new Object[]{}.getClass();
        Object res = JSONUtil.parseObjectWithType(text, clazz);
        System.out.println(res);
        /*
         [{"@type":"org.jsirenia.json.User","name":"lucy"},null]
		[Ljava.lang.Object;@6bf256fa 
         */
    }
    private void print(Object o){
		String s = JSON.toJSONString(o,SerializerFeature.WriteClassName);
		System.out.println(s);
    }
    /**
     */
    @Test
    public void testPrimitive() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] args = new Object[]{"hello",100,'z'};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testPrimitive");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     * class 带泛型的时候，没有泛型信息fastjson也无能为力
     */
    @Test
    public void testMap() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,User> map = new HashMap<>();
    	map.put("john", new User("john"));
    	String str = "str";
    	Object[] args = new Object[]{map,str};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testMap");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     * type 传class实际上会调用传type的方法,Class是Type的子类型
     */
    @Test
    public void testMapNest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,Res<List<User>>> map = new HashMap<>();
    	map.put("res", new Res(Lists.asList(new User("john"), new User[]{new User("lucy")})));
    	String str = "str";
    	Object[] args = new Object[]{map,str};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testMapNest");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    /**
     */
    @Test
    public void testGenericNest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Res res =  new Res(Lists.asList(new User("john"), new User[]{new User("lucy")}));
    	Object[] args = new Object[]{res};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testGenericNest");
    	Object[] arr = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), arr);
    	print(obj);
    }
    @Test
    public void testGenericArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Map<String,Res<List<User>>> map = new HashMap<>();
    	map.put("res", new Res(Lists.asList(new User("john"), new User[]{new User("lucy")})));
    	Object[] args = new Object[]{new Map[]{map}};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testGenericArray");
    	//Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	//Object obj = method.invoke(new User(), res);
    	//print(obj);
    	/*DefaultJSONParser存在缺陷，如下
    	 *  if (type instanceof Class) {
                            Class<?> clazz = (Class<?>) type;
                            isArray = clazz.isArray();
                            componentType = clazz.getComponentType();
                        }
    	 * 对于数组类型，反序列化时传class，则会丢失泛型信息；传type，则不会使用componentType。
    	 * */
    	Class<?>[] pts = method.getParameterTypes();
    	Type[] argTypes = method.getGenericParameterTypes();
    	list = JSON.parseArray(text,argTypes);//第一个元素的类型，应该是Map[]，但结果却是Object[]。
    	//Object obj = method.invoke(new User(), new Object[]{CollectionUtil.toArray((Object[])list.get(0), Map.class)});
    	for(int i=0;i<list.size();i++){
    		if(pts[i].isArray()){
    			args[i] = CollectionUtil.toArray((Object[])list.get(i), pts[i].getComponentType());
    		}else{
    			args[i] = list.get(i);
    		}
    	}
    	Object obj = method.invoke(new User(), args);
    	print(obj);
    }
    /**
     */
    @Test
    public void testArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object[] args = new Object[]{new Object[]{new User("john"), new User("lucy")}};
    	String text = JSON.toJSONString(args);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testArray");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    	
    	Type[] argTypes = method.getGenericParameterTypes();
    	list = JSON.parseArray(text,argTypes);
    	obj = method.invoke(new User(), list.toArray());
    	print(obj);
    }
    /**
     */
    @Test
    public void testEnum() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	Object args = new Object[]{UserStatus.VALID};
    	String text = JSON.toJSONString(args);
    	print(text);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testEnum");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
    @Test
    public void testJSON() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
    	JSONArray jsonArray = new JSONArray();
    	JSONObject json = new JSONObject();
    	json.put("hello", "world");
    	JSONObject json2 = new JSONObject();
    	json2.put("123", "456");
    	jsonArray.add(json2);
    	Object args = new Object[]{json,jsonArray};
    	String text = JSON.toJSONString(args);
    	print(text);
    	List<Object> list = JSON.parseArray(text);
    	Method method = MethodUtil.getMethodByName(userClass, "testJSON");
    	Object[] res = MethodUtil.parseJSONForArgs(method, list);
    	Object obj = method.invoke(new User(), res);
    	print(obj);
    }
}

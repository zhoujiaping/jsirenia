package org.jsirenia.aop;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsirenia.array.JArray;
import org.jsirenia.file.PathUtil;
import org.jsirenia.json.JSONTypeUtil;
import org.jsirenia.json.JSONUtil;
import org.jsirenia.json.MapTypeReference;
import org.jsirenia.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
/**
 *
 */
public class MethodHttpInterceptor implements MethodInterceptor{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String host = "localhost";
	private int port = 8080;
	private String contextPath = "/";
	private CloseableHttpClient client;//client = HttpClients.createMinimal();
	public MethodHttpInterceptor(CloseableHttpClient client,String host,int port,String contextPath) {
		this.client = client;
		this.host = host;
		this.port = port;
		this.contextPath = contextPath;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public String getContextPath() {
		return contextPath;
	}
	public CloseableHttpClient getClient() {
		return client;
	}
	private Object invoke(Method method,String clazzname, String funcName, String argsJson) {
		try{
			String url = "http://" + PathUtil.concat(host+":"+port, contextPath, clazzname, funcName+"Invoke");
			HttpGet request = new HttpGet(url);
			return client.execute(request, (response)->{
				HttpEntity entity = response.getEntity();
				String json = EntityUtils.toString(entity , "utf-8" );
				return deserializeJSON(method, json);
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private Object deserializeJSON(Method method,String json){
		Class<?> returnType = method.getReturnType();// 获取返回值类型
		Type genericReturnType = method.getGenericReturnType();// 获取泛型返回值类型
		if (returnType.isPrimitive()) {//基本类型，以及void
			return JSONObject.parseObject(json, returnType);
		}
		if (returnType.isArray()) {//数组类型
			Class<?> componentType = returnType.getComponentType();
			JSONArray jsonArray = JSONArray.parseArray(json);
			Object array = Array.newInstance(componentType,jsonArray.size());
			for(int i=0;i<jsonArray.size();i++){
				Array.set(array, i, jsonArray.get(i));
			}
			return array;
		}
		if(returnType.isAnnotation()){
			throw new RuntimeException("不支持注解类型");//无法实现
		}
		if(returnType.isEnum()){
			return JSONObject.parseObject(json, returnType);
		}
		if(returnType.isInterface()){//如果是接口
			if(List.class.isAssignableFrom(returnType)){//使用ArrayList
				returnType = ArrayList.class;
			}else if(Set.class.isAssignableFrom(returnType)){//使用HashSet
				returnType = HashSet.class;
			}else if(Map.class.isAssignableFrom(returnType)){
				returnType = HashMap.class;
			}else{
				throw new RuntimeException("不支持List, Set, Map之外的接口类型");
			}
		}
		if(Modifier.isAbstract(returnType.getModifiers())){//抽象类
			throw new RuntimeException("不支持抽象类型");
		}
		if(Collection.class.isAssignableFrom(returnType)){//集合类型
			if(genericReturnType instanceof ParameterizedType){//有泛型参数
				ParameterizedType pt = (ParameterizedType) genericReturnType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				if(actualTypeArguments[0] instanceof WildcardType){
					throw new RuntimeException("不支持集合泛型通配符");
				}
				if(actualTypeArguments[0] instanceof ParameterizedType){
					throw new RuntimeException("不支持集合嵌套泛型");
				}
				Class<?> c = (Class<?>) actualTypeArguments[0];
				return JSONArray.parseArray(json, c);
			}else{//无泛型参数
				throw new RuntimeException("不支持未泛化的集合类型");
			}
		}
		if(Map.class.isAssignableFrom(returnType)){
			if(genericReturnType instanceof ParameterizedType){//有泛型参数
				ParameterizedType pt = (ParameterizedType) genericReturnType;
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				Type keyType = actualTypeArguments[0];
				Type valueType =actualTypeArguments[1];
				if(keyType instanceof WildcardType || valueType instanceof WildcardType){
					throw new RuntimeException("不支持Map泛型通配符");
				}
				if(keyType instanceof ParameterizedType || valueType instanceof ParameterizedType){
					throw new RuntimeException("不支持Map嵌套泛型");
				}
				TypeReference<?> tf = new MapTypeReference<>(actualTypeArguments);
				return JSON.parseObject(json, tf.getType());
				//return JSONObject.parseObject(retString,tf );
			}else{//无泛型参数
				throw new RuntimeException("不支持未泛化的Map类型");
			}
		}
		if(genericReturnType instanceof ParameterizedType){//有泛型参数
			ParameterizedType pt = (ParameterizedType) genericReturnType;
			//Type[] actualTypeArguments = pt.getActualTypeArguments();
			Type type = JSONTypeUtil.createType(pt);
			return JSON.parseObject(json,type);
		}
		if(genericReturnType instanceof TypeVariable<?>){
			throw new RuntimeException("不支持返回值为泛型变量");
		}
		return JSONObject.parseObject(json, returnType);
	}
	private JSONObject before(Method method,String clazzname, String funcName, String retString) {
		JSONObject ret0 = new JSONObject();
		ret0.put("invoke", true);
		ret0.put("afterReturning", false);
		try{
			deserializeJSON(method,"null");
		}catch(Exception e){
			ret0.put("invoke", false);
			ret0.put("afterReturning", false);
			logger.warn(e.getMessage(), e);
		}
		try{
			String url = "http://" + PathUtil.concat(host+":"+port, contextPath, clazzname, funcName+"Before");
			HttpGet request = new HttpGet(url);
			return client.execute(request, (response)->{
				HttpEntity entity = response.getEntity();
				String body = EntityUtils.toString(entity , "utf-8" );
				return JSONObject.parseObject(body);
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private Object afterReturning(Method method,String clazzname, String funcName, Object ret) {
		try{
			String retJson = JSONUtil.toJSONString(ret);
			String url = "http://" + PathUtil.concat(host+":"+port, contextPath, clazzname, funcName+"AfterReturning");
			HttpPost request = new HttpPost(url);
			HttpEntity reqEntity = new StringEntity(retJson, "utf-8");
			request.setEntity(reqEntity );
			return client.execute(request, (response)->{
				HttpEntity entity = response.getEntity();
				String json = EntityUtils.toString(entity , "utf-8" );
				return deserializeJSON(method, json);
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	@Override
	public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy)
			throws Throwable {
		String argsJson = JSONArray.toJSONString(args);
		String clazzname = target.getClass().getSimpleName();
		int index = clazzname.indexOf('$');
		if(index>-1){
			clazzname = clazzname.substring(0, index);
		}
		String funcName = method.getName();
		JSONObject ret0 = before(method,clazzname,funcName,argsJson);
		Object ret;
		if(ret0.getBoolean("invoke")){
			ret = invoke(method,clazzname,funcName,argsJson);
			return ret;
		}else{
			ret = methodProxy.invokeSuper(target, args);
			if(ret0.getBoolean("afterReturning")){
				ret = afterReturning(method,clazzname,funcName,ret);
			}
			return ret;
		}
	}
	public static void main(String[] args) {
		CloseableHttpClient client = HttpClients.createMinimal();
		MethodHttpInterceptor methodInterceptor = new MethodHttpInterceptor(client,"localhost",8080,"/");
		ProxyFactory fac = new ProxyFactory();
		MyService service = fac.createProxy(MyService.class, methodInterceptor);
		List<String> res = service.query();
		System.out.println(res);
		res = service.query();
		System.out.println(res);
	}
	public static class MyService{
		public List<String> query(){
			List<String> list = new ArrayList<>();
			list.add("hello");
			list.add("world");
			return list;
		}
	}
}

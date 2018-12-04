package org.jsirenia.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsirenia.file.PathUtil;
import org.jsirenia.json.JSONUtil;
import org.jsirenia.proxy.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
				return JSONUtil.parseJSON( json,method);
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private JSONObject before(Method method,String clazzname, String funcName, String retString) {
		JSONObject ret0 = new JSONObject();
		ret0.put("invoke", true);
		ret0.put("afterReturning", false);
		try{
			JSONUtil.parseJSON("null",method);
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
				return JSONUtil.parseJSON( json,method);
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
		MyService service = ProxyUtil.createProxy(MyService.class, methodInterceptor);
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

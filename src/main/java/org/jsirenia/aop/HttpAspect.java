package org.jsirenia.aop;

import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.jsirenia.file.PathUtil;
import org.jsirenia.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 *
 */
public class HttpAspect {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String host = "localhost";
	private int port = 8080;
	private String contextPath = "/";
	private CloseableHttpClient client;//client = HttpClients.createMinimal();
	public HttpAspect(CloseableHttpClient client,String host,int port,String contextPath) {
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

	@Around("execution(* ogr.jsirenia..*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature =	joinPoint.getSignature();
		String funcName = signature.getName();//方法名
		String clazzname = joinPoint.getTarget().getClass().getSimpleName();//简单类名
		try{
			Object ret = null;
			Object[] args = joinPoint.getArgs();
			String argsJson = null;
			if(args!=null && args.length>0){
				argsJson = JSONArray.toJSONString(args);
			}
			logger.info("方法"+clazzname+"."+funcName+"入参=>{}",argsJson);
			JSONObject ret0 = before(clazzname,funcName,argsJson);
			if(ret0.getBoolean("invoke")){
				ret = invoke(clazzname,funcName,argsJson,joinPoint);
				return ret;
			}else{
				ret = joinPoint.proceed();
				if(ret0.getBoolean("afterReturning")){
					ret = afterReturning(clazzname,funcName,ret,joinPoint);
				}
			}
			logger.info("方法"+clazzname+"."+funcName+"结果=>{}",JSONUtil.toJSONString(ret));
			return ret;
		}catch(Exception e){
			logger.error("方法"+clazzname+"."+funcName+"异常，{}=>{}",e.getMessage(),e);
			throw e;
		}finally{
		}
	}
	private Object invoke(String clazzname, String funcName, String argsJson, ProceedingJoinPoint joinPoint) {
		try{
			String url = PathUtil.concat("http://"+host+":"+port, contextPath, clazzname, funcName);
			HttpGet request = new HttpGet(url);
			return client.execute(request, (response)->{
				HttpEntity entity = response.getEntity();
				String body = EntityUtils.toString(entity , "utf-8" );
				Class<?> retClazz = AspectUtil.getReturnType(joinPoint);
				if(AspectUtil.isCollection(retClazz)){
					Type[] types = AspectUtil.getActualReturnTypeArguments(joinPoint);
					Class<?> typeArg = (Class<?>) types[0];
					return JSONArray.parseArray(body, typeArg);
				}else if(AspectUtil.isArray(retClazz)){
					throw new RuntimeException("返回值类型不支持数组");
				}else{
					if(AspectUtil.hasTypeArg(retClazz)){//返回类型有泛型
						throw new RuntimeException("返回值类型只对集合支持泛型");
					}else{
						return JSONObject.parseObject(body, retClazz);
					}
				}
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private JSONObject before(String clazzname, String funcName, String argsJson) {
		try{
			String url = PathUtil.concat("http://"+host+":"+port, contextPath, clazzname, funcName);
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
	private Object afterReturning(String clazzname, String funcName, Object ret, ProceedingJoinPoint joinPoint) {
		try{
			String retJson = JSONUtil.toJSONString(ret);
			String url = PathUtil.concat("http://"+host+":"+port, contextPath, clazzname, funcName);
			HttpPost request = new HttpPost(url);
			HttpEntity reqEntity = new StringEntity(retJson, "utf-8");
			request.setEntity(reqEntity );
			return client.execute(request, (response)->{
				HttpEntity entity = response.getEntity();
				String body = EntityUtils.toString(entity , "utf-8" );
				Class<?> retClazz = AspectUtil.getReturnType(joinPoint);
				if(AspectUtil.isCollection(retClazz)){
					Type[] types = AspectUtil.getActualReturnTypeArguments(joinPoint);
					Class<?> typeArg = (Class<?>) types[0];
					return JSONArray.parseArray(body, typeArg);
				}else if(AspectUtil.isArray(retClazz)){
					throw new RuntimeException("返回值类型不支持数组");
				}else{
					if(AspectUtil.hasTypeArg(retClazz)){//返回类型有泛型
						throw new RuntimeException("返回值类型只对集合支持泛型");
					}else{
						return JSONObject.parseObject(body, retClazz);
					}
				}
			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}

package org.jsirenia.bean;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Aspect
@Component
public class JsAspect {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());



	/**
	 * 环绕通知
	 * 
	 * @param joinPoint
	 *            可用于执行切点的类
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* com.xxx.impl..*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature =	joinPoint.getSignature();
		MethodSignature ms = (MethodSignature)signature;
		Object[] args = joinPoint.getArgs();
		String arg = null;
		if(args!=null && args.length>0){
			arg = JSONArray.toJSONString(args);
		}
		String funcName = signature.getName();//方法名
		String clazzname = joinPoint.getTarget().getClass().getSimpleName();//简单类名
		try{
			String res = Js.runFile("classpath:js/"+clazzname+".js", funcName, arg);
			Class<?> retType = ms.getReturnType();
			if(res==null){
				return res;
			}
			if(retType.isArray()){
				return JSONArray.parseArray(res,retType.getComponentType());
			}else if(Collection.class.isAssignableFrom(retType)){
				Type[] pts = AspectHelper.getActualReturnTypeArguments(joinPoint);
				if(pts==null){
					throw new RuntimeException("返回值类型的泛型类型未指定");
				}
				if(pts[0] instanceof WildcardType){
					throw new RuntimeException("返回值类型的泛型类型为通配符");
				}
				if(pts[0] instanceof Class){
					return JSONArray.parseArray(res,(Class) pts[0]);
				}
				throw new RuntimeException("wtf");
			}else{
				return JSONObject.parseObject(res, retType);
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			Object ret = joinPoint.proceed();
			return ret;
		}
		
	}
}

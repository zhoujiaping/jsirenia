package org.jsirenia.bean;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 日志切面
 * 之前为了排查问题，要求在调用
 */
//@Aspect
//@Component
public class LogAspect {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@PostConstruct
	public void init() {
		logger.info("void LogAspect.init()");
	}
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
		String funcName = signature.getName();//方法名
		String clazzname = joinPoint.getTarget().getClass().getSimpleName();//简单类名
		try{
			Object[] args = joinPoint.getArgs();
			String arg = "void";
			if(args!=null && args.length>0){
				arg = JSONArray.toJSONString(args);
			}
			logger.info("方法"+clazzname+"."+funcName+"入参=>{}",arg);
			Object ret = joinPoint.proceed();
			String res = "void";
			if(ret!=null){
				if(ret.getClass().isArray() || ret instanceof Collection){
					res = JSONArray.toJSONString(ret);
				}else{
					res = JSONObject.toJSONString(ret);
				}
			}
			logger.info("方法"+clazzname+"."+funcName+"结果=>{}",res);
			return ret;
		}catch(Exception e){
			logger.error("方法"+clazzname+"."+funcName+"异常，{}=>{}",e.getMessage(),e);
			throw e;
		}
	}
}

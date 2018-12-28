package org.jsirenia.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jsirenia.js.JsFunctionRunner;
import org.jsirenia.reflect.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;

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
		Method method = ms.getMethod();
		Object[] args = joinPoint.getArgs();
		String arg = null;
		if(args!=null){
			arg = JSONArray.toJSONString(args);
		}
		String funcName = signature.getName();//方法名
		String clazzname = joinPoint.getTarget().getClass().getSimpleName();//简单类名
		try{
			String res = JsFunctionRunner.runFile("classpath:js/"+clazzname+".js", funcName, arg);
			return MethodUtil.parseJSONForReturnType(method, res);
		}catch(Exception e){
			logger.error(e.getMessage());
			Object ret = joinPoint.proceed();
			return ret;
		}
	}

	/**
	 * 前置通知
	 */
	// @Before("execution(* com.xxx.impl..*(..))")
	public void before() {
		System.out.println("前置通知....");
	}

	/**
	 * 后置通知 returnVal,切点方法执行后的返回值
	 */
	// @AfterReturning(value="execution(*
	// com.zejian.spring.springAop.dao.UserDao.addUser(..))",returning =
	// "returnVal")
	public void afterReturning(Object returnVal) {
		System.out.println("后置通知...." + returnVal);
	}

	/**
	 * 抛出通知
	 * 
	 * @param e
	 */
	// @AfterThrowing(value="execution(*
	// com.zejian.spring.springAop.dao.UserDao.addUser(..))",throwing = "e")
	public void afterThrowable(Throwable e) {
		System.out.println("出现异常:msg=" + e.getMessage());
	}

	/**
	 * 无论什么情况下都会执行的方法
	 */
	// @After(value="execution(*
	// com.zejian.spring.springAop.dao.UserDao.addUser(..))")
	public void after() {
		System.out.println("最终通知....");
	}
}

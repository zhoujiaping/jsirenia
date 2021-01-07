package org.jsirenia.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Component
public class AppContextHolder implements ApplicationContextAware{
	private static WebApplicationContext webApplicationContext;
	private static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContext parent = applicationContext.getParent();
		if(parent==null){
			AppContextHolder.applicationContext = applicationContext;
		}else{
			AppContextHolder.applicationContext = parent;
			AppContextHolder.webApplicationContext = (WebApplicationContext) applicationContext;
		}
	}
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}
	public static WebApplicationContext getWebApplicationContext(){
		return webApplicationContext;
	}
	public static Object getBean(String name){
		Object bean = applicationContext.getBean(name);
		if(bean==null){
			bean = webApplicationContext.getBean(name);
		}
		return bean;
	}
	public static ApplicationContext getApplicationContext(HttpServletRequest request) {
		String dispatcherServletName = "mvc";
		ServletContext sc = /*HttpServletHolder.getCurrentRequest()*/request.getServletContext();
		return (ApplicationContext) sc.getAttribute(FrameworkServlet.SERVLET_CONTEXT_PREFIX + dispatcherServletName);
	}
}

package org.jsirenia.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

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
}

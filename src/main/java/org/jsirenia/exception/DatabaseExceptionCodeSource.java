package org.jsirenia.exception;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseExceptionCodeSource implements ExceptionCodeSource{
	private static Map<String,Properties> propertiesMap = new ConcurrentHashMap<>();
	public String getMsg(String code){
		throw new RuntimeException("没有实现");
	}
	//为了支持系统不重启就可以修改错误提示消息，添加重新加载的功能。
	public Properties reloadProperties(String module){
		throw new RuntimeException("没有实现");
	}
}

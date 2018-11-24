package org.jsirenia.exception;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jsirenia.properties.PropertiesUtil;
import org.springframework.util.ResourceUtils;

public class PropertiesExceptionCodeSource implements ExceptionCodeSource{
	private static Map<String,Properties> propertiesMap = new ConcurrentHashMap<>();
	public String getMsg(String code){
		int dotIndex = code.indexOf('.');
		String module = code.substring(0, dotIndex );
		Properties prop = propertiesMap.get(module);
		//根据code从properties文件中获取msg
		if(prop==null){
			prop = reloadProperties(module);
		}
		String key = code.substring(dotIndex+1);
		String value = prop.getProperty(key);
		if(value==null||value.trim()==""){
			value = "系统内部错误";
		}
		return value;
	}
	//为了支持系统不重启就可以修改错误提示消息，添加重新加载的功能。
	//也可以使用文件监听的方式，哪个文件改了自动刷新
	public Properties reloadProperties(String module){
		try {
			Properties props = PropertiesUtil.loadProperties(ResourceUtils.getFile("classpath:exception/"+module+".properties"));
			propertiesMap.put(module, props);
			return props;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}

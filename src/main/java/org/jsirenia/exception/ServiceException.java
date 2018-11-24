package org.jsirenia.exception;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jsirenia.properties.PropertiesUtil;
import org.springframework.util.ResourceUtils;

/**
 * 系统内部异常
 * 携带异常栈
 * 处理业务有异常时，抛此异常
 */
public class ServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	/**
	 * 这个属性是提供给接口调用者的消息
	 */
	private String msg;
	private static Map<String,Properties> propertiesMap = new ConcurrentHashMap<>();
	public static String getMsgFromProperties(String code){
		int dotIndex = code.indexOf('.');
		String module = code.substring(0, dotIndex );
		Properties prop = propertiesMap.get(module);
		//根据code从properties文件中获取msg
		if(prop==null){
			try {
				prop = PropertiesUtil.loadProperties(ResourceUtils.getFile("classpath:exception/"+module+".properties"));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			propertiesMap.put(module, prop);
		}
		String key = code.substring(dotIndex+1);
		String value = prop.getProperty(key);
		if(value==null||value.trim()==""){
			value = "系统内部错误";
		}
		return value;
	}
	public ServiceException(String code,String message){
		super(message);
		this.code = code;
		this.msg = getMsgFromProperties(code);
	}
	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	@Override
	public String getMessage() {
		String message = super.getMessage();
		return code+"#"+message+"#"+msg;
	}
}

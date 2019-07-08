package org.jsirenia.exception;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jsirenia.jdbc.DbClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DatabaseExceptionCodeSource implements ExceptionCodeSource{
	private static Map<String,Properties> propertiesMap = new ConcurrentHashMap<>();
	private DbClient dbClient;
	public void setDbClient(DbClient dbClient){
		this.dbClient = dbClient;
	}
	public String getMsg(String code){
		//
		int dotIndex = code.indexOf('.');
		String module = code.substring(0, dotIndex );
		Properties prop = propertiesMap.get(module);
		//根据code从properties文件中获取msg
		if(prop==null){
			prop = reloadProperties(module);
		}
		String key = code.substring(dotIndex+1);
		String value = prop.getProperty(key);
		return value;
	}
	//为了支持系统不重启就可以修改错误提示消息，添加重新加载的功能。
	public Properties reloadProperties(String module){
			Properties props = loadPropertiesFromDatabase(module);
			propertiesMap.put(module, props);
			return props;
	}
	private Properties loadPropertiesFromDatabase(String module) {
		return dbClient.withConn(conn->{
			List<Map<String, Object>> array = dbClient.queryMap("select id,module,code,msg from t_exception_code where module=#{module}", module );
			Properties props = new Properties();
			for(int i=0;i<array.size();i++){
				Map<String, Object> row = array.get(i);
				props.setProperty(module+"."+row.get("code"), String.valueOf(row.get("msg")));
			}
			return props;
		});
	}
}

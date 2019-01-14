package org.jsirenia.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 场景：需要往url上添加参数，但是url上有可能已经有参数了。
 * 效果
 * url : http://www.baidu.com?v=1		key :  name		value : lucy   =>   http://www.baidu.com?v=1&name=lucy
 * url : http://www.baidu.com		key :  name		value : lucy   =>   http://www.baidu.com?name=lucy
 * 键值对中的值，会被URLEncoder.encode，编码使用utf-8
 */
public class URLUtil {
	public static String addParam(String url,String key,String value) throws UnsupportedEncodingException{
		String encodedValue = URLEncoder.encode(value, "utf-8");
		if(url.contains("?")){
			return url+"&"+key+"="+encodedValue;
		}
		return url+"?"+key+"="+encodedValue;
	}
	public static String addParams(String url,String[] keys,String[] values) throws UnsupportedEncodingException{
		if(keys==null || keys.length==0 || values==null || values.length==0){
			return url;
		}
		if(keys.length!=values.length){
			throw new RuntimeException("keys.length is not equals values.length");
		}
		String encodedValue = null;
		if(url.contains("?")){
			StringBuilder sb = new StringBuilder(url);
			for(int i=0;i<keys.length;i++){
				if(values[i]!=null){
					encodedValue = URLEncoder.encode(values[i], "utf-8");
					sb.append("&").append(keys[i]).append("=").append(encodedValue);
				}
			}
			return sb.toString();
		}
		StringBuilder sb = new StringBuilder(url);
		int firstIndex = 0;
		while(firstIndex<keys.length){
			if(values[firstIndex]!=null){
				encodedValue = URLEncoder.encode(values[firstIndex], "utf-8");
				sb.append("?").append(keys[firstIndex]).append("=").append(encodedValue);
				break;
			}
			firstIndex++;
		}
		for(int i=firstIndex+1;i<keys.length;i++){
			if(values[i]!=null){
				encodedValue = URLEncoder.encode(values[i], "utf-8");
				sb.append("&").append(keys[i]).append("=").append(encodedValue);
			}
		}
		return sb.toString();
	}
	public static String addParams(String url,Map<String,String> keyValues) throws UnsupportedEncodingException{
		if(keyValues==null || keyValues.isEmpty()){
			return url;
		}
		String encodedValue = null;
		if(url.contains("?")){
			StringBuilder sb = new StringBuilder(url);
			Iterator<Entry<String, String>>  iter = keyValues.entrySet().iterator();
			Entry<String,String> entry = null;
			while(iter.hasNext()){
				entry = iter.next();
				if(entry.getValue()!=null){
					encodedValue = URLEncoder.encode(entry.getValue(), "utf-8");
					sb.append("&").append(entry.getKey()).append("=").append(encodedValue);
				}
			}
			return sb.toString();
		}
		StringBuilder sb = new StringBuilder(url);
		Iterator<Entry<String, String>>  iter = keyValues.entrySet().iterator();
		Entry<String,String> entry = null;
		while(iter.hasNext()){
			entry = iter.next();
			if(entry.getValue()!=null){
				encodedValue = URLEncoder.encode(entry.getValue(), "utf-8");
				sb.append("?").append(entry.getKey()).append("=").append(encodedValue);
				break;
			}
		}
		while(iter.hasNext()){
			entry = iter.next();
			if(entry.getValue()!=null){
				encodedValue = URLEncoder.encode(entry.getValue(), "utf-8");
				sb.append("&").append(entry.getKey()).append("=").append(encodedValue);
			}
		}
		return sb.toString();
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(addParam("http://www.baidu.com?q=1", "r", "http://www.baidu.com?q=2"));
	}
}

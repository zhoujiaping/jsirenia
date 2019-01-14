package org.jsirenia.http;

import java.io.UnsupportedEncodingException;

public class URLUtil {
	public static String addParam(String url,String key,String value) throws UnsupportedEncodingException{
		//String encodedValue = URLEncoder.encode(value, "utf-8");
		if(url.contains("?")){
			return url+"&"+key+"="+value;
		}
		return url+"?"+key+"="+value;
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(addParam("http://www.baidu.com?q=1", "n", "v"));
	}
}

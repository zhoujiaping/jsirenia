package org.jsirenia.http;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.jsirenia.reflect.MethodUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

/**
 * 将本地方法暴露为http接口
 */
@Controller
@RequestMapping("/http-exporter/{className}/{methodName}")
public class HttpExporter{
	private static final ParserConfig parseConfig = new ParserConfig();
	static{
		parseConfig.setAutoTypeSupport(true);
	}
	@RequestMapping
	@ResponseBody
	public Object callHessianApi(@RequestBody String body,
			@PathVariable("className")String className,
			@PathVariable("methodName")String methodName,
			HttpServletRequest req) throws Exception{
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(req.getSession(true).getServletContext());
		Class<?> type = Class.forName(className);
		Object target = context.getBean(type);
		if(target==null){
			throw new RuntimeException("没有找到"+className+"的实例");
		}
		Method method = MethodUtil.getMethodByName(className, methodName);
		if(body.contains("@type")){
			Object[] args = JSON.parseObject(body, new Object[]{}.getClass(), parseConfig);
			return method.invoke(target, args);
		}else{
			Type[] types = method.getGenericParameterTypes();
			Object[] args = JSON.parseArray(body, types).toArray();
			return method.invoke(target, args);
		}
	}
	public static void main(String[] args0) throws Exception{
		File f = ResourceUtils.getFile("classpath:test2.json");
		String body = StreamUtils.copyToString(new FileInputStream(f), Charset.forName("utf-8"));
		Object[] args = null;//MethodUtil.parseArgs("org.jsirenia.http.MyTest","test1",body);
		System.out.println(args);
	}
}
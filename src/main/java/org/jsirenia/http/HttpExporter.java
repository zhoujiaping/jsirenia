package org.jsirenia.http;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsirenia.http.model.HessianApiCall;
import org.jsirenia.json.JSONUtil;
import org.jsirenia.properties.PropertiesUtil;
import org.jsirenia.reflect.MethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.client.HessianProxyFactory;

/**
 * 将本地方法暴露为http接口
 */
@Controller
@RequestMapping("/http-exporter/{className}/{methodName}")
public class HttpExporter{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private HessianProxyFactory factory = new HessianProxyFactory();
	private Map<String,Object> apiCache = new ConcurrentHashMap<>();
	private Properties props = null;
	@PostConstruct
	public void init() throws FileNotFoundException{
		props = PropertiesUtil.loadProperties(ResourceUtils.getFile("classpath:hessian-uri.properties"));
	}
	@RequestMapping(value="/hessian-api",method=RequestMethod.POST,produces="application/json")
	@ResponseBody
	public Object callHessianApi(@RequestBody  String body,
			HttpServletRequest req,HttpServletResponse resp) throws Exception{
		try{
			logger.info("\n"+body);
			req.setCharacterEncoding("utf-8");
			resp.setCharacterEncoding("utf-8");
			HessianApiCall call = null;
			Object res = null;
			Object api = null;
			Object[] args = null;
			Method method = null;
			if(body.contains("@type")){//复杂类型，需要调用者指定类型信息
				call = JSONUtil.parseObjectWithType(body, HessianApiCall.class);
			}else{
				call = JSON.parseObject(body,HessianApiCall.class);
			}
			api =getHessianApi(call);
			if(call.getArgTypes()==null){//可以指定方法参数的类型，用来解决方法重载问题。如果未指定，就根据方法名获取方法。
				method = MethodUtil.getMethodByName(call.getClassName(), call.getMethodName());
			}else{
				method = MethodUtil.getMethod(call.getClassName(), call.getMethodName(),call.getArgTypes().toArray(new String[0]));
			}
			if(body.contains("@type")){//复杂类型，需要调用者指定类型信息
				args = new Object[call.getArgs().size()];
				for(int i=0;i<args.length;i++){
					args[i] = call.getArgs().get(i);
				}
			}else{//简单类型，自动处理类型信息
				String argsJSONArray = JSON.toJSONString(call.getArgs());
				args = MethodUtil.parseJSONForArgs(method, argsJSONArray);
			}
			res = method.invoke(api, args);
			/*WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(req.getSession(true).getServletContext());
			Object target = context.getBean(Class.forName(call.getClassName()));
			if(target==null){
				throw new RuntimeException("spring上下文中没有找到"+call.getClassName()+"的实例");
			}*/
			String resString = JSON.toJSONString(res);
			logger.info(resString);
			return resString;
		}catch(Exception e){
			logger.error("服务器内部错误",e);
			resp.setStatus(500);
			resp.setContentType("text/plain");
			e.printStackTrace(resp.getWriter());
			return null;
		}
	}
	private Object getHessianApi(HessianApiCall call ) throws MalformedURLException, ClassNotFoundException{
		Object api =apiCache.get(call.getClassName());
		if(api==null){
			String uri = props.getProperty(call.getModuleName());
			String serviceUri = call.getServiceUri();
			api = factory.create(Class.forName(call.getClassName()), uri+serviceUri);
			apiCache.put(call.getClassName(), api);
		}
		return api;
	}
}
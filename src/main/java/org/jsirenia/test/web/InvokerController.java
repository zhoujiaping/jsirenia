package org.jsirenia.test.web;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsirenia.js.JsInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("/jsInvoker")
public class InvokerController{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@RequestMapping(value="/invoke",method=RequestMethod.POST,produces="text/plain")
	@ResponseBody
	public Object callHessianApi(@RequestBody  String body,
			HttpServletRequest req,HttpServletResponse resp) throws Exception{
		try{
			req.setCharacterEncoding("utf-8");
			resp.setCharacterEncoding("utf-8");
			logger.info("\n"+body);
			Object target = JsInvoker.evalText(null, body);
			Bindings bindings = JsInvoker.engine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("_", target);
			String res = JSON.toJSONString(target);
			logger.info(res);
			return res;
		}catch(Exception e){
			logger.error("服务器内部错误",e);
			resp.setStatus(500);
			resp.setContentType("text/plain");
			e.printStackTrace(resp.getWriter());
			return null;
		}
	}
}

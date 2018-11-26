package org.jsirenia.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 */
public class JettyStart {

	public static final int PORT = 8080;

	public static Server createServerIn(int port) {
		// 创建Server
		Server server = new Server(port);
		
		HandlerWrapper beforeHandler = new HandlerWrapper(){
			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {
				System.out.println(target);
				PrintWriter pw = response.getWriter();
				JSONObject ret0 = new JSONObject();
				ret0.put("invoke", false);
				ret0.put("afterReturning", true);
				pw.write(ret0.toJSONString());
				pw.flush();
			}
		};
		ContextHandler invokeHandler = new ContextHandler("/*/*Invoke"){
			@Override
			public void doHandle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {
				super.doHandle(target, baseRequest, request, response);
				PrintWriter pw = response.getWriter();
				pw.write("null");
				pw.flush();
			}
		};
		HandlerWrapper afterReturingHandler = new HandlerWrapper(){
			@Override
			public void handle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {
				String json = StreamUtils.copyToString(request.getInputStream(), Charset.forName("utf-8"));
				JSONArray array = JSONArray.parseArray(json);
				array.add("en");
				json = array.toJSONString();
				PrintWriter pw = response.getWriter();
				pw.write(json);
				pw.flush();
			}
		};
		ContextHandler handler = new ContextHandler("/"){
			@Override
			public void doHandle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {
				String uri = request.getRequestURI();
				if(uri.endsWith("Before")){
					beforeHandler.handle(target, baseRequest, request, response);
				}else if(uri.endsWith("Invoke")){
					invokeHandler.handle(target,baseRequest,request,response);
				}else if(uri.endsWith("AfterReturning")){
					afterReturingHandler.handle(target,baseRequest,request,response);
				}else{
					PrintWriter pw = response.getWriter();
					pw.write("ok");
					pw.flush();
				}
			}
		};
		server.setHandler(handler);
		//handler.insertHandler(beforeHandler);
		//handler.insertHandler(invokeHandler);
		//handler.insertHandler(afterReturingHandler);
		//server.insertHandler(handler);
		//server.insertHandler(beforeHandler);
		//server.insertHandler(invokeHandler);
		//server.insertHandler(afterReturingHandler);
		return server;
	}

	public static void main(String[] args) throws Exception {
		//DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));
		Server server = createServerIn(PORT);
		server.stop();
		server.start();
		server.join();
	}
}

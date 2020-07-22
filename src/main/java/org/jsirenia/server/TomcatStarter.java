package org.jsirenia.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;

import javax.servlet.http.HttpServletRequest;

public class TomcatStarter {
	public static void main(String[] args) throws IOException {
		ServerBootstrap bs = ServerBootstrap.bootstrap().setListenerPort(8080);
		bs.registerHandler("/aaa*", new HttpRequestHandler() {
			@Override
			public void handle(HttpRequest request, HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				System.out.println(request.getProtocolVersion());
			}
		});
		HttpServer server = bs.create();
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		        server.shutdown(5, TimeUnit.SECONDS);
		    }
		});
	}
	public void test2() throws UnsupportedEncodingException, Exception{
		HttpProcessor httpproc = HttpProcessorBuilder.create()
		        .add(new ResponseDate())
		        .add(new ResponseServer("MyServer-HTTP/1.1"))
		        .add(new ResponseContent())
		        .add(new ResponseConnControl())
		        .build();
		HttpRequestHandler myRequestHandler1 = null;
		HttpRequestHandler myRequestHandler2 = null;
		HttpRequestHandler myRequestHandler3 = null;
		UriHttpRequestHandlerMapper handlerMapper = new UriHttpRequestHandlerMapper();
		handlerMapper.register("/service/*", myRequestHandler1);
		handlerMapper.register("*.do", myRequestHandler2);
		handlerMapper.register("*", myRequestHandler3);
	}
}

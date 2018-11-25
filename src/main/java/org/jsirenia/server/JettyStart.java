package org.jsirenia.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 */
public class JettyStart {

	public static final int PORT = 8080;

	public static Server createServerIn(int port) {
		// 创建Server
		Server server = new Server(port);
		ContextHandler handler = new ContextHandler("/*"){
			@Override
			public void doHandle(String target, Request baseRequest, HttpServletRequest request,
					HttpServletResponse response) throws IOException, ServletException {
				super.doHandle(target, baseRequest, request, response);
				System.out.println(target);
			}
		};
		server.insertHandler(handler );
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

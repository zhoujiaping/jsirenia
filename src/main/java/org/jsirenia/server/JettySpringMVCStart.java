package org.jsirenia.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 */
public class JettySpringMVCStart {

	public static final int PORT = 8080;

	// web访问的根路径http://ip:port/，相当于项目名,/即忽略项目名
	public static final String CONTEXT_PATH= "/xxx";

	private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";

	public static Server createServerIn(int port) {
		// 创建Server
		Server server = new Server(port);

		WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, CONTEXT_PATH);
		//webContext.setDescriptor(DEFAULT_WEBAPP_PATH+"/WEB-INF/web.xml");
		//webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
		//webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		//server.setHandler(webContext);
		server.insertHandler(server);
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

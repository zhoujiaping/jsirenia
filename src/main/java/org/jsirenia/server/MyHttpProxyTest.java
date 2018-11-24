package org.jsirenia.server;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.ExceptionLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * http代理demo
 */
public class MyHttpProxyTest {
	private static Logger logger = LoggerFactory.getLogger(MyHttpProxyTest.class);

	/**
	 * 所有通过httpclient对象发送的请求，实际上都发送给代理。
	 */
	public static void main(String[] args)throws IOException {
		// 创建客户端
		// 设置代理IP、端口、协议（请分别替换）
		HttpHost proxy = new HttpHost("localhost", 8081, "http");
		// 把代理设置到请求配置
		RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();

		// 实例化CloseableHttpClient对象
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		// 创建服务端
		ServerBootstrap bs = ServerBootstrap.bootstrap().setListenerPort(8080);
		bs.registerHandler("/*", new HttpRequestHandler() {
			@Override
			public void handle(HttpRequest request, HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				logger.info("请求=>{}", request);
				request.removeHeaders(HTTP.TRANSFER_ENCODING);// 不允许手动设置该响应头
				request.removeHeaders(HTTP.DATE_HEADER);
				request.removeHeaders(HTTP.CONTENT_LEN);// 不允许手动设置该响应头
				request.removeHeaders(HTTP.SERVER_HEADER);
			/*	BasicHttpEntityEnclosingRequest httpReq = (BasicHttpEntityEnclosingRequest) request;
				HttpEntity entity = httpReq.getEntity();
				byte[] buf = EntityUtils.toByteArray(entity);
				InputStream in = new ByteArrayInputStream(buf);
				OutputStream out = new FileOutputStream("d:/hessian.in.txt");
				StreamUtils.copy(in, out );*/
				CloseableHttpResponse resp = httpclient.execute(proxy, request);
				logger.info("响应=>{}", resp);
				response.setStatusLine(resp.getStatusLine());
				response.setHeaders(resp.getAllHeaders());
				response.setEntity(resp.getEntity());
				response.removeHeaders(HTTP.TRANSFER_ENCODING);// 不允许手动设置该响应头
				response.removeHeaders(HTTP.DATE_HEADER);
				response.removeHeaders(HTTP.CONTENT_LEN);// 不允许手动设置该响应头
				response.removeHeaders(HTTP.SERVER_HEADER);
				logger.info("代理响应=>{}", response);
			}
		});
		ExceptionLogger exceptionLogger = new ExceptionLogger() {
			@Override
			public void log(Exception ex) {
				logger.error("", ex);
			}
		};
		bs.setExceptionLogger(exceptionLogger);// 默认吞并异常
		HttpServer server = bs.create();
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown(5, TimeUnit.SECONDS);
			}
		});
	}
}

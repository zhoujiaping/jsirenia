package org.jsirenia.server;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.http.ExceptionLogger;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Proxy-Connection头  https://www.jianshu.com/p/ffc02a36b87e
 * http代理demo
 */
public class MyHttpProxyTest {
	private static Logger logger = LoggerFactory.getLogger(MyHttpProxyTest.class);
	private static Set<String> hopbyHopHeaders = Sets.newHashSet("Transfer-Encoding",
			"Connection","Proxy-Connection","Keep-Alive",
			"Proxy-Authenticate","Proxy-Authorization",
			"Trailer","TE","Upgrade","Content-Length"); 
	/**
	 * 所有通过httpclient对象发送的请求，实际上都发送给代理。
	 */
	public static void main(String[] args)throws IOException {
		// 创建客户端
		// 设置代理IP、端口、协议（请分别替换）
		//HttpHost proxy = new HttpHost("localhost", 1337, "http");
		HttpHost proxy = new HttpHost("www.baidu.com", 80, "https");
		// 把代理设置到请求配置
		RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();

		// 实例化CloseableHttpClient对象
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		//CloseableHttpClient httpclient = HttpClients.createMinimal();
		// 创建服务端
		ServerBootstrap bs = ServerBootstrap.bootstrap().setListenerPort(8080);
		HttpRequestHandler handler = (request,response,context)->{
			logger.info("请求=>{}", request);
			// 逐跳首部(http1.1共8个)  https://blog.csdn.net/alexshi5/article/details/80379086
			//删除 逐跳首部
			hopbyHopHeaders.forEach(name->{
				request.removeHeaders(name);// 不允许手动设置该响应头
			});
		/*	BasicHttpEntityEnclosingRequest httpReq = (BasicHttpEntityEnclosingRequest) request;
			HttpEntity entity = httpReq.getEntity();
			byte[] buf = EntityUtils.toByteArray(entity);
			InputStream in = new ByteArrayInputStream(buf);
			OutputStream out = new FileOutputStream("d:/hessian.in.txt");
			StreamUtils.copy(in, out );*/
			CloseableHttpResponse resp = httpclient.execute(proxy, request);
			//CloseableHttpResponse resp = httpclient.execute(new HttpHost(request.getHeaders("Host")[0].getValue()), request);
			logger.info("响应=>{}", resp);
			response.setStatusLine(resp.getStatusLine());
			response.setHeaders(resp.getAllHeaders());
			response.setEntity(resp.getEntity());
			hopbyHopHeaders.forEach(name->{
				response.removeHeaders(name);// 不允许手动设置该响应头
			});
			logger.info("代理响应=>{}", response);
		};
		//bs.registerHandler("/*", handler);
		//如果配置成/*,那么在作为代理服务器的时候，无法匹配http://xxxx/xxx，导致501错误。
		bs.registerHandler("*", handler);
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

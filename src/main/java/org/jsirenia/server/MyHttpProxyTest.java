package org.jsirenia.server;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.ExceptionLogger;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Proxy-Connection头  https://www.jianshu.com/p/ffc02a36b87e
 * https://segmentfault.com/a/1190000004093321?_ea=487081
 * http代理demo
 */
public class MyHttpProxyTest {
	private static Logger logger = LoggerFactory.getLogger(MyHttpProxyTest.class);
	private static Set<String> hopbyHopHeaders = Sets.newHashSet("Transfer-Encoding",
			"Connection","Proxy-Connection","Keep-Alive",
			"Proxy-Authenticate","Proxy-Authorization",
			"Trailer","TE","Upgrade","Content-Length"); 
	private static Map<String,String> hostMapping = Maps.newHashMap();
	{
	}
	/**
	 * 所有通过httpclient对象发送的请求，实际上都发送给代理。
	 * @throws Exception 
	 */
	public static void main(String[] args)throws Exception {
		
		 //采用绕过验证的方式处理https请求  
        SSLContext sslcontext = createIgnoreVerifySSL();  

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				String host = hostMapping.get(hostname);
				if(host == null){
					return SSLConnectionSocketFactory.getDefaultHostnameVerifier().verify(hostname, session);
				}
				boolean res = SSLConnectionSocketFactory.getDefaultHostnameVerifier().verify(host, session);
				if(res){
					return true;
				}
				return SSLConnectionSocketFactory.getDefaultHostnameVerifier().verify(hostname, session);
			}
		};
        
        SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2","TLSv1" }, null,hostnameVerifier);
        
        //设置协议http和https对应的处理socket链接工厂的对象  
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()  
            .register("http", PlainConnectionSocketFactory.INSTANCE)  
            .register("https", sf)  
            .build();  
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);  
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connManager); 
		
		// 创建客户端
		// 设置代理IP、端口、协议（请分别替换）
		//HttpHost proxy = new HttpHost("localhost", 1337, "http");
		HttpHost proxy = new HttpHost("www.baidu.com", 443, "https");
		// 把代理设置到请求配置
		RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
		httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
		// 实例化CloseableHttpClient对象
		CloseableHttpClient httpclient = httpClientBuilder.build();
		// 创建服务端
		ServerBootstrap bs = ServerBootstrap.bootstrap().setListenerPort(8080);
		HttpRequestHandler handler = (request,response,context)->{
			logger.info("请求=>{}", request);
			// 逐跳首部(http1.1共8个)  https://blog.csdn.net/alexshi5/article/details/80379086
			//删除 逐跳首部
			hopbyHopHeaders.forEach(name->{
				request.removeHeaders(name);// 不允许手动设置该响应头
			});
			CloseableHttpResponse resp = httpclient.execute(proxy, request);
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
	/** 
	* 绕过验证 
	*   
	* @return 
	* @throws NoSuchAlgorithmException  
	* @throws KeyManagementException  
	*/  
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {  
	        SSLContext sc = SSLContext.getInstance("TLS");//TLS,SSLv3  
	        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法  
	        X509TrustManager trustManager = new X509TrustManager() {  
	            @Override  
	            public void checkClientTrusted(  
	                    X509Certificate[] paramArrayOfX509Certificate,  
	                    String paramString) throws CertificateException {  
	            }  
	            @Override  
	            public void checkServerTrusted(  
	                  X509Certificate[] paramArrayOfX509Certificate,  
	                    String paramString) throws CertificateException {  
	            }  
	            @Override  
	            public X509Certificate[] getAcceptedIssuers() {  
	                return null;  
	            }  
	        };  
	        sc.init(null, new TrustManager[] { trustManager }, null);  
	        return sc;  
	    }

}

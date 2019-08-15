package org.jsirenia.server;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.google.common.collect.Maps;

public abstract class HttpsClients {
	public static CloseableHttpClient createDefault(){
		SSLContext sslcontext = createIgnoreVerifySSL();
		HostnameVerifier hostnameVerifier = createHostnameVerifier();
		SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2","TLSv1" }, null,
				hostnameVerifier);
		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sf).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(2000)
				.setConnectTimeout(2000)
				.setConnectionRequestTimeout(2000)
				.build();
		return HttpClients.custom().setConnectionManager(connManager)
		.setDefaultRequestConfig(defaultRequestConfig)
		.setMaxConnPerRoute(20)
		.setMaxConnTotal(100)
		.build();
	}
	private static HostnameVerifier createHostnameVerifier() {
		Map<String,String> hostMapping = Maps.newHashMap();
		return new HostnameVerifier() {
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
	}

	private static SSLContext createIgnoreVerifySSL(){
		try{
			return createIgnoreVerifySSL0();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private static SSLContext createIgnoreVerifySSL0() throws NoSuchAlgorithmException, KeyManagementException {
		//SSLContext sc = SSLContext.getInstance("SSLv3");
		SSLContext sc = SSLContext.getInstance("TLS");
		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,
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

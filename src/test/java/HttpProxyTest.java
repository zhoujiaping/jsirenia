import java.net.URI;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpProxyTest {
	@Test
	public void test() throws Exception {
		String proxyHost = "localhost";
		int proxyPort = 8080;
		String url = "https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&rsv_idx=2&tn=baiduhome_pg&wd=connecttimeout%20readtimeout&rsv_spt=1&oq=cas%25E6%25B3%25A8%25E9%2594%2580%25E5%258E%259F%25E7%2590%2586&rsv_pq=cd3cc5760009037e&rsv_t=3830ge8yA5etCI0RHnl7iyInuXxRbzAEbpnKNbxOAikKkKdmPkr8KcIN7Nt8GILfBsMU&rqlang=cn&rsv_enter=1&rsv_dl=ts_2&rsv_sug3=1&rsv_sug1=1&rsv_sug7=100&rsv_sug2=1&prefixsug=connectTimeout&rsp=2&inputT=3250&rsv_sug4=3250";
		url = "https://www.baidu.com/";
		HttpClientBuilder builder = HttpClients.custom();
		HttpClientContext context = null;
		HttpHost proxy = new HttpHost(proxyHost, proxyPort,"http");
		URI uri = new URI(url);
		//HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		// resp = new BasicCredentialsProvider();
		// resp.setCredentials(new AuthScope(proxyHost, proxyPort), new
		// UsernamePasswordCredentials("", ""));
		// builder.setDefaultCredentialsProvider(resp);
		// AuthCache auth = new BasicAuthCache();
		// BasicScheme basicAuth = new BasicScheme();
		// auth.put(proxy, basicAuth);
		// context = HttpClientContext.create();
		// context.setCredentialsProvider(resp);
		// context.setAuthCache(auth);

		HttpGet req = new HttpGet(url);
		RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
		builder.setDefaultRequestConfig(defaultRequestConfig);
		/*req.setConfig(RequestConfig.custom().setSocketTimeout(1000)
				.setConnectTimeout(1000).setProxy(proxy).build());*/
		req.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
		req.addHeader("aaa", "bbb");
		//req.setEntity(new StringEntity("hello=world", "utf-8"));

		CloseableHttpClient client = builder.build();
		CloseableHttpResponse resp = client.execute(req);
		System.out.println(resp);
		HttpEntity entity = resp.getEntity();
		String body = EntityUtils.toString(entity, "utf-8");
		System.out.println(body);
	}

	private static SSLConnectionSocketFactory createSSLFacoty() throws Exception {
	      X509TrustManager x509mgr = null;
	      //new SSLContextBuilder.TrustManagerDelegate();
	      SSLContext ctx = SSLContext.getInstance("TLS");
	      ctx.init((KeyManager[])null, new TrustManager[]{x509mgr}, (SecureRandom)null);
	      return new SSLConnectionSocketFactory(ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	   }
}

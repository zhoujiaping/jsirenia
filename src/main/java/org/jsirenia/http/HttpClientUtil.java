package org.jsirenia.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

//https://www.cnblogs.com/kingszelda/p/8988505.html
public class HttpClientUtil {

    static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    static volatile CloseableHttpClient client;

    private static CloseableHttpClient createHttpClient(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(500)
                .setConnectTimeout(2000)
                .setSocketTimeout(18000)
                .build();
        return HttpClients.custom()
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(200)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(10L, TimeUnit.SECONDS)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRetryHandler(new StandardHttpRequestRetryHandler())
                .build();
    }
    public static CloseableHttpClient httpClient() {
        if(client == null){
            synchronized (HttpClientUtil.class){
                if(client == null){
                    client = createHttpClient();
                }
            }
        }
        return client;
    }
}

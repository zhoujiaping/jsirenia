package org.jsirenia.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Sets;

@Controller
@RequestMapping("/proxy/**")
public class HttpProxy {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final Set<String> plains = Sets.newHashSet("application/json", "application/xml", "text/plain",
			"application/x-www-form-urlencoded");
	private CloseableHttpClient httpClient = HttpsClients.createDefault();
	private static final String TARGET_HOST_HEADER = "X-TARGET-HOST";
	private static final String THIS_MAPPING = "/proxy/";
	//private static final Pattern pattern = Pattern.compile("");
	@RequestMapping
	public void proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			doProxy(request, response);
		} catch (Exception e) {
			logger.error("执行代理异常",e);
		}
	}

	private void doProxy(HttpServletRequest request, HttpServletResponse response)
			throws MalformedURLException, IOException, ClientProtocolException {
		String method = request.getMethod().toUpperCase();
		String url = request.getRequestURL().toString();
		String protocol = request.getProtocol();
		URL origURL = new URL(url);
		HttpUriRequest req = null;
		List<Header> headerList = new ArrayList<>();
		StringBuilder reqHeaderString = new StringBuilder();
		Enumeration<?> headerNames = request.getHeaderNames();
		boolean isPlainReq = false;
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement().toString();
			Enumeration<?> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement().toString();
				Header header = new BasicHeader(headerName, headerValue);
				reqHeaderString.append(headerName).append(" :").append(headerValue).append("\n");
				if (headerName.equalsIgnoreCase("Content-Type")) {
					String[] values = headerValue.toLowerCase().split(";");
					for(int i=0;i<values.length;i++){
						if(plains.contains(values[i].trim())){
							isPlainReq = true;
							break;
						}
					}
				}
				if(!headerName.equalsIgnoreCase("connection")){
					headerList.add(header);
				}
			}
		}
		logger.info("请求行：{} {} {}", method, url, protocol);
		logger.info("请求头：{}", reqHeaderString);
		//将path部分所有连续的路径分隔符、windows风格的路径分隔符 替换为单个路径分隔符/
		String prefix = request.getContextPath() + THIS_MAPPING;
		String targetHost  = request.getHeader(TARGET_HOST_HEADER);
		String targetUrl = targetHost+"/"+origURL.getPath().substring(prefix.length());
		URL targetURL = new URL(targetUrl);
		switch (method) {
		case "GET":
			req = new HttpGet(targetUrl);
			req.setHeaders(headerList.toArray(new Header[0]));
			req.removeHeaders(HTTP.TRANSFER_ENCODING);// 不允许手动设置该响应头
			req.removeHeaders(HTTP.DATE_HEADER);
			req.removeHeaders(HTTP.CONTENT_LEN);// 不允许手动设置该响应头
			req.removeHeaders(HTTP.SERVER_HEADER);
			req.setHeader("host", targetURL.getHost()+":"+targetURL.getPort());
			break;
		case "POST":
			HttpPost httpPost = new HttpPost(targetUrl);
			req = httpPost;
			req.setHeaders(headerList.toArray(new Header[0]));
			req.removeHeaders(HTTP.TRANSFER_ENCODING);// 不允许手动设置该响应头
			req.removeHeaders(HTTP.DATE_HEADER);
			req.removeHeaders(HTTP.CONTENT_LEN);// 不允许手动设置该响应头
			req.removeHeaders(HTTP.SERVER_HEADER);
			req.setHeader("host", targetURL.getHost()+":"+targetURL.getPort());
			try(InputStream in = request.getInputStream();){
				HttpEntity entity = null;
				if(isPlainReq){
					String body = StreamUtils.copyToString(in, Charset.forName("utf-8"));
					logger.info("请求体：{}",body);
					entity = new StringEntity(body, "utf-8");
				}else{
					entity = new InputStreamEntity(in);
				}
				httpPost.setEntity(entity);
			}
			break;
		default:
			throw new RuntimeException("暂时不支持该http方法");
		}

		CloseableHttpResponse resp = httpClient.execute(req);
		//
		resp.removeHeaders(HTTP.TRANSFER_ENCODING);// 不允许手动设置该响应头
		resp.removeHeaders(HTTP.DATE_HEADER);
		resp.removeHeaders(HTTP.CONTENT_LEN);// 不允许手动设置该响应头
		resp.removeHeaders(HTTP.SERVER_HEADER);
		int statusCode = resp.getStatusLine().getStatusCode();
		if(statusCode!=HttpStatus.SC_OK){
			logger.error("http代理响应码：{}",statusCode);
		}
		response.setStatus(statusCode);
		Header[] respHeaders = resp.getAllHeaders();
		StringBuilder respHeaderString = new StringBuilder();
		boolean isPlainResp = false;
		for (Header header : respHeaders) {
			response.addHeader(header.getName(), header.getValue());
			respHeaderString.append(header.getName()).append(" :").append(header.getValue()).append("\n");
			if (header.getName().equalsIgnoreCase("Content-Type")) {
				String[] values = header.getValue().toLowerCase().split(";");
				for(int i=0;i<values.length;i++){
					if(plains.contains(values[i].trim())){
						isPlainResp = true;
						break;
					}
				}
			}
		}
		logger.info("状态行：{}", resp.getStatusLine());
		logger.info("响应头：{}", respHeaderString);
		HttpEntity entity = resp.getEntity();
		if (isPlainResp) {
			PrintWriter pw = response.getWriter();
			String body = EntityUtils.toString(entity, "utf-8");
			logger.info("响应体：{}", body);
			EntityUtils.consume(entity);
			pw.write(body);
			pw.flush();
		} else {
			OutputStream out = response.getOutputStream();
			entity.writeTo(out);
		}
	}

}

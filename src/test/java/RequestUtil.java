import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 */
public class RequestUtil {
	private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);
	private static final String baseUri = "http://localhost:8080/xx";
	// private String tmpdir = System.getProperty("user.dir")+"/src/test/java";
	// private static final File tmpdir = new File("/tomcat/xx/tmp");
	private static final File sessionFile = new File("/tomcat/xx/tmp/session");
	private static final File downloadDir = new File("/tomcat/download");
	private static final HttpClient client = HttpClients.createMinimal();
	static {
		File parent = sessionFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (!sessionFile.exists()) {
			try (OutputStream out = new FileOutputStream(sessionFile);) {
				StreamUtils.copy("", Charset.forName("utf-8"), out);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	private static String postJson(String api, Object reqBody, ResponseHandler<?> handler) throws Exception {
		if (reqBody == null) {
			reqBody = new Object();
		}
		// 请求体内容
		String body = JSON.toJSONString(reqBody);
		// 会话id
		String sessionId = readSessionId();
		// 创建请求
		HttpPost post = new HttpPost(baseUri + api);
		// 设置请求头
		post.addHeader("Cookie", "xxx_JSESSIONID=" + sessionId);
		post.addHeader("Content-Type", "application/json; charset=utf-8");
		// 设置请求体
		post.setEntity(new StringEntity(body,"utf-8"));
		// 打印请求日志
		logger.info("请求行：{}", post.getRequestLine());
		String reqHeader = Stream.of(post.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		logger.info("请求头：{}", reqHeader);
		logger.info("请求体：{}", body);
		// 发送请求
		HttpResponse resp = client.execute(post);
		// 打印响应日志
		String respHeader = Stream.of(resp.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		logger.info("状态行：{}", resp.getStatusLine());
		logger.info("响应头：{}", respHeader);
		String respContentType = findContentType(resp);
		writeSessionId(resp);
		// Set-Cookie:
		// xx_JSESSIONID=1p8bnvk60n5y5!1562290073434; Path=/
		String respBody = EntityUtils.toString(resp.getEntity(), "utf-8");
		JSONObject respJson = null;
		if (respContentType.equalsIgnoreCase("application/json")) {
			respJson = JSONObject.parseObject(respBody);
			logger.info("响应体：{}", JSON.toJSONString(respJson, true));
		} else {
			logger.info("响应体：{}", respBody);
		}
		Assert.assertTrue("状态码必须为200", resp.getStatusLine().getStatusCode() == 200);
		Assert.assertTrue("媒体类型必须为application/json", respContentType.equalsIgnoreCase("application/json"));
		Assert.assertTrue("结果码必须为SUCCESS", respJson.get("code").equals("SUCCESS"));
		if (handler != null) {
			handler.handleResponse(resp);
		}
		return respBody;
	}

	private static String readSessionId() {
		try (InputStream in = new FileInputStream(sessionFile);) {
			return StreamUtils.copyToString(in, Charset.forName("utf-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeSessionId(HttpResponse resp) {
		for (Header header : resp.getAllHeaders()) {
			if (header.getName().equalsIgnoreCase("Set-Cookie")) {
				HeaderElement[] eles = header.getElements();
				for (HeaderElement he : eles) {
					// 如果存在响应头中有会话id，就保存到文件
					if (he.getName().equalsIgnoreCase("xxx_JSESSIONID")) {
						try (OutputStream out = new FileOutputStream(sessionFile);) {
							StreamUtils.copy(he.getValue(), Charset.forName("utf-8"), out);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}

	private static String findContentType(HttpResponse resp) {
		String respContentType = null;
		for (Header header : resp.getAllHeaders()) {
			if (header.getName().trim().equalsIgnoreCase("Content-Type")) {
				respContentType = header.getValue().trim();
				// Content-Type: application/json; charset=utf-8
				respContentType = respContentType.split("\\s*;\\s*")[0];
			}
		}
		return respContentType;
	}
	/**
	 * 测试post application/json接口
	 * @param api
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String api) throws Exception {
		return postJson(api, null, null);
	}
	/**
	 * 测试post application/json接口
	 * @param api
	 * @param reqBody
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String api, Object reqBody) throws Exception {
		return postJson(api, reqBody, null);
	}
	/**
	 * 测试复合表单提交（文件上传接口）
	 * @param api
	 * @param reqBody
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String postMultipartForm(String api, Object reqBody, File file) throws Exception {
		return postMultipartForm(api, reqBody, file, null);
	}

	/**
	 * 
	 * @param api
	 * @param reqBody
	 * @param file
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	private static String postMultipartForm(String api, Object reqBody, File file, ResponseHandler<?> handler)
			throws Exception {
		if (reqBody == null) {
			reqBody = new Object();
		}
		// 请求体内容
		JSONObject reqJson = (JSONObject) JSON.toJSON(reqBody);
		// 会话id
		String sessionId = readSessionId();
		// 创建请求
		HttpPost post = new HttpPost(baseUri + api);
		// 设置请求头
		post.addHeader("Cookie", "xx_JSESSIONID=" + sessionId);
		// post.addHeader("Content-Type","multipart/form-data");
		// 设置请求体
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (Entry<String, Object> entry : reqJson.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				builder.addTextBody(entry.getKey(), value.toString(),ContentType.APPLICATION_JSON);
			}
		}
		HttpEntity multiEntity = builder.addBinaryBody("file", file).build();
		logger.info("entity：{}", multiEntity);

		post.setEntity(multiEntity);
		// 打印请求日志 和真实的报文有区别
		logger.info("请求行：{}", post.getRequestLine());
		String reqHeader = Stream.of(post.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		logger.info("请求头：{}", reqHeader);
		logger.info("请求体：{}", reqJson);
		// 发送请求
		HttpResponse resp = client.execute(post);
		// 打印响应日志
		String respHeader = Stream.of(resp.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		logger.info("状态行：{}", resp.getStatusLine());
		logger.info("响应头：{}", respHeader);
		String respContentType = findContentType(resp);
		writeSessionId(resp);
		// Content-Type: application/json; charset=utf-8
		respContentType = respContentType.split("\\s*;\\s*")[0];
		HttpEntity entity = resp.getEntity();
		// Set-Cookie:
		// xx_JSESSIONID=1p8bnvk60n5y5!1562290073434; Path=/
		String respBody = EntityUtils.toString(entity, "utf-8");
		JSONObject respJson = null;
		if (respContentType.equalsIgnoreCase("application/json")) {
			respJson = JSONObject.parseObject(respBody);
			logger.info("响应体：{}", JSON.toJSONString(respJson, true));
		} else {
			logger.info("响应体：{}", respBody);
		}
		Assert.assertTrue("状态码必须为200", resp.getStatusLine().getStatusCode() == 200);
		Assert.assertTrue("媒体类型必须为application/json", respContentType.equalsIgnoreCase("application/json"));
		Assert.assertTrue("结果码必须为SUCCESS", respJson.get("code").equals("SUCCESS"));
		if (handler != null) {
			handler.handleResponse(resp);
		}
		return respBody;
	}
	/**
	 * 测试下载附件接口
	 * @param api
	 * @param attachmentId
	 * @return
	 * @throws Exception
	 */
	public static String downloadAttach(String api, Long attachmentId) throws Exception {
		return downloadAttach(api, attachmentId,null);
	}
	private static String downloadAttach(String api, Long attachmentId, ResponseHandler<?> handler) throws Exception{
		return downloadAttach(api, attachmentId, downloadDir, null);
	}

	private static String downloadAttach(String api, Long attachmentId, File dir, ResponseHandler<?> handler) throws Exception{
		HttpGet get = new HttpGet(baseUri + api+"?attachmentId="+attachmentId);
		logger.info("请求行：{}", get.getRequestLine());
		// 会话id
		String sessionId = readSessionId();
		get.addHeader("Cookie", "xx_JSESSIONID=" + sessionId);
		String reqHeader = Stream.of(get.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		logger.info("请求头：{}", reqHeader);
		HttpResponse resp = client.execute(get);
		// 打印响应日志
		String respHeader = Stream.of(resp.getAllHeaders()).map(h -> h.toString()).collect(Collectors.joining("\n"));
		Optional<Header> op = Stream.of(resp.getAllHeaders()).filter(header->header.getName().equalsIgnoreCase("Content-Disposition")).findFirst();
		logger.info("状态行：{}", resp.getStatusLine());
		logger.info("响应头：{}", respHeader);
		// Content-Type: application/json; charset=utf-8
		HttpEntity entity = resp.getEntity();
		if(op.isPresent()){
			Header header = op.get();
			byte[] bytes = header.getValue().getBytes("iso-8859-1");
			String headerValue = new String(bytes,"utf-8");
			String[] parts = headerValue.split("\\s*[;=]\\s*");
			String filename = parts[parts.length-1];
			logger.info("转码后Content-Disposition: {}",headerValue);
			if(!dir.exists()){
				dir.mkdir();
			}
			String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss.SSS"));
			//long epoch = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
			File attachFile = new File(dir,time+"-"+filename);
			try(InputStream in = entity.getContent();OutputStream out = new FileOutputStream(attachFile);){
				StreamUtils.copy(in, out);
			}
			logger.info("响应体（流）：{}", attachFile.getAbsolutePath());
		}else{
			String respContentType = findContentType(resp);
			writeSessionId(resp);
			JSONObject respJson = null;
			String respBody = EntityUtils.toString(entity, "utf-8");
			if ("application/json".equalsIgnoreCase(respContentType)) {
				respJson = JSONObject.parseObject(respBody);
				logger.info("响应体：{}", JSON.toJSONString(respJson, true));
			} else {
				logger.info("响应体：{}", respBody);
			}
		}
		Assert.assertTrue("状态码必须为200", resp.getStatusLine().getStatusCode() == 200);
		Assert.assertTrue("响应头必须包含Content-Disposition",op.isPresent());
		if (handler != null) {
			handler.handleResponse(resp);
		}
		return null;
	}

}

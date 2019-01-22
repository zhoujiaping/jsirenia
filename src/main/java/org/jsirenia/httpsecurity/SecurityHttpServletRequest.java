package org.jsirenia.httpsecurity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsirenia.security.AESCBCUtil;
import org.jsirenia.security.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public class SecurityHttpServletRequest extends HttpServletRequestWrapper{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Charset charset =Charset.forName("utf-8");
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private ServletInputStream in;//提供的字节流
	private BufferedReader reader;//提供的字符流
	
	public SecurityHttpServletRequest(HttpServletRequest request,HttpServletResponse response) {
		super(request);
		this.request = request;
		this.response = response;
	}
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(in!=null){
			return in;
		}
		InputStream inputStream = super.getInputStream();
		String contentType = request.getContentType();
		String abstracts = request.getHeader(SecurityConst.HEADER_ABSTRACT);
		HttpSession session = request.getSession();
		String key = (String) session.getAttribute("secretKey");
		String iv = (String) session.getAttribute("secretKey-iv");
		boolean isText = contentType==null || contentType.matches(".*json.*|.*text.*|.*xml.*|.*x-www-form-urlencoded");
		//application/x-www-form-urlencoded
		//text/plain
		//text/xml
		byte[] encryptData = StreamUtils.copyToByteArray(inputStream);
		String codetype = request.getHeader(SecurityConst.HEADER_CODETYPE);
		if(codetype!=null && codetype.equalsIgnoreCase("base64")){
			logger.info("请求数据（未解密）：{}",new String(encryptData,charset));
			encryptData = Base64.getDecoder().decode(encryptData);
		}else{
			logger.info("请求数据（未解密）：{}",encryptData);
		}
		//解密 验签
		byte[] decryptData = AESCBCUtil.decrypt(encryptData, key,iv);
		if(isText){
			logger.info("请求数据（已解密）：{}",new String(decryptData,charset));
		}else{
			logger.info("请求数据（已解密）：{}",decryptData);
		}
		String md5 = MD5Util.md5Hex(decryptData);
		//
		if(!md5.equals(abstracts)){
			throw new RuntimeException("摘要不匹配");
		}
		inputStream.close();
		in =  new SecurityServletInputStream(new ByteArrayInputStream(decryptData));
		return in;
	}
	@Override
	public BufferedReader getReader() throws IOException {
		if(reader!=null){
			return reader;
		}
		InputStream in = getInputStream();
		reader = new BufferedReader(new InputStreamReader(in,charset));
		return reader;
	}
}

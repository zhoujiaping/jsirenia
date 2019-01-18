package org.jsirenia.httpsecurity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsirenia.security.AESUtil;
import org.jsirenia.security.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityServletOutputStream extends ServletOutputStream{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Charset charset =Charset.forName("utf-8");
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream out;
	private ByteArrayOutputStream bos;
	private String secretKey;
	private boolean flushed;
	public SecurityServletOutputStream(HttpServletRequest request, HttpServletResponse response, ServletOutputStream out,String secretKey){
		this.out = out;
		bos = new ByteArrayOutputStream();
		this.secretKey = secretKey;
		this.request = request;
		this.response = response;
	}
	 public void print(String s) throws IOException {
		 if(flushed){
			 throw new RuntimeException("流已经关闭");
		 }
		 super.print(s);
	     afterWrite();
	 }
	@Override
	public void write(int b) throws IOException {
		bos.write(b);
	}
	public void write(byte b[], int off, int len) throws IOException {
		if(flushed){
			throw new RuntimeException("流已经关闭");
		}
       super.write(b,off,len);
       afterWrite();
    }
	/**
	 * 吐槽一下servlet的流设计，写出内容之后，没有调用close方法。导致我们并不知道什么时候写完了。
	 * @throws IOException
	 */
	private void afterWrite() throws IOException{
		String contentType = response.getContentType();
		boolean isText = contentType==null || contentType.matches(".*json.*|.*text.*|.*xml.*|.*x-www-form-urlencoded");
		byte[] decryptData = bos.toByteArray();
		if(isText){
			logger.info("响应数据（未加密）：{}",new String(decryptData,charset));
		}else{
			logger.info("响应数据（未加密）：{}",decryptData);
		}
		//签名 加密
		byte[] encryptData = AESUtil.encrypt(decryptData, secretKey);
		String codetypeAccept = request.getHeader(SecurityConst.HEADER_CODETYPE_ACCEPT);
		if(codetypeAccept!=null && codetypeAccept.equalsIgnoreCase("base64")){
			encryptData = Base64.getEncoder().encode(encryptData);
			logger.info("响应数据（已加密）：{}",new String(encryptData,charset));
		}else{
			logger.info("响应数据（已加密）：{}",encryptData);
		}
		String md5 = MD5Util.md5Hex(decryptData);
		response.setHeader(SecurityConst.HEADER_ABSTRACT, md5);
		response.setContentLength(encryptData.length);//这一行代码千万不能省略，否则响应数据不全。
		out.write(encryptData);
		out.flush();
		flushed = true;
	}
	@Override
	public boolean isReady() {
		throw new RuntimeException("not supported");
	}
	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new RuntimeException("not supported");
	}
}

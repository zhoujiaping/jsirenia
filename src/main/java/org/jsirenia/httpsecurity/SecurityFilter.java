package org.jsirenia.httpsecurity;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsirenia.security.RSAUtil;
import org.jsirenia.security.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 封装HttpServletRequest和HttpServletResponse，
 * 重写其获取请求体和输出响应体的方法，做加解密处理。
 *
 */
public class SecurityFilter implements Filter{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Charset charset =Charset.forName("utf-8");

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		//不处理get请求
		if(req.getMethod().equalsIgnoreCase("get")){
			chain.doFilter(request, response);
			return;
		}
		HttpSession session = req.getSession(true);
		String secretKey = (String) session.getAttribute("secretKey");
		String contextPath = req.getContextPath();
		String uri = req.getRequestURI();
		if(uri.equals(contextPath+"/static/img/c.png")){
			logger.info("请求证书");
			if(secretKey != null){
				SecurityHttpServletResponse sresp = new SecurityHttpServletResponse(req,resp);
				PrintWriter pw = sresp.getWriter();
				pw.print("304");//客户端判断响应内容是否为304，如果是，说明会话中已经有了密钥，不需要再请求证书。
				pw.flush();
				sresp.setStatus(200);
				return;
			}
			CustomCert cert = new CustomCert();
			cert.setOwner("test");
			cert.setPublicKey(Base64.getEncoder().encodeToString(KeyHolder.publicKey.getEncoded()));
			cert.setVersion("v1.0");
			cert.setTimestamp(System.currentTimeMillis()+"");
			String data = JSON.toJSONString(cert);
			String sign = SignUtil.signBase64(data, KeyHolder.privateKey);
			cert.setSign(sign);
			String certData = JSON.toJSONString(cert);
			logger.info("证书内容：{}",certData);
			resp.getWriter().print(certData);
			resp.flushBuffer();
			return;
		}
		if(uri.equals(contextPath+"/static/img/s.png")){
			logger.info("保存密钥");
			if(secretKey != null){
				SecurityHttpServletResponse sresp = new SecurityHttpServletResponse(req,resp);
				PrintWriter pw = sresp.getWriter();
				pw.print("304");
				//客户端判断响应内容是否为304，如果是，说明会话中已经有了密钥，不需要再保存密钥。
				//防止中间人在用户已经有密钥的时候，修改会话中的密钥。
				pw.flush();
				sresp.setStatus(200);
				return;
			}
			String base64 = StreamUtils.copyToString(req.getInputStream(), charset);//base64
			logger.info("加密的密钥：{}",base64);
			byte[] encryptData = Base64.getDecoder().decode(base64);//rsa加密的内容
			byte[] decryptData;
			try {
				decryptData = RSAUtil.decryptByPrivateKey(encryptData, KeyHolder.privateKey);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			String decryptString = new String(decryptData,charset);
			logger.info("解密的密钥：{}",decryptString);
			JSONObject json = JSON.parseObject(decryptString);
			session.setAttribute("secretKey", json.get("sk"));
			session.setAttribute("secretKey-iv", json.get("iv"));
			try{
				SecurityHttpServletResponse sresp = new SecurityHttpServletResponse(req,resp);
				PrintWriter pw = sresp.getWriter();
				pw.print("OK");
				pw.flush();
				sresp.setStatus(200);
			}catch(Exception e){
				session.removeAttribute("secretKey");
				session.removeAttribute("secretKey-iv");
				throw new RuntimeException(e);
			}
			return;
		}
		SecurityHttpServletRequest sreq = new SecurityHttpServletRequest(req,resp);
		SecurityHttpServletResponse sresp = new SecurityHttpServletResponse(req,resp);
		chain.doFilter(sreq, sresp);
	}
	@Override
	public void destroy() {
	}

}

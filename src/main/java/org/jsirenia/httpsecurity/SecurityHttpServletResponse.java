package org.jsirenia.httpsecurity;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityHttpServletResponse extends HttpServletResponseWrapper{
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletOutputStream out;//提供的字节流
	private PrintWriter writer;//提供的打印流
	public SecurityHttpServletResponse(HttpServletRequest request,HttpServletResponse response) {
		super(response);
		this.request = request;
		this.response = response;
	}
	@Override
	public PrintWriter getWriter() throws IOException {
		if(writer!=null){
			return writer;
		}
		ServletOutputStream out = getOutputStream();
		writer = new PrintWriter(out);
		return writer;
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if(out!=null){
			return out;
		}
		out = new SecurityServletOutputStream(request,response,super.getOutputStream());
		return out;
	}
}

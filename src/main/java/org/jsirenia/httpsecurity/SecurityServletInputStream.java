package org.jsirenia.httpsecurity;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class SecurityServletInputStream extends ServletInputStream {
	private ByteArrayInputStream bis;
	public SecurityServletInputStream(ByteArrayInputStream bis){
		this.bis = bis;
	}
	@Override
	public int read() throws IOException {
		return bis.read();
	}
	@Override
	public boolean isFinished() {
		throw new RuntimeException("not supported");
	}
	@Override
	public boolean isReady() {
		throw new RuntimeException("not supported");
	}
	@Override
	public void setReadListener(ReadListener readListener) {
		throw new RuntimeException("not supported");
	}

}

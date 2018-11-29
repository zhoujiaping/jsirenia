package org.jsirenia.dubbo.proxy;

import java.io.IOException;

public class ProxyBootstrap {
	public static void main(String[] args) throws IOException {
		DubboProxy.getInstance();
		System.in.read();
	}
}
 

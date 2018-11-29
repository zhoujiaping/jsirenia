package org.jsirenia.dubbo.proxy;

public interface StubService {
	Object invoke(String interfaceClazz,String method,String jsonArray);
}

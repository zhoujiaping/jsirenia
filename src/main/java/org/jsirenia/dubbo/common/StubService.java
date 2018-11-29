package org.jsirenia.dubbo.common;

public interface StubService {
	Object invoke(String interfaceClazz,String method,String jsonArray);
}

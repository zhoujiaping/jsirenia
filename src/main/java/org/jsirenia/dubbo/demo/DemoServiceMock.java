package org.jsirenia.dubbo.demo;

import org.jsirenia.dubbo.proxy.Delegateable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
//目前必须实现Delegateable，可以优化，判断是否有delegate这个属性，如果有，赋值。不用写其他代码。
//服务的name必须是 简单类名（第一个字母小写）
@Service("demoServiceMock")
public class DemoServiceMock implements DemoService,Delegateable{
	private DemoService delegate;
    public String sayHello(String name){
    	String res = delegate.sayHello(name);
    	System.out.println(res);
    	return "mockSayHello";
    }
    public JSONObject testjson(){
    	JSONObject o = new JSONObject();
    	o.put("mock", "mock");
    	JSONObject res = delegate.testjson();
    	System.out.println(res);
    	return o;
    }
	@Override
	public void setDelegate(Object delegate) {
		this.delegate = (DemoService) delegate;
	}
}
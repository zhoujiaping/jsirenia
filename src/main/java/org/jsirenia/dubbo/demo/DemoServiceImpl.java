package org.jsirenia.dubbo.demo;

import com.alibaba.fastjson.JSONObject;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }

	@Override
	public JSONObject testjson() {
		JSONObject res= new JSONObject();
		res.put("hello", "world");
		return res;
	}
}
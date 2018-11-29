package org.jsirenia.dubbo.demo;

import com.alibaba.fastjson.JSONObject;

public interface DemoService {
    String sayHello(String name);
    JSONObject testjson();
}
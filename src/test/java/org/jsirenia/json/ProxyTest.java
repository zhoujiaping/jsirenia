package org.jsirenia.json;

import javax.annotation.Resource;

import org.jsirenia.dubbo.demo.AnotherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath*:/application-context.xml","classpath*:/application-mvc.xml"})
public class ProxyTest {
	@Resource
	AnotherService anotherService;
	@Test
	public void test(){
		anotherService.sayHello();
	}
}

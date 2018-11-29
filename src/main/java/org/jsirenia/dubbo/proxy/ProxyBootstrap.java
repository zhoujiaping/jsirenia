package org.jsirenia.dubbo.proxy;

import java.io.IOException;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class ProxyBootstrap {
	static ApplicationConfig application;
	static RegistryConfig localRegistry;
	static RegistryConfig actualRegistry;
	static ConsumerConfig consumerConfig;
	static ProviderConfig providerConfig;
	public static void main(String[] args) throws IOException {
		init();
		//DemoService xxxService = getRefFromActual(DemoService.class);
		//JSONObject json = xxxService.testjson();
		//DemoService demoService = getRefFromActual(DemoService.class);
		//Object res = demoService.testjson();
		//System.out.println(res);
		//Object json = stub.invoke(DemoService.class.getName(), "testjson", "[]");
		//System.out.println(json);
		System.in.read();
		//String res = xxxService.sayHello("duboooo...");
	}
	public static void init(){
		// Application Info
		application = new ApplicationConfig();
		//application.setName(UUID.randomUUID().toString());
		application.setName("dubbo-proxy");
		 
		// Registry Info
		localRegistry = new RegistryConfig();
		localRegistry.setProtocol("zookeeper");
		localRegistry.setAddress("127.0.0.1:2181");
		localRegistry.setCheck(false);
		//registry.setUsername("aaa");
		//registry.setPassword("bbb");
		
		actualRegistry = new RegistryConfig();
		actualRegistry.setProtocol("zookeeper");
		actualRegistry.setAddress("");//环境1的zk地址 TODO
		actualRegistry.setCheck(false);
		
		consumerConfig = new ConsumerConfig();
		consumerConfig.setApplication(application);
		consumerConfig.setRegistry(actualRegistry);
		consumerConfig.setCheck(false);
		//consumerConfig.setVersion("1.0.0");
		
		providerConfig = new ProviderConfig();
		providerConfig.setApplication(application);
		//providerConfig.setVersion("1.0.0");
		
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("dubbo");
		//protocol.setContextpath("dubbo");
		protocol.setPort(8080);
		protocol.setThreads(10);
		
		ServiceConfig<StubService> service = new ServiceConfig<>(); // In case of memory leak, please cache.
		service.setApplication(application);
		service.setRegistry(localRegistry); // Use setRegistries() for multi-registry case
		service.setProtocol(protocol); // Use setProtocols() for multi-protocol case
		service.setInterface(StubService.class);
		service.setRef(new StubServiceImpl());
		service.export();
	}
	public static <T> T getRefFromActual(Class<T> c){
		// NOTES: ReferenceConfig holds the connections to registry and providers, please cache it for performance.
		// Refer remote service
		ReferenceConfig<T> reference = new ReferenceConfig<>(); // In case of memory leak, please cache.
		reference.setApplication(application);
		reference.setRegistry(actualRegistry); 
		reference.setInterface(c);
		//reference.setVersion("1.0.0");
		reference.setRetries(1);
		reference.setConsumer(consumerConfig);
		//reference.setProtocol("hessianProxy");
		reference.setCheck(false);
		// Use xxxService just like a local bean
		return reference.get(); // NOTES: Please cache this proxy instance.
	}
}
 

package org.jsirenia.dubbo.proxy;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class DubboProxy {
	private static ApplicationConfig application;
	private static RegistryConfig localRegistry;
	private static RegistryConfig testRegistry;
	private static ConsumerConfig consumerConfig;
	private static ProviderConfig providerConfig;
	private static DubboProxy instance;
	private DubboProxy(){
		
	}
	public static DubboProxy getInstance(){
		if(instance == null){
			synchronized (DubboProxy.class) {
				if(instance == null){
					instance = new DubboProxy();
					instance.init();
				}
			}
		}
		return instance;
	}
	public void init(){
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
		
		testRegistry = new RegistryConfig();
		testRegistry.setProtocol("zookeeper");
		testRegistry.setAddress("");//TODO
		testRegistry.setCheck(false);
		
		consumerConfig = new ConsumerConfig();
		consumerConfig.setApplication(application);
		consumerConfig.setRegistry(testRegistry);
		consumerConfig.setCheck(false);
		//consumerConfig.setVersion("1.0.0");
		
		providerConfig = new ProviderConfig();
		providerConfig.setApplication(application);
		//providerConfig.setVersion("1.0.0");
		
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("dubbo");
		//protocol.setContextpath("dubbo");
		protocol.setPort(7070);
		protocol.setThreads(10);
		
		ServiceConfig<StubService> service = new ServiceConfig<>(); // In case of memory leak, please cache.
		service.setApplication(application);
		service.setRegistry(localRegistry); // Use setRegistries() for multi-registry case
		service.setProtocol(protocol); // Use setProtocols() for multi-protocol case
		service.setInterface(StubService.class);
		service.setRef(new StubServiceImpl());
		service.export();
	}
	public <T> T getRef(Class<T> c){
		// NOTES: ReferenceConfig holds the connections to registry and providers, please cache it for performance.
		// Refer remote service
		ReferenceConfig<T> reference = new ReferenceConfig<>(); // In case of memory leak, please cache.
		reference.setApplication(application);
		reference.setRegistry(testRegistry); 
		reference.setInterface(c);
		//reference.setVersion("1.0.0");
		reference.setRetries(1);
		reference.setConsumer(consumerConfig);
		//reference.setProtocol("hessianProxy");
		reference.setCheck(false);
		reference.setTimeout(100000);
		// Use xxxService just like a local bean
		return reference.get(); // NOTES: Please cache this proxy instance.
	}
}

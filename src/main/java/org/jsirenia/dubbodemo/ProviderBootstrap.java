package org.jsirenia.dubbodemo;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class ProviderBootstrap {
	public static void main(String[] args) throws Exception {
		// Implementation
		DemoService xxxService = new DemoServiceImpl();
		 
		// Application Info
		ApplicationConfig application = new ApplicationConfig();
		application.setName("dubbo-provider");
		 
		// Registry Info
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("127.0.0.1:2181");
		/*registry.setUsername("aaa");
		registry.setPassword("bbb");*/
		// Protocol
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName("dubbo");
		//protocol.setContextpath("dubbo");
		protocol.setPort(12345);
		protocol.setThreads(10);
		
		// NOTES: ServiceConfig holds the serversocket instance and keeps connections to registry, please cache it for performance.
		 
		// Exporting
		ServiceConfig<DemoService> serviceConfig = new ServiceConfig<>(); // In case of memory leak, please cache.
		serviceConfig.setApplication(application);
		serviceConfig.setRegistry(registry); // Use setRegistries() for multi-registry case
		serviceConfig.setProtocol(protocol); // Use setProtocols() for multi-protocol case
		serviceConfig.setInterface(DemoService.class);
		serviceConfig.setRef(xxxService);
		serviceConfig.setFilter("producerExceptionFilter");
		//service.setVersion("1.0.0");
		 
		/*ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setApplication(application);
		providerConfig.setRegistry(registry);
		providerConfig.setFilter("");*/
		// Local export and register
		serviceConfig.export();
		System.in.read();
	}
}
 

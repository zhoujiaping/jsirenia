package com.sf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.sf.exception.ExceptionHandler;

public class ConsumerBootstrap {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerBootstrap.class);

	static ApplicationConfig application;
	static RegistryConfig registry;
	static ConsumerConfig consumerConfig;

	public static void main(String[] args) {
		logger.info("开始执行");
		init();
		DemoService xxxService = getRef(DemoService.class);
		ExceptionHandler.handle(()->{
			xxxService.testNoException("1");
		});
		ExceptionHandler.handle(()->{
			xxxService.testServiceException("2");
		});
		ExceptionHandler.handle(()->{
			xxxService.testServiceExceptionWithCause("3");
		});
		ExceptionHandler.handle(()->{
			xxxService.testRuntimeException("4");
		});
		ExceptionHandler.handle(()->{
			xxxService.testNullPointerException("5");
		});
	}

	public static void init() {
		// Application Info
		application = new ApplicationConfig();
		// application.setName(UUID.randomUUID().toString());
		application.setName("dubbo-client");

		// Registry Info
		registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		// registry.setAddress("10.118.238.66:2181,10.118.238.48:2181,10.118.238.71:2181");
		registry.setAddress("127.0.0.1:2181");
		// registry.setAddress("127.0.0.1:2181");
		registry.setCheck(false);
		// registry.setUsername("aaa");
		// registry.setPassword("bbb");

		consumerConfig = new ConsumerConfig();
		consumerConfig.setFilter("consumerExceptionFilter");
		consumerConfig.setApplication(application);
		consumerConfig.setRegistry(registry);
		consumerConfig.setCheck(false);
		// consumerConfig.setVersion("1.0.0");
	}

	public static <T> T getRef(Class<T> c) {
		// NOTES: ReferenceConfig holds the connections to registry and
		// providers, please cache it for performance.
		// Refer remote service
		ReferenceConfig<T> reference = new ReferenceConfig<>(); // In case of
																// memory leak,
																// please cache.
		reference.setApplication(application);
		reference.setRegistry(registry);
		reference.setInterface(c);
		// reference.setVersion("1.0.0");
		reference.setRetries(1);
		reference.setConsumer(consumerConfig);
		// reference.setProtocol("hessianProxy");
		reference.setCheck(false);
		reference.setTimeout(100000);
		// Use xxxService just like a local bean
		return reference.get(); // NOTES: Please cache this proxy instance.
	}
	/*
	 * public static <T> T getRef(Class<T> c,String... methodNames){
	 * List<MethodConfig> methods = new ArrayList<MethodConfig>(); for(int
	 * i=0;i<methodNames.length;i++){ MethodConfig method = new MethodConfig();
	 * method.setName(methodNames[i]); method.setTimeout(10000);
	 * methods.add(method); } // Referring ReferenceConfig<T> reference = new
	 * ReferenceConfig<T>(); reference.setApplication(application);
	 * reference.setRegistry(registry); reference.setMethods(methods);
	 * //reference.setVersion("1.0.0"); reference.setRetries(1);
	 * reference.setConsumer(consumerConfig); return reference.get(); } public
	 * static <T> T getRef(String url,Class<T> c){ ReferenceConfig<T> reference
	 * = new ReferenceConfig<T>(); // If you know the address of the provider
	 * and want to bypass the registry, //use `reference.setUrl()` to specify
	 * the provider directly. //Refer [How to Invoke a specific
	 * provider](../demos/explicit-target.md) for details.
	 * reference.setApplication(application); reference.setRegistry(registry);
	 * reference.setUrl("dubbo://localhost:12345/"+c.getName());
	 * reference.setInterface(c); //reference.setVersion("1.0.0");
	 * reference.setRetries(1); reference.setConsumer(consumerConfig); return
	 * reference.get(); }
	 */

}

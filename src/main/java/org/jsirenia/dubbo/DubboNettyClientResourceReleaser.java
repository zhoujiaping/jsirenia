package org.jsirenia.dubbo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.PreDestroy;

import org.jboss.netty.channel.ChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 依赖dubbo2.5.3
 * 解决优雅停机的问题（dubbo间接创建的非守护线程导致的不能优雅停机） 参考文章
 * https://jeremy-xu.oschina.io/2016/12/%E8%A7%A3%E5%86%B3dubbo%E5%AF%BC%E8%87%B4tomcat%E6%97%A0%E6%B3%95%E4%BC%98%E9%9B%85shutdown%E7%9A%84%E9%97%AE%E9%A2%98/
 * 解决方案：
 * 在停掉应用的回调里面，比如spring的PreDestroy，比如ServletContextListener的contextDestroyed，
 * 获取NettryClient的静态属性 channelFactory，调用其releaseExternalResource方法。
 * 
 * @author 01375156 2019-04-18
 */
public class DubboNettyClientResourceReleaser {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 最低优先级
	@PreDestroy
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void destroy() {
		try {
			// 先释放dubbo所占用的资源
			Class<?> protocolConfigClass = Class.forName("com.alibaba.dubbo.config.ProtocolConfig");
			Method destroyAllMethod = protocolConfigClass.getMethod("destroyAll");
			destroyAllMethod.invoke(protocolConfigClass);
			//ProtocolConfig.destroyAll();
			logger.info("ProtocolConfig destroyAll success");
		} catch (Exception e) {
			logger.error("ProtocolConfig destroyAll error", e);
		}
		try {
			// 用反射释放NettyClient所占用的资源, 以避免不能优雅shutdown的问题
			releaseNettyClientExternalResources();
			logger.info("Release NettyClient's external resources");
		} catch (Exception e) {
			logger.error("Release NettyClient's external resources error", e);
		}
	}

	private void releaseNettyClientExternalResources() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		Class<?> nettyClientClass = Class.forName("com.alibaba.dubbo.remoting.transport.netty.NettyClient");
		Field field = nettyClientClass.getDeclaredField("channelFactory");
		field.setAccessible(true);
		ChannelFactory channelFactory = (ChannelFactory) field.get(nettyClientClass);
		channelFactory.releaseExternalResources();
		field.setAccessible(false);
	}
}

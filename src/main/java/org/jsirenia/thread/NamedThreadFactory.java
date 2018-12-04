package org.jsirenia.thread;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.util.BlockingArrayQueue;
import org.jsirenia.proxy.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class NamedThreadFactory implements ThreadFactory {
	private static final Logger logger = LoggerFactory.getLogger(NamedThreadFactory.class);
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;
	private MethodInterceptor methodInterceptor = new MethodInterceptor() {
		@Override
		public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			if(method.getName()=="run"){
				try{
					return methodProxy.invokeSuper(target, args);
				}catch(Exception e){
					logger.error("",e);
					throw e;
				}
			}
	    	return methodProxy.invokeSuper(target, args);
		}
	};

	public NamedThreadFactory(String namePrefix) {
		SecurityManager sm = System.getSecurityManager();
		this.group = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix + poolNumber.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(this.group, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (thread.getPriority() != 5) {
			thread.setPriority(5);
		}
		thread = ProxyUtil.createProxy(Thread.class, methodInterceptor);
		return thread;
	}
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<Runnable> workQueue = new BlockingArrayQueue<>(1024);
		ThreadFactory threadFactory = new NamedThreadFactory("test");
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1000, TimeUnit.SECONDS,workQueue,threadFactory);
		executor.submit(()->{
			throw new RuntimeException("a exception throwd");
		});
		executor.shutdown();
		//TODO
		while(!executor.awaitTermination(3, TimeUnit.SECONDS)){
		}
	}
}

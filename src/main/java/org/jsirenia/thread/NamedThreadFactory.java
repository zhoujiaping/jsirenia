package org.jsirenia.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//线程池https://www.jianshu.com/p/ae67972d1156
/**
 * 多线程防止异常吞并的解决方案：
 * 使用静态代理或者动态代理，执行run方法时记录异常（无法实现）。
 * 遵循最佳实践，要么在runnable中自己捕获异常，记录日志；
 * 要么在submit时获取Future，然后调用get方法。
 * 推荐在runnable中自己捕获异常，记录日志。
 *
 */
public class NamedThreadFactory implements ThreadFactory {
	private static final Logger logger = LoggerFactory.getLogger(NamedThreadFactory.class);
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

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
		/*thread = ProxyUtil.createProxy(thread, (target,method,args)->{
			if(method.getName()=="run"){
				try{
					return method.invoke(target, args);
				}catch(Exception e){
					logger.error("",e);
					throw new RuntimeException(e);
				}
			}
			try{
				return method.invoke(target, args);
			}catch(Exception e){
				logger.error("",e);
				throw new RuntimeException(e);
			}
		});*/
		return thread;
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1024);
		ThreadFactory threadFactory = new NamedThreadFactory("test");
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1000L, TimeUnit.SECONDS,workQueue,threadFactory);
		Future<?> f = executor.submit(new Runnable(){
			@Override
			public void run() {
				throw new RuntimeException("a exception throwd");
				//System.out.println("submit");
			}});
		executor.shutdown();
		//TODO
		while(true){
			boolean finished = executor.awaitTermination(3, TimeUnit.SECONDS);
			if(finished){
				break;
			}
			System.out.println("wait");
		}
		Object res = f.get();
		System.out.println(res);
		/*while(!executor.awaitTermination(3, TimeUnit.SECONDS)){
			System.out.println("wait");
		}*/
	}
}

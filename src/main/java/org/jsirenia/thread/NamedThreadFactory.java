package org.jsirenia.thread;

import java.io.IOException;
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
 * 1、使用静态代理或者动态代理，执行run方法时记录异常（无法实现）。
 * 2、遵循最佳实践，要么在runnable中自己捕获异常，记录日志； 要么在submit时获取Future，然后调用get方法。
 * 推荐在runnable中自己捕获异常，记录日志。
 * 3、设置全局的未捕获异常处理 ThreadUtil#setDefaultUncaughtExceptionHandlerNX
 * 这种方式可以作用于executor#execute，不能作用于executor#submit。因为后者会将异常捕获，只有在
 * 调用Future#get时才将异常抛出来。
 * 4、最佳实践
 * 如果需要获取线程执行结果，就用submit，并且必须调用其get方法。
 * 否则，就用execute，并且在项目启动时最早的时候，调用Thread#setDefaultUncaughtExceptionHandler
 * Runnable或者Callable中，要判断当前线程是否interrupted。
 * （从业务上区分 代码的执行粒度，即可以从哪些地方被interrupted。在可以interrupted的地方判断interrupted）
 * 中断线程，调用线程的interrupt方法（修改它的标记）
 * 线程内部判断interrupted就是判断这个标记。
 * 应用停止时，必须保证销毁线程池。
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
		return thread;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		
		logger.error("错误：{}",new RuntimeException("异常"));
		
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1024);
		ThreadFactory threadFactory = new NamedThreadFactory("test");
		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1000L, TimeUnit.SECONDS, workQueue, threadFactory);
		ThreadUtil.setDefaultUncaughtExceptionHandlerNX((t, e) -> {
			logger.error("哈？异常了？", e);
		});
		Future<?> f = executor.submit(new Runnable() {
			@Override
			public void run() {
				if(Thread.currentThread().isInterrupted()){
					logger.info("线程被interrupted");
					return;
				}
				throw new RuntimeException("a exception throwd");
				// System.out.println("submit");
			}
		});
		executor.execute(() -> {
			throw new RuntimeException("哈，异常");
		});
		executor.shutdown();
		// TODO
		while (true) {
			boolean finished = executor.awaitTermination(3, TimeUnit.SECONDS);
			if (finished) {
				break;
			}
			System.out.println("wait");
		}
		Object res = f.get();
		System.out.println(res);
		/*
		 * while(!executor.awaitTermination(3, TimeUnit.SECONDS)){
		 * System.out.println("wait"); }
		 */

		// System.in.read();
	}
}

package org.jsirenia.thread;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExecutorUtil.class);
	public static void shutdownAndAwaitUntilFinished(ExecutorService executorService) throws InterruptedException{
		//拒绝接受新的任务
		executorService.shutdown();
		while(true){
			//等待正在执行的任务执行完毕
			boolean finished = executorService.awaitTermination(3, TimeUnit.SECONDS);
			if(finished){
				break;
			}
		}
	}
	public static ScheduledThreadPoolExecutor newScheduledThreadPoolExecutor(int corePoolSize,
            ThreadFactory threadFactory){
		ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(0, new NamedThreadFactory("autoPayJob"));
		/*ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(0, new NamedThreadFactory("autoPayJob"));
		executorService.setMaximumPoolSize(1);*/
		return executorService;
	}
	public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory){
		ThreadPoolExecutor executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,keepAliveTime, unit,
				workQueue,threadFactory);
		/*executorService = new ThreadPoolExecutor(0, 1,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),new NamedThreadFactory("myThreadPool"));*/
		return executorService;
	}
}

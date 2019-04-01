package org.jsirenia.thread;


import java.util.concurrent.ExecutorService;
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
}

package org.jsirenia.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExecutorUtil.class);
	public static void shutdownAndAwaitUntilFinished(ExecutorService executorService) throws InterruptedException{
		executorService.shutdown();
		while(true){
			boolean finished = executorService.awaitTermination(3, TimeUnit.SECONDS);
			if(finished){
				break;
			}
		}
	}
}

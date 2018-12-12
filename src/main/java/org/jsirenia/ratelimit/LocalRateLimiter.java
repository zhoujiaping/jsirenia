package org.jsirenia.ratelimit;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 限流器
 */
public class LocalRateLimiter {
	protected Logger logger = LoggerFactory.getLogger(LocalRateLimiter.class);
	private int capacity;
	private long tokens;
	private long timestamp;
	private int rate;
	private String service;
	private static Map<String,LocalRateLimiter> map = new ConcurrentHashMap<>();
	public static LocalRateLimiter getRateLimiter(String service){
		LocalRateLimiter limiter = map.get(service);
		if(limiter == null){
			limiter = new LocalRateLimiter(service);
			map.put(service, limiter);
		}
		return limiter;
	}
	public LocalRateLimiter withCapacity(int capacity){
		this.capacity = capacity;
		return this;
	}
	public LocalRateLimiter withRate(int rate){
		this.rate = rate;
		return this;
	}
	public String getService() {
		return service;
	}
	private LocalRateLimiter(String service){
		this.service = service;
	}
	/**
	 * 申请令牌
	 */
	public boolean applyToken(){
		long now = System.currentTimeMillis();
		if(timestamp<=0){
			tokens = capacity-1;
			timestamp = now;
			return true;
		}
		tokens = tokens + (now-timestamp)/1000*rate;
		if(tokens>capacity){
			tokens = capacity;
		}
		if(tokens<1){
			return false;
		}else{
			timestamp = now;
			tokens = tokens-1;
			return true;
		}
	}
	/**
	 * 清理token
	 */
	public void clear(){
		tokens = 0;
		timestamp = System.currentTimeMillis();
	}
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		LocalRateLimiter limiter = LocalRateLimiter.getRateLimiter("test");
		limiter.withCapacity(10).withRate(5);
		for(int i=0;i<100;i++){
			boolean b = limiter.applyToken();
			System.out.println(b);
			Thread.sleep(200);
		}
	}
}

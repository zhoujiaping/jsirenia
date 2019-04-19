package org.jsirenia.ratelimit;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private Lock lock = new ReentrantLock(false);
	private static Map<String,LocalRateLimiter> map = new ConcurrentHashMap<>();
	public static LocalRateLimiter getRateLimiter(String service){
		LocalRateLimiter limiter = map.get(service);
		if(limiter == null){
			limiter = new LocalRateLimiter(service);
			limiter.timestamp = System.currentTimeMillis();
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
		try{
			if(lock.tryLock(200, TimeUnit.MILLISECONDS)){
				long now = System.currentTimeMillis();
				//更新令牌数
				tokens = tokens + (now-timestamp)/1000*rate;
				if(tokens>capacity){
					tokens = capacity;
				}
				//令牌被申请完了
				if(tokens<1){
					return false;
				}else{
					//令牌没被申请完，更新时间戳，更新令牌数
					timestamp = now;
					tokens = tokens-1;
					return true;
				}
			}else{
				return false;
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 清理token
	 */
	public void clear(){
		try{
			if(lock.tryLock(200, TimeUnit.MILLISECONDS)){
				tokens = 0;
				timestamp = System.currentTimeMillis();
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			lock.unlock();
		}
		
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

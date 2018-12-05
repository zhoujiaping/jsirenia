package org.jsirenia.lock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsirenia.util.callback.Callback01;
import org.jsirenia.util.callback.Callback11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ReentrantLockRunner<T> implements Callback11<T,Callback01<T>>{
	protected Logger logger = LoggerFactory.getLogger(ReentrantLockRunner.class);

	protected String lockKey;
	protected String requestId = UUID.randomUUID().toString();
	protected static long expireTime = 5*60;
	protected Lock lock;

	private static final Cache<String,Lock> cb = CacheBuilder.newBuilder().expireAfterWrite(expireTime, TimeUnit.SECONDS).build();

	public ReentrantLockRunner(RedisLock redisLock,String lockKey,long expireTime){
		lock = cb.asMap().getOrDefault(lockKey, new ReentrantLock());
		this.lockKey = lockKey;
	}
	
	protected T onGetLockFailed(){
		logger.info("获取redis锁失败,lockKey={},requestId={},expireTime={}",lockKey, requestId, expireTime);
		throw new RuntimeException(String.format("获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
	}
	protected void onReleaseLockFailed(){
		logger.error("释放redis锁失败,lockKey={},requestId={}",lockKey, requestId);
		throw new RuntimeException(String.format("释放redis锁失败,lockKey=%s,requestId=%s", lockKey,requestId));
	}
	protected void onReleaseLockException(Exception e){
		logger.error("释放redis锁异常,lockKey={},requestId={}",lockKey, requestId,e);
		throw new RuntimeException(e);
	}
	protected void onReleaseLockSuccess(){
		
	}
	@Override
	public T apply(Callback01<T> cb) {
		boolean locked = lock.tryLock();
		if(locked){
			try{
				return cb.apply();
			}finally{
				Boolean unlockSuccess = null;
				try{
					//boolean unlockSuccess = redisLock.delValue(lockKey)>0;
					lock.unlock();
					unlockSuccess = true;
				}catch(Exception e){
					onReleaseLockException(e);
				}
				if(unlockSuccess!=null){
					if(unlockSuccess){
						onReleaseLockSuccess();
					}else{
						onReleaseLockFailed();
					}
				}
			}
		}else{
			return onGetLockFailed();
		}
	}

}

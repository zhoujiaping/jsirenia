package org.jsirenia.lock;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用模板方法模式，封装Redis锁的相关操作。
 * 应用该模板，在获取redis锁成功，失败，释放redis锁成功，失败，异常时，自定义行为。
 * 默认在获取锁失败、释放锁失败、释放锁异常的时候，打印日志并抛出异常。
 * */
public abstract class RedisLockCallback<T> implements Callback<T>{
	protected Logger logger = LoggerFactory.getLogger(RedisLockCallback.class);
	private RedisLock redisLock;
	protected String lockKey;
	protected long expireTime;
	protected String requestId = UUID.randomUUID().toString();
	/**
	 * 
	 * @param redisLock
	 * @param lockKey
	 * @param expireTime 锁的过期时间，单位秒
	 */
	public RedisLockCallback(RedisLock redisLock,String lockKey,long expireTime){
		this.redisLock = redisLock;
		this.lockKey = lockKey;
		this.expireTime = expireTime;
	}
	protected abstract T onGetLockSuccess();
	protected void onReleaseLockSuccess(){
		
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
	@Override
	public T execute() {
		//boolean locked = redisLock.setValueNxExpire(lockKey,requestId, ""+expireTime);
		boolean locked = redisLock.tryGetDistributedLock(lockKey, requestId, expireTime*1000);
		if(locked){
			try{
				return onGetLockSuccess();
			}finally{
				Boolean unlockSuccess = null;
				try{
					//boolean unlockSuccess = redisLock.delValue(lockKey)>0;
					unlockSuccess = redisLock.releaseDistributedLock(lockKey, requestId);
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

package org.jsirenia.lock;

import org.jsirenia.util.Callback;
import org.jsirenia.util.Callback.Callback01;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

public class TimeoutRedisLockRunner<T> extends SpringRedisLockRunner<T>{
	/** 超时，毫秒 */
	private long retryTimeout = 2000;
	/** 如果设置了超时，默认每20毫秒去获取一次锁。 */
	private long sleepMillisecond = 20;
	private boolean executed;//为了防止执行后各配置被修改，加一个是否正执行过的标记。执行过就不允许修改配置。
	private int tryedTimes = 0;
	private long beginMillisecond;
	private Callback.Callback01e<T> onGetLockSuccess;
	private void ensureNotAtExecuting(){
		if(executed){
			throw new RuntimeException("已经执行过，某些操作不被支持");
		}
	}
	/**
	 * 采用了链式调用风格，使调用更简洁方便
	 */
	protected TimeoutRedisLockRunner<T> withRetryTimeout(long retryTimeout,long sleepMillisecond) {
		ensureNotAtExecuting();
		Assert.isTrue(retryTimeout >= 5, "retryTimeout必须大于等于5ms");
		this.retryTimeout = retryTimeout;
		this.sleepMillisecond = sleepMillisecond;
		return this;
	}
	@Override
	protected T onGetLockFailed(){
		tryedTimes++;
		logger.info("第{}次获取redis锁失败,lockKey={},requestId={},expireTime={}",tryedTimes,lockKey, requestId, expireTime);
		if(beginMillisecond+retryTimeout < System.currentTimeMillis()){
			throw new RuntimeException(String.format("获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
		}
		try {
			Thread.sleep(sleepMillisecond);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return super.apply(onGetLockSuccess);
	}
	public TimeoutRedisLockRunner(RedisLock redisLock, String lockKey, long expireTime) {
		super(redisLock, lockKey, expireTime);
	}
	@Override
	public T apply(Callback.Callback01e<T> onGetLockSuccess) {
		this.onGetLockSuccess = onGetLockSuccess;
		beginMillisecond = System.currentTimeMillis();
		return super.apply(onGetLockSuccess);
	}
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost",6379);
		//jedis.auth(password);
		JedisLock lock = new JedisLock();
		lock.setRedis(jedis);
		jedis.set("test", "123456");
		TimeoutRedisLockRunner<Object> runner = new TimeoutRedisLockRunner<>(lock,"test2",10);
		runner.withRetryTimeout(1000*10,2000).apply(()->{
			System.out.println("获取锁成功");
			return null;
		});
	}

}

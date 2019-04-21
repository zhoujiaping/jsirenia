package org.jsirenia.lock;

import org.jsirenia.util.Callback.Callback01;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

public class RetryableRedisLockRunner<T> extends SpringRedisLockRunner<T>{
	public RetryableRedisLockRunner(RedisLock redisLock, String lockKey, long expireTime) {
		super(redisLock, lockKey, expireTime);
	}
	private int retryTimes = 1;
	/** 如果没有设置超时，默认每20毫秒去获取一次锁。 */
	private long sleepMillisecond = 20;
	private boolean executed;//为了防止执行后各配置被修改，加一个是否正执行过的标记。执行过就不允许修改配置。
	private int tryedTimes = 0;
	private Callback01<T> onGetLockSuccess;
	private void ensureNotAtExecuting(){
		if(executed){
			throw new RuntimeException("已经执行过，某些操作不被支持");
		}
	}
	@Override
	protected T onGetLockFailed(){
		tryedTimes++;
		logger.info("第{}次获取redis锁失败,lockKey={},requestId={},expireTime={}",tryedTimes,lockKey, requestId, expireTime);
		if(tryedTimes>=retryTimes){
			throw new RuntimeException(String.format("获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
		}
		try {
			Thread.sleep(sleepMillisecond);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return super.apply(onGetLockSuccess);
	}
	protected RetryableRedisLockRunner<T> withRetryTimes(int retryTimes, long millisecond) {
		ensureNotAtExecuting();
		Assert.isTrue(millisecond >= 5, "millisecond必须大于等于5ms");
		Assert.isTrue(retryTimes >= 0, "retryTimes必须大于等于0");
		this.retryTimes = retryTimes;
		this.sleepMillisecond = millisecond;
		return this;
	}
	@Override
	public T apply(Callback01<T> onGetLockSuccess) {
		this.onGetLockSuccess = onGetLockSuccess;
		return super.apply(onGetLockSuccess);
	}
	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost",6379);
		//jedis.auth(password);
		JedisLock lock = new JedisLock();
		lock.setRedis(jedis);
		jedis.set("test", "123456");
		RetryableRedisLockRunner<Object> runner = new RetryableRedisLockRunner<>(lock,"test",10);
		runner.withRetryTimes(3, 2000).apply(()->{
			System.out.println("获取锁成功");
			return null;
		});
	}
}

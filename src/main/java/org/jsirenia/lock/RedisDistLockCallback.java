package org.jsirenia.lock;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;

/**
 * 可以使用三种模式： 一次性模式，获取不到锁直接返回失败； 超时模式，每隔一小段时间重试获取锁，如果一直获取不到锁并且超时，就返回失败；
 * 固定次数重试模式，每隔一小段时间重试获取锁，如果最后仍然获取不到锁，就返回失败。
 * 使用了模板方法模式和适配器模式、回调风格。在成功获取锁之后、获取锁异常、获取锁失败、释放锁成功等各个点设计了回调。
 */
public abstract class RedisDistLockCallback<T> implements Callback<T> {
	private static final Logger logger = LoggerFactory.getLogger(RedisDistLockCallback.class);
	private RedisDistLock redisDistLock;
	private String lockKey;
	/** 锁的过期时间，毫秒 */
	private long expireTime;
	/** 超时，毫秒 */
	private long retryTimeout;
	private String requestId = UUID.randomUUID().toString();
	/** 如果设置了超时，默认每20毫秒去获取一次锁。 */
	private long sleepMillisecond = 20;
	private int retryTimes;
	private RetryMode retryMode;
	private boolean executed;//为了防止执行后各配置被修改，加一个是否正执行过的标记。执行过就不允许修改配置。

	private enum RetryMode {
		DEFAULT, RETRY_TIMEOUT, RETRY_TIMES;
	}

	/**
	 * 
	 * @param redisLock
	 * @param lockKey
	 * @param expireTime
	 *            锁的过期时间，单位秒
	 */
	public RedisDistLockCallback(RedisDistLock redisDistLock, String lockKey, long expireTime) {
		this.redisDistLock = redisDistLock;
		this.lockKey = lockKey;
		this.expireTime = expireTime;
		this.retryMode = RetryMode.DEFAULT;
	}

	protected abstract T onGetLockSuccess();

	protected void onReleaseLockSuccess() {
		logger.info(String.format("释放redis锁成功,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
	}

	protected T onGetLockFailed() {
		throw new RuntimeException(
				String.format("获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
	}

	protected void onReleaseLockFailed() {
		// logger.error("释放redis锁失败,lockKey={},requestId={}",lockKey,
		// requestId);
		logger.info(String.format("释放redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
		throw new RuntimeException(String.format("释放redis锁失败,lockKey=%s,requestId=%s", lockKey, requestId));
	}

	protected void onReleaseLockException(Exception e) {
		// logger.error("释放redis锁异常,lockKey={},requestId={}",lockKey,
		// requestId,e);
		logger.info(String.format("释放redis锁异常,lockKey=%s,requestId=%s", lockKey, requestId));
		throw new RuntimeException(e);
	}
	private void ensureNotAtExecuting(){
		if(executed){
			throw new RuntimeException("已经执行过，某些操作不被支持");
		}
	}
	protected RedisDistLockCallback<T> withRetryTimeout(long retryTimeout, long millisecond) {
		ensureNotAtExecuting();
		this.retryTimeout = retryTimeout;
		Assert.isTrue(millisecond >= 5, "millisecond必须大于等于5ms");
		Assert.isTrue(retryTimeout >= 5, "retryTimeout必须大于等于5ms");
		this.sleepMillisecond = millisecond;
		this.retryMode = RetryMode.RETRY_TIMEOUT;
		return this;
	}


	/**
	 * 采用了链式调用风格，使调用更简洁方便
	 */
	protected RedisDistLockCallback<T> withRetryTimeout(long retryTimeout) {
		ensureNotAtExecuting();
		Assert.isTrue(retryTimeout >= 5, "retryTimeout必须大于等于5ms");
		this.retryTimeout = retryTimeout;
		this.retryMode = RetryMode.RETRY_TIMEOUT;
		return this;
	}

	protected RedisDistLockCallback<T> withRetryTimes(int retryTimes, long millisecond) {
		ensureNotAtExecuting();
		Assert.isTrue(millisecond >= 5, "millisecond必须大于等于5ms");
		Assert.isTrue(retryTimes >= 0, "retryTimes必须大于等于0");
		this.retryTimes = retryTimes;
		this.sleepMillisecond = millisecond;
		this.retryMode = RetryMode.RETRY_TIMES;
		return this;
	}

	protected RedisDistLockCallback<T> withRetryTimes(int retryTimes) {
		ensureNotAtExecuting();
		Assert.isTrue(retryTimes >= 0, "retryTimes必须大于等于0");
		this.retryTimes = retryTimes;
		this.retryMode = RetryMode.RETRY_TIMES;
		return this;
	}

	@Override
	public T execute() {
		try {
			executed = true;
			return executeInternal();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private T executeInternal() throws InterruptedException {
		// boolean locked = redisLock.setValueNxExpire(lockKey,requestId,
		// ""+expireTime);
		boolean locked = false;
		int count = 1;
		switch (retryMode) {
		case DEFAULT:
			locked = redisDistLock.tryGetDistributedLock(lockKey, requestId, expireTime * 1000);
			if(!locked){
				logger.info(String.format("获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
			}
			break;
		case RETRY_TIMEOUT:
			int i = retryTimes;
			while (true) {
				locked = redisDistLock.tryGetDistributedLock(lockKey, requestId, expireTime * 1000);
				if (locked) {
					break;
				}
				i--;
				logger.info(String.format("第%s次获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s",count, lockKey, requestId, expireTime));
				count++;
				if (i >= 0) {
					Thread.sleep(sleepMillisecond);
				}
			}
			break;
		case RETRY_TIMES:
			long millisecond = System.currentTimeMillis();
			while (true) {
				locked = redisDistLock.tryGetDistributedLock(lockKey, requestId, expireTime * 1000);
				if (locked) {
					break;
				}
				logger.info(String.format("第%s次获取redis锁失败,lockKey=%s,requestId=%s,expireTime=%s",count, lockKey, requestId, expireTime));
				count++;
				if (retryTimeout + millisecond > System.currentTimeMillis()) {
					Thread.sleep(sleepMillisecond);
				} else {
					break;
				}
			}
			break;
		default:
			throw new RuntimeException("未知的retryMode值:" + retryMode);
		}
		if (locked) {
			try {
				logger.info(String.format("获取redis锁成功,lockKey=%s,requestId=%s,expireTime=%s", lockKey, requestId, expireTime));
				return onGetLockSuccess();
			} finally {
				Boolean unlockSuccess = null;
				try {
					// boolean unlockSuccess = redisLock.delValue(lockKey)>0;
					//logger.info("正在释放锁...");
					unlockSuccess = redisDistLock.releaseDistributedLock(lockKey, requestId);
				} catch (Exception e) {
					onReleaseLockException(e);
				}
				if (unlockSuccess != null) {
					if (unlockSuccess) {
						onReleaseLockSuccess();
					} else {
						onReleaseLockFailed();
					}
				}
			}
		} else {
			return onGetLockFailed();
		}
	}

	public static void main(String[] args) {
		Jedis redis = new Jedis("localhost", 6379);
        // redis.auth("");
		RedisDistLock redisDistLock = new RedisDistLock();
		redisDistLock.setRedis(redis);
		String lockKey = "aaaa";
		long expireTime = 1200;
		long timeout = 1000 * 600;
		RedisDistLockCallback<Object> cb = new RedisDistLockCallback<Object>(redisDistLock, lockKey, expireTime) {
			@Override
			protected Object onGetLockSuccess() {
				logger.info("onGetLockSuccess1");
				try {
					new RedisDistLockCallback<Object>(redisDistLock, lockKey, expireTime) {
						@Override
						protected Object onGetLockSuccess() {
							logger.info("onGetLockSuccess2");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								logger.error("",e);
								e.printStackTrace();
							}
							return null;
						}
					}.withRetryTimes(3).execute();
					//Thread.sleep(1000);
				} catch (Exception e) {
					logger.error("",e);
				}
				return null;
			}
		};
		cb.withRetryTimeout(timeout, 20).execute();
		//
		
	}
}
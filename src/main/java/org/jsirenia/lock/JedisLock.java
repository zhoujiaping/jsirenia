package org.jsirenia.lock;

import java.util.Collections;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

import redis.clients.jedis.Jedis;

public class JedisLock implements RedisLock {
	// private static Logger logger = LoggerFactory.getLogger(RedisLock.class);
	private static final String LOCK_SUCCESS = "OK";
	private static final Long RELEASE_SUCCESS = 1L;
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_IF_EXIST = "XX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private Jedis redis;

	public void setRedis(Jedis redis) {
		this.redis = redis;
	}

	/**
	 * 尝试获取分布式锁 如果已经持有锁，则重新设置过期时间。
	 * 
	 * @param lockKey
	 *            锁
	 * @param requestId
	 *            请求标识
	 * @param expireTime
	 *            单位为毫秒 超期时间
	 * @return 是否获取成功
	 */
	public boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
		// SET key value XX PX millisecond
		// SET key value NX PX millisecond
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
				+ "				return redis.call('set', KEYS[1],ARGV[1],ARGV[2],ARGV[3])" + "           else "
				+ "				return redis.call('set', KEYS[1],ARGV[1],ARGV[4],ARGV[2],ARGV[3])" + "           end ";
		Object result = redis.eval(script, 1, lockKey, requestId, SET_WITH_EXPIRE_TIME, "" + expireTime,
				SET_IF_NOT_EXIST);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 释放分布式锁
	 * 
	 * @param jedis
	 *            Redis客户端
	 * @param lockKey
	 *            锁
	 * @param requestId
	 *            请求标识，防止释放别人申请的锁。
	 * @return 是否释放成功
	 */
	public boolean releaseDistributedLock(String lockKey, String requestId) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
				+ "				return redis.call('del', KEYS[1]) " + "			else " + "				return 0 end";
		Object result = redis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	public boolean tryGetDistributedReentrantLock(final String lockKey, final String requestId, final long expireTime) {
		// SET key value XX PX millisecond
		// SET key value NX PX millisecond
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
				+ "				return redis.call('set', KEYS[1],ARGV[1],ARGV[2],ARGV[3])" + "           else "
				+ "				return redis.call('set', KEYS[1],ARGV[1],ARGV[4],ARGV[2],ARGV[3])" + "           end ";
		Object result = redis.eval(script, 1, lockKey, requestId, SET_WITH_EXPIRE_TIME, "" + expireTime,
				SET_IF_NOT_EXIST);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
}

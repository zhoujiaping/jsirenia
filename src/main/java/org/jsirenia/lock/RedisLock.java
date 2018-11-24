package org.jsirenia.lock;

public interface RedisLock {
	boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime);
	boolean releaseDistributedLock(String lockKey, String requestId);
}

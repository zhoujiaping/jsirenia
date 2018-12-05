package org.jsirenia.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
/**
 * 有时候没有redis环境，使用jdk的lock作为替代环境。
 * 该类仅仅用于测试。
 */
public class JdkLock implements RedisLock{
	private static final Cache<String,Lock> cb = CacheBuilder.newBuilder().expireAfterWrite(5*60, TimeUnit.SECONDS).build();
	private Lock lock = new ReentrantLock();
	@Override
	public boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
		try{
			lock.tryLock();
			if(cb.asMap().containsKey(lockKey)){//虽然用的是可重入锁，但是这里不能支持重入。
				return false;
			}
			Lock l = new ReentrantLock();
			cb.put(lockKey, l);
			return l.tryLock();
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public boolean releaseDistributedLock(String lockKey, String requestId) {
		try{
			lock.tryLock();
			Lock l = cb.asMap().remove(lockKey);
			if(l!=null){
				l.unlock();
			}
			return true;
		}finally{
			lock.unlock();
		}
	}
	public static void main(String[] args) {
		JdkLock lock = new JdkLock();
		boolean locked = lock.tryGetDistributedLock("aaa", "", 1);
		System.out.println(locked);
		locked = lock.tryGetDistributedLock("aaa", "", 1);
		System.out.println(locked);
		lock.releaseDistributedLock("aaa", "");
		locked = lock.tryGetDistributedLock("aaa", "", 1);
		System.out.println(locked);
		lock.releaseDistributedLock("aaa", "");
		lock.releaseDistributedLock("aaa", "");
		lock.releaseDistributedLock("aaa", "");
	}
}

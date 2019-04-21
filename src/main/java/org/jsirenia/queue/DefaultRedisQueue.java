package org.jsirenia.queue;

import com.google.common.base.Objects;

import redis.clients.jedis.Jedis;
/**
 * redis隊列
 * 特點：
 * 簡單
 * 快速
 * 沒有消息確認機制
 * @author zhoujiaping
 */
public class DefaultRedisQueue implements RedisQueue{
	private Jedis redis;
	private String key;
	private int capacity;
	
	public DefaultRedisQueue config(Jedis redis,String key, int capacity){
		this.redis = redis;
		this.key = key;
		this.capacity = capacity;
		return this;
	}
	public static class RedisQueueException extends RuntimeException{
		public RedisQueueException(String string) {
			super(string);
		}
		private static final long serialVersionUID = 1L;
	}
	@Override
	public boolean add(String... source) {
		if(capacity>0){
			Long len = redis.llen(key);
			int overSize =  (int) (len+source.length - capacity);
			if(overSize > 0){
				int effectSize = source.length - overSize;
				String[] target = new String[effectSize];
				System.arraycopy(source, 0, target, 0, effectSize);
				redis.lpush(key, target);
				return false;
			}else{
				redis.lpush(key, source);
			}
		}else{
			redis.lpush(key, source);
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see org.jsirenia.queue.RedisQueueI#clear()
	 */
	@Override
	public void clear() {
		redis.del(key);
	}
	/* (non-Javadoc)
	 * @see org.jsirenia.queue.RedisQueueI#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return Objects.equal(redis.llen(key),0);
	}
	/* (non-Javadoc)
	 * @see org.jsirenia.queue.RedisQueueI#toArray()
	 */
	@Override
	public Object[] toArray() {
		return redis.lrange(key, 0, -1).toArray();
	}
	/* (non-Javadoc)
	 * @see org.jsirenia.queue.RedisQueueI#offer(java.lang.String)
	 */
	@Override
	public boolean offer(String e) {
		redis.lpush(key,e);
		return true;
	}
	/* (non-Javadoc)
	 * @see org.jsirenia.queue.RedisQueueI#peek()
	 */
	@Override
	public String peek() {
		return redis.lrange(key, 0, 0).get(0);
	}
}

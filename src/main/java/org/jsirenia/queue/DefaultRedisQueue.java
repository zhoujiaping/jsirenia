package org.jsirenia.queue;

import java.util.Arrays;
import java.util.List;

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
	public int add(String... source) {
		if(source == null){
			throw new RedisQueueException("参数不能为空");
		}
		if(source.length==0){
			return 0;
		}
		int successCount = source.length;
		if(capacity>0){
			Long len = redis.llen(key);
			int overSize =  (int) (len+source.length - capacity);
			if(overSize > 0){
				successCount = source.length - overSize;
				String[] target = new String[successCount];
				System.arraycopy(source, 0, target, 0, successCount);
				redis.lpush(key, target);
				return successCount;
			}else{
				redis.lpush(key, source);
			}
		}else{
			redis.lpush(key, source);
		}
		return successCount;
	}
	@Override
	public void clear() {
		redis.del(key);
	}
	@Override
	public boolean isEmpty() {
		return Objects.equal(redis.llen(key),0);
	}
	@Override
	public int size() {
		return redis.llen(key).intValue();
	}
	@Override
	public String peek() {
		return redis.lrange(key, 0, 0).get(0);
	}
	@Override
	public List<String> peek(int count) {
		return redis.lrange(key, 0, count-1);
	}
	@Override
	public List<String> take(int count){
		String script = "local msgList = redis.call('lrange', KEYS[1],ARGV[1],ARGV[2]); redis.call('ltrim',KEYS[1],ARGV[3],ARGV[4]); return msgList";
		Object result = redis.eval(script, Arrays.asList(key), Arrays.asList("0", String.valueOf(count-1), ""+count, "-1"));
		return (List<String>) result;
	}
	@Override
	public String take(){
		return redis.rpop(key);
	}
}

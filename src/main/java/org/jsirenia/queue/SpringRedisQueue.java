package org.jsirenia.queue;

import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;

/**
 * redis隊列 特點： 簡單 快速 沒有消息確認機制
 * 
 * @author zhoujiaping
 */
public class SpringRedisQueue implements RedisQueue {
	private RedisTemplate<String, String> redisTemplate;
	private String key;
	private int capacity;

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public static class RedisQueueException extends RuntimeException {
		public RedisQueueException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}

	@Override
	public int add(final String... source) {
		return redisTemplate.execute(new RedisCallback<Integer>() {
			@Override
			public Integer doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				if (source == null) {
					throw new RedisQueueException("参数不能为空");
				}
				if (source.length == 0) {
					return 0;
				}
				int successCount = source.length;
				if (capacity > 0) {
					Long len = redis.llen(key);
					int overSize = (int) (len + source.length - capacity);
					if (overSize > 0) {
						successCount = source.length - overSize;
						String[] target = new String[successCount];
						System.arraycopy(source, 0, target, 0, successCount);
						redis.lpush(key, target);
						return successCount;
					} else {
						redis.lpush(key, source);
					}
				} else {
					redis.lpush(key, source);
				}
				return successCount;
			}
		});
	}

	@Override
	public void clear() {
		redisTemplate.execute(new RedisCallback<Integer>() {
			@Override
			public Integer doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				redis.del(key);
				return null;
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				Long len = redis.llen(key);
				return len == 0;
			}
		});
	}

	@Override
	public int size() {
		return redisTemplate.execute(new RedisCallback<Integer>() {
			@Override
			public Integer doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				return redis.llen(key).intValue();
			}
		});
	}

	@Override
	public String peek() {
		return redisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				return redis.lrange(key, 0, 0).get(0);
			}
		});
	}

	@Override
	public List<String> peek(final int count) {
		return redisTemplate.execute(new RedisCallback<List<String>>() {
			@Override
			public List<String> doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				return redis.lrange(key, 0, count - 1);
			}
		});
	}

	@Override
	public List<String> take(final int count) {
		return redisTemplate.execute(new RedisCallback<List<String>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<String> doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				String script = "local msgList = redis.call('lrange', KEYS[1],ARGV[1],ARGV[2]); redis.call('ltrim',KEYS[1],ARGV[3],ARGV[4]); return msgList";
				Object result = redis.eval(script, Arrays.asList(key),
						Arrays.asList("0", String.valueOf(count - 1), "" + count, "-1"));
				return (List<String>) result;
			}
		});
	}

	@Override
	public String take() {
		return redisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				return redis.rpop(key);
			}
		});
	}
}

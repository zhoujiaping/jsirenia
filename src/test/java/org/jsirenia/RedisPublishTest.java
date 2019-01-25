package org.jsirenia;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis 发布订阅
 * 还可以使用  支持模式的发布订阅
 */
public class RedisPublishTest {
	Jedis redis = new Jedis("localhost", 6379);

	@Test
	public void testSubscribe(){
		JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				System.out.println(channel);
				System.out.println(message);
			}
		};
		String channels = "a";
		redis.subscribe(jedisPubSub, channels );
	}
	@Test
	public void testPublish(){
		Long count = redis.publish("a", "hello");
		System.out.println(count);
	}
}

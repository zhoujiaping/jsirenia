package org.jsirenia;

import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * Redis 队列
 */
public class RedisQueueTest {
	Jedis redis = new Jedis("localhost", 6379);
	String topic = "/app/test";

	@Test
	public void testPush(){
		String msg = "one";
		Long total = redis.lpush(topic, msg);
		System.out.println(total);
	}
	@Test
	public void testPop(){
		String msg = redis.lpop(topic);
		System.out.println(msg);
	}
}

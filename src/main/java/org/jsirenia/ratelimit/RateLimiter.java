package org.jsirenia.ratelimit;

import java.io.FileNotFoundException;
import java.util.List;

import org.jsirenia.file.JFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.google.common.collect.Lists;

import redis.clients.jedis.Jedis;
/**
 * 限流器
 */
public class RateLimiter {
	protected Logger logger = LoggerFactory.getLogger(RateLimiter.class);

	private Jedis jedis;
	private String script;
	private static List<String> emptyList = Lists.newArrayList();

	public RateLimiter(Jedis jedis){
		this.jedis = jedis;
		String file = "classpath:ratelimit2.lua";
		try {
			script = new JFile(ResourceUtils.getFile(file)).text();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 申请令牌
	 * @return
	 */
	public boolean applyToken(String service){
		Object result = jedis.eval(script, emptyList,
				Lists.newArrayList(service, System.currentTimeMillis()/1000 + ""));
		return result.equals(1);
	}
	/**
	 * 清理token
	 * @param args
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public void clear(String service){
		jedis.del(service+":tokens");
		jedis.del(service+":timestamp");
	}
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Jedis jedis = new Jedis("localhost", 6379);
		try {
			//清理环境
			jedis.del("myservice:tokens");
			jedis.del("myservice:timestamp");
			String script = new JFile(ResourceUtils.getFile("classpath:ratelimit2.lua")).text();
			for(int i=0;i<500;i++){
				Object result = jedis.eval(script, Lists.newArrayList(),
						Lists.newArrayList("myservice", System.currentTimeMillis()/1000 + ""));
				System.out.println(result);
				Thread.sleep(1000);
			}
		} finally {
			jedis.close();
		}
	}
}

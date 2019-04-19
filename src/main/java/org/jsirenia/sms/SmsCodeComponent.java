package org.jsirenia.sms;

import javax.annotation.Resource;

import org.jsirenia.exception.ServiceException;
import org.jsirenia.string.JString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import redis.clients.jedis.Jedis;

/**
 * 短信验证码服务
 */
@Component
public class SmsCodeComponent {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private RedisTemplate<String, String> redisTemplate;
	public final String COUNT_SUFFIX = "#times";
	public final String LOCK_SUFFIX = "#lock";
	public final String CODE_INTERVAL_SUFFIX = "#interval";

	private int codeExpireSeconds = 60*2;// 过期时间,即验证码有效期2分钟
	private int codeIntervalSeconds = 60;// 短信发送间隔，60秒
	private int lockSeconds = 60*60;//连续5次验证失败的锁定时间，60分钟之内不能再次发送验证码
	private int maxFailTimes = 5;//最大连续失败次数

	public void config(int codeExpireSeconds, int codeIntervalSeconds, int lockSeconds, int maxFailTimes) {
		this.codeExpireSeconds = codeExpireSeconds;
		this.codeIntervalSeconds = codeIntervalSeconds;
		this.lockSeconds = lockSeconds;
		this.maxFailTimes = maxFailTimes;
	}

	/**
	 * 
	 * @param id
	 *            redis中保存验证码的key
	 * @param code
	 *            验证码
	 * @return true：保存验证码成功，false：保存验证码失败
	 * @throws ServiceException
	 *             获取验证码太频繁/由于失败次数达到最大值已被锁定，暂时不能获取验证码
	 */
	public Result saveCode(final String id, final String code) {
		return redisTemplate.execute(new RedisCallback<Result>() {
			@Override
			public Result doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				// 判断是否被锁定
				Long lockttl = redis.ttl(id + LOCK_SUFFIX);
				if (lockttl > 0) {
					Double left = Math.ceil(lockttl * 1.0 / 60);
					logger.info("验证失败次数连续达到{}次，{}分钟之内不能获取验证码。key={},code={}",
							new Object[] { maxFailTimes, left.intValue(), id, code });
					String msg = JString.render("验证失败次数连续达到{}次，{}分钟之内不能获取验证码", maxFailTimes, left.intValue());
					return new Result(Result.LOCKED, msg);
				}
				Long ttl = redis.pttl(id + CODE_INTERVAL_SUFFIX);
				if (ttl > 0) {
					logger.info("获取验证码太频繁。key={},code={}", id, code);
					return new Result(Result.FREQUENT, "获取验证码太频繁");
				}
				// 故意设置从1开始，使后面校验验证码时，可以根据值域判断不同的结果。顺序无关
				String result1 = redis.setex(id + COUNT_SUFFIX, codeExpireSeconds, "1");
				String result2 = redis.setex(id, codeExpireSeconds, code);
				String result3 = redis.setex(id + CODE_INTERVAL_SUFFIX, codeIntervalSeconds, "");
				// 这里不保证原子性也没问题
				if ("OK".equals(result1) && "OK".equals(result2) && "OK".equals(result3)) {
					return new Result(Result.OK, "生成验证码成功");
				}
				return new Result(Result.FAILED, "生成验证码失败");
			}
		});
	}

	/**
	 * 
	 * @param id
	 *            redis中保存校验码的key
	 * @param code
	 *            校验码
	 * @return true:校验成功，false:校验失败。调用者不用关心校验码的保存与删除。
	 * @throws ServiceException
	 *             验证码已过期/验证码失败已达最大次数
	 */
	public Result verifyCode(final String id, final String code) {
		return redisTemplate.execute(new RedisCallback<Result>() {
			@Override
			public Result doInRedis(RedisConnection conn) throws DataAccessException {
				Jedis redis = (Jedis) conn.getNativeConnection();
				Long lockttl = redis.ttl(id + LOCK_SUFFIX);
				if (lockttl > 0) {
					Double left = Math.ceil(lockttl * 1.0 / 60);
					logger.info("验证码失败已达{}次，请{}分钟后再次获取验证码。key={},code={}",
							new Object[] { maxFailTimes, left.intValue(), id, code });
					String msg = JString.render("验证码失败已达{}次，请{}分钟后再次获取验证码", maxFailTimes, left.intValue());
					return new Result(Result.LOCKED, msg);
				}
				/*
				 * 使用lua脚本，保证执行的原子性。//返回值含义
				 * -1：验证码已过期；0：验证码验证成功，并且删除不成功（几乎不可能的情况）
				 * 1：验证码验证成功，并且被成功删除；大于1：返回失败次数加1。
				 */
				String script = "if redis.call('get', KEYS[2]) then" + // 如果次数的key存在
				"	redis.call('incr', KEYS[2])" + // 次数加1
				"	if redis.call('get', KEYS[1]) == ARGV[1] then" + // 如果校验码和传进来的相等
				"		redis.call('del', KEYS[2])" + // 删除记录次数的key
				"		return redis.call('del', KEYS[1])" + // 删除记录校验码的key
				"	else " + "		return redis.call('get', KEYS[2]) " + // 校验码不相等,返回次数
				"	end " + "else " + "	return -1 " + // 次数的key不存在，要么不存在，要么已过期
				"end";

				Object result = redis.eval(script, Lists.newArrayList(id, id + COUNT_SUFFIX), Lists.newArrayList(code));
				Integer res = Integer.parseInt(result.toString());// 转成String然后再转数值，避免对eval返回各种类型的判断。
				if (res == 1) {
					return new Result(Result.OK, "校验验证码成功");// 校验码相等
				} else if (res <= 0) {
					logger.info("验证码已过期。key={},code={}", new Object[] { id, code });
					String msg = "验证码已过期";
					return new Result(Result.EXPIRED, msg);
				} else {
					if (res > maxFailTimes) {
						logger.info("验证失败次数连续达到{}次", res - 1);
						// 锁定
						redis.setex(id + LOCK_SUFFIX, lockSeconds, "");
						Double left = Math.ceil(lockSeconds * 1.0 / 60);
						logger.info("验证码失败已达{}次，请{}分钟后再次获取验证码。key={},code={}",
								new Object[] { res - 1, left.intValue(), id, code });
						String msg = JString.render("验证码失败已达{}次，请{}分钟后再次获取验证码", res - 1, left.intValue());
						return new Result(Result.LOCKED, msg);
					}
					return new Result(Result.FAILED, "验证码不匹配");// 校验码不匹配并且未达到最大次数
				}
			}
		});
	}

	public static class Result {
		public static String OK = "OK";
		public static String FAILED = "FAILED";
		public static String FREQUENT = "FREQUENT";
		public static String EXPIRED = "EXPIRED";
		public static String LOCKED = "LOCKED";
		private String code;
		private String msg;

		public Result(String code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}

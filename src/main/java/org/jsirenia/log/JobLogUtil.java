package org.jsirenia.log;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 对于跑批任务的日志，通常由于一些原因，查日志不方便。 1、线程池。无法根据线程id区分不同的批次。
 * 2、批量处理。循环中处理数据，无法区分日志是针对哪个数据主体。 该工具类就是要解决这个问题，使跑批任务的日志也非常方便查看，非常方便定位问题。
 * 能实现的效果：（要配合其他实践，配合NamedThreadFactory，MDC）
 * 根据日志可以区分不同job、同一job的不同task、同一task的不同data。
 *
 */
public class JobLogUtil {
	private static final Logger logger = LoggerFactory.getLogger(com.sfpay.msfs.jyd.common.util.JobLogUtil.class);
	private static AtomicInteger taskid = new AtomicInteger(0);
	private static AtomicInteger dataid = new AtomicInteger(0);
	private static IntUnaryOperator updateFunction = operand -> {
		if (operand >= Integer.MAX_VALUE) {
			return 0;
		}
		return operand + 1;
	};
	private static final String UID_KEY = "uid";
	private static final String OUID_KEY = com.sfpay.msfs.jyd.common.util.JobLogUtil.class.getSimpleName() + "-uid";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	/**
	 * 有一些job，不是通过调度平台发起的，而是业务模块通过spring或者quartz实现的。 这时候MDC中没有uid。
	 * 
	 * @param cb
	 */
	public static void withJob(Callback cb) {
		String uid = MDC.get(UID_KEY);
		if (StringUtils.isEmpty(uid)) {
			try {
				String datetime = LocalDateTime.now().format(formatter);
				MDC.put(UID_KEY, datetime + UUID.randomUUID().toString().replaceAll("-", ""));
				cb.apply();
			} finally {
				MDC.remove(UID_KEY);
			}
		} else {
			cb.apply();
		}
	}

	/**
	 * 给日志上下文添加信息 建议taskName取2-5个字符的简称 不保证同一个job的taskid按顺序增长，只保证递增。
	 */
	public static void withTask(String taskName, Callback cb) {
		try {
			saveUid();
			// 如果taskName是空，就采用计数的方式
			if (StringUtils.isEmpty(taskName)) {
				int id = taskid.getAndUpdate(updateFunction);
				taskName = "" + id;
			}
			String uid = MDC.get(UID_KEY);
			if (StringUtils.isEmpty(uid)) {
				logger.warn("跑批日志的uid为空，请检查配置。taskName=【{}】", taskName);
			} else {
				MDC.put(UID_KEY, uid + "-T" + taskName);
			}
			cb.apply();
		} finally {
			recoverUid();
		}
	}

	/**
	 * 将原来的uid保存到另一个key（JobLogUtil-uid）中
	 */
	private static void saveUid() {
		String uid = MDC.get(UID_KEY);
		if (!StringUtils.isEmpty(uid)) {
			MDC.put(OUID_KEY, uid);
		}
	}

	/**
	 * 将保存到JobLogUtil-uid中的uid，还原到原来的key（uid）中
	 */
	private static void recoverUid() {
		String ouid = MDC.get(OUID_KEY);
		if (!StringUtils.isEmpty(ouid)) {
			MDC.put(UID_KEY, ouid);
			MDC.remove(OUID_KEY);
		}
	}

	public static void withTask(Callback cb) {
		withTask(null, cb);
	}

	/**
	 * 给日志上下文添加信息 建议dataId取主键值
	 */
	public static void withData(String dataId, Callback cb) {
		try {
			saveUid();
			// 如果dataId是空，就采用计数的方式
			if (StringUtils.isEmpty(dataId)) {
				int id = dataid.getAndUpdate(updateFunction);
				dataId = "" + id;
			}
			String uid = MDC.get("uid");
			if (StringUtils.isEmpty(uid)) {
				logger.warn("跑批日志的uid为空，请检查配置。dataId=【{}】", dataId);
			} else {
				MDC.put(UID_KEY, uid + "-D" + dataId);
			}
			cb.apply();
		} finally {
			recoverUid();
		}
	}

	public static void withData(Callback cb) {
		withData(null, cb);
	}

	public interface Callback {
		void apply();
	}
}

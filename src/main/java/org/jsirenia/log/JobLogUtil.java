package org.jsirenia.log;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
/**
 * 对于跑批任务的日志，通常由于一些原因，查日志不方便。
 * 1、线程池。无法根据线程id区分不同的批次。
 * 2、批量处理。循环中处理数据，无法区分日志是针对哪个数据主体。
 * 该工具类就是要解决这个问题，使跑批任务的日志也非常方便查看，非常方便定位问题。
 * 能实现的效果：（要配合其他实践，配合NamedThreadFactory，MDC）
 * 根据日志可以区分不同job、同一job的不同task、同一task的不同data。
 *
 */
public class JobLogUtil {
	private static final Logger logger = LoggerFactory.getLogger(JobLogUtil.class);
	private static AtomicInteger taskid = new AtomicInteger(0);
	private static AtomicInteger dataid = new AtomicInteger(0);
	private static IntUnaryOperator updateFunction = new IntUnaryOperator(){
		@Override
		public int applyAsInt(int operand) {
			if(operand>=Integer.MAX_VALUE){
				return 0;
			}
			return operand+1;
		}
	};
	private static final String uidKey = "uid";
	private static final String ouidKey = JobLogUtil.class.getSimpleName()+"-uid";

	/**
	 * 给日志上下文添加信息 建议taskName取2-5个字符的简称 不保证同一个job的taskid按顺序增长，只保证递增。
	 */
	public static void withTask(String taskName,Callback cb) {
		try{
			saveUid();
			// 如果taskName是空，就采用计数的方式
			if (StringUtils.isEmpty(taskName)) {
				int id = taskid.getAndUpdate(updateFunction );
				taskName = "task" + id;
			}
			String uid = MDC.get(uidKey);
			if (StringUtils.isEmpty(uid)) {
				logger.warn("跑批日志的uid为空，请检查配置。taskName=【{}】", taskName);
			} else {
				MDC.put(uidKey, uid + "-" + taskName);
			}
			cb.apply();
		}finally{
			recoverUid();
		}
	}
	/**
	 * 将原来的uid保存到另一个key（JobLogUtil-uid）中
	 */
	private static void saveUid(){
		String ouid= MDC.get(ouidKey);
		//为空，则说明还没保存过。不为空说明已经保存过。已经保存过不能再次保存，不然会有问题。
		if(StringUtils.isEmpty(ouid)){
			String uid = MDC.get(uidKey);
			if(!StringUtils.isEmpty(uid)){
				MDC.put(ouidKey, ouid);
			}
		}
	}
	/**
	 * 将保存到JobLogUtil-uid中的uid，还原到原来的key（uid）中
	 */
	private static void recoverUid(){
		String ouid= MDC.get(ouidKey);
		if(!StringUtils.isEmpty(ouid)){
			MDC.put(uidKey, ouid);
			MDC.remove(ouidKey);
		}
	}
	public static void withTask(Callback cb) {
		withTask(null,cb);
	}
	
	/**
	 * 给日志上下文添加信息 建议dataId取主键值
	 */
	public static void withData(String dataId,Callback cb) {
		try{
			saveUid();
			// 如果dataId是空，就采用计数的方式
			if (StringUtils.isEmpty(dataId)) {
				int id = dataid.getAndUpdate(updateFunction );
				dataId = "data"+id;
			}
			String uid = MDC.get("uid");
			if (StringUtils.isEmpty(uid)) {
				logger.warn("跑批日志的uid为空，请检查配置。dataId=【{}】", dataId);
			} else {
				MDC.put(uidKey, uid + "-" + dataId);
			}
		}finally{
			recoverUid();
		}
	}
	public static void withData(Callback cb) {
		withData(null,cb);
	}
	public interface Callback {
		void apply();
	}
}

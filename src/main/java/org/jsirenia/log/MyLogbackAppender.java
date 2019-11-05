package org.jsirenia.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.jsirenia.exception.ServiceException;

/**
 *
 在logback-inc.xml中添加
 <appender name="myLogbackAppender" class="com.xx.exception.MyLogbackAppender">
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<level>ERROR</level>
			<onMatch>ACCEPT</onMatch> 
			<onMismatch>DENY</onMismatch>
		</filter>
	</appender>
 和
 <root level="info">
		<appender-ref ref="myLogbackAppender" />
 </root>
 如果想使用异步日志，可以添加
 	<appender name ="ASYNC-myLogbackAppender" class= "ch.qos.logback.classic.AsyncAppender"> 
        <discardingThreshold>0</discardingThreshold> 
        <queueSize>1024</queueSize> 
        <appender-ref ref ="myLogbackAppender" /> 
    </appender>
    并且
     <root level="info">
		<appender-ref ref="myLogbackAppender" />
 </root>
 */
public class MyLogbackAppender<E> extends UnsynchronizedAppenderBase<E>{
	public MyLogbackAppender(){
	}
	private void sendNotify(Throwable e) {
		System.out.println("发送通知"+e.getMessage());
	}
	@Override
	protected void append(E eventObject) {
		LoggingEvent event = (LoggingEvent) eventObject;
		ThrowableProxy tp = (ThrowableProxy) event.getThrowableProxy();
		Throwable e = tp.getThrowable();
		if(e instanceof ServiceException){
			ServiceException se = (ServiceException) e;
			Throwable c = se.getCause();
			if(c != null){
				sendNotify(se);
			}
		}else{
			sendNotify(e);
		}
	}
}

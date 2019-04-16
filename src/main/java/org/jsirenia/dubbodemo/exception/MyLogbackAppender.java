package com.sf.exception;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

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

package org.jsirenia.exception;
/**
 * 某个模块的异常码
 * 为什么不用枚举？因为枚举不方便拓展，不能用继承，不方便实现通用性。
 */
public interface Demo2ExceptionCode{
	String SYSTEM_ERROR = "DEMO.9999";
	String USER_NOT_FOUND = "DEMO.1111";
	String SESSION_TIMEOUT = "DEMO.2222";
}

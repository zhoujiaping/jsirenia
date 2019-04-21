package org.jsirenia.exception.demo;
/**
 * 某个模块的异常码
 * 为什么不用枚举？因为枚举不方便拓展，不能用继承，不方便实现通用性。
 */
public interface ResultCode{
	String SUCCESS = "200";
	String SYSTEM_ERROR = "500";
}

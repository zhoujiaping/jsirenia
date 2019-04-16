package com.sf;

import com.sf.exception.ServiceException;

public interface DemoService {
	/**
	 * 无异常情况
	 * 预期：
	 * provider不打印error，不发送通知
	 * consumer不打印error，不发送通知
	 * @param text
	 * @return
	 * @throws ServiceException
	 */
    String testNoException(String text) throws ServiceException;
    /**
     * ServiceException不带cause
     * 预期：
	 * provider不打印error，不发送通知
	 * consumer不打印error，不发送通知
     * @param text
     * @return
     * @throws ServiceException
     */
    String testServiceException(String text) throws ServiceException;
    /**
     * ServiceException带cause
     * 预期：
	 * provider打印error，发送通知
	 * consumer不打印error，不发送通知
     * @param text
     * @return
     * @throws ServiceException
     */
    String testServiceExceptionWithCause(String text) throws ServiceException;
    /**
     * RuntimeException
     * 预期：
	 * provider打印error，发送通知
	 * consumer打印error，不发送通知
     * @param text
     * @return
     * @throws ServiceException
     */
    String testRuntimeException(String text) throws ServiceException;
    /**
     * NullPointerException
     * 预期：
	 * provider打印error，发送通知
	 * consumer打印error，不发送通知
     * @param text
     * @return
     * @throws ServiceException
     */
    String testNullPointerException(String text) throws ServiceException;
}
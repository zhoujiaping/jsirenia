package com.sf;

import com.sf.exception.ServiceException;

public class DemoServiceImpl implements DemoService {

	@Override
	public String testNoException(String text) throws ServiceException {
		return text;
	}

	@Override
	public String testServiceException(String text) throws ServiceException {
		throw new ServiceException("E001","用户名不合法");
	}

	@Override
	public String testServiceExceptionWithCause(String text) throws ServiceException {
		throw new ServiceException("E001",text,new RuntimeException("数据异常"));
	}

	@Override
	public String testRuntimeException(String text) throws ServiceException {
		throw new RuntimeException("系统异常");
	}

	@Override
	public String testNullPointerException(String text) throws ServiceException {
		throw new NullPointerException();
	}

}
package org.jsirenia.dubbo;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 *
 */
public class StringExtUtil {
	public static String overlayPwd(String pwd) {
		return "";
	}
	/**
	 * 证件号脱敏，只展示前3后4位。
	 * @param certifyNo
	 * @return
	 */
	public static String overlayCertifyNo(String certifyNo) {
		if(certifyNo==null){
			return null;
		}
		int len = certifyNo.length();
		return StringUtils.overlay(certifyNo, StringUtils.repeat("*", len-7), 3, len-4);
	}
	public static String overlayMobileNo(String mobileNo) {
		if(mobileNo==null){
			return null;
		}
		int len = mobileNo.length();
		return StringUtils.overlay(mobileNo, StringUtils.repeat("*", 7), len-11, len-4);
	}
	/**
	 * @param name 中文姓名
	 * @return
	 */
	public static String overlayUserNameCN(String name) {
		if(name==null){
			return null;
		}
		return StringUtils.overlay(name, "*", 0, 1);
	}
	public static void main(String[] args) {
		System.out.println(overlayCertifyNo("432112457859568475"));//"432***********8475"
		System.out.println(overlayCertifyNo(""));//""
		System.out.println(overlayCertifyNo("12"));//""
		System.out.println(overlayCertifyNo("123456789"));//"123**6789"
		System.out.println(overlayCertifyNo(null));//null
		
		System.out.println(overlayMobileNo("13412345678"));//*******5678
		System.out.println(overlayMobileNo(""));//*******
		System.out.println(overlayMobileNo(null));//null
		
		System.out.println(overlayUserNameCN("方便面"));
	}
}

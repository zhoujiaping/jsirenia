package org.jsirenia.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class MyTest {
	public static void main(String[] args) throws Exception {
		test();
	}
	public static void test() throws Exception{
		//公钥的base64
		String data = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA30pnfUBSiocu4X0gbneapKjLBIyhZl7Q2/Eaydb75BA25gzgXzvPH8e09zzrFVu/tosThjN4beS7ltZK0CuZlxKXcTmnhQal21eG+Z4L7DlAQz4Kl32aU2B4IDkLnB6J8ALPv0x7dcIa09FwSgeYJw2JIX+1UPeg3D9Sk1C57lgeDBNyFZxbiiLKRogTLhsC9jgP5pUzBN5Sg/Wompp0ybAp0scjmBwZI1EtnBPcr01C9LZSCWXFF3BVdO2WBCkRJh2vnCqWs4hH+Ag0QqWXIqVXccxd0iN4ac1aYnpCRLjz7akdg+Iw4KVl6ppKtbMyei8kpp9POG6i+OxTr1FCTwIDAQAB";
		//公钥的字节数组
		byte[] publicKeyBuf = Base64.getDecoder().decode(data);
		//公钥加密对称密钥
		byte[] buf = RSAUtil.encryptByPublicKey("123456".getBytes(), publicKeyBuf, 2048);
		//转成base64
		String res = Base64.getEncoder().encodeToString(buf);
		System.out.println(res);
	}
	@Test
	public void test2(){
		//响应内容
		String s = "5EKLkk0Eo+d9Vk+tyLfJ2Eq/iLyQCjhr8Bi9M8wlmfg=";
		//解密
		String res = AESUtil.decryptBase64(s, "123456");
		System.out.println(res);
		String data = "{\"args\":[\"a\",\"b\"]}";
		String md5 = MD5Util.md5Hex(data);
		//加密data
		byte[] buf = AESUtil.encrypt(data.getBytes(Charset.forName("utf-8")), "123456");
		System.out.println(md5);
		System.out.println(Base64.getEncoder().encodeToString(buf));
	}
	@Test
	public void test3() throws UnsupportedEncodingException{
		//响应内容
		String data = "bATW3vqLvnNe7qwrZGdEuS9ZLX2BGLC2jlBN42xpmEJeL4qLe7xJ55Kl6JFwNBoopL24TQ3Yq3JaBqeRdz4jbtMv8nbe0Kt+X6msUe3Ltp3T1uNWtzyxHjdCDVd2HQlo47CNzukwccXICel11zILA6uDKKiVdAQzcARAR7xxQSn5LUUcz/aZDAu3BBJge/nhc4OfSh90wwYj1TRnl5XVHg==";
		//解密
		String res = AESUtil.decryptBase64(data, "123456");
		System.out.println(res);
	}
	@Test
	public void test4() throws UnsupportedEncodingException{
		//响应内容
		String data = "eA0bPdjKN/HPEcyReo74LSQAoND6M7ROUtHU/iKLYMvjtk1VI8jP9VklXZxzrY5u+Fg3PJJ4OKtO5tdB6xRFjwWP89Ni2ooizhN7Uwa50MSkvbhNDdircloGp5F3PiNuCjUj6YVrN9wtTW9d2WquM1yKepK7qfGaSfQMkyiafQ6St20k4rRO7EXoks2BSvA0xRHhkk+dkynO479q5jZiDlqheL4lrG04Afsaof8uU1A=";
		//解密
		String res = AESUtil.decryptBase64(data, "123456");
		System.out.println(res);
	}
}

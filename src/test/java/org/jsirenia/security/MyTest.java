package org.jsirenia.security;

import java.security.PublicKey;
import java.util.Base64;

import org.junit.Test;

public class MyTest {
	public static void main(String[] args) throws Exception {
		test();
	}
	public static void test() throws Exception{
		//公钥的base64
		String data = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA30pnfUBSiocu4X0gbneapKjLBIyhZl7Q2/Eaydb75BA25gzgXzvPH8e09zzrFVu/tosThjN4beS7ltZK0CuZlxKXcTmnhQal21eG+Z4L7DlAQz4Kl32aU2B4IDkLnB6J8ALPv0x7dcIa09FwSgeYJw2JIX+1UPeg3D9Sk1C57lgeDBNyFZxbiiLKRogTLhsC9jgP5pUzBN5Sg/Wompp0ybAp0scjmBwZI1EtnBPcr01C9LZSCWXFF3BVdO2WBCkRJh2vnCqWs4hH+Ag0QqWXIqVXccxd0iN4ac1aYnpCRLjz7akdg+Iw4KVl6ppKtbMyei8kpp9POG6i+OxTr1FCTwIDAQAB";
		//公钥的字节数组
		byte[] publicKeyBuf = Base64.getDecoder().decode(data);
		PublicKey publicKey = RSAUtil.getPublicKey(publicKeyBuf);
		//公钥加密对称密钥
		byte[] buf = RSAUtil.encryptByPublicKey("123456".getBytes(), publicKey);
		//转成base64
		String res = Base64.getEncoder().encodeToString(buf);
		System.out.println(res);
	}
	@Test
	public void test1(){
		System.out.println(MD5Util.md5AsHex("1234abc".getBytes()));
	}
}

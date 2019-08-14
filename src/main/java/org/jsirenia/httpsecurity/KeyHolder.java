package org.jsirenia.httpsecurity;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import org.jsirenia.http.URLUtil;
import org.jsirenia.security.KeyStoreUtil;
import org.jsirenia.security.RSAUtil;
import org.jsirenia.security.SignUtil;
import org.springframework.util.ResourceUtils;

public class KeyHolder {
	//public static KeyPair pair;
	
	public static PrivateKey privateKey;
	public static PublicKey publicKey;
	static{
			String storepass = "123456";
			String keypass = "123456";
			String type = "JKS";
			File file;
			try {
				//file = ResourceUtils.getFile("classpath:nofilter/mykey.keystore");
				file = ResourceUtils.getFile("d:/test/mykey.keystore");
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			KeyStore ks = KeyStoreUtil.loadKeyStore(file, storepass, type);
			String keyAlias = "tomcat";
			privateKey = RSAUtil.getPrivateKey(ks, keyAlias, keypass);
			publicKey = RSAUtil.getPublicKey(ks, keyAlias, keypass);
			//pair = KeyUtil.generateKeyPair("RSA",size );
			//throw new RuntimeException(e);
	}
	public static void main(String[] args) throws Exception {
		byte[] pk = KeyHolder.publicKey.getEncoded();
		String key = Base64.getEncoder().encodeToString(pk);
		System.out.println(key);
		String data = "SkCbza7Oz3CN1O6wIeFvDSN/rxMzgsJQ68Ow/vTaqeVF9fZyQHUJ/Q8GNhG1cUN5c0QLJ6Eaj8W4j3mIUSJhafcShd2HGDW8DVbaYMkExnyiOAz5bKbN/yxc97IO10rYt0valpmU3VQ8iqsLfRoc3l5aSQ6G/3qpniKN/y+tbEI2Rp/Ve8+WDCxRV0qfFN//irebMWDzDwZBfSkAOgexZO8XX5JqyR6n0GH/RCQnFJzXLzvFxSdzn6zw39NvOHagp6R6WKkZZcnV3TNzMqmO2cF9bK0JaawnBFSbKVVigLnm2BX1JrB+8JyNCVGPHPVJvbEeAxJuxLyAsYlS6AgN+Q==";
		pk = KeyHolder.privateKey.getEncoded();
		System.out.println(Base64.getEncoder().encodeToString(pk));
		byte[] decrypted = RSAUtil.decryptByPrivateKey(Base64.getDecoder().decode(data), KeyHolder.privateKey);
		System.out.println(new String(decrypted));
		
		String signBase64 = SignUtil.signBase64("147258369", privateKey);
		System.out.println(signBase64);
	}
}

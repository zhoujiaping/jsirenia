package org.jsirenia.httpsecurity;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.jsirenia.security.KeyStoreUtil;
import org.jsirenia.security.KeyUtil;
import org.springframework.util.ResourceUtils;

public class KeyHolder {
	//public static KeyPair pair;
	
	public static PrivateKey privateKey;
	public static PublicKey publicKey;
	public static int size = 2048;
	static{
			String storepass = "123456";
			String keypass = "123456";
			String type = "JKS";
			File file;
			try {
				file = ResourceUtils.getFile("classpath:nofilter/mykey.keystore");
				//file = ResourceUtils.getFile("d:/test/mykey.keystore");
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			KeyStore ks = KeyStoreUtil.loadKeyStore(file, storepass, type);
			String keyAlias = "tomcat";
			privateKey = KeyUtil.getPrivateKey(ks, keyAlias, keypass);
			publicKey = KeyUtil.getPublicKey(ks, keyAlias, keypass);
			//pair = KeyUtil.generateKeyPair("RSA",size );
			//throw new RuntimeException(e);
	}
}

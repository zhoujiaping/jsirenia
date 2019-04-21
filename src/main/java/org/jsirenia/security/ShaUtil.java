package org.jsirenia.security;

import java.security.MessageDigest;
import java.util.Base64;

public class ShaUtil {
	public static String shaAsHex(byte[] data){
		return HexUtil.toHexString(sha(data));
	}
	public static String shaAsBase64(byte[] data){
		return Base64.getEncoder().encodeToString(sha(data));
	}
	public static byte[] sha(byte[] data){
		try{
			MessageDigest md = MessageDigest.getInstance("SHA256");
			return md.digest(data);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}

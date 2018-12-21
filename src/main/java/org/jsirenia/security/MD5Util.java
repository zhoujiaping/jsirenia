package org.jsirenia.security;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util{
	private static final Charset charset = Charset.forName("utf-8");

	public static MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] md5(byte[] data) {
		return getDigest().digest(data);
	}


	public static byte[] md5(String data){
		return md5(data.getBytes(charset));
	}

	public static String md5Hex(byte[] data) {
		return HexUtil.toHexString(md5(data));
	}

	public static String md5Hex(String data) {
		return HexUtil.toHexString(md5(data));
	}

	public static byte[] md5(byte[] data, byte[] salt) {
		MessageDigest md5 = getDigest();
		md5.update(data);
		return md5.digest(salt);
	}

	/**
	 * 既然调用了带盐的md5方法，那么必然不希望盐失去作用，不希望退化到没有加盐的情况。
	 * 所以方法参数salt不能为null。
	 * @param data
	 * @param salt
	 * @return
	 */
	public static String md5Hex(String data, String salt) {
		return HexUtil.toHexString(md5(data.getBytes(charset),salt.getBytes(charset)));
	}
	public static void main(String[] args){
		String data = "123";
		String salt = "null";
		String res = md5Hex(data,salt);
		//res = md5Hex(data);
		System.out.println(res);

	}
}
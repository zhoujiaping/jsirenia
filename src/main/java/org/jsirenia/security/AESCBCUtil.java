package org.jsirenia.security;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * 为了简介及方便，对于密钥、偏移、签名等数据，使用hex编码。这样可读性好。
 * 对于被加密的data，可以使用base64编码，或者hex编码。
 */
public class AESCBCUtil {
	private static final String ALGORITHM = "AES";
	private static final String ALGORITHM_PROVIDER = "AES/CBC/PKCS5Padding"; // 算法/模式/补码方式
	// private static final String ALGORITHM_PROVIDER = "AES/CBC/NoPadding";
	// //算法/模式/补码方式

	public static SecretKey randomSecretKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			//random.setSeed(seed);不要使用seed，相同seed产生相同的结果
			kgen.init(128, random);
			// kgen.init(128, new SecureRandom(key));
			// 3.产生原始对称密钥
			SecretKey secretKey = kgen.generateKey();
			return secretKey;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static SecretKey toSecretKey(byte[] key){
		SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
		return secretKey;
	}
	
	public static IvParameterSpec toIvSpec(String ivHex) {
		IvParameterSpec ivParameterSpec = new IvParameterSpec(HexUtil.toByteArray(ivHex));
		return ivParameterSpec;
	}

	// 偏移量字符串必须是16位 当模式是CBC的时候必须设置偏移量
	public static String randomIvHex() throws NoSuchAlgorithmException {
		return HexUtil.randomHex(32);
	}

	public static byte[] encrypt(byte[] data, String keyHex, String ivHex) {
		return doAES(data, keyHex, Cipher.ENCRYPT_MODE, ivHex);
	}

	
	
	public static String encryptAsBase64(byte[] data, String keyHex, String ivHex) {
		byte[] buf = doAES(data, keyHex, Cipher.ENCRYPT_MODE, ivHex);
		return Base64.getEncoder().encodeToString(buf);
	}

	public static String encryptAsHex(byte[] data, String keyHex, String ivHex) {
		byte[] buf = doAES(data, keyHex, Cipher.ENCRYPT_MODE, ivHex);
		return HexUtil.toHexString(buf);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            数据
	 * @param key
	 *            密钥
	 * @return
	 */
	public static byte[] decrypt(byte[] data, String keyHex, String ivHex) {
		return doAES(data, keyHex, Cipher.DECRYPT_MODE, ivHex);
	}

	public static byte[] decryptBase64(String base64, String keyHex, String ivHex) {
		byte[] buf = Base64.getDecoder().decode(base64);
		return doAES(buf, keyHex, Cipher.DECRYPT_MODE, ivHex);
	}

	public static byte[] decryptHex(String hex, String keyHex, String ivHex) {
		byte[] buf = HexUtil.toByteArray(hex);
		return doAES(buf, keyHex, Cipher.DECRYPT_MODE, ivHex);
	}

	public static byte[] doAES(byte[] data, String keyHex, int mode, String ivHex) {
		return doAES(data, HexUtil.toByteArray(keyHex), mode, ivHex);
	}

	

	/**
	 * 加解密
	 * 
	 * @param data
	 *            数据
	 * @param key
	 *            密钥
	 * @param mode
	 *            加解密mode
	 * @return
	 */
	public static byte[] doAES(byte[] data, byte[] key, int mode, String ivHex) {
		try {
			// true 加密内容 false 解密内容
			// 1.构造密钥生成器，指定为AES算法,不区分大小写
			// KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			// 2.根据ecnodeRules规则初始化密钥生成器
			// 生成一个128位的随机源,根据传入的字节数组
			// SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// random.setSeed(key);
			// kgen.init(128, random);
			// kgen.init(128, new SecureRandom(key));
			// 3.产生原始对称密钥
			// SecretKey secretKey = kgen.generateKey();
			// 4.获得原始对称密钥的字节数组
			SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
			// byte[] enCodeFormat = secretKey.getEncoded();
			// 5.根据字节数组生成AES密钥
			// SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat,
			// ALGORITHM);
			// 6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance(ALGORITHM_PROVIDER);// 创建密码器
			// 7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			// cipher.init(mode, keySpec);// 初始化
			IvParameterSpec ivParameterSpec = toIvSpec(ivHex);
			cipher.init(mode, secretKey, ivParameterSpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		int size = 1024;
		// 随机生成AES加密的密钥
		String key = HexUtil.randomHex(32);
		String content = "{'repairPhone':'18547854787','customPhone':'12365478965','captchav':'58m7'}";
		System.out.println("加密前：" + content);
		System.out.println("加密密钥和解密密钥：" + key);
		String ivHex = randomIvHex();
		ivHex = "C74274C19D349391A0172B700F8F8BB6";
		System.out.println("iv："+ivHex);
		// 加密
		String encrypt = encryptAsBase64(content.getBytes("utf-8"), key, ivHex);
		System.out.println("加密后：" + encrypt);
		// 解密
		String decrypt = new String(decryptBase64(encrypt, key, ivHex),"utf-8");
		System.out.println("解密后：" + decrypt);
		// 生成rsa的密钥对
		KeyPair pair = RSAUtil.generateKeyPair("RSA", size);
		PrivateKey privateKey = pair.getPrivate();
		PublicKey publicKey = pair.getPublic();
		// 对AES的密钥加密
		byte[] ek = RSAUtil.encryptByPrivateKey(key.getBytes("utf-8"), privateKey.getEncoded(), size);
		System.out.println(Base64.getEncoder().encodeToString(ek));
		// 解密得到AES的密钥
		byte[] buf = RSAUtil.decryptByPublicKey(ek, publicKey.getEncoded(), size);
		System.out.println("===");
		System.out.println(new String(buf, "utf-8").equals(key));

	}
}

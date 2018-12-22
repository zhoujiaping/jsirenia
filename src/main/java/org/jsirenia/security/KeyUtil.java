package org.jsirenia.security;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class KeyUtil {
	  //用于生成随机密钥
    private static final char[] alphas =("0123456789"
            +"`~!@#$%^&*()-=_+[];',./{}|:<>?" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    public static String randomUUIDKey(){
    	return UUID.randomUUID().toString();
    }
	public static String randomKey(int size){
        char[] arr = new char[size];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int len = alphas.length;
        for(int i=0;i<size;i++){
            arr[i] = alphas[random.nextInt(len)];
        }
        return new String(arr);
    }
	 /**
    *
    * @param size 密钥长度，单位比特
    * @return
    * @throws NoSuchAlgorithmException
    */
   public static KeyPair generateKeyPair(String algol,int size) throws NoSuchAlgorithmException {
       /** RSA算法要求有一个可信任的随机数源 */
       SecureRandom sr = new SecureRandom();
       /** 为RSA算法创建一个KeyPairGenerator对象 */
       KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(algol);
       /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
       keyPairGenerator.initialize(size,sr);
       //keyPairGenerator.initialize(KEYSIZE);  

       /** 生成密匙对 */
       KeyPair keyPair=keyPairGenerator.generateKeyPair();

       //Key publicKey=keyPair.getPublic();
       //Key privateKey=keyPair.getPrivate();
       return keyPair;
   }
   public static PrivateKey getPrivateKey(KeyStore ks,String keyAlias,String pwd){
       try {
           Key key = (PrivateKey) ks.getKey(keyAlias,pwd.toCharArray());
           return (PrivateKey)key;
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
   public static PublicKey getPublicKey(String algol,byte[] encodedKey) throws Exception{
	   KeyFactory keyFactory = KeyFactory.getInstance(algol);//algol例如RSA
       PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
       return pubKey;
   }
   public static PrivateKey getPrivateKey(String algol,byte[] encodedKey) throws Exception{
	PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(encodedKey);
		//algol例如RSA
       KeyFactory keyf = KeyFactory.getInstance(algol);
       PrivateKey priKey = keyf.generatePrivate(priPKCS8);
       return priKey;
   }
   public static PublicKey getPublicKey(Certificate cert){
	   return cert.getPublicKey();
   }
   public static PublicKey getPublicKey(KeyStore ks,String keyAlias,String pwd){
       try {
           return ks.getCertificate(keyAlias).getPublicKey();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
   }
   
   

}

package org.jsirenia.security;

import java.io.ByteArrayOutputStream;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * https://blog.csdn.net/defonds/article/details/42775183
 * 1024位的证书，加密时最大支持127个字节，解密时为128；
 * 2048位的证书，加密时最大支持255个字节，解密时为256。
 * 加密时支持的最大字节数：证书位数/8 -11（比如：2048位的证书，支持的最大加密字节数：2048/8 - 11 = 245）
 */
public class RSAUtil {
    private static final String ALGOL = "RSA";
    private static final String PADDING = "RSA/ECB/PKCS1Padding";
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
   public static PublicKey getPublicKey(byte[] encodedKey) throws Exception{
	   KeyFactory keyFactory = KeyFactory.getInstance(ALGOL);//algol例如RSA
       PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
       return pubKey;
   }
   public static PrivateKey getPrivateKey(byte[] encodedKey) throws Exception{
	PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(encodedKey);
		//algol例如RSA
       KeyFactory keyf = KeyFactory.getInstance(ALGOL);
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

    /**
     *
     * @param size 密钥长度，单位比特
     * @return 加密块大小
     */
    private static int encryptBlockSize(int size){
        return size/8-11;//这个12，是rsa算法固定的

    }
    private static int decryptBlockSize(int size){
        return size/8;
    }
    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key       密钥
     * @param size 密钥长度，单位比特
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data,PublicKey pubKey) throws Exception {
        //数据加密
        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        RSAPublicKey rsaPk = (RSAPublicKey) pubKey;
        int keySize = rsaPk.getModulus().bitLength();
        int blockSize = encryptBlockSize(keySize);
        int begin = 0;
        int end = begin+blockSize;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while(end<data.length){
            bos.write(cipher.doFinal(data,begin,blockSize));
            begin = end;
            end += blockSize;
        }
        bos.write(cipher.doFinal(data,begin,data.length-begin));//Data must not be longer than 53 bytes
        return bos.toByteArray();
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @param size 密钥长度，单位比特
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        //数据解密
        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        RSAPrivateKey rsaPk = (RSAPrivateKey) privateKey;
        int keySize = rsaPk.getModulus().bitLength();
        int blockSize = decryptBlockSize(keySize);
        int begin = 0;
        int end = begin+blockSize;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while(end<data.length){
            bos.write(cipher.doFinal(data,begin,blockSize));
            begin = end;
            end += blockSize;
        }
        bos.write(cipher.doFinal(data,begin,data.length-begin));
        //Data must not be longer than 53 bytes。cipher.密钥长度为64*8=512比特时，update()和cipher.doFinal()传入的字节数之和不能超过64-11=53
        return bos.toByteArray();
    }
}

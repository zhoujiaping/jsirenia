package org.jsirenia.security;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final Charset defaultCharset = Charset.forName("UTF-8");
    private static final String KEY_AES = "AES";
    private static final char[] alphas =("0123456789"
            +"`~!@#$%^&*()-=_+[];',./{}|:<>?" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    /**
     * 加密
     * @param data 数据
     * @param key 密钥
     * @return
     */
    public static byte[] encrypt(byte[] data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }
    public static String encryptBase64(String data, String key) {
        byte[] buf = doAES(data.getBytes(defaultCharset), key, Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(buf);
    }
    public static String encryptHex(String data, String key) {
        byte[] buf = doAES(data.getBytes(defaultCharset), key, Cipher.ENCRYPT_MODE);
        return HexUtil.toHexString(buf);
    }

    /**
     * 解密
     * @param data 数据
     * @param key 密钥
     * @return
     */
    public static byte[] decrypt(byte[] data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }
    public static String decryptBase64(String data, String key) {
        byte[] buf = Base64.getDecoder().decode(data);
        return new String(doAES(buf, key, Cipher.DECRYPT_MODE),defaultCharset);
    }
    public static String decryptHex(String data, String key) {
        byte[] buf = HexUtil.toByteArray(data);
        return new String(doAES(buf, key, Cipher.DECRYPT_MODE),defaultCharset);
    }

    /**
     * 加解密
     * @param data 数据
     * @param key  密钥
     * @param mode 加解密mode
     * @return
     */
    private static byte[] doAES(byte[] data, String key, int mode) {
        try {
            //true 加密内容 false 解密内容
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            kgen.init(128, new SecureRandom(key.getBytes()));
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance(KEY_AES);// 创建密码器
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);// 初始化
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String randomKey(int size){
        char[] arr = new char[size];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int len = alphas.length;
        for(int i=0;i<size;i++){
            arr[i] = alphas[random.nextInt(len)];
        }
        return new String(arr);
        //return UUID.randomUUID().toString();
    }

    public static void main(String[] args) throws Exception {
        int size = 1024;
        //随机生成AES加密的密钥
        String key = randomKey(128);
        String content = "{'repairPhone':'18547854787','customPhone':'12365478965','captchav':'58m7'}";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + key);
        //加密
        String encrypt = encryptBase64(content, key);
        System.out.println("加密后：" + encrypt);
        //解密
        String decrypt = decryptBase64(encrypt, key);
        System.out.println("解密后：" + decrypt);
        //生成rsa的密钥对
        KeyPair pair = RSAUtil.generateKeyPair(size);
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        //对AES的密钥加密
        byte[] ek = RSAUtil.encryptByPrivateKey(key.getBytes(defaultCharset),privateKey.getEncoded(),size);
        System.out.println(Base64.getEncoder().encodeToString(ek));
        //解密得到AES的密钥
        byte[] buf = RSAUtil.decryptByPublicKey(ek,publicKey.getEncoded(),size);
        System.out.println("===");
        System.out.println(new String(buf,defaultCharset).equals(key));

    }
}

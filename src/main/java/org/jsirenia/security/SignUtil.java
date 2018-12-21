package org.jsirenia.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.util.ResourceUtils;

public class SignUtil {
    /**
     *
     * @param keystorefile   .keystore或者.pfx
     * @param pwd
     * @param type
     * @return
     */
    public static KeyStore loadKeyStore(File keystorefile, String pwd, String type){
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(keystorefile))){
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(bis,pwd.toCharArray());
            return ks;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param ks
     * @param pwd
     * @return
     */
    public static PrivateKey getPrivateKey(KeyStore ks,String keyAlias,String pwd){
        try {
            Key key = (PrivateKey) ks.getKey(keyAlias,pwd.toCharArray());
            return (PrivateKey)key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static PublicKey getPublicKey(KeyStore ks,String keyAlias,String pwd){
        try {
            return ks.getCertificate(keyAlias).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Certificate loadCertificate(File cerfile){
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(cerfile))){
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(bis);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static String signToBase64(byte[] data,byte[] encodedKey){
        try{
            return sign(data,encodedKey);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    private static String sign(byte[] data,byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);//为什么不从keystore里面直接取PrivateKey，非要转成byte[]然后再通过PKCS8EncodedKeySpec构造PrivateKey?
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(priKey);
            signature.update(data);
            return Base64.getEncoder().encodeToString(signature.sign());
    }
    private static boolean verifySign(byte[] data,String sign,byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }
    public static boolean verifyBase64Sign(byte[] data,String sign,byte[] encodedKey){
        try{
            return verifySign(data,sign,encodedKey);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        File ksfile = ResourceUtils.getFile("xxx.keystore");
        KeyStore ks = SignUtil.loadKeyStore(ksfile,"xxx","JKS");//JKS:java keystore
        PrivateKey privateKey = SignUtil.getPrivateKey(ks,"xxx","xxx");
        String sign = SignUtil.signToBase64("my precious".getBytes("utf-8"),privateKey.getEncoded());

        PublicKey publicKey = SignUtil.getPublicKey(ks,"xxx","xxx");
        boolean verify = SignUtil.verifyBase64Sign("my precious".getBytes("utf-8"),sign,publicKey.getEncoded());
        System.out.println(verify);

        File cerfile = ResourceUtils.getFile("xxx.cer");
        Certificate cert = loadCertificate(cerfile);
        verify = SignUtil.verifyBase64Sign("my precious".getBytes("utf-8"),sign,cert.getPublicKey().getEncoded());
        System.out.println(verify);
    }
}

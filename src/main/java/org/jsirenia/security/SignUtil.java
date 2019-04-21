package org.jsirenia.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;

import org.springframework.util.ResourceUtils;

public class SignUtil {
   public static final Charset charset = Charset.forName("utf-8");
    public static byte[] sign(byte[] data,PrivateKey privateKey){
    	 try{
    		 Signature signature = Signature.getInstance("SHA256withRSA");
    	        signature.initSign(privateKey);
    	        signature.update(data);
    	        return signature.sign();
         }catch(Exception e){
             throw new RuntimeException(e);
         }
    }
    public static String signHex(byte[] data,PrivateKey privateKey){
    	return HexUtil.toHexString(sign(data,privateKey));
    }
    public static String signHex(String data,PrivateKey privateKey){
    	return signHex(data.getBytes(charset),privateKey);
    }
    public static String signBase64(String data,PrivateKey privateKey){
    	return signBase64(data.getBytes(charset),privateKey);
    }
    public static String signBase64(byte[] data,PrivateKey privateKey){
   	     return Base64.getEncoder().encodeToString(sign(data,privateKey));
   }
    public static boolean verifySign(byte[] data,byte[] sign,PublicKey publickKey) {
    	try{
    		Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publickKey);
            signature.update(data);
            return signature.verify(sign);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static boolean verifySignHex(String data,String hexSign,PublicKey publickKey){
    	return verifySignHex(data.getBytes(charset),hexSign,publickKey);
    }
    public static boolean verifySignHex(byte[] data,String hexSign,PublicKey publickKey){
    	return verifySign(data,HexUtil.toByteArray(hexSign),publickKey);
    }
    public static boolean verifySignBase64(String data,String base64Sign,PublicKey publickKey){
		return verifySignBase64(data.getBytes(charset),base64Sign,publickKey);
    }
    public static boolean verifySignBase64(byte[] data,String base64Sign,PublicKey publickKey){
        return verifySign(data,Base64.getDecoder().decode(base64Sign),publickKey);
    }
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        File ksfile = ResourceUtils.getFile("xxx.keystore");
        KeyStore ks = KeyStoreUtil.loadKeyStore(ksfile,"xxx","JKS");//JKS:java keystore
        PrivateKey privateKey = RSAUtil.getPrivateKey(ks,"xxx","xxx");
        String sign = SignUtil.signBase64("my precious".getBytes("utf-8"),privateKey);

        PublicKey publicKey = RSAUtil.getPublicKey(ks,"xxx","xxx");
        boolean verify = SignUtil.verifySignBase64("my precious".getBytes("utf-8"),sign,publicKey);
        System.out.println(verify);

        File cerfile = ResourceUtils.getFile("xxx.cer");
        Certificate cert = CertUtil.loadCertificate(cerfile);
        verify = SignUtil.verifySignBase64("my precious".getBytes("utf-8"),sign,cert.getPublicKey());
        System.out.println(verify);
    }
}

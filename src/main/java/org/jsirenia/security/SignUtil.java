package org.jsirenia.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;

import org.springframework.util.ResourceUtils;

public class SignUtil {
   
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
    public static String signBase64(byte[] data,PrivateKey privateKey){
   	     return Base64.getEncoder().encodeToString(sign(data,privateKey));
   }
    private static boolean verifySign(byte[] data,byte[] sign,PublicKey publickKey) {
    	try{
    		Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publickKey);
            signature.update(data);
            return signature.verify(sign);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static boolean verifySignBase64(byte[] data,String base64Sign,PublicKey publickKey){
        return verifySign(data,Base64.getDecoder().decode(base64Sign),publickKey);
    }
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        File ksfile = ResourceUtils.getFile("xxx.keystore");
        KeyStore ks = KeyStoreUtil.loadKeyStore(ksfile,"xxx","JKS");//JKS:java keystore
        PrivateKey privateKey = KeyUtil.getPrivateKey(ks,"xxx","xxx");
        String sign = SignUtil.signBase64("my precious".getBytes("utf-8"),privateKey);

        PublicKey publicKey = KeyUtil.getPublicKey(ks,"xxx","xxx");
        boolean verify = SignUtil.verifySignBase64("my precious".getBytes("utf-8"),sign,publicKey);
        System.out.println(verify);

        File cerfile = ResourceUtils.getFile("xxx.cer");
        Certificate cert = CertUtil.loadCertificate(cerfile);
        verify = SignUtil.verifySignBase64("my precious".getBytes("utf-8"),sign,cert.getPublicKey());
        System.out.println(verify);
    }
}

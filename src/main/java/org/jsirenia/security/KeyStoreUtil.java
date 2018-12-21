package org.jsirenia.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

public class KeyStoreUtil {
    public static final String PKCS12 = "PKCS12";
    public static final String JKS = "JKS";

    /**
     * 将pfx或p12的文件转为keystore
     */
    public static void convertToKeyStore(File pfxFile,String keyAlias, String pwd, File keyStoreFile) {
        try(FileInputStream fis = new FileInputStream(pfxFile);
            FileOutputStream out = new FileOutputStream(keyStoreFile);
        ){
            KeyStore inputKeyStore = KeyStore.getInstance(PKCS12);
            char[] nPassword = pwd.toCharArray();
            inputKeyStore.load(fis, nPassword);
            KeyStore outputKeyStore = KeyStore.getInstance(JKS);
            outputKeyStore.load(null, nPassword);
            Key key = inputKeyStore.getKey(keyAlias, nPassword);
            Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
            outputKeyStore.setKeyEntry(keyAlias, key,nPassword, certChain);
            outputKeyStore.store(out, nPassword);
            //转换所有alias
            /*Enumeration<String> enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) { // we are readin just one
                // certificate.
                String keyAlias = enums.nextElement();
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, nPassword);
                    Certificate[] certChain = inputKeyStore
                            .getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key,
                            keyStorePwd.toCharArray(), certChain);
                }
            }
            outputKeyStore.store(out, nPassword);*/
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    /**
     * 将keystore转为pfx
     */
    public static void convertToPfx(File keyStoreFile,String keyAlias, String pwd, File pfxFile) {
        try (
                FileInputStream fis = new FileInputStream(keyStoreFile);
                FileOutputStream out = new FileOutputStream(pfxFile);
                ){
            KeyStore inputKeyStore = KeyStore.getInstance(JKS);
            char[] nPassword = pwd.toCharArray();
            inputKeyStore.load(fis, nPassword);
            KeyStore outputKeyStore = KeyStore.getInstance(PKCS12);
            outputKeyStore.load(null, nPassword);
            Key key = inputKeyStore.getKey(keyAlias, nPassword);
            Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
            outputKeyStore.setKeyEntry(keyAlias, key,nPassword,certChain);
            outputKeyStore.store(out, nPassword);
            /*Enumeration enums = inputKeyStore.aliases();

            while (enums.hasMoreElements()) { // we are readin just one
                // certificate.

                String keyAlias = (String) enums.nextElement();

                System.out.println("alias=[" + keyAlias + "]");

                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, nPassword);
                    Certificate[] certChain = inputKeyStore
                            .getCertificateChain(keyAlias);

                    outputKeyStore.setKeyEntry(keyAlias, key,
                            KEYSTORE_PASSWORD.toCharArray(), certChain);
                }
            }
            outputKeyStore.store(out, nPassword);*/
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String ksfile = "xxx.keystore";
        String pfxfile = "xxx.pfx";
        convertToPfx(new File(ksfile),"xxx","xxx",new File(pfxfile));
        ksfile = "xxx.keystore";
        convertToKeyStore(new File(pfxfile),"xxx","xxx",new File(ksfile));

    }
}

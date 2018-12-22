package org.jsirenia.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertUtil {
	 public static Certificate loadCertificate(File cerfile){
	        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(cerfile))){
	            CertificateFactory cf = CertificateFactory.getInstance("X.509");
	            return (X509Certificate) cf.generateCertificate(bis);
	        }catch(Exception e){
	            throw new RuntimeException(e);
	        }
	    }
}

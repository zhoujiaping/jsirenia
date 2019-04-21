//https://blog.csdn.net/coolcoffee168/article/details/8128321
@Grab(group='org.apache.commons', module='commons-email', version='1.5')

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

EmailAttachment attachment = new EmailAttachment();
attachment.setPath("email.groovy");
attachment.setDisposition(EmailAttachment.ATTACHMENT);
attachment.setDescription("txt file");
attachment.setName("email.groovy");
 
MultiPartEmail email = new MultiPartEmail();
email.setHostName("smtp.exmail.qq.com");
email.setSmtpPort(465);
email.setAuthenticator(new DefaultAuthenticator("421211679@qq.com",
				"passowrd"));
email.setSSLOnConnect(true);
email.setFrom("421211679@qq.com");
email.setSubject("�����ʼ�");
email.setMsg("������");
email.addTo("421211679@qq.com");
 
email.attach(attachment);
 
println email.send();

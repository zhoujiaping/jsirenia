package org.jsirenia;
import java.util.Properties;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailTest {
	@Test
	public void test(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.qq.com");
		mailSender.setPort(465);
		mailSender.setDefaultEncoding("utf-8");
		mailSender.setProtocol("smtp");
		mailSender.setUsername("421211679@qq.com");
		mailSender.setPassword("eppidhfycxyebibj");
		Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.setProperty("mail.smtp.port", "465");
        //props.setProperty("mail.smtp.socketFactory.port", "465");
		mailSender.setJavaMailProperties(props);
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setFrom("421211679@qq.com");
		simpleMessage.setTo("421211679@qq.com");
		simpleMessage.setSubject("邮件测试-小村庄");
		simpleMessage.setText("邮件测试");
		mailSender.send(simpleMessage);
	}
}

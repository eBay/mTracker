package com.ccoe.build.persistent.healthcheck.scheduler;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ccoe.build.utils.ServiceConfig;

public class EmailSender {
	private final String MAIL_HOST = ServiceConfig.get("scheduler.email.host");
	private final Integer MAIL_TIMEOUT = ServiceConfig.getInt("scheduler.email.timeout");
	
	public void sendEmail(String to, String from, String content, String subject) {
		Properties properties = System.getProperties();
		properties.put("mail.host", MAIL_HOST);
		properties.put("mail.timeout", MAIL_TIMEOUT);

		Session session = Session.getInstance(properties, null);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			message.setSubject(subject);

			message.setContent(content, "text/html;charset=utf-8");

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		EmailSender emailer = new EmailSender();
		emailer.sendEmail("mmao@ebay.com", "mmao@ebay.com", "test", "test subj");
	}
}

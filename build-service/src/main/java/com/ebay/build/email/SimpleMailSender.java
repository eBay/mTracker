package com.ebay.build.email;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SimpleMailSender {

	/**
	 * 
	 * @param mailInfo
	 *      MailSenderInfo
	 * @return boolean
	 */
	public boolean sendHtmlSender(MailSenderInfo mailInfo) { 	
		
	    Properties props = mailInfo.getProperties();
	    
	    //use mail pros to create a session about sending mail 
	    Session sendMailSession = Session.getInstance(props, null);
	    sendMailSession.setDebug(false);
	    
	    try {
	    	//create a mail message according to session 
			Message mailMessage = new MimeMessage(sendMailSession);
			
			//create an address about sender 
			Address from = new InternetAddress(mailInfo.getFromAddress());		
		
			//set the address of sender
			mailMessage.setFrom(from);
			
			//create an address about recipients
			Address to = new InternetAddress(mailInfo.getToAddress());			
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			/**
			 * if you want to send the mail to more people
			 * this segment of code will help you
			 */		
			
		   /*	
		    InternetAddress[] adrs = new InternetAddress[toMore.length];
			for(int i = 0; toMore.length; i++)
			{
				adrs[i] = new InternetAddress(toMore[i]);
			}			
			mailMessage.setRecipients(Message.RecipientType.TO, adrs);
		    */
				
			mailMessage.setSubject(mailInfo.getSubject());
			mailMessage.setSentDate(new Date());
			
			if (null != mailInfo.getMimepartMethod()) {
				// MimeMultipart is a container, and it contains MimeBodyPart
				Multipart mainPart = new MimeMultipart(mailInfo.getMimepartMethod());
				// create a MimeBodyPart which contains content of HTML
				
				BodyPart contentPart = new MimeBodyPart();
				contentPart.setContent(mailInfo.getContent(), mailInfo.getContentType());
				mainPart.addBodyPart(contentPart);

				List<BodyPart> parts = mailInfo.getBodyParts();
				for (BodyPart part : parts) {
					mainPart.addBodyPart(part);
				}
				
				mailMessage.setContent(mainPart);
				mailMessage.saveChanges();
			} else {
				mailMessage.setContent(mailInfo.getContent(), mailInfo.getContentType());
			}
			
			Transport.send(mailMessage);
			
			return true;
		} catch (AddressException e) {			
			e.printStackTrace();
		} catch (MessagingException e) {			
			e.printStackTrace();
		}
	    
		return false;
	}
	
}

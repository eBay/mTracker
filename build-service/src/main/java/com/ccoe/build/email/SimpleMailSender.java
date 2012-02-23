/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.email;

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

	public SimpleMailSender() {
		
	}
	public boolean sendHtmlSender(MailSenderInfo mailInfo) { 	
	    Properties props = mailInfo.getProperties();
	    //use mail pros to create a session about sending mail 
	    Session sendMailSession = Session.getInstance(props, null);
	    sendMailSession.setDebug(mailInfo.isDebug());
	    
	    try {
	    	//create a mail message according to session 
			Message mailMessage = new MimeMessage(sendMailSession);
			
			//create an address about sender 
			Address from = new InternetAddress(mailInfo.getFromAddress());		
		
			//set the address of sender
			mailMessage.setFrom(from);
			
			//create an address about recipients
			//create an address about recipients
			int addressLength = mailInfo.getToAddresses().length;
			InternetAddress[] adrs = new InternetAddress[addressLength];					
			for(int i = 0; i < addressLength; i++)
			{
				adrs[i] = new InternetAddress(mailInfo.getToAddresses()[i]);
			}			
			mailMessage.setRecipients(Message.RecipientType.TO, adrs);
				
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
			System.out.println("[INFO]: ready to transport the mailmessage!");
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

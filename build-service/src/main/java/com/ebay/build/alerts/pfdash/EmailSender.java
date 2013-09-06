package com.ebay.build.alerts.pfdash;

import java.io.File;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.VelocityParse;
import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.email.SimpleMailSender;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.utils.ServiceConfig;



public class EmailSender {
	
	AlertResult ar = null;
	
	public EmailSender(AlertResult ar){
		
		this.ar = ar;
		
	
	}
	public void sendmail(){
		
		MailSenderInfo msinfo = getEmailContent();
		
		SimpleMailSender sms = new SimpleMailSender();

        sms.sendHtmlSender(msinfo); 
	}
	
	
	
	public String generateMailHtml() {
		
        System.out.println("[INFO]: velocity initing...");
		try {
			new VelocityParse("alert_email_template.vm", ar);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[INFO]: velocity init completed!");
		String readHtml = FileUtils.readFile(new File("./content.html"));

		return readHtml;
	}


	

	public MailSenderInfo getEmailContent() {
		/**
		 * set content for the mail
		 */
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(ServiceConfig.get("scheduler.reliability.email.host"));
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(false);
		mailInfo.setDebug(false);

		mailInfo.setFromAddress(ServiceConfig.get("scheduler.reliability.email.from"));
		
		String[] toAddresses = ServiceConfig.get("scheduler.reliability.email.to").split(";");
		for(String address : toAddresses) {
			address = address.trim();
		}
		 
		mailInfo.setToAddresses(toAddresses);
		
		mailInfo.setSubject(ServiceConfig.get("scheduler.reliability.email.subject"));
		mailInfo.setMimepartMethod("related");


		/**
		 * generate mail template and send mail
		 */

		String content = generateMailHtml();
		mailInfo.setContent(content);
		
		return mailInfo;
	}
		
	
	

}

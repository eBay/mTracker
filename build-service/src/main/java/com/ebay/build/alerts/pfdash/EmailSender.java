package com.ebay.build.alerts.pfdash;

import java.io.File;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.Time;
import com.ebay.build.alerts.VelocityParse;
import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.email.SimpleMailSender;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.utils.ServiceConfig;


public class EmailSender {
	private AlertResult ar;
	private Time time;
	
	public EmailSender(AlertResult ar, Time time){
		this.ar = ar;
		this.time = time;
	}
	
	public void sendmail(File directory){
		MailSenderInfo msinfo = getEmailContent(directory);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public String generateMailHtml(File directory) {
        System.out.println("[INFO]: Velocity initing...");
		try {
			new VelocityParse("alert_email_template.vm", ar, time, directory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[INFO]: velocity init completed!");
		String readHtml = FileUtils.readFile(new File(directory, "PFDash_KPI_Alert.html"));

		return readHtml;
	}

	public MailSenderInfo getEmailContent(File directory) {
		/**
		 * set content for the mail
		 */
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(ServiceConfig.get("scheduler.reliability.email.host"));
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(false);
		mailInfo.setDebug(false);

		mailInfo.setFromAddress(ServiceConfig.get("scheduler.email.from"));
		
		String[] toAddresses = ServiceConfig.get("scheduler.email.to").split(";");
		for(String address : toAddresses) {
			address = address.trim();
		}
		 
		mailInfo.setToAddresses(toAddresses);
		
		mailInfo.setSubject(ServiceConfig.get("pfdash.alert.email.subject"));
		mailInfo.setMimepartMethod("related");


		/**
		 * generate mail template and send mail
		 */

		String content = generateMailHtml(directory);
		mailInfo.setContent(content);
		
		return mailInfo;
	}
}

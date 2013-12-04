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
	
	public EmailSender() {
		
	}
	
	public EmailSender(AlertResult ar, Time time){
		this.ar = ar;
		this.time = time;
	}
	
	public void sendmail(String description, boolean warning) {
		MailSenderInfo msinfo = getEmailContent(description, warning);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public void sendmail(File directory, boolean warning){
		MailSenderInfo msinfo = getEmailContent(directory, warning);
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

	public MailSenderInfo getEmailContent(File directory, boolean warning) {
		/**
		 * set content for the mail
		 */
		MailSenderInfo mailInfo = getEmailBasicInfo(warning);		
		mailInfo.setSubject(ServiceConfig.get("pfdash.alert.email.subject"));
						
		/**
		 * generate mail template and send mail
		 */
		
		String content = generateMailHtml(directory);
		mailInfo.setContent(content);
		
		return mailInfo;
	}
	
	public MailSenderInfo getEmailContent(String content, boolean warning) {
		MailSenderInfo mailInfo = getEmailBasicInfo(warning);		
		mailInfo.setSubject(ServiceConfig.get("pfdash.alert.email.exception"));
		mailInfo.setContent("<html><body><div  style='font-size:16px;'>" +
				"It encounters some problems with sending pfDash alert email!" +
				"</div><br/><div style='color:blue;'>" + content + "</div></body></html>");
		return mailInfo;
	}
	

	private MailSenderInfo getEmailBasicInfo(boolean warning) {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(ServiceConfig.get("scheduler.reliability.email.host"));
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(false);
		mailInfo.setDebug(false);

		mailInfo.setFromAddress(ServiceConfig.get("scheduler.email.from"));
		
		String[] toAddresses = null;
		if (warning) {
			toAddresses = ServiceConfig.get("scheduler.reliability.email.to").split(";");
		} else {
			
			toAddresses = ServiceConfig.get("pfdash.alert.nowarning.email.to").split(";");
		}
		
		for(String address : toAddresses) {
			address = address.trim();
		}
		 
		mailInfo.setToAddresses(toAddresses);
		mailInfo.setMimepartMethod("related");
		return mailInfo;
	}
}

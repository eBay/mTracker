package com.ccoe.build.alerts.pfdash;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import com.ccoe.build.alerts.AlertResult;
import com.ccoe.build.alerts.Time;
import com.ccoe.build.alerts.VelocityParse;
import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.email.MailSenderInfo;
import com.ccoe.build.email.SimpleMailSender;
import com.ccoe.build.utils.ServiceConfig;


public class EmailSender {
	private AlertResult ar;
	private Time time;
	
	public EmailSender() {
		
	}
	
	public EmailSender(AlertResult ar, Time time){
		this.ar = ar;
		this.time = time;
	}
	
	public void sendMail(String description, boolean warning) {
		MailSenderInfo msinfo = getEmailContent(description, warning);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public void sendMail(File directory, boolean warning){
		MailSenderInfo msinfo = getEmailContent(directory, warning);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public String generateMailHtml(File directory, String dateString) {
        System.out.println("[INFO]: Velocity initing...");
		try {
			new VelocityParse("alert_email_template.vm", ar, time, directory, dateString);
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
		String dateString = DateUtils.getDateTimeString(DateUtils.getOneDayBack(new Date()), "yyyy-MM-dd", TimeZone.getDefault());
		MailSenderInfo mailInfo = getEmailBasicInfo(warning);		
		mailInfo.setSubject(ServiceConfig.get("pfdash.alert.email.subject") + " for " + dateString);
						
		/**
		 * generate mail template and send mail
		 */
		
		String content = generateMailHtml(directory, dateString);
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

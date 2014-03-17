package com.ebay.build.alerts;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.email.SimpleMailSender;
import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.build.utils.ServiceConfig;

public class AlertEmail {
	private String topic = "";
	
	public AlertEmail(String t) {
		this.topic = t;
	}
	
	public void sendMail(String description, boolean warning) {
		MailSenderInfo msinfo = getEmailContent(description, warning);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public void sendMail(File directory, String htmlContent, boolean warning){
		MailSenderInfo msinfo = getEmailContent(directory, htmlContent, warning);
		SimpleMailSender sms = new SimpleMailSender();
        sms.sendHtmlSender(msinfo); 
	}
	
	public MailSenderInfo getEmailContent(File directory, String htmlContent, boolean warning) {
		
		
		MailSenderInfo mailInfo = getEmailBasicInfo(warning);		
		mailInfo.setSubject(ServiceConfig.get(topic + ".alert.email.subject") + " for " 
		+ DateUtils.getDateTimeString(DateUtils.getOneDayBack(new Date()), "yyyy-MM-dd", TimeZone.getDefault()));
		mailInfo.setContent(htmlContent);
		return mailInfo;
	}
	
	public MailSenderInfo getEmailContent(String content, boolean warning) {
		MailSenderInfo mailInfo = getEmailBasicInfo(warning);		
		mailInfo.setSubject(ServiceConfig.get(topic + ".alert.email.exception"));
		mailInfo.setContent("<html><body><div  style='font-size:16px;'>" +
				"It encounters some problems with sending " + topic + " alert email!" +
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
			toAddresses = ServiceConfig.get(topic + ".alert.nowarning.email.to").split(";");
		}
		
		for(String address : toAddresses) {
			address = address.trim();
		}
		 
		mailInfo.setToAddresses(toAddresses);
		mailInfo.setMimepartMethod("related");
		return mailInfo;
	}
}

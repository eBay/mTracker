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

package com.ccoe.build.alerts;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.email.MailSenderInfo;
import com.ccoe.build.email.SimpleMailSender;
import com.ccoe.build.utils.ServiceConfig;

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

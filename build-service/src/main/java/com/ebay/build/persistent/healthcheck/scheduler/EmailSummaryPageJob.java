package com.ebay.build.persistent.healthcheck.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.persistent.healthcheck.HealthTrackJDBCTemplate;
import com.ebay.build.persistent.healthcheck.SummaryReport;
import com.ebay.build.utils.ServiceConfig;

public class EmailSummaryPageJob implements Job {
	private ApplicationContext context = null;
	private final HealthTrackJDBCTemplate modelJDBCTemplate; 
	
	public EmailSummaryPageJob() {
		 context = new ClassPathXmlApplicationContext("healthtrack-sping-jdbc-config.xml");
		 modelJDBCTemplate = (HealthTrackJDBCTemplate) context.getBean("HealthTrackJDBCTemplate");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing email sender: " + new Date());
		EmailSender sender = new EmailSender();
		
		Map<String, SummaryReport> report = processReport(modelJDBCTemplate.getSummaryReport());
		
		String content = generateEmailBody(report);
		if (report.isEmpty()) {
			content = "No validation issues found in DB.";
		}
		
		sender.sendEmail(ServiceConfig.get("scheduler.email.to"), 
				ServiceConfig.get("scheduler.email.from"),  
				content,
				ServiceConfig.get("scheduler.email.subject"));
		System.out.println("Email sent: " + new Date());
	}
	
	private String generateEmailBody(Map<String, SummaryReport> reports) {
		StringBuffer sb = new StringBuffer();
		for (SummaryReport report : reports.values()) {
			sb.append("<p style='font-family: \"Open Sans\", \"Helvetica Neue\", sans-serif;'> <span style='font-size: 15.0pt;'>Jenkins Job: <a href='").append(report.getBuildURL()).append("' target='_blank'>").append(report.getJobName()).append("</a></span>");
			sb.append("  <br/><span style='font-size: 12.0pt;'>  Critical: ").append(report.getCriticalCount()).append(" Major: " ).append(report.getMajorCount()).append("</span>");
			sb.append("  <br/><span style='font-size: 8.0pt; color: #796E65'> Git Repository: ").append(report.getGitUrl()).append("  Branch: ").append(report.getGitBranch()).append(" </span> </p>");
		}
		return sb.toString();
	}
	
	public Map<String, SummaryReport> processReport(List<SummaryReport> report) {
		Map<String, SummaryReport> map = new HashMap<String, SummaryReport>();
		
		for (SummaryReport r1 : report) {
			SummaryReport r2 = map.get(r1.getBuildURL());
			if (r2 == null) {
				r2 = r1;
				map.put(r2.getBuildURL(), r1);
			}
			
			if ("critical".equalsIgnoreCase(r1.getSeverity())) {
				r2.setCriticalCount(r1.getCount());
			}
			
			if ("major".equalsIgnoreCase(r1.getSeverity())) {
				r2.setMajorCount(r1.getCount());
			}
		}
		
		return map;
	}
	
}

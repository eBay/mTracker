package com.ebay.build.reliability;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.jfree.data.category.CategoryDataset;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.charts.LineChart;
import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.email.SimpleMailSender;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.service.BuildServiceScheduler;
import com.ebay.build.utils.ServiceConfig;



public class ReliabilityEmailJob implements Job {
	private ApplicationContext context = null;
	private ReliabilityEmailJDBCTemplate modelJDBCTemplate = null;	
	
	public ReliabilityEmailJob() {
		System.out.println("[INFO]: init ReliabilityEmailJDBCTemplate bean...");
		context = new ClassPathXmlApplicationContext("healthtrack-sping-jdbc-config.xml");
		modelJDBCTemplate = (ReliabilityEmailJDBCTemplate) context.getBean("ReliabilityEmailJDBCTemplate");
		System.out.println("[INFO]: finish initing ReliabilityEmailJDBCTemplate bean!");
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("[INFO]: executing email sender of CI Build :" + new Date());
		System.out.println("[INFO]: charting CI bulid reliability");
		
		File embeddedImage = drawChart(BuildServiceScheduler.contextPath);	
		
		System.out.println("[INFO]: complete to chart CI bulid reliability!");
		System.out.println("[INFO]: generate an email...");
		
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(getEmailContent(embeddedImage, BuildServiceScheduler.contextPath));		
		System.out.println("Email of CI bulid reliability sent: " + new Date());
	}

	
	public File drawChart(File directory) {
		String[] columnKeys1 = { formatDay(49) + "~" + formatDay(43),
				formatDay(42) + "~" + formatDay(36),
				formatDay(35) + "~" + formatDay(29),
				formatDay(28) + "~" + formatDay(22),
				formatDay(21) + "~" + formatDay(15),
				formatDay(14) + "~" + formatDay(8),
				formatDay(7) + "~" + formatDay(1) };
		
		List<ReportInfo> reportList = modelJDBCTemplate.getWeeklyReliability();
		double[] systemReliabilityRate = modelJDBCTemplate.getWeeklySystemReliability(reportList);
		double[] overallReliabilityRate = modelJDBCTemplate.getWeeklyOverallReliability(reportList);

		LineChart lineChart = new LineChart("");
		CategoryDataset dataSet = lineChart.createDataset(systemReliabilityRate, overallReliabilityRate, columnKeys1);
		File chartFile = lineChart.createChart(dataSet, "LineChart.jpeg", overallReliabilityRate, directory);
		lineChart.pack();
		return chartFile;
	}

	public String generateMailHtml(File directory) {
		Map<String, ReportInfo> infoList = modelJDBCTemplate.generateReliabilityTable();
		
        System.out.println("[INFO]: velocity initing...");
		try {
			new VelocityParse("mailtemplate.vm", 
					infoList, null,
					getTopSystemErrorsTable(infoList.get("info_30day")),
					getTopUserErrorsTable(infoList.get("info_30day")),
					null, null, "mail.html", directory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[INFO]: velocity init completed!");
		String readHtml = FileUtils.readFile(new File(directory, "html/mail.html"));

		return readHtml;
	}


	private List<ErrorCode> getTopSystemErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenSystemError = modelJDBCTemplate.getTopTenError("system");
		modelJDBCTemplate.setDescriptionNPercentage(topTenSystemError, info30day.getFailedSessions());
		return topTenSystemError;
	}
	
	private List<ErrorCode> getTopUserErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenUserError = modelJDBCTemplate.getTopTenError("user");
		modelJDBCTemplate.setDescriptionNPercentage(topTenUserError, info30day.getFailedSessions());
		return topTenUserError;
	}

	public MailSenderInfo getEmailContent(File embeddedImage, File directory) {
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String sendDate = sdf.format(new Date());
		mailInfo.setSubject(ServiceConfig.get("scheduler.reliability.email.subject") + " " +sendDate);
		mailInfo.setMimepartMethod("related");

		try {
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setHeader("Content-Location", "line_chart");
			FileDataSource fds = new FileDataSource(embeddedImage);
			bodyPart.setDataHandler(new DataHandler(fds));
			mailInfo.getBodyParts().add(bodyPart);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		/**
		 * generate mail template and send mail
		 */

		String content = generateMailHtml(directory);
		mailInfo.setContent(content);
		
		return mailInfo;
	}
		
	// set day before current day
	public String formatDay(int previous) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(calendar.DATE, 0 - previous);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.US);
		return sdf.format(date);
	}
	

}

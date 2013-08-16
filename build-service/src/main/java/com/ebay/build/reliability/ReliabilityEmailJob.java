package com.ebay.build.reliability;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.charts.LineChart;
import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.email.SimpleMailSender;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.utils.ServiceConfig;



public class ReliabilityEmailJob implements Job {
	private ApplicationContext context = null;
	private ReliabilityEmailJDBCTemplate modelJDBCTemplate = null;
	
	public ReliabilityEmailJob(){
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing email sender :" + new Date());
		
		emailSummaryPageJob();		
		drawChart();		

		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(getEmailContent());
		
		System.out.println("Email sent: " + new Date());

	}

	
	public void emailSummaryPageJob() {
		context = new ClassPathXmlApplicationContext(
				"healthtrack-sping-jdbc-config.xml");
		modelJDBCTemplate = (ReliabilityEmailJDBCTemplate) context
				.getBean("ReliabilityEmailJDBCTemplate");
	}
	
	public void drawChart() {
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
		String lineImage = "LineChart.jpeg";
		LineChart lineChart = new LineChart("", systemReliabilityRate, overallReliabilityRate, columnKeys1, lineImage);
		lineChart.pack();
	}

	public String generateMailHtml() {
		Map<String, ReportInfo> infoList = getReliabilityTable();
		
        System.out.println("[Info]: velocity initing...");
		try {
			new VelocityParse("mailtemplate.vm", 
					infoList,
					getTopSystemErrorsTable(infoList.get("info_30day")),
					getTopUserErrorsTable(infoList.get("info_30day")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[Info]: velocity init completed!");
		String mailHtml = "./html/mail.html";
		String readHtml = FileUtils.readFile(new File(mailHtml));

		return readHtml;
	}


	private List<ErrorCode> getTopSystemErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenSystemError = modelJDBCTemplate.getTopTenError("system");
		modelJDBCTemplate.setDescriptionNPercentage(topTenSystemError, info30day.getSystemErrors());
		return topTenSystemError;
	}
	
	private List<ErrorCode> getTopUserErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenUserError = modelJDBCTemplate.getTopTenError("user");
		modelJDBCTemplate.setDescriptionNPercentage(topTenUserError, info30day.getUserErrors());
		return topTenUserError;
	}

	private Map<String, ReportInfo> getReliabilityTable() {
		ReportInfo reportList1 = modelJDBCTemplate.getReportInfoBeforeDay("1");
		ReportInfo reportList2 = modelJDBCTemplate.getReportInfoBeforeDay("7");
		ReportInfo reportList3 = modelJDBCTemplate.getReportInfoBeforeDay("30");

		Map<String, ReportInfo> infoList = new HashMap<String, ReportInfo>();
		infoList.put("info_24h", reportList1);
		infoList.put("info_7day", reportList2);
		infoList.put("info_30day", reportList3);

		return infoList;
	}

	public MailSenderInfo getEmailContent() {
		/**
		 * set content for the mail
		 */
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(ServiceConfig.get("scheduler.reliability.email.host"));
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);

		mailInfo.setFromAddress(ServiceConfig.get("scheduler.reliability.email.from"));
		mailInfo.setToAddress(ServiceConfig.get("scheduler.reliability.email.to"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String sendDate = sdf.format(new Date());
		mailInfo.setSubject(ServiceConfig.get("scheduler.reliability.email.subject") + " " +sendDate);
		mailInfo.setMimepartMethod("related");

		try {
//			BodyPart bodyPart1 = new MimeBodyPart();
//			bodyPart1.setHeader("Content-Location", "weekly_trend");
//			FileDataSource fds1 = new FileDataSource("./images/Weekly_Trend.jpeg");
//			bodyPart1.setDataHandler(new DataHandler(fds1));

			BodyPart bodyPart2 = new MimeBodyPart();
			bodyPart2.setHeader("Content-Location", "line_chart");
			FileDataSource fds2 = new FileDataSource("./images/LineChart.jpeg");
			bodyPart2.setDataHandler(new DataHandler(fds2));
			

			//mailInfo.getBodyParts().add(bodyPart1);
			mailInfo.getBodyParts().add(bodyPart2);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		/**
		 * generate mail template and send mail
		 */

		String content = generateMailHtml();
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

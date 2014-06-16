package com.ebay.build.reliability;

import java.io.File;
import java.net.URISyntaxException;
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
import com.ebay.build.core.utils.DateUtils;
import com.ebay.build.core.utils.FileUtils;
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
		Date sentTime = new Date();
		System.out.println("[INFO]: executing email sender of CI Build :" + sentTime);
		System.out.println("[INFO]: charting CI bulid reliability");
		
		Date midNight = DateUtils.getMidnightZero(sentTime);
		Object[] dataArrays = getReliabilityData(midNight);
		File embeddedImage = drawChart(BuildServiceScheduler.contextPath, midNight, dataArrays);	
		
		System.out.println("[INFO]: complete to chart CI bulid reliability!");
		System.out.println("[INFO]: generate an email...");
		
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(getEmailContent(embeddedImage, BuildServiceScheduler.contextPath, sentTime));		
		System.out.println("Email of CI bulid reliability sent: " + new Date());
	}

	
	public File drawChart(File directory, Date date, Object[] dataArrays) {
		String[] columnKeys1 = { formatDay(date, 49) + "~" + formatDay(date, 43),
				formatDay(date, 42) + "~" + formatDay(date, 36),
				formatDay(date, 35) + "~" + formatDay(date, 29),
				formatDay(date, 28) + "~" + formatDay(date, 22),
				formatDay(date, 21) + "~" + formatDay(date, 15),
				formatDay(date, 14) + "~" + formatDay(date, 8),
				formatDay(date, 7) + "~" + formatDay(date, 1) };
		
		double[] systemReliabilityRate = (double[]) dataArrays[0];
		double[] overallReliabilityRate = (double[]) dataArrays[1];

		LineChart lineChart = new LineChart("");
		CategoryDataset dataSet = lineChart.createDataset(systemReliabilityRate, overallReliabilityRate, columnKeys1);
		File chartFile = lineChart.createChart(dataSet, "LineChart.jpeg", overallReliabilityRate, directory);
		lineChart.pack();
		return chartFile;
	}

	private Object[] getReliabilityData(Date date) {
		System.out.println("[INFO]: Prepare getting reliability data, please wait...");
		List<ReportInfo> reportList = modelJDBCTemplate.getWeeklyReliability(date);
		double[] systemReliabilityRate = modelJDBCTemplate.getWeeklySystemReliability(reportList);
		double[] overallReliabilityRate = modelJDBCTemplate.getWeeklyOverallReliability(reportList);
		Object[] arrays = new Object[] {systemReliabilityRate, overallReliabilityRate};
		System.out.println("[INFO]: Finish getting reliability data!");
		return arrays;
	}
	
	public String generateMailHtml(File directory, Date sendTime) {
		Map<String, ReportInfo> infoList = modelJDBCTemplate.generateReliabilityTable();
		
        System.out.println("[INFO]: velocity initing...");
		try {
			new VelocityParse("mailtemplate.vm", 
					infoList, null,
					getTopSystemErrorsTable(infoList.get("info_30day")),
					getTopUserErrorsTable(infoList.get("info_30day")),
					null, null, "mail.html", directory, sendTime);
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

	public MailSenderInfo getEmailContent(File embeddedImage, File directory, Date sendTime) {
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

		String content = generateMailHtml(directory, sendTime);
		mailInfo.setContent(content);
		
		return mailInfo;
	}
		
	// set day before current day
	public String formatDay(Date date, int previous) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 0 - previous);
		date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.US);
		return sdf.format(date);
	}
	
	public static void main(String[] args) {
		
		ReliabilityEmailJob reliabilityEmailJob = new ReliabilityEmailJob();
		Date sendTime = new Date();
		System.out.println("[INFO]: executing email sender of CI Build :" + sendTime);
		System.out.println("[INFO]: charting CI bulid reliability");
		try {
			File directory = new File(ReliabilityEmailJob.class.getResource("/").toURI());
			Date midNight = DateUtils.getMidnightZero(sendTime);
			Object[] dataArrays = reliabilityEmailJob.getReliabilityData(midNight);
			File embeddedImage = reliabilityEmailJob.drawChart(directory, midNight, dataArrays);	
			
			System.out.println("[INFO]: complete to chart CI bulid reliability!");
			System.out.println("[INFO]: generate an email...");
			
			SimpleMailSender sms = new SimpleMailSender();
			sms.sendHtmlSender(reliabilityEmailJob.getEmailContent(embeddedImage, directory, sendTime));		
			System.out.println("Email of CI bulid reliability sent: " + new Date());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}
}

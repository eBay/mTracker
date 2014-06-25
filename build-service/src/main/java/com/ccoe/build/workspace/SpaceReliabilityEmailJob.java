package com.ccoe.build.workspace;

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

import org.jfree.data.category.CategoryDataset;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ccoe.build.charts.LineChart;
import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.email.MailSenderInfo;
import com.ccoe.build.email.SimpleMailSender;
import com.ccoe.build.reliability.ErrorCode;
import com.ccoe.build.reliability.ReportInfo;
import com.ccoe.build.reliability.VelocityParse;
import com.ccoe.build.service.BuildServiceScheduler;
import com.ccoe.build.utils.ServiceConfig;



public class SpaceReliabilityEmailJob implements Job {
	private ApplicationContext context = null;
	private WorkSpaceReliabilityJDBC modelJDBCTemplate = null;
	private ServerReliabilityJDBC template = null;	
	
	public SpaceReliabilityEmailJob(){
		System.out.println("[INFO]: init WorkSpaceReliabilityJDBC and ServerReliabilityJDBC bean...");	
		context = new ClassPathXmlApplicationContext("workspace-reliability-spring-jdbc-config.xml");
		modelJDBCTemplate = (WorkSpaceReliabilityJDBC) context.getBean("WorkSpaceReliabilityJDBC");
		template = (ServerReliabilityJDBC) context.getBean("ServerReliabilityJDBC");
		System.out.println("[INFO]: finish initing  WorkSpaceReliabilityJDBC and ServerReliabilityJDBC bean!");
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("[INFO]: executing email sender of server and workspace reliability:" + new Date());
		System.out.println("[INFO]: charting linechart of server reliability and workspace reliability");
		File dirFile = BuildServiceScheduler.contextPath;
		File[] embeddedImages = drawChart(dirFile);	
		System.out.println("[INFO]: complete to chart image of server reliability and workspace reliability!");
		System.out.println("[INFO]: generate an email of server reliability and workspace reliability");
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(getEmailContent(embeddedImages, dirFile));		
		System.out.println("Email of server and workspace reliability sent: " + new Date());
	}
	
	public File[] drawChart(File directory) {
		String[] columnKeys1 = { formatDay(49) + "~" + formatDay(43),
				formatDay(42) + "~" + formatDay(36),
				formatDay(35) + "~" + formatDay(29),
				formatDay(28) + "~" + formatDay(22),
				formatDay(21) + "~" + formatDay(15),
				formatDay(14) + "~" + formatDay(8),
				formatDay(7) + "~" + formatDay(1) };
		
		List<ReportInfo> reportList = modelJDBCTemplate.getWeeklyReliability();
		List<ReportInfo> serverReportList = template.getWeeklyReliability();
		double[] systemReliabilityRate = null;
		double[] overallReliabilityRate = null;
		double[] serverSystemReliability = null;
		double[] serverOverallReliability = null;
		try {
			systemReliabilityRate = modelJDBCTemplate.getWeeklySystemReliability(reportList);
			overallReliabilityRate = modelJDBCTemplate.getWeeklyOverallReliability(reportList);
			serverSystemReliability = template.getWeeklySystemReliability(serverReportList);
			serverOverallReliability = template.getWeeklyOverallReliability(serverReportList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String lineImage = "WorkSpace.jpeg";
		String serverImage = "server.jpeg";
		LineChart workSpaceLineChart = new LineChart("");
		workSpaceLineChart.setTickUnit(2);
		CategoryDataset workSpaceDataSet = workSpaceLineChart.createDataset(systemReliabilityRate, overallReliabilityRate, columnKeys1);
		File workSpaceImageFile = workSpaceLineChart.createChart(workSpaceDataSet, lineImage, overallReliabilityRate, directory);
		LineChart serverLineChart = new LineChart("");
		CategoryDataset serverDataSet = serverLineChart.createDataset(serverSystemReliability, serverOverallReliability, columnKeys1);
		File serverImageFile = serverLineChart.createChart(serverDataSet, serverImage, serverOverallReliability, directory);
		workSpaceLineChart.pack();
		serverLineChart.pack();
		
		File[] imageFiles = new File[2];
		imageFiles[0] = workSpaceImageFile;
		imageFiles[1] = serverImageFile;
		return imageFiles;
	}

	public String generateMailHtml(File directory) {
		Map<String, ReportInfo> infoList = getReliabilityTable();
		Map<String, ReportInfo> ServerInfoList = getServerReliabilityTable();
		
		Date sendTime = new Date();
        System.out.println("[INFO]: velocity initing...");
		try {
			new VelocityParse("workspace.vm", infoList, ServerInfoList,
					getTopSystemErrorsTable(infoList.get("info_30day")),
					getTopUserErrorsTable(infoList.get("info_30day")),
					getServerSystemErrorsTable(ServerInfoList.get("serverInfo_30day")),
					getServerUserErrorsTable(ServerInfoList.get("serverInfo_30day")),
					"server.html", directory, sendTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("[INFO]: velocity init completed!");
		String readHtml = FileUtils.readFile(new File(directory, "html/server.html"));
		return readHtml;
	}


	private List<ErrorCode> getTopSystemErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenSystemError = modelJDBCTemplate.getTopTenError("System Error");
		modelJDBCTemplate.setDescriptionNPercentage(topTenSystemError, info30day.getFailedSessions());
		return topTenSystemError;
	}
	
	private List<ErrorCode> getTopUserErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenUserError = modelJDBCTemplate.getTopTenError("User Error");
		modelJDBCTemplate.setDescriptionNPercentage(topTenUserError, info30day.getFailedSessions());
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
	
	
	private List<ErrorCode> getServerSystemErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenSystemError = template.getTopTenError("System Error");
		template.setDescriptionNPercentage(topTenSystemError, info30day.getFailedSessions());
		return topTenSystemError;
	}
	
	private List<ErrorCode> getServerUserErrorsTable(ReportInfo info30day) {
		List<ErrorCode> topTenUserError = template.getTopTenError("User Error");
		template.setDescriptionNPercentage(topTenUserError, info30day.getFailedSessions());
		return topTenUserError;
	}

	private Map<String, ReportInfo> getServerReliabilityTable() {
		ReportInfo reportList1 = template.getReportInfoBeforeDay("1");
		ReportInfo reportList2 = template.getReportInfoBeforeDay("7");
		ReportInfo reportList3 = template.getReportInfoBeforeDay("30");
		
		Map<String, ReportInfo> infoList = new HashMap<String, ReportInfo>();
		infoList.put("serverInfo_24h", reportList1);
		infoList.put("serverInfo_7day", reportList2);
		infoList.put("serverInfo_30day", reportList3);
		
		return infoList;
	}

	public MailSenderInfo getEmailContent(File[] embeddedImages, File directory) {
		/**
		 * set content for the mail
		 */
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(ServiceConfig.get("scheduler.workspace.email.host"));
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(false);

		mailInfo.setFromAddress(ServiceConfig.get("scheduler.workspace.email.from"));
		String[] toAddresses = ServiceConfig.get("scheduler.workspace.email.to").split(";");
		for(String address : toAddresses) {
			address = address.trim();
		}
		mailInfo.setToAddresses(toAddresses);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		String sendDate = sdf.format(new Date());
		mailInfo.setSubject(ServiceConfig.get("scheduler.workspace.email.subject") + " " +sendDate);
		mailInfo.setMimepartMethod("related");

		try {
			BodyPart bodyPart1 = new MimeBodyPart();
			bodyPart1.setHeader("Content-Location", "work_space");
			FileDataSource fds1 = new FileDataSource(embeddedImages[0]);
			bodyPart1.setDataHandler(new DataHandler(fds1));
			
			BodyPart bodyPart2 = new MimeBodyPart();
			bodyPart2.setHeader("Content-Location", "server_image");
			FileDataSource fds2 = new FileDataSource(embeddedImages[1]);
			bodyPart2.setDataHandler(new DataHandler(fds2));
			
			mailInfo.getBodyParts().add(bodyPart1);
			mailInfo.getBodyParts().add(bodyPart2);
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

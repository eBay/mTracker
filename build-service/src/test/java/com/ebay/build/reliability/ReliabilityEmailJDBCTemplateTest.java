package com.ebay.build.reliability;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReliabilityEmailJDBCTemplateTest {
	private ApplicationContext context = new ClassPathXmlApplicationContext(
			"healthtrack-sping-jdbc-config.xml");
	private ReliabilityEmailJDBCTemplate modelJDBCTemplate = (ReliabilityEmailJDBCTemplate) context
			.getBean("ReliabilityEmailJDBCTemplate");

	@Test
	public void testGetErrorDescription() {		
		Map<String, ErrorCode> des = modelJDBCTemplate.getErrorDescription();
		System.out.println(des);
	}
	
	@Test
	public void testGetWeeklyReliability() {
		List<ReportInfo> info = modelJDBCTemplate.getWeeklyReliability();
		for(ReportInfo item : info) {
			System.out.println(item);
		}
	}
	
	@Test
	public void testGetWeeklySystemReliability() {
		List<ReportInfo> info = modelJDBCTemplate.getWeeklyReliability();
		for(ReportInfo list : info) {
			System.out.println(list.getSystemReliabilityRate());
		}
		double[] weeks = modelJDBCTemplate.getWeeklySystemReliability(info);
		for(double week : weeks ) {
			System.out.println(week);
		}
	}

}

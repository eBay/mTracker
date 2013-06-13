package com.ebay.build.persistent.healthcheck;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class HealthTrackJDBCTemplateTest {
	
	@Test
	public void testProcessReport() {
//		List<SummaryReport> report = new ArrayList<SummaryReport>();
//		
//		SummaryReport r1 = new SummaryReport();
//		r1.setBuildURL("http://r1.com");
//		r1.setSeverity("critical");
//		r1.setCount(10);
//		
//		SummaryReport r2 = new SummaryReport();
//		r2.setBuildURL("http://r1.com");
//		r2.setSeverity("major");
//		r2.setCount(20);
//		
//		SummaryReport r3 = new SummaryReport();
//		r3.setBuildURL("http://r2.com");
//		r3.setSeverity("major");
//		r3.setCount(30);
//		
//		report.add(r1);
//		report.add(r2);
//		report.add(r3);
//		
//		HealthTrackJDBCTemplate template = new HealthTrackJDBCTemplate();
//		Map<String, SummaryReport> result = template.processReport(report);
//		assertEquals(2, result.size());
//		
//		SummaryReport s1 = result.get("http://r1.com");
//		
//		assertEquals(10, s1.getCriticalCount());
//		assertEquals(20, s1.getMajorCount());
//		
//		SummaryReport s2 = result.get("http://r2.com");
//		assertEquals(0, s2.getCriticalCount());
//		assertEquals(30, s2.getMajorCount());
	}
}
package com.ebay.build.reliability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Test;

import com.ebay.build.email.SimpleMailSender;

public class ReliabilityEmailJobTest {
	
	@Test
	public void testDrawChart() {
		ReliabilityEmailJob job = new ReliabilityEmailJob();
		File file = null;
		try {
			file = new File(ReliabilityEmailJob.class.getResource("/").toURI());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		assertNotNull(file);
		
		File embeddedImage = job.drawChart(file);
		assertNotNull(embeddedImage);
		assertTrue(embeddedImage.exists());
		assertEquals(embeddedImage.getAbsoluteFile().getParentFile().getParentFile(), file);
		assertTrue(embeddedImage.getAbsoluteFile().toString().endsWith("LineChart.jpeg"));
		embeddedImage.delete();
		assertFalse(embeddedImage.exists());
	}
	
	@Test
	public void sendEmail() {
		ReliabilityEmailJob emailJob = new ReliabilityEmailJob();
		System.out.println("[INFO]: executing email sender of CI Build :" + new Date());
		System.out.println("[INFO]: charting CI bulid reliability");
		File directory = new File("./target/classes");
		File embeddedImage = emailJob.drawChart(directory);	
		
		System.out.println("[INFO]: complete to chart CI bulid reliability!");
		System.out.println("[INFO]: generate an email...");
		
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(emailJob.getEmailContent(embeddedImage, directory));		
		System.out.println("Email of CI bulid reliability sent: " + new Date());		
	}
}


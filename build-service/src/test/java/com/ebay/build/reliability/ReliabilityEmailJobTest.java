package com.ebay.build.reliability;

import java.util.Date;

import org.junit.Test;

import com.ebay.build.email.SimpleMailSender;

public class ReliabilityEmailJobTest {
	@Test
	public void test() {
		ReliabilityEmailJob job = new ReliabilityEmailJob();
		System.out.println("[Info]: executing email sender :" + new Date());
		System.out.println("[Info]: init the spring bean...");
		job.emailSummaryPageJob();
		System.out.println("[Info]: finish initing spring bean!");
		System.out.println("[Info]: charting ...");
		job.drawChart();
		System.out.println("[Info]: charting completed!");
		System.out.println("[Info]: generate an email...");

		SimpleMailSender sms = new SimpleMailSender();
		sms.sendHtmlSender(job.getEmailContent());
		System.out.println("[Info]: email sent: " + new Date());

	}
}

package com.ebay.build.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class SimpleMailSenderTest {
	
	private GreenMail greenMail;
	private static final int SMTP_PORT = 1618;
	private MailSenderInfo mailSenderInfo;
	
	@Before
	public void setUp() throws Exception {
		mailSenderInfo = new MailSenderInfo();
		greenMail = new GreenMail(new ServerSetup(SMTP_PORT, null, "smtp"));
		greenMail.start();
	}
	
	@Test
	public void testSendHtmlSender() throws IOException, MessagingException, InterruptedException {
		SimpleMailSender sms = new SimpleMailSender();	
		mailSenderInfo.setContent("icegreen greenmail");
		mailSenderInfo.setSubject("icegree");
		mailSenderInfo.setMailServerHost("localhost");
		mailSenderInfo.setFromAddress("xiaobao@local.com");
		mailSenderInfo.setMailServerPort("1618");
		String[] ads = {"xiaobao@local.com"};
		mailSenderInfo.setToAddresses(ads);
		sms.sendHtmlSender(mailSenderInfo);
		assertTrue(greenMail.waitForIncomingEmail(5000, 1));
		MimeMessage[]  message = greenMail.getReceivedMessages();
		assertEquals(1, message.length);
		assertEquals((String) message[0].getSubject(), "icegree");
		assertTrue(message[0].getContent().toString().contains("icegreen greenmail"));
	}
	
	@After
	public void tearDown() {
		greenMail.stop();
	}

}

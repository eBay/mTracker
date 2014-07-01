/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ccoe.build.email.MailSenderInfo;
import com.ccoe.build.email.SimpleMailSender;
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

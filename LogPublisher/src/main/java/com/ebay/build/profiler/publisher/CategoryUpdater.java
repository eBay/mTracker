package com.ebay.build.profiler.publisher;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.StringUtils;

public class CategoryUpdater {
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jdbc-config.xml");
		LoaderProcessor processor = applicationContext.getBean("loaderProcessor", LoaderProcessor.class);

		List<Session> sessions = processor.querySessionsWithoutCategory();
		SessionTransformer transformer = new SessionTransformer();
		
		for (Session session : sessions) {
			transformer.tranform(session);
			if (!StringUtils.isEmpty(session.getCategory())) {
				System.out.println("Updating " + session.getId() + "  --> " + session.getCategory());
				processor.updateSessionCategory(session);
			} else {
				System.out.println("NO category " + session.getId());
			}
		}
	}
}

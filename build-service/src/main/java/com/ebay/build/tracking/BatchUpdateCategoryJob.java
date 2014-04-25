package com.ebay.build.tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ebay.build.profiler.filter.SessionTransformer;
import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.StringUtils;
import com.ebay.build.tracking.jdbc.SessionJDBCTemplate;

public class BatchUpdateCategoryJob implements Job {
	private ApplicationContext context = null;
    private final SessionJDBCTemplate sessionJDBCTemplate;

	public BatchUpdateCategoryJob() {
		 context = new ClassPathXmlApplicationContext("tracking-spring-jdbc-config.xml");
         sessionJDBCTemplate = (SessionJDBCTemplate) context.getBean("sessionJDBCTemplate");
	}
	
	public void execute(JobExecutionContext context) {
		System.out.println("[INFO] " + new Date() + " Start executing BatchUpdateCategoryJob...");
		List<Session> sessions = sessionJDBCTemplate.getExpSessionWithNullCategory();
		SessionTransformer transformer = new SessionTransformer();
		
		List<Session> batchUpdates = new ArrayList<Session>();
		for (Session session : sessions) {
			transformer.tranform(session);
			if (!StringUtils.isEmpty(session.getCategory())) {
				System.out.println("Updating " + session.getId() + "  --> " + session.getCategory());
				batchUpdates.add(session);
			} else {
				System.out.println("NO category " + session.getId());
			}
		}
		sessionJDBCTemplate.batchUpdateCategory(batchUpdates);
		System.out.println("DONE!");
	}
	
    public static void main(String[] args) {
    	BatchUpdateCategoryJob job = new BatchUpdateCategoryJob();
        job.execute(null);
    }

}

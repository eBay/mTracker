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

package com.ccoe.build.tracking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ccoe.build.core.filter.SessionTransformer;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.utils.StringUtils;
import com.ccoe.build.tracking.jdbc.SessionJDBCTemplate;

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

package com.ebay.build.profiler.publisher;

import java.util.List;

import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.processors.LoaderProcessor;
import com.ebay.build.cal.query.utils.StringUtils;

public class CategoryUpdater {

	public static void main(String[] args) {
		LoaderProcessor processor = new LoaderProcessor();
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

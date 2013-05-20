package com.ebay.build.profiler.publisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ebay.build.cal.model.Session;
import com.ebay.build.profiler.filter.FilterFactory;
import com.ebay.build.profiler.filter.model.Cause;
import com.ebay.build.profiler.filter.model.Filter;

public class SessionTransformer {

	private final List<Filter> filters = new ArrayList<Filter>();
	private final static String FILTER_LIST_IN_GIT = "";

	public SessionTransformer() {
		FilterFactory factory = new FilterFactory();
		URL defaultFilterList = this.getClass().getResource("/default-filters.xml");
		URL remoteFilterList = null;
		try {
			System.out.println("[INFO] SessionTransformer Local List File: " + defaultFilterList);
			remoteFilterList = new URL(FILTER_LIST_IN_GIT);
			System.out.println("[INFO] SessionTransformer Remote List File: " + remoteFilterList);			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
			
		filters.addAll(factory.build(remoteFilterList, defaultFilterList));
			
		System.out.println("[INFO] SessionTransformer loaded " + filters.size() + " filters.");
		
	}
	
	public void tranform(List<Session> sessions) {
		for (Session session : sessions) {
			if (session.getFullStackTrace() != null) {
				for (Filter filter : this.filters) {
					if (isMatch(session.getFullStackTrace(), filter)) {
						session.setCategory(filter.getCategory());
						break; // assumed that one session fits into one category
					}
				}
			}
		}
	}

	protected boolean isMatch(String fullstack, Filter filter) {
		List<String> keywords = new ArrayList<String>();

		for (Cause cause : filter.getCause()) {
			keywords.add(cause.getKeyword());
		}

		for (String keyword : keywords) {
			if (!fullstack.contains(keyword)) {
				return false;
			}
		}
		return true;
	}
}

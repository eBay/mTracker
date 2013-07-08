package com.ebay.build.profiler.publisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profiler.filter.FilterFactory;
import com.ebay.build.profiler.filter.model.Cause;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.model.Session;

public class SessionTransformer {

	private final List<Filter> filters = new ArrayList<Filter>();
	public final static String FILTER_LIST_IN_GIT = "https://github.scm.corp.ebay.com/DevExTech/maven-time-tracking/raw/master/LogPublisher/src/main/resources/default-filters.xml";

	public SessionTransformer() {
		FilterFactory factory = new FilterFactory();
		URL defaultFilterList = this.getClass().getResource("/default-filters.xml");
		URL remoteFilterList = null;
		try {
			remoteFilterList = new URL(FILTER_LIST_IN_GIT);
		} catch (MalformedURLException e) {
			System.err.println("[WARNING] can not load the filter list from remote " + remoteFilterList);
		}
			
		filters.addAll(factory.build(remoteFilterList, defaultFilterList));
			
		System.out.println("[INFO] SessionTransformer loaded " + filters.size() + " filters.");
		
	}
	
	public void tranform(Session session) {
		if (session.getFullStackTrace() != null) {
			for (Filter filter : this.filters) {
				if (isMatch(session.getFullStackTrace(), filter)) {
					session.setCategory(filter.getCategory());
					session.setFilter(filter.getName());
					break; // assumed that one session fits into one category
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

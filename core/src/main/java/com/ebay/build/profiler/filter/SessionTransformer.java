package com.ebay.build.profiler.filter;

import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.model.Session;

public class SessionTransformer {

	private final List<Filter> filters = new ArrayList<Filter>();
	private final FilterMatcher matcher = new FilterMatcher();

	public SessionTransformer() {
		FilterFactory factory = new MavenBuildFilterFactory();
		filters.addAll(factory.getFilters());
		System.out.println("[INFO] SessionTransformer loaded " + filters.size() + " filters.");
	}
	
	public void tranform(Session session) {
		if (session.getFullStackTrace() != null) {
			for (Filter filter : this.filters) {
				if (matcher.isMatch(session.getFullStackTrace(), filter)) {
					session.setCategory(filter.getCategory());
					session.setFilter(filter.getName());
					break; // assumed that one session fits into one category
				}
			}
		}
	}
}

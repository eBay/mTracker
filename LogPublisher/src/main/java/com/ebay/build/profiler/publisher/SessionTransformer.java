package com.ebay.build.profiler.publisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ebay.build.profiler.filter.FilterFactory;
import com.ebay.build.profiler.filter.model.Cause;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.StringUtils;

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
		for (Cause cause : filter.getCause()) {
			if (!StringUtils.isEmpty(cause.getKeyword())) {
				if (!fullstack.contains(cause.getKeyword())) {
					return false;
				}
			}
			if (!StringUtils.isEmpty(cause.getPattern())) {
				if (!Pattern.compile(cause.getPattern(), Pattern.DOTALL).matcher(fullstack).matches()) {
				//if (StringUtils.isEmpty(StringUtils.getFirstFound(fullstack, cause.getPattern(), true))) {
					return false;
				}
			}
		}
		return true;
	}
}

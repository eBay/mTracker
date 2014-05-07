package com.ebay.build.profiler.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ebay.build.profiler.filter.model.Filter;

public class RideErrorClassifier {
	private final List<Filter> filters = new ArrayList<Filter>();
	private final FilterMatcher matcher = new FilterMatcher();

	public RideErrorClassifier() {
		FilterFactory factory = new RideFilterFactory();
		filters.addAll(factory.getFilters());
		System.out.println("[INFO] RideTransformer loaded " + filters.size() + " filters.");
	}
	
	public Filter doClassify(String what, String exception) {
		if(what == null || exception == null)
			return null;
		HashMap<String, String> source = new HashMap<String, String>();
		source.put("what", what);
		source.put("exception", exception);
		source.put(null, exception);   //in case that filter rule will not contains source information, by default it means exception
		for (Filter filter : this.filters) {
			if (matcher.isMatch(source, filter)) {
				return filter; // assumed that one session fits into one category
			}
		}
		return null;
	}
	
	
}

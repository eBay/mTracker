package com.ebay.build.profiler.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ebay.build.profiler.filter.model.Filter;

public class ErrorClassifier {
	protected  List<Filter> filters = new ArrayList<Filter>();
	private  FilterMatcher matcher = new FilterMatcher();

	public ErrorClassifier(List<Filter> lsFilters) {
		if(lsFilters != null)
			filters.addAll(lsFilters);
		Logger.getLogger(ErrorClassifier.class.getName()).log(Level.INFO, "ErrorClassifier loaded " + filters.size() + " filters.");
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

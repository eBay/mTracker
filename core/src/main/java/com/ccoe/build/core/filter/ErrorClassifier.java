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

package com.ccoe.build.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ccoe.build.core.filter.model.Filter;

public class ErrorClassifier {
	private UDCFilterFactory factory;
	public void setFactory(UDCFilterFactory factory) {
		this.factory = factory;
		filters.addAll(factory.getFilters());
		Logger.getLogger(ErrorClassifier.class.getName()).log(Level.INFO, 
				"Load " + filters.size() + " filter rules");
	}

	protected  List<Filter> filters = new ArrayList<Filter>();
	private  FilterMatcher matcher = new FilterMatcher();
	
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

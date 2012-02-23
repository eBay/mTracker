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
import java.util.List;

import com.ccoe.build.core.filter.model.Filter;
import com.ccoe.build.core.model.Session;

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

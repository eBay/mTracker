package com.ebay.build.profiler.filter;

import java.net.MalformedURLException;
import java.net.URL;

public class MavenBuildFilterFactory extends FilterFactory {

	public final static String FILTER_LIST_FILE = "/default-filters.xml";
	public final static String FILTER_LIST_IN_GIT = BASE_FILTER_LIST_IN_GIT + FILTER_LIST_FILE;
	
	@Override
	public URL getRemoteFilterURL() {
		try {
			return new URL(FILTER_LIST_IN_GIT);
		} catch (MalformedURLException e) {
			System.err.println("[WARNING] can not load the filter list from remote " + FILTER_LIST_IN_GIT);
		}
		return null;
	}

	@Override
	public URL getLocalFilterURL() {
		 return this.getClass().getResource(FILTER_LIST_FILE);
	}
}

package com.ebay.build.profiler.filter;

import java.net.MalformedURLException;
import java.net.URL;

public class UDCFilterFactory extends FilterFactory {

	protected String filterFile;
	
	protected String filterFileInGit;
	
	public UDCFilterFactory(){
		super();
	}
	public UDCFilterFactory(String file){
		this.filterFile = file;
		filterFileInGit = BASE_FILTER_LIST_IN_GIT + filterFile;
	}
	
	public void setFilterFile(String filterFile) {
		this.filterFile = filterFile;
		filterFileInGit = BASE_FILTER_LIST_IN_GIT + filterFile;
	}
	
	@Override
	public URL getRemoteFilterURL() {
		try {
			return new URL(filterFileInGit);
		} catch (MalformedURLException e) {
			System.err.println("[WARNING] can not load the filter list from remote " + filterFileInGit);
		}
		return null;
	}

	@Override
	public URL getLocalFilterURL() {
		 return this.getClass().getResource(filterFile);
	}
}

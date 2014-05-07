package com.ebay.build.profiler.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ebay.build.profiler.filter.model.Category;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.filter.model.Filters;

public class RideFilterFactory extends FilterFactory {
	
	public final static String FILTER_LIST_FILE = "/ride-filters.xml";
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

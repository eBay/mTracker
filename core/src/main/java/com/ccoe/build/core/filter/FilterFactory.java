package com.ccoe.build.core.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ccoe.build.core.filter.model.Category;
import com.ccoe.build.core.filter.model.Filter;
import com.ccoe.build.core.filter.model.Filters;

public abstract class FilterFactory {
	
	public final static String BASE_FILTER_LIST_IN_GIT = "https://github.scm.corp.ebay.com/DevExTech/maven-time-tracking/raw/master/core/src/main/resources";
	
	public abstract URL getRemoteFilterURL();
	public abstract URL getLocalFilterURL();
	
	protected void marshal(File reportFile,  Filters filters){
		try {
			JAXBContext jc;
		
			jc = JAXBContext.newInstance(Filters.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(filters, reportFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
	}
	
	protected Filters unmarshal(File reportFile) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Filters.class);
			Unmarshaller u = jc.createUnmarshaller();
			return (Filters) u.unmarshal(new FileInputStream(reportFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Filters();
	}
	
	protected Filters unmarshal(InputStream is) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(Filters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (Filters) u.unmarshal(is);
	}
	
	protected Filters unmarshal(URL aURL) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(Filters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (Filters) u.unmarshal(aURL);
	}
	
	protected List<Filter> build(URL url, URL defaultFilterList) {
		Filters filters = new Filters();

		try {
			filters = unmarshal(url);
		} catch (Exception e) {
			System.err.println("Can not unmarshall the remote file " + url + " Caused by " + e.getMessage());
			try {
				filters = unmarshal(defaultFilterList);
			} catch (JAXBException e1) {
				System.err.println("Can not unmarshall the local file " + defaultFilterList + " Caused by " + e.getMessage());
			}
		}
		
		List<Filter> results = new ArrayList<Filter>();
		
		for (Category category : filters.getCategory()) {
			for (Filter filter : category.getFilter()) {
				filter.setCategory(category.getName());
				results.add(filter);
			}
		}
		return results;
	}
	
	public List<Filter> getFilters() {
		return build(this.getRemoteFilterURL(), this.getLocalFilterURL());
	}
}

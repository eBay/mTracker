package com.ebay.build.profiler.filter;

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

import com.ebay.build.profiler.filter.model.Category;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.filter.model.Filters;

public class FilterFactory {
	public void marshal(File reportFile,  Filters filters){
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
	
	public Filters unmarshal(File reportFile) {
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
	
	public Filters unmarshal(InputStream is) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(Filters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (Filters) u.unmarshal(is);
	}
	
	public Filters unmarshal(URL aURL) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(Filters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (Filters) u.unmarshal(aURL);
	}
	
	public List<Filter> build(URL url, URL defaultFilterList) {
		Filters filters = new Filters();

		try {
			filters = unmarshal(url);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				filters = unmarshal(defaultFilterList);
			} catch (JAXBException e1) {
				e1.printStackTrace();
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
}

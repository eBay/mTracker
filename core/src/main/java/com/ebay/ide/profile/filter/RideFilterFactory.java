package com.ebay.ide.profile.filter;

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

import com.ebay.ide.profile.filter.model.RideCategory;
import com.ebay.ide.profile.filter.model.RideFilter;
import com.ebay.ide.profile.filter.model.RideFilters;

public class RideFilterFactory {
	
	private void marshal(File reportFile,  RideFilters RideFilters){
		try {
			JAXBContext jc;
		
			jc = JAXBContext.newInstance(RideFilters.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(RideFilters, reportFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
	}
	
	private RideFilters unmarshal(File reportFile) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(RideFilters.class);
			Unmarshaller u = jc.createUnmarshaller();
			return (RideFilters) u.unmarshal(new FileInputStream(reportFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new RideFilters();
	}
	
	private RideFilters unmarshal(InputStream is) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(RideFilters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (RideFilters) u.unmarshal(is);
	}
	
	private RideFilters unmarshal(URL aURL) throws JAXBException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(RideFilters.class);
		Unmarshaller u = jc.createUnmarshaller();
		return (RideFilters) u.unmarshal(aURL);
	}
	
	private List<RideFilter> build(URL url) throws JAXBException {
		RideFilters RideFilters = new RideFilters();
		try {
			RideFilters = unmarshal(url);
		} catch (JAXBException e) {
			System.err.println("Can not unmarshall the remote file " + url + " Caused by " + e.getMessage());
			throw e;
		}
		
		List<RideFilter> results = new ArrayList<RideFilter>();
		
		for (RideCategory category : RideFilters.getCategory()) {
			for (RideFilter filter : category.getFilter()) {
				filter.setCategory(category.getName());
				results.add(filter);
			}
		}
		return results;
	}
	
	public List<RideFilter> getRideFilters(URL filterFile) throws JAXBException {
		return build(filterFile);
	}
	public List<RideFilter> getRideFilters() throws JAXBException {
		//URL defaultFile = this.getClass().getResource("/ride-filters.xml");
		URL defaultFile = this.getClass().getResource("/ride-filters.xml");
		return getRideFilters(defaultFile);
	}
	
	public static void main(String[] args) throws JAXBException{
		RideFilterFactory factory = new RideFilterFactory();
		URL url = factory.getClass().getResource("/ride-filters.xml");
		List<RideFilter> ls = factory.getRideFilters(url);
		for(RideFilter filter: ls){
			System.out.println(filter.toString());
		}
	}
	
}

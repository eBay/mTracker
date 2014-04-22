package com.ebay.ide.profile.filter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RideCategory {
	@XmlAttribute
	private String name;
	
	@XmlElement(name="filter")
	private List<RideFilter> filters = new ArrayList<RideFilter>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<RideFilter> getFilter() {
		return this.filters;
	}
	
	public void setFilter(List<RideFilter> lsRideFilter){
		this.filters = lsRideFilter;
	}
}

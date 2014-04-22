package com.ebay.ide.profile.filter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "filters")
public class RideFilters {
	@XmlElement(name="category")
	List<RideCategory> categories = new ArrayList<RideCategory>();
	
	public List<RideCategory> getCategory() {
		return this.categories;
	}
	public void setCategory(List<RideCategory> ls){
		this.categories = ls;
	}
}

package com.ebay.build.core.filter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "filters")
public class Filters {
	@XmlElement(name="category")
	List<Category> categories = new ArrayList<Category>();
	
	public List<Category> getCategory() {
		return this.categories;
	}
}

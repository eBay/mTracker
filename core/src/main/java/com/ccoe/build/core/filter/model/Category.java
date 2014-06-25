package com.ccoe.build.core.filter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Category {
	@XmlAttribute
	private String name;
	
	@XmlElement(name="filter")
	private List<Filter> filters = new ArrayList<Filter>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Filter> getFilter() {
		return this.filters;
	}
}

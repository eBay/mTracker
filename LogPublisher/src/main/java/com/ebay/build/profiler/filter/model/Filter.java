package com.ebay.build.profiler.filter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Filter {
	@XmlAttribute
	private String name;
	
	private String category;

	@XmlElement(name="cause")
	private List<Cause> cause = new ArrayList<Cause>();
	
	public List<Cause> getCause() {
		return this.cause;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}

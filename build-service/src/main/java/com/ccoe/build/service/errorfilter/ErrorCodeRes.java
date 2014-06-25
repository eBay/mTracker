package com.ccoe.build.service.errorfilter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorCodeRes {
	
	private String name;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}

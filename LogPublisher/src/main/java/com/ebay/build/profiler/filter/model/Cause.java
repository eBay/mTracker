package com.ebay.build.profiler.filter.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Cause {
	@XmlAttribute
	private String keyword;
	
	@XmlAttribute
	private String pattern;


	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getPattern() {
		return this.pattern;
	}
	
	public void setPattern(String p) {
		this.pattern = p;
	}
}

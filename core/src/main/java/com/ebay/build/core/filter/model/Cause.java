package com.ebay.build.core.filter.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Cause {
	
	@XmlAttribute
	private String source;
	
	@XmlAttribute
	private String keyword;
	
	@XmlAttribute
	private String pattern;

	@XmlAttribute
	private String value;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
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
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

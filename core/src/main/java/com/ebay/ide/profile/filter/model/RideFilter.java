package com.ebay.ide.profile.filter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ebay.build.profiler.utils.StringUtils;

@XmlAccessorType(XmlAccessType.FIELD)
public class RideFilter {
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String description;
	
	private String category;

	@XmlElement(name="cause")
	private List<RideCause> lsCause = new ArrayList<RideCause>();
	
	public List<RideCause> getCause() {
		return this.lsCause;
	}
	public void setCause(List<RideCause> lsCause){
		this.lsCause = lsCause;
	}
	
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
		this.description= description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public boolean isMatch(String what, String exception){
		if(what == null || exception == null)
			return false;
		
		//check exception
		for (RideCause cause : lsCause) {
			if(!StringUtils.isEmpty(cause.getSource())){
				if(cause.getSource().equalsIgnoreCase("what")&&!FilterEngineer.check(what, cause))
					return false;
				if(cause.getSource().equalsIgnoreCase("exception_") && !FilterEngineer.check(exception, cause))
					return false;
			}else
				if(!FilterEngineer.check(exception, cause))
					return false;
			
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "RideFilter [name=" + name + ", description=" + description
				+ ", category="
				+ category + ", cause=" + lsCause + "]";
	}
	
	
}
class FilterEngineer{
	public static boolean check(String value, RideCause cause){
		if(!StringUtils.isEmpty(cause.getValue())){
			if(!value.equals(value))
				return false;
		}
		if (!StringUtils.isEmpty(cause.getKeyword())) {
			if (!value.contains(cause.getKeyword())) {
				return false;
			}
		}
		if (!StringUtils.isEmpty(cause.getPattern())) {
			if (!Pattern.compile(cause.getPattern(), Pattern.DOTALL).matcher(value).matches()) {
				return false;
			}
		}
		return true;
	}
}
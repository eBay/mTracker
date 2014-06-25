package com.ccoe.build.alerts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name = "Rules")
public class Rules {
	@XmlElement(name = "Rule")
	List<Rule> rulelist = new ArrayList<Rule>();

	public List<Rule> getRulelist() {
		return rulelist;
	}
}
package com.ebay.build.profile.render;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProfileOutputJSON {
	private List<String> projects = new LinkedList<String>();
	private Map<String,LinkedList<Long>> plugin = new LinkedHashMap<String,LinkedList<Long>>();
	
	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
	public List<String> getProjects() {
		return projects;
	}
	public void setPlugin(Map<String,LinkedList<Long>> plugin) {
		this.plugin = plugin;
	}
	public Map<String,LinkedList<Long>> getPlugin() {
		return plugin;
	}
	
	
}

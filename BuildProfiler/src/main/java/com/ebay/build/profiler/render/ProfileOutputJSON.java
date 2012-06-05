package com.ebay.build.profiler.render;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProfileOutputJSON {
	private Set<String> projects = new LinkedHashSet<String>();
	private Map<String,LinkedList<Long>> plugin = new LinkedHashMap<String,LinkedList<Long>>();
	private Map<String, Long> timingSlice = new LinkedHashMap<String, Long>();
	

	public void setPlugin(Map<String,LinkedList<Long>> plugin) {
		this.plugin = plugin;
	}
	public Map<String, Long> getTimingSlice() {
		return timingSlice;
	}
	public void setTimingSlice(Map<String, Long> timingSlice) {
		this.timingSlice = timingSlice;
		this.projects = timingSlice.keySet();
	}
	public Map<String,LinkedList<Long>> getPlugin() {
		return plugin;
	}
	
	public void toCSV() {
		for(Entry<String,LinkedList<Long>> entry : plugin.entrySet()) {
			System.out.println(csvLine(entry));
		}
	}
	
	private String csvLine(Entry<String, LinkedList<Long>> entry) {
		StringBuilder sb = new StringBuilder();
		sb.append(entry.getKey());
		for(Long l : entry.getValue()) {
			sb.append(",");
			sb.append(l.toString());
		}
		
		return sb.toString();
	}
}

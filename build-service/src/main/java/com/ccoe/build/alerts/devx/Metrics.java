package com.ccoe.build.alerts.devx;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
	
	private String collection;
	private Map<String, String> metrics = new HashMap<String, String>();
	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public Map<String, String> getMetrics() {
		return metrics;
	}
}

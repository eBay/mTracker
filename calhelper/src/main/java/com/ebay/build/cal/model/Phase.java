package com.ebay.build.cal.model;

import java.util.ArrayList;
import java.util.List;

public class Phase extends TrackingModel {
	private String name;
	
	private List<Plugin> plugins =  new ArrayList<Plugin>();
	
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<Plugin> plugins) {
		this.plugins = plugins;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();
		appendTransacionStart(sBuffer, 3, "Phase", getName());
		for (Plugin plugin : this.getPlugins()) {
			appendLine(sBuffer, plugin.toString());
		}
		appendTransacionEnd(sBuffer, 3, "Phase", getName(), getStatus(), getDuration().toString());
		return sBuffer.toString();
	}
}

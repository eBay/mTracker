package com.ebay.build.cal.model;


public class Pool {
	private String id;
	private String name;
	private String gitURL;
	
	private Machine machine;
	
	public void setMachine(Machine m) {
		this.machine = m;
	}
	
	public Machine getMachine() {
		return machine;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGitURL() {
		return gitURL;
	}
	public void setGitURL(String gitURL) {
		this.gitURL = gitURL;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}

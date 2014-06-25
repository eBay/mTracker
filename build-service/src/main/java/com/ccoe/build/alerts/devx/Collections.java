package com.ccoe.build.alerts.devx;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "collections")
public class Collections {
	@XmlElement(name="collection")
	List<Collection> collections = new ArrayList<Collection>();
	
	public List<Collection> getCollections() {
		return this.collections;
	}
}

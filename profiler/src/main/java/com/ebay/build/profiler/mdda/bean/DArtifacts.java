package com.ebay.build.profiler.mdda.bean;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Artifacts")
public class DArtifacts {
	@XmlElement(name="Artifact")
	List<DArtifact> dArtifactList = new ArrayList<DArtifact>();
	
	public List<DArtifact> getDArtifactList() {
		return dArtifactList;
	}
}
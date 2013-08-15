package com.ebay.build.profiler.mdda.bean;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import com.ebay.build.profiler.mdda.util.FormatTransform;

@XmlAccessorType(XmlAccessType.FIELD)
public class DArtifact {

	@XmlAttribute
	String localRepo="";

	@XmlAttribute
	String group_id="";

	@XmlAttribute
	String artifact_id="";

	@XmlAttribute
	String version="";

	@XmlAttribute
	private
	String repositoryURL="";
	
	@XmlAttribute
	private
	String repositoryId="";

	@XmlAttribute
	String classifier="";

	@XmlAttribute
	String extension="";

	@XmlAttribute
	String quick_url="";
	
	@XmlAttribute
	long size=0;
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean artifactOK(long size){
		
		return this.size==size;
	
	}
	
	public DArtifact() {
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getArtifact_id() {
		return artifact_id;
	}

	public void setArtifact_id(String artifact_id) {
		this.artifact_id = artifact_id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getQuick_url() {
		return quick_url;
	}

	public void setQuick_url(String quick_url) {
		this.quick_url = quick_url;
	}

	public boolean equals(Object dItem) {
		DArtifact dItem2 = null;

		if (!(dItem instanceof DArtifact)) {
			return false;
		} else {
			dItem2 = (DArtifact) dItem;
		}

		boolean T1 = false;
		boolean T2 = false;
		boolean T3 = false;
		boolean T4 = false;
		boolean T5 = false;
		boolean T6 = false;
		
		if (this.artifact_id.equals(dItem2.artifact_id)) {
			T1 = true;
		}
		if (this.group_id.equals(dItem2.group_id)) {
			T2 = true;
		}
		if (this.version.equals(dItem2.version)) {
			T3 = true;
		}
		if (this.classifier.equals(dItem2.classifier)) {
			T4 = true;
		}
		if (this.extension.equals(dItem2.extension)) {
			T5 = true;
		}
		if (this.size == (dItem2.getSize())){
			T6 = true;
		}

		if (T1 && T2 && T3 && T4 && T5 && T6) {
			return true;
		}

		return false;
	}
	
	public int hashCode() {
		return (this.group_id + this.artifact_id + this.version + this.classifier + this.extension).hashCode(); 
	}

	public void generateUrl() {
		quick_url = repositoryURL;

		if (!quick_url.endsWith("/")) {
			quick_url += "/";
		}
		quick_url += FormatTransform.transform2(group_id) + "/" + artifact_id + "/" + version + "/"
				+ artifact_id + "-" + version;

		if (classifier != "") {
			quick_url += "-" + classifier;
		}
		quick_url += "." + extension;
	}
	
	public File generateFilePath() { 
		String filename = artifact_id + "-" + version;

		if (classifier != "") {
			filename += "-" + classifier;
		}

		filename += "." + extension;
		
		return new File(generateFolderPath(), filename);
	}
	
	public File generateFolderPath(){
		return new File(localRepo, FormatTransform.transform2(group_id)+ "/" + artifact_id + "/" + version + "/");
	}

	public String getRepositoryURL() {
		return repositoryURL;
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	public String getLocalRepo() {
		return localRepo;
	}

	public void setLocalRepo(String localRepo) {
		this.localRepo = localRepo;
	}
	
}

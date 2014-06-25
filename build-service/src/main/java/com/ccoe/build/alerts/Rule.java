package com.ccoe.build.alerts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Rule {

	@XmlAttribute
	String collection="";
	
	@XmlAttribute
	String field="";

	@XmlAttribute
	String operator="";

	@XmlAttribute
	String goal = "";
	
	@XmlAttribute
	String lower = "";
	
	@XmlAttribute
	String upper = "";
	
	@XmlAttribute
	String collectionPresentName = "";
	
	@XmlAttribute
	String fieldPresentName = "";
	
	@XmlAttribute
	String link = "";

	public String getCollectionPresentName() {
		return collectionPresentName;
	}

	public void setCollectionPresentName(String collectionPresentName) {
		this.collectionPresentName = collectionPresentName;
	}

	public String getFieldPresentName() {
		return fieldPresentName;
	}

	public void setFieldPresentName(String fieldPresentName) {
		this.fieldPresentName = fieldPresentName;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getLower() {
		return lower;
	}

	public void setLower(String lower) {
		this.lower = lower;
	}

	public String getUpper() {
		return upper;
	}

	public void setUpper(String upper) {
		this.upper = upper;
	}
	
	public String getLink() {
		return this.link;
	}
	
	public void setLink(String l) {
		this.link = l;
	}

}

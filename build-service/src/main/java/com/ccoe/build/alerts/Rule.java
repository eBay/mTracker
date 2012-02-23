/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

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

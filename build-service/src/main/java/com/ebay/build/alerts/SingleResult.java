package com.ebay.build.alerts;

public class SingleResult {

	String collection;
	
	String color;
	
	String field;
	
	String nowValue;
	
	String operator;

	String threshold;


	public SingleResult(String collection, String field, String nowValue, String operator, String threshold) {
		
		this.collection = collection;
		this.field = field;
		this.nowValue = nowValue;
		this.operator = operator;
		this.threshold = threshold;
	}


	public String getCollection() {
		return collection;
	}


	public void setCollection(String collection) {
		this.collection = collection;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getNowValue() {
		return nowValue;
	}


	public void setNowValue(String nowValue) {
		this.nowValue = nowValue;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public String getThreshold() {
		return threshold;
	}


	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	
}

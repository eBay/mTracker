package com.ebay.build.alerts;

public class SingleResult {

	private String collection;
	
	private String field;
	
	private String nowValue;
	
	private String operator;

	private String threshold;
	
	private String outOfThreshold;
	
	private String thresholdColor;
	
	private String beforeValue;
	
	private String deltaThreshold;
		
	private String outOfThresholdDelta;
	
	private String thresholdDeltaColor;

	public SingleResult(String collection, String field, String nowValue, String operator, String threshold, String outOfThreshold, 
			String thresholdColor, String beforeValue, String deltaThreshold, String outOfThresholdDelta, String thresholdDeltaColor) {
		
		this.collection = collection;
		this.field = field;
		this.nowValue = nowValue;
		this.operator = operator;
		this.threshold = threshold;
		this.outOfThreshold = outOfThreshold;
		this.thresholdColor = thresholdColor;
		this.beforeValue = beforeValue;
		this.deltaThreshold = deltaThreshold;
		this.outOfThresholdDelta = outOfThresholdDelta;
		this.thresholdDeltaColor = thresholdDeltaColor;
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

	public String getOutOfThreshold() {
		return outOfThreshold;
	}

	public void setOutOfThreshold(String outOfThreshold) {
		this.outOfThreshold = outOfThreshold;
	}

	public String getThresholdColor() {
		return thresholdColor;
	}

	public void setThresholdColor(String thresholdColor) {
		this.thresholdColor = thresholdColor;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}

	public String getDeltaThreshold() {
		return deltaThreshold;
	}

	public void setDeltaThreshold(String deltaThreshold) {
		this.deltaThreshold = deltaThreshold;
	}

	public String getOutOfThresholdDelta() {
		return outOfThresholdDelta;
	}

	public void setOutOfThresholdDelta(String outOfThresholdDelta) {
		this.outOfThresholdDelta = outOfThresholdDelta;
	}

	public String getThresholdDeltaColor() {
		return thresholdDeltaColor;
	}

	public void setThresholdDeltaColor(String thresholdDeltaColor) {
		this.thresholdDeltaColor = thresholdDeltaColor;
	}

		
}

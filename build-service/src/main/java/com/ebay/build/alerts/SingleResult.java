package com.ebay.build.alerts;

public class SingleResult {

	private String collection;
	
	private String field;
	
	private String nowValue;
	
	private String color;
	
	private String operator;

	private String goal;
	
	private String lower;
	
	private String upper;
	
	private String movingAverage;
	
	private String flag;
	
	private String link;

	public SingleResult() {
		super();
	}

    

	public SingleResult(String collection, String field, String nowValue, String color,
			String operator, String goal, String lower, String upper,
			String movingAverage, String flag) {
		super();
		this.collection = collection;
		this.field = field;
		this.nowValue = nowValue;
		this.color = color;
		this.operator = operator;
		this.goal = goal;
		this.lower = lower;
		this.upper = upper;
		this.movingAverage = movingAverage;
		this.flag = flag;
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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

	public String getMovingAverage() {
		return movingAverage;
	}

	public void setMovingAverage(String movingAverage) {
		this.movingAverage = movingAverage;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}

package com.ebay.build.alerts;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.alerts.connector.XMLConnector;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Compare {


	/**
	 * @param args
	 */
	Rules rules;
	DB db;
	AlertResult alertResult = new AlertResult();
	
	public Compare(File file, DB db) {

		rules = XMLConnector.unmarshal(file);

		this.db = db;
	}

	
	public AlertResult judgeRules() {
		
		List<Rule> rulelist = rules.getRulelist();
		
		for(Rule rule : rulelist){
			judgeSingleRule(rule);
		}
		
		return alertResult;
	}
	
	public void judgeSingleRule(Rule rule) {
		
		
		String collection = rule.getCollection();
		
		DBCollection dbc = Connector.connectCollection(db, collection);
	
		Date startDate = java.sql.Date.valueOf("2013-8-30");

		Date endDate = java.sql.Date.valueOf("2013-9-2");

		DBObject totaldbo = Connector.getLastRecord(dbc, startDate, endDate);
		
		DBObject dbo = (DBObject)totaldbo.get("Data");
		
		String field = rule.getField();

		String operatorStr = rule.getOperator();

		int operator;

		if (operatorStr.equals("lt")) {
			operatorStr = "<";
			operator = 0;
		} else if (operatorStr.equals("eq")) {
			operatorStr = "=";
			operator = 1;
		} else if (operatorStr.equals("gt")) {
			operatorStr = ">";
			operator = 2;
		} else
			operator = 3;

		String thresholdStr = rule.getThreshold();
		
		//only support type double 
		Double threshold = Double.parseDouble(thresholdStr);
		
		Set<String> keyset = dbo.keySet();

		for (String keyname : keyset) {

			if (field.equals(keyname)) {

				Object keyvalue = dbo.get(keyname);
				double keyvaluenum = 0 ;
				
				if(keyvalue instanceof Integer){
					int k1 = (Integer)keyvalue;
					Double.parseDouble(""+k1);
				}
				else if(keyvalue instanceof Double){
					keyvaluenum = (Double)keyvalue;
				}
				
				SingleResult singleResult = new SingleResult(collection,keyname,keyvalue.toString(),operatorStr,thresholdStr);
				
				switch (operator) {

				
				case 0:
										
					if (keyvaluenum < threshold) {
						singleResult.setColor("black");		
						
					}
					else{
						singleResult.setColor("red");
						
					}
					break;

				case 1:
					if (keyvalue.equals(threshold)) {
						singleResult.setColor("black");	
						
					}
					else{
						singleResult.setColor("red");
						
					}
					break;

				case 2:
					if (keyvaluenum > threshold) {
						singleResult.setColor("black");	
						
					}
					else{
						singleResult.setColor("red");
						
					}
					break;

				case 3:
				}

				alertResult.getResultlist().add(singleResult);
			}

		}
		return;
	}
	
		
}
